
madura-objects
==

A more detailed document can be found at [Madura Objects (PDF)](http://www.madurasoftware.com/madura-objects.pdf) 

Business Objects
--

Imagine Business Objects looking like ordinary Java Objects, specifically they look like Java objects generated from [JAXB](https://jaxb.dev.java.net/) . That means there are setters and getters for the properties, there are no constructor arguments (so they are simple beans so far) and they use the array stuff that JAXB generates.

Since they actually *are* Java objects generated from JAXB they are backed by a schema file and they can be serialised to XML easily.

And since they are actually generated using the [HyperJAXB3](https://hyperjaxb3.dev.java.net/) plugin they are JPA compatible.

So far, so standard. Programming with these beans takes ordinary Java skills. Designing the Business Objects in XSD takes a little more but nothing that cannot be picked up in an hour or two, especially if you already know JPA. Knowing JPA is a more serious requirement but JPA skills can be reasonably expected.

But we can add a little more. Using another JAXB plugin called [Annox](http://confluence.highsource.org/display/ANX/Home) we can add annotations to the business objects generated from the XSD. This means that fixed metadata (as opposed to dynamic metadata) can be added to the business objects. The best example is a field label.

Now for the cool bit. The Business Objects need to self-validate, and they need to self-validate based on the whole object graph they are in. So, for example you have a Customer object with attached Invoice objects. The total in the customer should calculate automatically as invoices are added. The DAO program that operates the objects does not need to know anything about this.

Also, if the DAO tries to set a value that is invalid in some way the Business Object will throw an exception. The attempted value will not be retained.

So the collection of related Business Objects, we will call this collection a *case*, is always *valid*, although it may be *incomplete*.

To achieve this we use business rules that are run in plugable rules engines. Depending on the need different rules engines can be plugged into the validation engine, or none at all. The latter case makes testing simple. The Business Objects behave almost like ordinary Java Objects when no engine is present.

Metadata
--

This was touched on in the previous section. Metadata is very important. People write lots of code to manage things that could instead be driven by metadata. There are two basic kinds: static and dynamic. Static is simple enough. It is easily handled by annotations. You need a label for a field? Put it in an annotation. You need some processing instruction for a treewalker that looks at this field? Put it in an annotation. These are static.

Dynamic metadata might cover the following:

 * Sometimes a field is available/applicable, sometimes not. It depends on other data.
 * Sometimes it is read-only.
 * There might be a list of valid values. This might be static, in which case it is just an enum, but sometimes it changes, then it is dynamic metadata.

Validation Engine
--

The Validation Engine handles simple validation, which means validating fields in isolation from each other.

It can handle a number of validation requirements based on static metadata:

 * Field length (min/max).
 * Number of digits (integer/fractional).
 * Email: is this a valid email address format?
 * Range: min/max inclusive/exclusive.
 * Matching a Regex expression

These are loosely based on [JSR-303](http://blog.jteam.nl/2009/08/04/bean-validation-integrating-jsr-303-with-spring/) but not the same. Why not? There are good reasons.

 * The JSR-303 definitions, especially as implemented in Hibernate Validation, is designed to be called explicitly to validate some objects you have already loaded with data. Madura Objects works differently. The data is actively and transparently validated behind the setters. So at no time is there ever invalid data in the objects.
 * The Madura Objects validators have a more obvious way to specify the error messages.

This is not to say anything against JSR-303. Just that Madura Objects took a different approach.

Like most of the JSR-303 frameworks you can add your own annotations/validators where you need to.

Plugins		
--
Madura Objects can be injected with plugins which are used to do more than simple field validation. The obvious example is cross-field validation but they might be used to integrate specialised engines, perhaps to derive a price for an order described by the bound objects, or perhaps to assess risk. The plugins must implement the `nz.co.senanque.validationengine.Plugin` interface.

See [madura-rules](./madura-rules/README.md) for details of a rules based plugin.

Like the validation engine the operation of the plugins is completely invisible to the code driving the business objects.

The plugins are optional. You can have none if you want.

Advantages
--
The advantages of all this should be obvious but let's spell them out:

 * The business rules end up below the domain objects rather than implemented above them, which means you do not get them creeping into DAOs and UI layers, and that means for example, when you need to implement a different UI technology you don't find the old UI is riddled with business rules that need to be re-implemented.
 * There is no API to learn. It is just ordinary Java. Not quite true, as we shall see, but the API is smaller than JPA. Almost all the time you are just operating simple Java objects.
 * Serialising to XML and back for web service messages etc is easily handled by standard JAXB. The other main use for this is generating XSL/FO reports.
 * Database is handled by JPA (thanks to HyperJAXB3).
 * Objects are defined outside of Java, in an XSD file. This means they get generated and they cannot be messed about with by people adding code to them when they shouldn't.
 * Simple objects means simple DAOs and simple UI code.

This looks like an Anemic Data model but it is not quite. We call this a *Delegating* Anemic Data Model
![Anemic Data Model](/madura-objects/docs/images/Architecture.png)

Everything centres around the business objects. Multiple applications may use the same business objects, and all of them will automatically implement the same validation and business rules.

In addition we support configuring the application with [Spring](http://www.springframework.org) XML, Spring annotations and [CDI](http://docs.oracle.com/javaee/6/tutorial/doc/giwhl.html) .

Example
--
This is what a small program looks like that uses these objects:

```
// Create a new session using the (probably injected) engine.
ValidationSession validationSession = m_validationEngine.createSession();

// create a customer using the (probably injected) DAO
Customer customer = m_customerDAO.createCustomer();
// This tells the validation session about the object
validationSession.bind(customer);
Invoice invoice = new Invoice();
invoice.setDescription("test invoice");
// Attached objects are automatically added to the session
customer.getInvoices().add(invoice);
boolean exceptionFound = false;
try {
	// Setting an invalid value...
	customer.setName("ttt");
} 
catch (ValidationException e) {
	// ...results in an exception
	exceptionFound = true;
}
assertTrue(exceptionFound);
// But valid values are fine
customer.setName("aaaab");
customer.setBusiness(IndustryType.AG);
// save the customer to database
long id = m_customerDAO.save(customer);
		
```

The code in the DAO is not very complicated, but it is tidier to keep it together. The following leaves out the imports and getters and setters for brevity.

```
public class CustomerJPAImpl implements CustomerDAO {
    @PersistenceContext
    private EntityManager m_entityManager;
    private transient ObjectFactory m_objectFactory;

    public Customer createCustomer() {
        return getObjectFactory().createCustomer();
    }

    @Transactional
    public long save(final Customer customer) {
    	m_entityManager.merge(customer);
    	m_entityManager.flush();
        return customer.getId();
    }

	@Transactional(readOnly=true)
    public Customer getCustomer(long id) {
		Customer ret = null;
		try {
			ret = m_entityManager.find(Customer.class, id, LockModeType.PESSIMISTIC_WRITE);
			if (ret == null) {
				throw new RuntimeException("Could not find customer "+id);
			}
			ret.getInvoices().size(); // ensures the invoices are not lazily loaded
		} catch (Exception e) {
			throw e;
		}
        return ret;
    }
}
		
```
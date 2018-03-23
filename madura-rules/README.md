madura-rules
============

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/nz.co.senanque/madura-objects-parent/badge.svg)](http://mvnrepository.com/artifact/nz.co.senanque/madura-objects-parent)

[![build_status](https://travis-ci.org/RogerParkinson/madura-objects-parent.svg?branch=master)](https://travis-ci.org/RogerParkinson/madura-objects-parent)

A more detailed document can be found at [Madura Rules (PDF)](http://www.madurasoftware.com/madura-rules.pdf) 

Madura Rules is a rules engine designed to work closely with [Madura Objects](https://github.com/RogerParkinson/MaduraObjects). Where Madura Objects provides a transparent validation and metadata facility, Madura Rules is a plugin to Madura Objects that extends it to provide cross-field validation, dynamic metadata and dynamic data generation.

Let's look at a real scenario. You have an object called Customer and to that is linked some Address objects and some Invoice objects. These are all just simple Java beans with getters and setters. Well, they look like that at first. You actually defined them using an XSD file and generated the Java objects using JAXB. Even better you have used the [HyperJAXB3](https://hyperjaxb3.dev.java.net/) and Madura Objects plugins to JAXB to add some extra hooks to those objects. You did not have to write the objects, you just had to define them in an XSD file.

So far this gives you the following features:

* The objects can be serialised to/from XML which is really handy if they need to be passed to PDF tools like FOP, and also useful for web services.
* The objects can be saved and fetched to a database with JPA.
* The objects will self validate. If you attempt to set a value that is incorrect they will throw an exception and the value will not be kept. For example you can set a range on a numeric value and this will automatically be checked.
* You can query the objects for metadata information, specifically for choices available.

If you add Madura Rules to this mix then you extend the validation to cross-field validations, as well as rules to manipulate the metadata. The rules operate totally transparently, except when they throw violation exceptions. All your applications see is a set of Java objects (POJOs). The rules are also able to manipulate metadata. For example they can eliminate some of the available options, set fields to inactive and active, or read only or required.

Each of the generated Java classes therefore exposes an interface for fetching this metadata. So your application can generate a drop-down list of the currently available options, or disable a field if it has been switched to inactive.

The rules are able to generate new data, for example deriving a discount rate based on a customer type, channel and current sale details. 

The rules are also able to work in a mode called *directed questioning* whereby the UI is dynamically driven by the rules. The rules determine what piece of information is needed next based on what they need to know, and generate UI prompts accordingly. Typically you combine this with a more conventional form based approach because directed questioning can only prompt for one field at a time. It can, however, reduce a complex data input exercise to very few inputs instead of presenting the user with an overwhelming list of forms to fill in. If the rules change then the UI changes automatically with no UI rework.


//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.10.25 at 09:52:47 PM NZDT 
//


package nz.co.senanque.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import nz.co.senanque.validationengine.ListeningArray;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationSession;
import nz.co.senanque.validationengine.ValidationUtils;
import org.jvnet.jaxb2_commons.lang.Equals;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCode;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBEqualsStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBHashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;
import org.w3._2001.xmlschema.Adapter1;


/**
 * <p>Java class for Session complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Session">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="started" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="customers" type="{http://www.senanque.co.nz/pizzaorder}Customer" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Session", propOrder = {
    "user",
    "started",
    "customers"
})
@Entity(name = "Session")
@Table(name = "SESSION_")
@Inheritance(strategy = InheritanceType.JOINED)
public class Session
    implements Serializable, ValidationObject, Equals, HashCode, ToString
{

    @XmlElement(required = true)
    protected String user;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date started;
    protected List<Customer> customers;
    @XmlAttribute(name = "Hjid")
    protected Long hjid;
    @XmlTransient
    protected ValidationSession m_validationSession;
    @XmlTransient
    protected ObjectMetadata m_metadata;
    @XmlTransient
    public final static String USER = "user";
    @XmlTransient
    public final static String STARTED = "started";
    @XmlTransient
    public final static String CUSTOMERS = "customers";
    @XmlTransient
    public final static String HJID = "hjid";

    public Session() {
        ValidationUtils.setDefaults(this);
    }

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Basic
    @Column(name = "USER_", length = 255)
    public String getUser() {
        if (m_validationSession!= null) {
            m_validationSession.clean(this);
        }
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        getMetadata().removeUnknown("user");
        if (m_validationSession!= null) {
            m_validationSession.set(this, "user", value, user);
        }
        this.user = value;
        if (m_validationSession!= null) {
            m_validationSession.invokeListeners(this, "user", value, user);
        }
    }

    /**
     * Gets the value of the started property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Basic
    @Column(name = "STARTED")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStarted() {
        if (m_validationSession!= null) {
            m_validationSession.clean(this);
        }
        return started;
    }

    /**
     * Sets the value of the started property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStarted(Date value) {
        getMetadata().removeUnknown("started");
        if (m_validationSession!= null) {
            m_validationSession.set(this, "started", value, started);
        }
        this.started = value;
        if (m_validationSession!= null) {
            m_validationSession.invokeListeners(this, "started", value, started);
        }
    }

    /**
     * Gets the value of the customers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the customers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCustomers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Customer }
     * 
     * 
     */
    @OneToMany(targetEntity = Customer.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "CUSTOMERS_SESSION__HJID")
    public List<Customer> getCustomers() {
        if (m_validationSession!= null) {
            m_validationSession.clean(this);
        }
        if (customers == null) {
            customers = new ListeningArray<Customer>();
        }
        if (customers == null) {
            customers = new ArrayList<Customer>();
        }
        return this.customers;
    }

    /**
     * 
     * 
     */
    public void setCustomers(List<Customer> customers) {
        getMetadata().removeUnknown("customers");
        if (m_validationSession!= null) {
            m_validationSession.set(this, "customers", customers, customers);
        }
        this.customers = customers;
        if (m_validationSession!= null) {
            m_validationSession.invokeListeners(this, "customers", customers, customers);
        }
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if (!(object instanceof Session)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final Session that = ((Session) object);
        {
            String lhsUser;
            lhsUser = this.getUser();
            String rhsUser;
            rhsUser = that.getUser();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "user", lhsUser), LocatorUtils.property(thatLocator, "user", rhsUser), lhsUser, rhsUser)) {
                return false;
            }
        }
        {
            Date lhsStarted;
            lhsStarted = this.getStarted();
            Date rhsStarted;
            rhsStarted = that.getStarted();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "started", lhsStarted), LocatorUtils.property(thatLocator, "started", rhsStarted), lhsStarted, rhsStarted)) {
                return false;
            }
        }
        {
            List<Customer> lhsCustomers;
            lhsCustomers = (((this.customers!= null)&&(!this.customers.isEmpty()))?this.getCustomers():null);
            List<Customer> rhsCustomers;
            rhsCustomers = (((that.customers!= null)&&(!that.customers.isEmpty()))?that.getCustomers():null);
            if (!strategy.equals(LocatorUtils.property(thisLocator, "customers", lhsCustomers), LocatorUtils.property(thatLocator, "customers", rhsCustomers), lhsCustomers, rhsCustomers)) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object object) {
        final EqualsStrategy strategy = JAXBEqualsStrategy.INSTANCE;
        return equals(null, null, object, strategy);
    }

    public String toString() {
        final ToStringStrategy strategy = JAXBToStringStrategy.INSTANCE;
        final StringBuilder buffer = new StringBuilder();
        append(null, buffer, strategy);
        return buffer.toString();
    }

    public StringBuilder append(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        strategy.appendStart(locator, this, buffer);
        appendFields(locator, buffer, strategy);
        strategy.appendEnd(locator, this, buffer);
        return buffer;
    }

    public StringBuilder appendFields(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        {
            String theUser;
            theUser = this.getUser();
            strategy.appendField(locator, this, "user", buffer, theUser);
        }
        {
            Date theStarted;
            theStarted = this.getStarted();
            strategy.appendField(locator, this, "started", buffer, theStarted);
        }
        {
            List<Customer> theCustomers;
            theCustomers = (((this.customers!= null)&&(!this.customers.isEmpty()))?this.getCustomers():null);
            strategy.appendField(locator, this, "customers", buffer, theCustomers);
        }
        return buffer;
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
        int currentHashCode = 1;
        {
            String theUser;
            theUser = this.getUser();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "user", theUser), currentHashCode, theUser);
        }
        {
            Date theStarted;
            theStarted = this.getStarted();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "started", theStarted), currentHashCode, theStarted);
        }
        {
            List<Customer> theCustomers;
            theCustomers = (((this.customers!= null)&&(!this.customers.isEmpty()))?this.getCustomers():null);
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "customers", theCustomers), currentHashCode, theCustomers);
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

    /**
     * Gets the value of the hjid property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    @Id
    @Column(name = "HJID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getHjid() {
        if (m_validationSession!= null) {
            m_validationSession.clean(this);
        }
        return hjid;
    }

    /**
     * Sets the value of the hjid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setHjid(Long value) {
        getMetadata().removeUnknown("hjid");
        if (m_validationSession!= null) {
            m_validationSession.set(this, "hjid", value, hjid);
        }
        this.hjid = value;
        if (m_validationSession!= null) {
            m_validationSession.invokeListeners(this, "hjid", value, hjid);
        }
    }

    @Transient
    public ObjectMetadata getMetadata() {
        if (m_validationSession!= null) {
            m_validationSession.clean(this);
        }
        if (m_metadata == null) {
            m_metadata = new ObjectMetadata();
        }
        return m_metadata;
    }

    @XmlTransient
    public void setValidationSession(ValidationSession validationSession) {
        m_validationSession = validationSession;
    }

}


package nz.co.senanque.pizza;

import java.io.Serializable;
import nz.co.senanque.base.Customer;
import nz.co.senanque.rules.FieldReference;
import nz.co.senanque.rules.NotTrueException;
import nz.co.senanque.rules.Operations;
import nz.co.senanque.rules.Rule;
import nz.co.senanque.rules.RuleContext;
import nz.co.senanque.rules.RuleProxyField;
import nz.co.senanque.rules.RuleSession;
import nz.co.senanque.rules.UnKnownFieldValueException;
import nz.co.senanque.validationengine.ValidationObject;
import org.springframework.stereotype.Component;


/**
 * rule:Customer dynamic
 * {if
 * (name==fred){
 * dynamic=true;}}
 * 
 */
@Component("nz.co.senanque.pizza.R16")
public class R16
    implements Serializable, Rule
{

    private final static long serialVersionUID = 1L;

    public void evaluate(final RuleSession session, final ValidationObject object, final RuleContext ruleContext) {
        final Operations operations = session.getOperations();
        final RuleProxyField proxyFieldname = session.getRuleProxyField(session.getMetadata(object).getProxyField("name"));
        final RuleProxyField proxyFielddynamic = session.getRuleProxyField(session.getMetadata(object).getProxyField("dynamic"));
        try {
            try {
                operations.checkTrue(operations.eq("fred", ((String) proxyFieldname.getValue())));
            } catch (NotTrueException _x) {
                return ;
            }
            session.assign(ruleContext, true, proxyFielddynamic);
        } catch (UnKnownFieldValueException _x) {
            return ;
        }
    }

    public String getRuleName() {
        return "nz.co.senanque.pizza.R16:dynamic";
    }

    public String getMessage(final RuleSession session, final ValidationObject object) {
        return session.getMessage("nz.co.senanque.pizza.R16", new Object[] { });
    }

    public String toString() {
        return "nz.co.senanque.pizza.R16:dynamic";
    }

    public String getClassName() {
        return "Customer";
    }

    public Class<Customer> getScope() {
        return Customer.class;
    }

    public FieldReference[] listeners() {
        return new FieldReference[] {new FieldReference("Customer", "Customer", "name")};
    }

    public FieldReference[] outputs() {
        return new FieldReference[] {new FieldReference("Customer", "Customer", "dynamic")};
    }

}

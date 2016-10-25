
package nz.co.senanque.directed;

import java.io.Serializable;
import nz.co.senanque.base.Customer;
import nz.co.senanque.rules.FieldReference;
import nz.co.senanque.rules.Operations;
import nz.co.senanque.rules.Rule;
import nz.co.senanque.rules.RuleContext;
import nz.co.senanque.rules.RuleProxyField;
import nz.co.senanque.rules.RuleSession;
import nz.co.senanque.rules.UnKnownFieldValueException;
import nz.co.senanque.validationengine.ValidationObject;
import org.springframework.stereotype.Component;


/**
 * formula:Customer Weight pounds
 * {
 * weight=weightPounds*0.453D;}
 * 
 */
@Component("nz.co.senanque.directed.R5")
public class R5
    implements Serializable, Rule
{

    private final static long serialVersionUID = 1L;

    public void evaluate(final RuleSession session, final ValidationObject object, final RuleContext ruleContext) {
        final Operations operations = session.getOperations();
        final RuleProxyField proxyFieldweight = session.getRuleProxyField(session.getMetadata(object).getProxyField("weight"));
        final RuleProxyField proxyFieldweightPounds = session.getRuleProxyField(session.getMetadata(object).getProxyField("weightPounds"));
        try {
            session.assign(ruleContext, operations.mul(0.453D, ((Double) proxyFieldweightPounds.getValue())), proxyFieldweight);
        } catch (UnKnownFieldValueException _x) {
            return ;
        }
    }

    public String getRuleName() {
        return "nz.co.senanque.directed.R5:Weight pounds";
    }

    public String getMessage(final RuleSession session, final ValidationObject object) {
        return session.getMessage("nz.co.senanque.directed.R5", new Object[] { });
    }

    public String toString() {
        return "nz.co.senanque.directed.R5:Weight pounds";
    }

    public String getClassName() {
        return "Customer";
    }

    public Class<Customer> getScope() {
        return Customer.class;
    }

    public FieldReference[] listeners() {
        return new FieldReference[] {new FieldReference("Customer", "Customer", "weightPounds")};
    }

    public FieldReference[] outputs() {
        return new FieldReference[] {new FieldReference("Customer", "Customer", "weight")};
    }

}

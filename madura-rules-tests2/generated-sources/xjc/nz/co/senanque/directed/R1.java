
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
 * formula:Customer BMI
 * {
 * bmi=weight/(height*height);}
 * 
 */
@Component("nz.co.senanque.directed.R1")
public class R1
    implements Serializable, Rule
{

    private final static long serialVersionUID = 1L;

    public void evaluate(final RuleSession session, final ValidationObject object, final RuleContext ruleContext) {
        final Operations operations = session.getOperations();
        final RuleProxyField proxyFieldweight = session.getRuleProxyField(session.getMetadata(object).getProxyField("weight"));
        final RuleProxyField proxyFieldbmi = session.getRuleProxyField(session.getMetadata(object).getProxyField("bmi"));
        final RuleProxyField proxyFieldheight = session.getRuleProxyField(session.getMetadata(object).getProxyField("height"));
        try {
            session.assign(ruleContext, operations.div(operations.mul(((Double) proxyFieldheight.getValue()), ((Double) proxyFieldheight.getValue())), ((Double) proxyFieldweight.getValue())), proxyFieldbmi);
        } catch (UnKnownFieldValueException _x) {
            return ;
        }
    }

    public String getRuleName() {
        return "nz.co.senanque.directed.R1:BMI";
    }

    public String getMessage(final RuleSession session, final ValidationObject object) {
        return session.getMessage("nz.co.senanque.directed.R1", new Object[] { });
    }

    public String toString() {
        return "nz.co.senanque.directed.R1:BMI";
    }

    public String getClassName() {
        return "Customer";
    }

    public Class<Customer> getScope() {
        return Customer.class;
    }

    public FieldReference[] listeners() {
        return new FieldReference[] {new FieldReference("Customer", "Customer", "height"), new FieldReference("Customer", "Customer", "weight")};
    }

    public FieldReference[] outputs() {
        return new FieldReference[] {new FieldReference("Customer", "Customer", "bmi")};
    }

}

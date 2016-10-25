
package nz.co.senanque.notknown;

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
 * formula:Customer Height Imperial
 * {
 * height=(heightFeet*0.3048D)+(heightInches*0.0254D);}
 * 
 */
@Component("nz.co.senanque.notknown.R4")
public class R4
    implements Serializable, Rule
{

    private final static long serialVersionUID = 1L;

    public void evaluate(final RuleSession session, final ValidationObject object, final RuleContext ruleContext) {
        final Operations operations = session.getOperations();
        final RuleProxyField proxyFieldheight = session.getRuleProxyField(session.getMetadata(object).getProxyField("height"));
        final RuleProxyField proxyFieldheightFeet = session.getRuleProxyField(session.getMetadata(object).getProxyField("heightFeet"));
        final RuleProxyField proxyFieldheightInches = session.getRuleProxyField(session.getMetadata(object).getProxyField("heightInches"));
        try {
            session.assign(ruleContext, operations.add(operations.mul(0.0254D, ((Double) proxyFieldheightInches.getValue())), operations.mul(0.3048D, ((Double) proxyFieldheightFeet.getValue()))), proxyFieldheight);
        } catch (UnKnownFieldValueException _x) {
            return ;
        }
    }

    public String getRuleName() {
        return "nz.co.senanque.notknown.R4:Height Imperial";
    }

    public String getMessage(final RuleSession session, final ValidationObject object) {
        return session.getMessage("nz.co.senanque.notknown.R4", new Object[] { });
    }

    public String toString() {
        return "nz.co.senanque.notknown.R4:Height Imperial";
    }

    public String getClassName() {
        return "Customer";
    }

    public Class<Customer> getScope() {
        return Customer.class;
    }

    public FieldReference[] listeners() {
        return new FieldReference[] {new FieldReference("Customer", "Customer", "heightInches"), new FieldReference("Customer", "Customer", "heightFeet")};
    }

    public FieldReference[] outputs() {
        return new FieldReference[] {new FieldReference("Customer", "Customer", "height")};
    }

}

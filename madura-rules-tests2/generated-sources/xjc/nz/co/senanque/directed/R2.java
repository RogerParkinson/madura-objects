
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
 * formula:Customer Height Metric
 * {
 * height=heightMetric;}
 * 
 */
@Component("nz.co.senanque.directed.R2")
public class R2
    implements Serializable, Rule
{

    private final static long serialVersionUID = 1L;

    public void evaluate(final RuleSession session, final ValidationObject object, final RuleContext ruleContext) {
        final Operations operations = session.getOperations();
        final RuleProxyField proxyFieldheight = session.getRuleProxyField(session.getMetadata(object).getProxyField("height"));
        final RuleProxyField proxyFieldheightMetric = session.getRuleProxyField(session.getMetadata(object).getProxyField("heightMetric"));
        try {
            session.assign(ruleContext, ((Double) proxyFieldheightMetric.getValue()), proxyFieldheight);
        } catch (UnKnownFieldValueException _x) {
            return ;
        }
    }

    public String getRuleName() {
        return "nz.co.senanque.directed.R2:Height Metric";
    }

    public String getMessage(final RuleSession session, final ValidationObject object) {
        return session.getMessage("nz.co.senanque.directed.R2", new Object[] { });
    }

    public String toString() {
        return "nz.co.senanque.directed.R2:Height Metric";
    }

    public String getClassName() {
        return "Customer";
    }

    public Class<Customer> getScope() {
        return Customer.class;
    }

    public FieldReference[] listeners() {
        return new FieldReference[] {new FieldReference("Customer", "Customer", "heightMetric")};
    }

    public FieldReference[] outputs() {
        return new FieldReference[] {new FieldReference("Customer", "Customer", "height")};
    }

}

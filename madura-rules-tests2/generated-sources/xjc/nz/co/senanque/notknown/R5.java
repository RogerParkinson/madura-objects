
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
 * formula:Customer Weight metric
 * {
 * weight=weightKilos;}
 * 
 */
@Component("nz.co.senanque.notknown.R5")
public class R5
    implements Serializable, Rule
{

    private final static long serialVersionUID = 1L;

    public void evaluate(final RuleSession session, final ValidationObject object, final RuleContext ruleContext) {
        final Operations operations = session.getOperations();
        final RuleProxyField proxyFieldweightKilos = session.getRuleProxyField(session.getMetadata(object).getProxyField("weightKilos"));
        final RuleProxyField proxyFieldweight = session.getRuleProxyField(session.getMetadata(object).getProxyField("weight"));
        try {
            session.assign(ruleContext, ((Double) proxyFieldweightKilos.getValue()), proxyFieldweight);
        } catch (UnKnownFieldValueException _x) {
            return ;
        }
    }

    public String getRuleName() {
        return "nz.co.senanque.notknown.R5:Weight metric";
    }

    public String getMessage(final RuleSession session, final ValidationObject object) {
        return session.getMessage("nz.co.senanque.notknown.R5", new Object[] { });
    }

    public String toString() {
        return "nz.co.senanque.notknown.R5:Weight metric";
    }

    public String getClassName() {
        return "Customer";
    }

    public Class<Customer> getScope() {
        return Customer.class;
    }

    public FieldReference[] listeners() {
        return new FieldReference[] {new FieldReference("Customer", "Customer", "weightKilos")};
    }

    public FieldReference[] outputs() {
        return new FieldReference[] {new FieldReference("Customer", "Customer", "weight")};
    }

}

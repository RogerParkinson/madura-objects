
package nz.co.senanque.notknown;

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
 * rule:Customer isnotknown
 * {if
 * (isNotKnown((weightKilos)){
 * address=not known rule fired;}}
 * 
 */
@Component("nz.co.senanque.notknown.R2")
public class R2
    implements Serializable, Rule
{

    private final static long serialVersionUID = 1L;

    public void evaluate(final RuleSession session, final ValidationObject object, final RuleContext ruleContext) {
        final Operations operations = session.getOperations();
        final RuleProxyField proxyFieldaddress = session.getRuleProxyField(session.getMetadata(object).getProxyField("address"));
        final RuleProxyField proxyFieldweightKilos = session.getRuleProxyField(session.getMetadata(object).getProxyField("weightKilos"));
        try {
            try {
                operations.checkTrue(session.isNotKnown(proxyFieldweightKilos));
            } catch (NotTrueException _x) {
                return ;
            }
            session.assign(ruleContext, "not known rule fired", proxyFieldaddress);
        } catch (UnKnownFieldValueException _x) {
            return ;
        }
    }

    public String getRuleName() {
        return "nz.co.senanque.notknown.R2:isnotknown";
    }

    public String getMessage(final RuleSession session, final ValidationObject object) {
        return session.getMessage("nz.co.senanque.notknown.R2", new Object[] { });
    }

    public String toString() {
        return "nz.co.senanque.notknown.R2:isnotknown";
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
        return new FieldReference[] {new FieldReference("Customer", "Customer", "address")};
    }

}

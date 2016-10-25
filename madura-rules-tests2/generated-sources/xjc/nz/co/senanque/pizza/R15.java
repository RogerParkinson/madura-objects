
package nz.co.senanque.pizza;

import java.io.Serializable;
import nz.co.senanque.base.Pizza;
import nz.co.senanque.rules.ConstraintViolationException;
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
 * constraint:Pizza p5
 * {
 * testDouble>100D;}
 * 
 */
@Component("nz.co.senanque.pizza.R15")
public class R15
    implements Serializable, Rule
{

    private final static long serialVersionUID = 1L;

    public void evaluate(final RuleSession session, final ValidationObject object, final RuleContext ruleContext) {
        final Operations operations = session.getOperations();
        final RuleProxyField proxyFieldtestDouble = session.getRuleProxyField(session.getMetadata(object).getProxyField("testDouble"));
        try {
            try {
                operations.checkTrue(operations.gt(100.0D, ((Double) proxyFieldtestDouble.getValue())));
            } catch (NotTrueException _x) {
                throw new ConstraintViolationException(getMessage(session, object));
            }
        } catch (UnKnownFieldValueException _x) {
            return ;
        }
    }

    public String getRuleName() {
        return "nz.co.senanque.pizza.R15:p5";
    }

    public String getMessage(final RuleSession session, final ValidationObject object) {
        return session.getMessage("nz.co.senanque.pizza.R15", new Object[] { });
    }

    public String toString() {
        return "nz.co.senanque.pizza.R15:p5";
    }

    public String getClassName() {
        return "Pizza";
    }

    public Class<Pizza> getScope() {
        return Pizza.class;
    }

    public FieldReference[] listeners() {
        return new FieldReference[] {new FieldReference("Pizza", "Pizza", "testDouble")};
    }

    public FieldReference[] outputs() {
        return null;
    }

}

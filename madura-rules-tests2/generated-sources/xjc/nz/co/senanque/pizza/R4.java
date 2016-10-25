
package nz.co.senanque.pizza;

import java.io.Serializable;
import nz.co.senanque.base.Drink;
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
 * formula:Drink d
 * {
 * amount=2.50;}
 * 
 */
@Component("nz.co.senanque.pizza.R4")
public class R4
    implements Serializable, Rule
{

    private final static long serialVersionUID = 1L;

    public void evaluate(final RuleSession session, final ValidationObject object, final RuleContext ruleContext) {
        final Operations operations = session.getOperations();
        final RuleProxyField proxyFieldamount = session.getRuleProxyField(session.getMetadata(object).getProxyField("amount"));
        try {
            session.assign(ruleContext, 2.5D, proxyFieldamount);
        } catch (UnKnownFieldValueException _x) {
            return ;
        }
    }

    public String getRuleName() {
        return "nz.co.senanque.pizza.R4:d";
    }

    public String getMessage(final RuleSession session, final ValidationObject object) {
        return session.getMessage("nz.co.senanque.pizza.R4", new Object[] { });
    }

    public String toString() {
        return "nz.co.senanque.pizza.R4:d";
    }

    public String getClassName() {
        return "Drink";
    }

    public Class<Drink> getScope() {
        return Drink.class;
    }

    public FieldReference[] listeners() {
        return null;
    }

    public FieldReference[] outputs() {
        return new FieldReference[] {new FieldReference("OrderItem", "Drink", "amount")};
    }

}

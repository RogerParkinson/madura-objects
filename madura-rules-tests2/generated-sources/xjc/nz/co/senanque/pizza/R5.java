
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
 * itemType=ItemType.DRINK;}
 * 
 */
@Component("nz.co.senanque.pizza.R5")
public class R5
    implements Serializable, Rule
{

    private final static long serialVersionUID = 1L;

    public void evaluate(final RuleSession session, final ValidationObject object, final RuleContext ruleContext) {
        final Operations operations = session.getOperations();
        final RuleProxyField proxyFielditemType = session.getRuleProxyField(session.getMetadata(object).getProxyField("itemType"));
        try {
            session.assign(ruleContext, (nz.co.senanque.base.ItemType.DRINK), proxyFielditemType);
        } catch (UnKnownFieldValueException _x) {
            return ;
        }
    }

    public String getRuleName() {
        return "nz.co.senanque.pizza.R5:d";
    }

    public String getMessage(final RuleSession session, final ValidationObject object) {
        return session.getMessage("nz.co.senanque.pizza.R5", new Object[] { });
    }

    public String toString() {
        return "nz.co.senanque.pizza.R5:d";
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
        return new FieldReference[] {new FieldReference("OrderItem", "Drink", "itemType")};
    }

}

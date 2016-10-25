
package nz.co.senanque.pizza;

import java.io.Serializable;
import nz.co.senanque.base.Extra;
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
 * formula:Extra e
 * {
 * itemType=ItemType.EXTRA;}
 * 
 */
@Component("nz.co.senanque.pizza.R6")
public class R6
    implements Serializable, Rule
{

    private final static long serialVersionUID = 1L;

    public void evaluate(final RuleSession session, final ValidationObject object, final RuleContext ruleContext) {
        final Operations operations = session.getOperations();
        final RuleProxyField proxyFielditemType = session.getRuleProxyField(session.getMetadata(object).getProxyField("itemType"));
        try {
            session.assign(ruleContext, (nz.co.senanque.base.ItemType.EXTRA), proxyFielditemType);
        } catch (UnKnownFieldValueException _x) {
            return ;
        }
    }

    public String getRuleName() {
        return "nz.co.senanque.pizza.R6:e";
    }

    public String getMessage(final RuleSession session, final ValidationObject object) {
        return session.getMessage("nz.co.senanque.pizza.R6", new Object[] { });
    }

    public String toString() {
        return "nz.co.senanque.pizza.R6:e";
    }

    public String getClassName() {
        return "Extra";
    }

    public Class<Extra> getScope() {
        return Extra.class;
    }

    public FieldReference[] listeners() {
        return null;
    }

    public FieldReference[] outputs() {
        return new FieldReference[] {new FieldReference("OrderItem", "Extra", "itemType")};
    }

}

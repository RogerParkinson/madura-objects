
package nz.co.senanque.pizza;

import java.io.Serializable;
import nz.co.senanque.base.Pizza;
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
 * rule:Pizza p4
 * {if
 * (size==Large){
 * activate((testing);
 * amount=20;}}
 * 
 */
@Component("nz.co.senanque.pizza.R14")
public class R14
    implements Serializable, Rule
{

    private final static long serialVersionUID = 1L;

    public void evaluate(final RuleSession session, final ValidationObject object, final RuleContext ruleContext) {
        final Operations operations = session.getOperations();
        final RuleProxyField proxyFieldamount = session.getRuleProxyField(session.getMetadata(object).getProxyField("amount"));
        final RuleProxyField proxyFieldsize = session.getRuleProxyField(session.getMetadata(object).getProxyField("size"));
        final RuleProxyField proxyFieldtesting = session.getRuleProxyField(session.getMetadata(object).getProxyField("testing"));
        try {
            try {
                operations.checkTrue(operations.eq("Large", ((String) proxyFieldsize.getValue())));
            } catch (NotTrueException _x) {
                return ;
            }
            session.activate(ruleContext, proxyFieldtesting);
            session.assign(ruleContext, 20L, proxyFieldamount);
        } catch (UnKnownFieldValueException _x) {
            return ;
        }
    }

    public String getRuleName() {
        return "nz.co.senanque.pizza.R14:p4";
    }

    public String getMessage(final RuleSession session, final ValidationObject object) {
        return session.getMessage("nz.co.senanque.pizza.R14", new Object[] { });
    }

    public String toString() {
        return "nz.co.senanque.pizza.R14:p4";
    }

    public String getClassName() {
        return "Pizza";
    }

    public Class<Pizza> getScope() {
        return Pizza.class;
    }

    public FieldReference[] listeners() {
        return new FieldReference[] {new FieldReference("Pizza", "Pizza", "size")};
    }

    public FieldReference[] outputs() {
        return new FieldReference[] {new FieldReference("OrderItem", "Pizza", "amount")};
    }

}

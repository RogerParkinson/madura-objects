
package nz.co.senanque.pizza;

import java.io.Serializable;
import nz.co.senanque.base.OrderItem;
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
 * formula:OrderItem description
 * {
 * description=format((name,name);}
 * 
 */
@Component("nz.co.senanque.pizza.R1")
public class R1
    implements Serializable, Rule
{

    private final static long serialVersionUID = 1L;

    public void evaluate(final RuleSession session, final ValidationObject object, final RuleContext ruleContext) {
        final Operations operations = session.getOperations();
        final RuleProxyField proxyFieldname = session.getRuleProxyField(session.getMetadata(object).getProxyField("name"));
        final RuleProxyField proxyFielddescription = session.getRuleProxyField(session.getMetadata(object).getProxyField("description"));
        try {
            session.assign(ruleContext, operations.format(((String) proxyFieldname.getValue()), ((String) proxyFieldname.getValue())), proxyFielddescription);
        } catch (UnKnownFieldValueException _x) {
            return ;
        }
    }

    public String getRuleName() {
        return "nz.co.senanque.pizza.R1:description";
    }

    public String getMessage(final RuleSession session, final ValidationObject object) {
        return session.getMessage("nz.co.senanque.pizza.R1", new Object[] { });
    }

    public String toString() {
        return "nz.co.senanque.pizza.R1:description";
    }

    public String getClassName() {
        return "OrderItem";
    }

    public Class<OrderItem> getScope() {
        return OrderItem.class;
    }

    public FieldReference[] listeners() {
        return new FieldReference[] {new FieldReference("OrderItem", "OrderItem", "name")};
    }

    public FieldReference[] outputs() {
        return new FieldReference[] {new FieldReference("OrderItem", "OrderItem", "description")};
    }

}

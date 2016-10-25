
package nz.co.senanque.pizza;

import java.io.Serializable;
import java.util.List;
import nz.co.senanque.base.Order;
import nz.co.senanque.rules.FieldReference;
import nz.co.senanque.rules.Operations;
import nz.co.senanque.rules.Rule;
import nz.co.senanque.rules.RuleContext;
import nz.co.senanque.rules.RuleProxyField;
import nz.co.senanque.rules.RuleSession;
import nz.co.senanque.rules.UnKnownFieldValueException;
import nz.co.senanque.validationengine.ProxyField;
import nz.co.senanque.validationengine.ValidationObject;
import org.springframework.stereotype.Component;


/**
 * formula:Order sum
 * {
 * amount=sum((orderItems.amount);}
 * 
 */
@Component("nz.co.senanque.pizza.R2")
public class R2
    implements Serializable, Rule
{

    private final static long serialVersionUID = 1L;

    public void evaluate(final RuleSession session, final ValidationObject object, final RuleContext ruleContext) {
        final Operations operations = session.getOperations();
        final RuleProxyField proxyFieldamount = session.getRuleProxyField(session.getMetadata(object).getProxyField("amount"));
        final List<ProxyField> proxyFieldorderItems_amount = session.getMetadata(object).getProxyFields("orderItems", "amount");
        try {
            session.assign(ruleContext, operations.sum(proxyFieldorderItems_amount), proxyFieldamount);
        } catch (UnKnownFieldValueException _x) {
            return ;
        }
    }

    public String getRuleName() {
        return "nz.co.senanque.pizza.R2:sum";
    }

    public String getMessage(final RuleSession session, final ValidationObject object) {
        return session.getMessage("nz.co.senanque.pizza.R2", new Object[] { });
    }

    public String toString() {
        return "nz.co.senanque.pizza.R2:sum";
    }

    public String getClassName() {
        return "Order";
    }

    public Class<Order> getScope() {
        return Order.class;
    }

    public FieldReference[] listeners() {
        return new FieldReference[] {new FieldReference("Order", "Order", "orderItems"), new FieldReference("OrderItem", "Order", "amount")};
    }

    public FieldReference[] outputs() {
        return new FieldReference[] {new FieldReference("Order", "Order", "amount")};
    }

}

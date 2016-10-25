
package nz.co.senanque.pizza;

import java.io.Serializable;
import nz.co.senanque.base.Order;
import nz.co.senanque.rules.FieldReference;
import nz.co.senanque.rules.NotTrueException;
import nz.co.senanque.rules.Operations;
import nz.co.senanque.rules.Rule;
import nz.co.senanque.rules.RuleContext;
import nz.co.senanque.rules.RuleProxyField;
import nz.co.senanque.rules.RuleSession;
import nz.co.senanque.rules.UnKnownFieldValueException;
import nz.co.senanque.validationengine.ListeningArray;
import nz.co.senanque.validationengine.ValidationObject;
import org.springframework.stereotype.Component;


/**
 * rule:Order shoppingcartsize
 * {if
 * (count((orderItems)==0){
 * shoppingCartStatus=format((shopping.cart.status.empty,0);}}
 * 
 */
@Component("nz.co.senanque.pizza.R12")
public class R12
    implements Serializable, Rule
{

    private final static long serialVersionUID = 1L;

    public void evaluate(final RuleSession session, final ValidationObject object, final RuleContext ruleContext) {
        final Operations operations = session.getOperations();
        final RuleProxyField proxyFieldorderItems = session.getRuleProxyField(session.getMetadata(object).getProxyField("orderItems"));
        final RuleProxyField proxyFieldshoppingCartStatus = session.getRuleProxyField(session.getMetadata(object).getProxyField("shoppingCartStatus"));
        try {
            try {
                operations.checkTrue(operations.eq(0L, operations.count(((ListeningArray) proxyFieldorderItems.getValue()))));
            } catch (NotTrueException _x) {
                return ;
            }
            session.assign(ruleContext, operations.format(0L, "shopping.cart.status.empty"), proxyFieldshoppingCartStatus);
        } catch (UnKnownFieldValueException _x) {
            return ;
        }
    }

    public String getRuleName() {
        return "nz.co.senanque.pizza.R12:shoppingcartsize";
    }

    public String getMessage(final RuleSession session, final ValidationObject object) {
        return session.getMessage("nz.co.senanque.pizza.R12", new Object[] { });
    }

    public String toString() {
        return "nz.co.senanque.pizza.R12:shoppingcartsize";
    }

    public String getClassName() {
        return "Order";
    }

    public Class<Order> getScope() {
        return Order.class;
    }

    public FieldReference[] listeners() {
        return new FieldReference[] {new FieldReference("Order", "Order", "orderItems")};
    }

    public FieldReference[] outputs() {
        return new FieldReference[] {new FieldReference("Order", "Order", "shoppingCartStatus")};
    }

}

package nz.co.senanque.pizzaorder;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
		"nz.co.senanque.pizza" })
public class PizzaOrderConfiguration {

}

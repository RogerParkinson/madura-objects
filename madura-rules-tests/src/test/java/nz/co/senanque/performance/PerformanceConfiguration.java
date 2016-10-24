package nz.co.senanque.performance;

import nz.co.senanque.validationengine.choicelists.ChoiceListFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
		"nz.co.senanque.performance" })
public class PerformanceConfiguration {
	
	@Bean
	public ChoiceListFactory getChoiceListFactories() {
		return new Factories();
	}

}

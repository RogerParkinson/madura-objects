package nz.co.senanque.validationengine.cdi;

import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import nz.co.senanque.validationengine.ValidationEngine;

import org.apache.commons.lang.StringUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ResourceBundleMessageSource;

@Singleton
public class ValidationEngineProducer {
	
    @Inject
    @ConfigProperty(name = "nz.co.senanque.validationengine.metadata.AnnotationsMetadataFactory.packages")
    private String rulePackages;
    @Inject
    @ConfigProperty(name = "nz.co.senanque.validationengine.cdi.ValidationEngineProducer.messages:ValidationMessages")
    private String messages;

	AnnotationConfigApplicationContext context;

	public ValidationEngineProducer() {
	}
	@PostConstruct
	public void init() {
		context = new AnnotationConfigApplicationContext();
		context.registerBeanDefinition("propertyConfigurer", getPropertyPlaceHolder());
		context.registerBeanDefinition("messageSource", getMessageSource());
		context.scan(
				"nz.co.senanque.validationengine",
				"nz.co.senanque.validationengine.annotations",
				"nz.co.senanque.rules");
		if (!StringUtils.isEmpty(rulePackages)) {
			context.scan(StringUtils.split(rulePackages, ','));
		}
		context.refresh();
	}
	
	private BeanDefinition getPropertyPlaceHolder() {
		Map<String,String> map = ConfigResolver.getAllProperties();
		Properties props = new Properties();
		props.putAll(map);

		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(PropertyPlaceholderConfigurer.class);
		beanDefinition.setPropertyValues(
				new MutablePropertyValues()
//					.add("location", new ClassPathResource("config.properties")));
					.add("properties", props));
		return beanDefinition;
	}

	private BeanDefinition getMessageSource() {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(ResourceBundleMessageSource.class);
		beanDefinition.setPropertyValues(
				new MutablePropertyValues()
					.add("basenames", StringUtils.split(messages, ',')));
		return beanDefinition;
	}
	
	@PreDestroy
	public void close() {
		context.close();
	}
	
	@Produces
	public ValidationEngine getValidationEngine() {
		return context.getBean(ValidationEngine.class);
	}

}

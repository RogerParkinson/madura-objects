/*******************************************************************************
 * Copyright (c)2015 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nz.co.senanque.base;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.oxm.Marshaller;

import nz.co.senanque.resourceloader.ResourceBundleMessageSourceExt;
import nz.co.senanque.validationengine.ValidationEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Roger Parkinson
 *
 */

@Configuration
@ComponentScan(basePackages = { 
		"nz.co.senanque.validationengine",
		"nz.co.senanque.rules",
		"nz.co.senanque.base" })
@PropertySource("classpath:config.properties")
public class SpringConfiguration {

	@Autowired
	Environment env;

	@Autowired
	ValidationEngine m_engine;

	public SpringConfiguration() {
		"".toString();
	}

	public ValidationEngine getEngine() {
		return m_engine;
	}

	public void setEngine(ValidationEngine engine) {
		m_engine = engine;
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSourceExt();
		messageSource.setBasenames("Messages");
		return messageSource;
	}

	@Bean
	public DataSource dataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		return builder.setType(EmbeddedDatabaseType.H2).build();
	}

	@Bean
	public EntityManagerFactory entityManagerFactory() {

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(true);

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setDataSource(dataSource());
		factory.afterPropertiesSet();

		return factory.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {

		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory());
		return txManager;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public ObjectFactory getObjectFactory() {
		return new ObjectFactory();
	}

	@Bean
	public Marshaller getJaxb2Marshaller() {
		Jaxb2Marshaller ret = new Jaxb2Marshaller();
		ret.setContextPath("nz.co.senanque.base");
		return ret;
	}

}

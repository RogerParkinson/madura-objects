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

import nz.co.senanque.validationengine.ValidationEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Common configuration file. This demonstrates how simple the configuration
 * files can get, though there is a small complication that needs an XML import
 * because of the message source. The message source uses a custom message source here
 * which reads from an XML file.
 * 
 * @author Roger Parkinson
 *
 */

@Configuration
@ComponentScan(basePackages = { 
		"nz.co.senanque.validationengine",
		"nz.co.senanque.rules",
		"nz.co.senanque.base" })
@PropertySource("classpath:config.properties")
@ImportResource("classpath:SpringConfiguration.xml")
@EnableTransactionManagement
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
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public ObjectFactory getObjectFactory() {
		return new ObjectFactory();
	}


}

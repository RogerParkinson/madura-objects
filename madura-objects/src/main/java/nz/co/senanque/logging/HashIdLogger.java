/*******************************************************************************
 * Copyright (c)2016 Prometheus Consulting
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
package nz.co.senanque.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;

/**
 * Classes call this to log information about their hash id and, optionally, the bean factory
 * that created them. It helps when trying to establish if there is just one
 * instance of this class active or multiple instances.
 *  
 * @author Roger Parkinson
 *
 */
public class HashIdLogger {
	
	static Logger logger = LoggerFactory.getLogger(HashIdLogger.class);
	
	public static void log(Object object, Object id) {
		if (logger.isDebugEnabled()) {
			logger.debug("{}@{} {}",object.getClass(),System.identityHashCode(object),id);
		}
	}

	public static void log(Object object, String id, BeanFactory beanFactory) {
		if (logger.isDebugEnabled()) {
			logger.debug("{}@{} {} {}",object.getClass(),System.identityHashCode(object),id,beanFactory);
		}
	}


}

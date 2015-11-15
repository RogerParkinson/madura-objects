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
package nz.co.senanque.validationengine;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

/**
 * @author Roger Parkinson
 *
 */
@Service("localeAwareExceptionFactory")
public class LocaleAwareExceptionFactory implements MessageSourceAware {

	private MessageSource m_messageSource;

	public Exception getException(String message, Object... args ) {
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);
        String localisedMessage = messageSourceAccessor.getMessage(message,args);
        return new Exception(localisedMessage);
	}

	public RuntimeException getRuntimeException(String message, Object... args ) {
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);
        String localisedMessage = messageSourceAccessor.getMessage(message,args);
        return new RuntimeException(localisedMessage);
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
	}

}

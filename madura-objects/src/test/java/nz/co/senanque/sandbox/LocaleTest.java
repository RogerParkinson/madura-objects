/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
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
package nz.co.senanque.sandbox;

import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * Short description
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/TestBase-spring.xml"})
public class LocaleTest
{
    @Autowired private transient MessageSource m_messageSource;

    @Test
    public void testValidate()
    {
    	Locale locale = new Locale("fr");
        Locale.setDefault(locale);
    	String t = MessageFormat.format("Échec de l''envoi: label = {0} n''est pas une adresse email valide, a tenté = {1}",new Object[]{"aa","bb"});
    	assertEquals("Échec de l'envoi: label = aa n'est pas une adresse email valide, a tenté = bb",t);
    }

	public MessageSource getMessageSource() {
		return m_messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
	}

}

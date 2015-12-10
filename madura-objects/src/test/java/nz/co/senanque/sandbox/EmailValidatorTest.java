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

import static org.junit.Assert.assertTrue;
import nz.co.senanque.validationengine.ValidationException;
import nz.co.senanque.validationengine.annotations.Email;
import nz.co.senanque.validationengine.fieldvalidators.EmailValidator;

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
@Email
public class EmailValidatorTest
{
    @Autowired private transient MessageSource m_messageSource;

    @Test
    public void testValidate()
    {
        final Email emailDTO = this.getClass().getAnnotation(Email.class);
        final PropertyMetadataMock propertyMetadata = new PropertyMetadataMock(m_messageSource);
        propertyMetadata.setClass(String.class);
        final EmailValidator emailValidator = new EmailValidator();
        emailValidator.init(emailDTO, propertyMetadata);
        
        emailValidator.validate("aa@aa");
        boolean exception = false;
        try
        {
            emailValidator.validate("aa");
        }
        catch (ValidationException e)
        {
            exception =true;
        }
        assertTrue(exception);
        
    }

	public MessageSource getMessageSource() {
		return m_messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
	}

}

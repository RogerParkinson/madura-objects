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
package nz.co.senanque.rules;

import nz.co.senanque.validationengine.ValidationException;



/**
 * 
 * Thrown when a constraint fails
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.2 $
 */
public class ConstraintViolationException extends ValidationException
{
    private Rule m_rule;
    
    private static final long serialVersionUID = 1L;

    public ConstraintViolationException(final String constraint_message)
    {
        super(constraint_message);
    }

    public ConstraintViolationException(final String constraint_message, Exception e)
    {
        super(constraint_message);
    }

    public Rule getRule()
    {
        return m_rule;
    }

    public void setRule(Rule rule)
    {
        m_rule = rule;
    }
}

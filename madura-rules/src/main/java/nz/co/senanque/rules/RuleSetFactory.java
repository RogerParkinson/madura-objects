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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.FactoryBean;

/**
 * Identifies all the rules classes. Instantiates an object for each and returns them all in a list
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.2 $
 */
@SuppressWarnings("rawtypes")
@Deprecated
public class RuleSetFactory implements FactoryBean
{
    private List<Class<?>> m_class;

    public List<Rule> getObject() throws Exception
    {
        final List<Rule> rules = new ArrayList<Rule>();
        for (Class<?> clazz: m_class)
        {
            rules.add((Rule)clazz.newInstance());
        }
        return rules;
    }

    public Class<?> getObjectType()
    {
        return List.class;
    }

    public boolean isSingleton()
    {
        return true;
    }

}

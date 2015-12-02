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
package nz.co.senanque.schemaparser;

/**
 * 
 * Holds a reference to an enumerated constant
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.1 $
 */
public class EnumeratedConstant
{
    private final String m_className;
    private final String m_fieldName;
    private final String m_packageName;

    public EnumeratedConstant(String packageName, String className, String fieldName)
    {
        m_packageName = packageName;
        m_className = className;
        m_fieldName = fieldName;
    }

    public String getClassName()
    {
        return m_className;
    }

    public String getFieldName()
    {
        return m_fieldName;
    }
    public String toString()
    {
        return m_packageName+"."+m_className + "." + m_fieldName;
    }

}

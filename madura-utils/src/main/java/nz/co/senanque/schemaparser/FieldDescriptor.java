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
 * Field descriptor
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.2 $
 */
public class FieldDescriptor
{
    private final String m_name;
    private final String m_clazz;
    private final String m_type;
    private final boolean m_list;
    
    public String toString() {
    	return m_name;
    }
    
    public FieldDescriptor(String clazz,String name, String type, boolean list)
    {
        m_clazz = clazz;
        m_name = name;
        m_type = type;
        m_list = list;
    }
    public String getName()
    {
        return m_name;
    }
    public String getType()
    {
        return m_type;
    }
    public String getClazz()
    {
        return m_clazz;
    }
    public boolean isList()
    {
        return m_list;
    }
    
}
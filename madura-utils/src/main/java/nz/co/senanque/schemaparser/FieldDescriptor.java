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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nz.co.senanque.schemaparser.restrictions.Restriction;

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
    private List<Restriction> m_restrictions;
	private final boolean m_simpleType;
    
    public String toString() {
    	return m_name;
    }
    
    public FieldDescriptor(String clazz,String name, String type, boolean list, List<Restriction> restrictions, boolean simpleType)
    {
        m_clazz = clazz;
        m_name = name;
        m_type = type;
        m_simpleType = simpleType;
        m_list = list;
        m_restrictions = restrictions;
        for (Restriction r: restrictions) {
        	r.setFieldDescriptor(this);
        }
    }
    public FieldDescriptor(String clazz,String name, String type, boolean list) {
    	this(clazz,name,type,list,new ArrayList<Restriction>(),true);
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

	public void traverse(SchemaVisitor visitor) {
		visitor.beginField(this);
		visitor.endField(this);
	}

	public List<Restriction> getRestrictions() {
		return Collections.unmodifiableList(m_restrictions);
	}
	
	public String getSortKey() {
		return m_clazz+"."+m_name;
	}

	public boolean isSimpleType() {
		return m_simpleType;
	}
    
}

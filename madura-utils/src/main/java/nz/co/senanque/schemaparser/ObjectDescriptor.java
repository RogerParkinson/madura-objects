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

import java.util.HashMap;

/**
 * 
 * Object descriptor
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public class ObjectDescriptor extends HashMap<String, FieldDescriptor>
{
    private static final long serialVersionUID = 1L;
	private final String m_name;
    private String m_parent;
    public ObjectDescriptor(String name)
    {
        m_name = name;
    }
    public String getName()
    {
        return m_name;
    }
    public String getParent()
    {
        return m_parent;
    }
    public void setParent(String parent)
    {
        m_parent = parent;
    }
	public void traverse(SchemaVisitor visitor) {
		visitor.beginObject(this);
		for (FieldDescriptor fd: this.values()) {
			fd.traverse(visitor);
		}
		visitor.endObject(this);
	}
    
    
}

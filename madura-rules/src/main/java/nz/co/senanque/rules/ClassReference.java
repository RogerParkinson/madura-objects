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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * The class reference stores a map of field references.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.3 $
 */
public class ClassReference implements Serializable
{

	private static final long serialVersionUID = 1L;
	private final transient Map<String,FieldReference> m_fieldReferenceMap = new HashMap<String,FieldReference>();
    private final transient Class<?> m_class;
    private final transient List<ClassReference> m_children = new ArrayList<ClassReference>();
    
    public ClassReference(final Class<?> clazz)
    {
        m_class = clazz;
    }

    public String getClassName()
    {
        return m_class.getSimpleName();
    }

    public FieldReference getFieldReference(final String fieldName)
    {
        FieldReference fieldReference = m_fieldReferenceMap.get(fieldName);
        if (fieldReference == null)
        {
            fieldReference = new FieldReference(getClassName(), fieldName);
            m_fieldReferenceMap.put(fieldName,fieldReference);
        }
        return fieldReference;
    }

    public String getSuperClass()
    {
        return m_class.getSuperclass().getSimpleName();
    }

    public void figureChildren(Map<String, ClassReference> classReferenceMap)
    {
        for (ClassReference cr: classReferenceMap.values())
        {
            String superClass = cr.getSuperClass();
            if (superClass.equals(getClassName()))
            {
                m_children.add(cr);
            }
        }
    }

    public List<ClassReference> getChildren()
    {
        List<ClassReference> ret = new ArrayList<ClassReference>();
        ret.add(this);
        for (ClassReference cr: m_children)
        {
            ret.addAll(cr.getChildren());
        }
        return ret;
    }

    public Class<?> getClazz()
    {
        return m_class;
        
    }

}

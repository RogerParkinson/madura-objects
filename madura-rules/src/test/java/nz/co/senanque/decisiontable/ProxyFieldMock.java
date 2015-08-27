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
package nz.co.senanque.decisiontable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nz.co.senanque.validationengine.FieldMetadata;
import nz.co.senanque.validationengine.History;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.ProxyField;
import nz.co.senanque.validationengine.ProxyObject;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationUtils;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;
import nz.co.senanque.validationengine.metadata.PropertyMetadataImpl;

/**
 * 
 * Short description
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public class ProxyFieldMock implements ProxyField
{
    private final String m_fieldName;
    private final List<ChoiceBase> m_choiceList;
    private Object m_value;
    private Set<String> m_excludes = new HashSet<String>();
    
    public ProxyFieldMock(RuleSessionMock session, String fieldName, ValidationObject object)
    {
        m_fieldName = fieldName;
        m_choiceList = session.getChoices(fieldName);
        Method m = ValidationUtils.figureGetter(fieldName, object.getClass());
        try
        {
            m_value = m.invoke(object, new Object[]{});
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#getPath()
     */
    @Override
    public String getPath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#getPropertyMetadata()
     */
    @Override
    public PropertyMetadataImpl getPropertyMetadata()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#isReadOnly()
     */
    @Override
    public boolean isReadOnly()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#setReadOnly(boolean)
     */
    @Override
    public void setReadOnly(boolean b)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#setInActive(boolean)
     */
    public void setInActive(boolean b)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#isInActive()
     */
    public boolean isInActive()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#getChoiceList()
     */
    public List<ChoiceBase> getChoiceList()
    {
        List<ChoiceBase> ret = new ArrayList<ChoiceBase>();
        for (ChoiceBase choice: m_choiceList)
        {
            if (!m_excludes.contains(choice.getKey()))
            {
                ret.add(choice);
            }
        }
        return ret;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#setValue(java.lang.Object)
     */
    public void setValue(Object newValue)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#reset()
     */
    public void reset()
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#getValue()
     */
    public Object getValue()
    {
        return m_value;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#assign(java.lang.Object)
     */
    public void assign(Object a)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#isRequired()
     */
    public boolean isRequired()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#isDerived()
     */
    public boolean isDerived()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#setDerived(boolean)
     */
    public void setDerived(boolean derived)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#getFieldName()
     */
    public String getFieldName()
    {
        // TODO Auto-generated method stub
        return m_fieldName;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#updateValue()
     */
    public void updateValue()
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#fetchValue()
     */
    public Object fetchValue()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#useCurrentValue(boolean)
     */
    public void useCurrentValue(boolean useCurrentValue)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#setRequired(boolean)
     */
    public void setRequired(boolean b)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#isExcluded(java.lang.String)
     */
    public boolean isExcluded(String key)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#exclude(java.lang.String)
     */
    public void exclude(String key)
    {
        m_excludes.add(key);
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#getExcludes()
     */
    public Set<String> getExcludes()
    {
        return new HashSet<String>(m_excludes);
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#clearExclude(java.lang.String)
     */
    public void clearExclude(String key)
    {
        m_excludes.remove(key);
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#clearExcludes()
     */
    public void clearExcludes()
    {
        m_excludes.clear();
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#getProxyObject()
     */
    @Override
    public ProxyObject getProxyObject()
    {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public boolean isSecret() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setInitialValue(Object value) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object getInitialValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public FieldMetadata getFieldMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectMetadata getObjectMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isNotKnown() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isUnknown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Method getGetter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<History> getHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHistory(List<History> history) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean expire() {
		// TODO Auto-generated method stub
		return false;
	}

}

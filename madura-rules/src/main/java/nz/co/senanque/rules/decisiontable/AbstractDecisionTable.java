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
package nz.co.senanque.rules.decisiontable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import nz.co.senanque.rules.FieldReference;
import nz.co.senanque.rules.Rule;
import nz.co.senanque.rules.RuleContext;
import nz.co.senanque.rules.RuleSession;
import nz.co.senanque.validationengine.ProxyField;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationUtils;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * The generated decision table rules all extend this
 * Table cells may be null to represent any value. 
 * If a proxy value has not yet been assigned it is ignored.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.3 $
 */
public abstract class AbstractDecisionTable implements Rule, Serializable
{
    private static Logger logger = LoggerFactory.getLogger(AbstractDecisionTable.class);
    private transient String[] m_columnNames;
    private transient Row[] m_rows;
    private transient boolean[] m_autoAssign;
    
    protected ProxyField getProxyField(RuleSession session, ValidationObject object, String fieldName)
    {
        return session.getMetadata(object).getProxyField(fieldName);
    }

    public void evaluate(final RuleSession session, final ValidationObject object,
            final RuleContext ruleContext)
    {
        final int columnCount = m_columnNames.length;
        ProxyField[] proxyFields = new ProxyField[columnCount];
        Object[] values = new Object[columnCount];
        
        ProxyField lastProxyField = session.getLastProxyField();
        int columnLastEntered = -1;
        for (int i=0;i<columnCount;i++)
        {
            proxyFields[i] = getProxyField(session, object, m_columnNames[i]);
            if (proxyFields[i].equals(lastProxyField))
            {
                columnLastEntered = i;
            }
            Object fieldValue = proxyFields[i].getValue();
            values[i] = (fieldValue==null)?null:String.valueOf(fieldValue);
        }
        Set<String>[] valids = figureValids(columnCount,values);
        for (int columnIndex=0; columnIndex < columnCount; columnIndex++)
        {
            if (values[columnIndex]==null)
            {
                continue;
            }
            Object value = values[columnIndex];
            values[columnIndex] =  null;
            Set<String>[] valids1 = figureValids(columnCount,values);
            if (valids1[columnIndex] != null)
            {
	            try {
	                if (valids[columnIndex] == null)
	                {
	                	valids[columnIndex] = new HashSet<String>();
	                }
	        		valids[columnIndex].addAll(valids1[columnIndex]);
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
            }
            values[columnIndex] = value;
        }
        if (logger.isDebugEnabled())
        {
            debugDump("before", columnCount,proxyFields,values);
        }
        
        for (int columnIndex=0; columnIndex < columnCount; columnIndex++)
        {
            if (valids[columnIndex]==null)
            {
                continue;
            }
            Set<String> validsList = valids[columnIndex];
            for (ChoiceBase choiceBase: proxyFields[columnIndex].getChoiceList())
            {
                if (!validsList.contains(choiceBase.getKey().toString()))
                {
                    session.exclude(session.getRuleProxyField(proxyFields[columnIndex]), choiceBase.getKey().toString(), ruleContext);
                }
            }
            if (m_autoAssign[columnIndex])
            {
                session.autoAssign(proxyFields[columnIndex],ruleContext);
            }
        }
        if (logger.isDebugEnabled())
        {
            debugDump("after", columnCount,proxyFields,values);
        }
    }
    private void debugDump(String comment, int columnCount, ProxyField[] proxyFields, Object[] values)
    {
        logger.debug("-----------{}",comment);
        for (int columnIndex=0; columnIndex < columnCount; columnIndex++)
        {
            logger.debug("\tfield {} \tcurrent {} \tvalids {}",new Object[]{proxyFields[columnIndex].getFieldName(),values[columnIndex],proxyFields[columnIndex].getChoiceList()});
        }
        logger.debug("---------end {}",comment);

    }
    private Set<String>[] figureValids(int columnCount,Object[] values)
    {
        Set<String>[] valids = new Set[columnCount];
        for (Row row : m_rows)
        {
            boolean excludeRow = false;
            final Column[] columns = row.getColumns();
            for (int i = 0; i < columnCount; i++)
            {
                if (values[i] != null
                        && columns[i] != null
                        && (!ValidationUtils.equals(values[i], columns[i]
                                .getValue())))
                {
                    // We have a value on this property so we can use it to
                    // exclude.
                    excludeRow = true;
                    break;
                }
            }
            if (!excludeRow)
            {
                // this row has either nulls or valid values so record them.
                for (int i = 0; i < columnCount; i++)
                {
                    String value = columns[i].getValue();
                    if (value != null)
                    {
                        addValid(valids, i, value);
                    }
                }
            }
        }
        return valids;
    }
    public void addValid(Set<String>[] valids, int i, String value)
    {
        if (valids[i] == null)
        {
            valids[i] = new HashSet<String>();
        }
        Set<String> v = (Set<String>)valids[i];
        if (v.contains(value))
        {
            return;
        }
        v.add(value);
    }

    public FieldReference[] updaters()
    {
        return null;
    }

    protected String[] getColumnNames()
    {
        return m_columnNames.clone();
    }

    protected void setColumnNames(String[] columnNames)
    {
        m_columnNames = columnNames.clone();
    }
    protected void setRows(Row[] rows)
    {
        m_rows = rows.clone();        
    }

    public void setAutoAssign(boolean[] autoAssign)
    {
        m_autoAssign = autoAssign.clone();
    }
    public FieldReference[] outputs() {
    	return null;
    }
}

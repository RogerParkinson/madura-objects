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

import java.util.List;
import java.util.Map;

import nz.co.senanque.rules.FieldReference;
import nz.co.senanque.rules.RuleSession;
import nz.co.senanque.rules.decisiontable.AbstractDecisionTable;
import nz.co.senanque.rules.decisiontable.Column;
import nz.co.senanque.rules.decisiontable.Row;
import nz.co.senanque.rules.factories.DecisionTableFactory;
import nz.co.senanque.validationengine.ProxyField;
import nz.co.senanque.validationengine.ValidationObject;

import org.jdom.Element;

/**
 * 
 * The generated decision table rules all extend this
 * Table cells may be null to represent any value. 
 * If a proxy value has not yet been assigned it is ignored.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.3 $
 */
public class PrototypeDecisionTable extends AbstractDecisionTable
{
    private transient final String m_ruleName;
    private transient final String m_message;
    private transient final String m_typeName;
    private transient final FieldReference[] m_listeners;
    private transient Class<?> m_clazz;

    protected ProxyField getProxyField(RuleSession session, ValidationObject object, String fieldName)
    {
        ProxyField proxyField = new ProxyFieldMock((RuleSessionMock)session, fieldName, object);
        return proxyField;
    }
    public PrototypeDecisionTable(final Element tableElement, Map<String,DecisionTableFactory> factoryMap)
    {
        m_ruleName = tableElement.getAttributeValue("name");
        final String type = tableElement.getAttributeValue("scope");
        m_typeName = type;
        m_message = tableElement.getAttributeValue("message");
        final List<Element> columNameElements = (List<Element>)tableElement.getChild("ColumnNames").getChildren("ColumnName");
        String[] columnNames = new String[columNameElements.size()];
        m_listeners = new FieldReference[columNameElements.size()];
        boolean autoAssign[] = new boolean[columNameElements.size()];
        int index = 0;
        for (Element columnNameElement:columNameElements)
        {
            final String autoAssignValue = columnNameElement.getAttributeValue("autoAssign");
            if (autoAssignValue != null)
            {
                autoAssign[index] = new Boolean(autoAssignValue).booleanValue();
            }
            columnNames[index] = columnNameElement.getTextTrim();
            m_listeners[index] = new FieldReference(type,columnNames[index]);
            index++;
        }
        DecisionTableFactory dtf = factoryMap.get(m_ruleName);
        if (dtf == null)
        {
            final List<Element> rowElements = (List<Element>)tableElement.getChild("Rows").getChildren("Row");
            Row[] rows = new Row[rowElements.size()];
            int rowIindex = 0;
            for (Element rowElement:rowElements)
            {
                final List<Element> columnElements = (List<Element>)rowElement.getChildren("Column");
                Column[] columns = new Column[columnElements.size()];
                int columnIindex = 0;
                for (Element columnElement:columnElements)
                {
                    columns[columnIindex++] = new Column(columnElement.getTextTrim());
                }
                rows[rowIindex++] = new Row(columns);
            }
            setRows(rows);
        }
        else
        {
            setRows(dtf.getRows(m_ruleName));
        }
        setColumnNames(columnNames);
        setAutoAssign(autoAssign);
    }
    
    private Class<?> getClazz(String name)
    {
        return null;
    }

    public String getRuleName()
    {
        return m_ruleName;
    }

    public FieldReference[] listeners()
    {
        return m_listeners.clone();
    }
   public String getMessage(RuleSession session, ValidationObject object)
    {
        return m_message;
    }
   public String toString()
   {
       return getRuleName();
   }

   public String getClassName()
   {
//       return m_clazz.getSimpleName();
       return m_typeName;
   }
   public Class getScope()
   {
       return m_clazz;
   }
}

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
package nz.co.senanque.performance;

import java.util.ArrayList;
import java.util.List;

import nz.co.senanque.base.IndustryType;
import nz.co.senanque.rules.decisiontable.Column;
import nz.co.senanque.rules.decisiontable.Row;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

public class Factories implements MessageSourceAware, nz.co.senanque.rules.factories.DecisionTableFactory,nz.co.senanque.validationengine.choicelists.ChoiceListFactory
{

    private static final Logger log = LoggerFactory.getLogger(Factories.class);

    private List<Row> m_rows = new ArrayList<Row>();
    private List<ChoiceBase> m_choices = new ArrayList<ChoiceBase>();

	private MessageSource m_messageSource;
    public Row[] getRows(String ruleName)
    {
        if (m_rows.isEmpty())
        {
            generateRows();
        }
        return (Row[])m_rows.toArray(new Row[m_rows.size()]);
    }
    public List<ChoiceBase> getChoiceList(MessageSource messageSource)
    {
        if (m_choices.isEmpty())
        {
            generateRows();
        }
        return m_choices;
    }
    
    private void generateRows()
    {
        for (IndustryType industryType: IndustryType.values())
        {
//            Column column1 = new Column(industryType.value());
            Column column1 = new Column(industryType.name());
            for (int i=0;i<TablePerformanceTest.MAX_LOOP2;i++)
            {
                String r = RandomStringUtils.randomNumeric(5);
                Column column2 = new Column(r);
                Column[] columns = new Column[2];
                columns[0] = column1;
                columns[1] = column2;
                Row row = new Row(columns);
                m_rows.add(row);
                m_choices.add(new ChoiceBase(r,r,m_messageSource));
            }
        }
        log.info("total rows in decision table: {}",m_rows.size());
     }
	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
		
	}

}

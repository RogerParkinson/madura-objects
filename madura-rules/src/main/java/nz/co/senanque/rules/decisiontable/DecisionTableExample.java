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

import nz.co.senanque.rules.FieldReference;
import nz.co.senanque.rules.RuleSession;
import nz.co.senanque.validationengine.ValidationObject;

/**
 * 
 * This is no longer used
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
public class DecisionTableExample extends AbstractDecisionTable
{

	private static final long serialVersionUID = 1L;
	public DecisionTableExample()
    {
        setColumnNames(new String[]{"A","B"});
        setRows(new Row[]{
                new Row(new Column[]{new Column("First"),new Column("Second")}),
                new Row(new Column[]{new Column("First1"),new Column("Second")}),
                new Row(new Column[]{new Column("First2"),new Column("Second")})});
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.newrules.Rule#getRuleName()
     */
    public String getRuleName()
    {
        return "This is a sample decision table";
    }
    public FieldReference[] listeners()
    {
        return new FieldReference[]{new FieldReference("Customer","A"),new FieldReference("Customer","B")};
    }
    public String getMessage(RuleSession session, ValidationObject object)
    {
        return "";
    }
    public String getClassName()
    {
        return "Customer";
    }
    public Class<?> getScope()
    {
        // TODO Auto-generated method stub
        return null;
    }
}

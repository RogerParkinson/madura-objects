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

import java.util.Date;
import java.util.List;

import nz.co.senanque.validationengine.ListeningArray;
import nz.co.senanque.validationengine.ProxyField;



/**
 * 
 * Describes the operations available to the engine.
 * Different implementations of this can be injected as required.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.8 $
 */
public interface Operations
{
    public Boolean eq(Object value, Object value2);
    public Boolean gt(Object value, Object value2);
    public Boolean lt(final Object value, final Object value2);
    public Boolean ge(final Object value, final Object value2);
    public Boolean le(final Object value, final Object value2);
    public void checkTrue(final Boolean value) throws NotTrueException;
    public void checkFalse(final Boolean value) throws NotFalseException;
    public Number count(final ListeningArray<?> array);
    public Number sum(final List<ProxyField> list);
    public Boolean anyTrue(final List<ProxyField> list);
    public Number countTrue(final List<ProxyField> list);
    public Boolean allTrue(final List<ProxyField> list);
    public Boolean unique(final List<ProxyField> list);
    public Boolean match(final List<ProxyField> list, final List<ProxyField> list2);
    public Boolean logicalAnd(Boolean value, Boolean value2);
    public Boolean logicalNot(Boolean value);
    public Boolean logicalOr(Boolean value, Boolean value2);
    public Number div(Number value, Number value2);
    public Number mod(Number value, Number value2);
    public Number mul(Number value, Number value2);
    public Number pow(Number value, Number value2);
    public Number sub(Number value, Number value2);
    public String concat(String value, String value2);
    public Number add(Number value, Number value2);
    public Number yearsSince(Date value);
    public Number monthsSince(Date value);
    public Number daysSince(Date value);
    public Date toDate(String value);
    public String toString(Number value);
    public String toStringD(Date value);
    public Number toNumber(String value);
    public Long toLong(Number value);
    public Date addDays(Date date, Number days);
    public String format(Object value,String code);
}

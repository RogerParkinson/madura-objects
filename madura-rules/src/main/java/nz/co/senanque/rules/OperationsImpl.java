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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import nz.co.senanque.rules.annotations.InternalFunction;
import nz.co.senanque.validationengine.ListeningArray;
import nz.co.senanque.validationengine.ProxyField;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * All expression operations are described here.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.9 $
 */
public class OperationsImpl implements Operations, Serializable
{

	private static final long serialVersionUID = 1L;
	private transient final Date m_today;
	private transient final MessageSource m_messageSource;
	
	protected OperationsImpl(Date today, MessageSource messageSource) {
		m_today = today;
		m_messageSource = messageSource;
	}

    /* (non-Javadoc)
     * @see nz.co.senanque.newrules.Operations#eq(java.lang.Object, java.lang.Object)
     */
    @InternalFunction(operator="==", precedence=10)
    public Boolean eq(Object value, Object value2)
    {
        if (value == null && value2 == null)
        {
            return Boolean.TRUE;
        }
        if (value != null)
        {
            return new Boolean((value.equals(value2)));
        }
        return Boolean.FALSE;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.newrules.Operations#gt(java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @InternalFunction(operator=">", precedence=10)
    public Boolean gt(Object value, Object value2)
    {
        if (value2 != null && value2 instanceof Comparable)
        {
            final int ret = ((Comparable)value2).compareTo(value);
            return (ret > 0); 
        }
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.newrules.Operations#gt(java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @InternalFunction(operator="<", precedence=10)
    public Boolean lt(final Object value, final Object value2)
    {
        if (value != null && value instanceof Comparable)
        {
            final int ret = ((Comparable)value2).compareTo(value);
            return (ret < 0); 
        }
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.newrules.Operations#gt(java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @InternalFunction(operator=">=", precedence=10)
    public Boolean ge(final Object value, final Object value2)
    {
        if (value != null && value instanceof Comparable)
        {
            final int ret = ((Comparable)value2).compareTo(value);
            return (ret >= 0); 
        }
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.newrules.Operations#gt(java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @InternalFunction(operator="<=", precedence=10)
    public Boolean le(final Object value, final Object value2)
    {
        if (value != null && value instanceof Comparable)
        {
            final int ret = ((Comparable)value2).compareTo(value);
            return (ret <= 0); 
        }
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.newrules.Operations#checkTrue(java.lang.Object)
     */
    @InternalFunction()
    public void checkTrue(final Boolean value) throws NotTrueException
    {
        if (value != null && !value)
        {
            throw new NotTrueException();
        }
    }
    @InternalFunction()
    public void checkFalse(final Boolean value) throws NotFalseException
    {
        if (value != null && value)
        {
            throw new NotFalseException();
        }
    }
    @InternalFunction()
    public Number count(final ListeningArray<?> array)
    {
    	if (array == null)
    	{
    		return new Long(0L);
    	}
        return new Long(array.size());
    }

    @InternalFunction()
    public Number sum(final List<ProxyField> list)
    {
    	if (list == null)
    	{
    		return new Long(0L);
    	}
        double ret = 0.0;
        for (ProxyField proxyField: list)
        {
            ret += getNumericValue(proxyField.getValue());
        }
        return new Long((long)ret);
    }
    private double getNumericValue(Object o)
    {
        double ret = 0.0;
        if (o == null)
        {
            return ret;
        }
        if (o instanceof Integer)
        {
            ret = (Integer)o;
            return ret;
        }
        if (o instanceof Long)
        {
            ret = (Long)o;
            return ret;
        }
        if (o instanceof Float)
        {
            ret = (Float)o;
            return ret;
        }
        if (o instanceof Double)
        {
            ret = (Double)o;
            return ret;
        }
        return ret;
    }
    private boolean getBooleanValue(Object o)
    {
        boolean ret = false;
        if (o == null)
        {
            return ret;
        }
        if (o instanceof Boolean)
        {
            ret = ((Boolean)o).booleanValue();
            return ret;
        }
        return ret;
    }
    @InternalFunction()
    public Boolean anyTrue(final List<ProxyField> list)
    {
    	if (list == null)
    	{
    		return Boolean.FALSE;
    	}
        for (ProxyField proxyField: list)
        {
            if (getBooleanValue(proxyField.getValue()))
            {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
    @InternalFunction()
    public Number countTrue(final List<ProxyField> list)
    {
    	if (list == null)
    	{
    		return new Long(0L);
    	}
        long ret = 0;
        for (ProxyField proxyField: list)
        {
            if (getBooleanValue(proxyField.getValue()))
            {
                ret++;
            }
        }
        return ret;
    }
    @InternalFunction()
    public Boolean allTrue(final List<ProxyField> list)
    {
    	if (list == null)
    	{
    		return Boolean.FALSE;
    	}
        for (ProxyField proxyField: list)
        {
            if (!getBooleanValue(proxyField.getValue()))
            {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * Every item in the list must be unique
     * @param list
     * @return true if all unique
     */
    @InternalFunction()
    public Boolean unique(final List<ProxyField> list)
    {
    	if (list == null)
    	{
    		return Boolean.TRUE;
    	}
        int size = list.size();
        int index = 0;
        for (ProxyField proxyField: list)
        {
            for (int i=index+1;i<size;i++)
            {
                if (list.get(i).getValue().equals(proxyField.getValue()))
                {
                    return Boolean.FALSE;
                }
            }
        }
        return Boolean.TRUE;
    }

    /**
     * Everything in the first list must be in the second list too
     * But not necessarily the reverse.
     * @param list
     * @param list2
     * @return true if first list is in the second
     */
    @InternalFunction()
    public Boolean match(final List<ProxyField> list,final List<ProxyField> list2)
    {
    	if (list == null)
    	{
    		return Boolean.TRUE;
    	}
        for (ProxyField proxyField: list)
        {
            boolean found = false;
            for (ProxyField proxyField2: list2)
            {
                if (proxyField.getValue().equals(proxyField2.getValue()))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    @InternalFunction(operator="&&", precedence=21)
    public Boolean logicalAnd(Boolean value, Boolean value2)
    {
    	if (value == null)
    	{
    		return Boolean.FALSE;
    	}
        return value && value2;
    }

    @InternalFunction(operator="!", precedence=22)
    public Boolean logicalNot(Boolean value)
    {
    	if (value == null)
    	{
    		return Boolean.TRUE;
    	}
        return !value;
    }

    @InternalFunction(operator="||", precedence=5)
    public Boolean logicalOr(Boolean value, Boolean value2)
    {
    	if (value == null || value2 == null)
    	{
    		return Boolean.FALSE;
    	}
        return value || value2;
    }

    @InternalFunction(operator="/", precedence=21)
    public Number div(Number value, Number value2)
    {
    	Number ret = null;
    	if (value == null || value2 == null)
    	{
    		return new Long(0L);
    	}
    	if (value.doubleValue() == 0.0)
    	{
            String message = new MessageSourceAccessor(m_messageSource).getMessage("nz.co.senanque.rules.divide.by.zero");
            throw new InferenceException(message);
    	}
    	ret = value2.doubleValue()/value.doubleValue();
        return ret;
    }

    @InternalFunction(operator="%", precedence=21)
    public Number mod(Number value, Number value2)
    {
    	if (value == null || value2 == null)
    	{
    		return new Long(0L);
    	}
        return value2.doubleValue() % value.doubleValue();
    }

    @InternalFunction(operator="*", precedence=21)
    public Number mul(Number value, Number value2)
    {
        return value.doubleValue() * value2.doubleValue();
    }

    @InternalFunction(operator="^", precedence=22)
    public Number pow(Number value, Number value2)
    {
    	if (value == null || value2 == null)
    	{
    		return new Long(0L);
    	}
        return Math.pow(value2.doubleValue(), value.doubleValue());
    }

    @InternalFunction(operator="-", precedence=20)
    public Number sub(Number value, Number value2)
    {
    	if (value == null || value2 == null)
    	{
    		return new Long(0L);
    	}
        return value2.doubleValue() - value.doubleValue();
    }

    @InternalFunction(precedence=20,isHidden=true)
    public String concat(String value, String value2)
    {
        return value2+value;
    }

    @InternalFunction(operator="+", precedence=20)
    public Number add(Number value, Number value2)
    {
    	if (value == null || value2 == null)
    	{
    		return new Long(0L);
    	}
        return value2.doubleValue()+value.doubleValue();
    }

    @InternalFunction()
    public Number yearsSince(Date value)
    {
        if (value == null)
        {
            return 0.0;
        }
        Calendar today = getToday();
        Calendar c = getCalendar(value);
        int thisYear = today.get(Calendar.YEAR);
        int ret = thisYear - c.get(Calendar.YEAR);
        return new Double(ret);
    }

    @InternalFunction()
    public Number monthsSince(Date value)
    {
        if (value == null)
        {
            return 0.0;
        }
        Calendar today = getToday();
        Calendar c = getCalendar(value);
        int thisYear = today.get(Calendar.YEAR);
        int thisMonth = today.get(Calendar.MONTH);
        int ret = ((thisYear - c.get(Calendar.YEAR))*12)+(thisMonth - c.get(Calendar.MONTH));
        return new Double(ret);
    }
    
    @InternalFunction()
    public String format(Object value,String code)
    {
        if (code == null)
        {
            return "";
        }
        return new MessageSourceAccessor(m_messageSource).getMessage(code,new Object[]{value});
    }
    
    private Calendar getCalendar(Date date)
    {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }

    @InternalFunction()
    public Date addDays(Date date, Number days)
    {
        if (date == null || days == null)
        {
            return null;
        }
        Calendar calendar = getCalendar(date);
        calendar.add(Calendar.DAY_OF_YEAR, days.intValue());
        Date ret = calendar.getTime();
        return ret;
    }
    @InternalFunction()
    public Date subtractDays(Date date, Number days)
    {
        if (date == null || days == null)
        {
            return null;
        }
        Calendar calendar = getCalendar(date);
        calendar.add(Calendar.DAY_OF_YEAR, days.intValue()*-1);
        Date ret = calendar.getTime();
        return ret;
    }
    @InternalFunction()
    public Number daysSince(Date value)
    {
        if (value == null)
        {
            return 0.0;
        }
        Calendar today = getToday();
        Calendar c = getCalendar(value);
        int thisYear = today.get(Calendar.YEAR);
        int thisDayOfYear = today.get(Calendar.DAY_OF_YEAR);
        int ret = ((thisYear - c.get(Calendar.YEAR))*365)+(thisDayOfYear - c.get(Calendar.DAY_OF_YEAR));
        return new Double(ret);
    }
    private Calendar getToday()
    {
        Calendar today = Calendar.getInstance();
        if (m_today != null)
        {
            today.setTime(m_today);
        }
        return today;
    }
//    @InternalFunction()
//    public Double daysSince(java.util.Date value)
//    {
//        Calendar c = Calendar.getInstance();
//        c.setTime(value);
//        int thisYear = c.get(Calendar.YEAR);
//        int thisMonth = c.get(Calendar.DAY_OF_YEAR);
//        int ret = (thisYear - c.get(Calendar.YEAR))+(thisMonth - c.get(Calendar.DAY_OF_YEAR));
//        return new Double(ret);
//    }
    @InternalFunction()
    public Number toNumber(String value)
    {
        return Double.valueOf(value);
    }
    @InternalFunction()
    public Long toLong(Number value)
    {
    	if (value == null)
    	{
    		return new Long(0L);
    	}
        return value.longValue();
    }
    @InternalFunction()
    public Date toDate(String value)
    {
        try
        {
            Date ret = java.sql.Date.valueOf(value);
            return ret;
        }
        catch (IllegalArgumentException e)
        {
            String message = new MessageSourceAccessor(m_messageSource).getMessage("nz.co.senanque.rules.illegal.date.format", new Object[]{ value });
            throw new InferenceException(message);
        }
    }
    @InternalFunction()
    public String toString(Number value)
    {
        return String.valueOf(value);
    }
    @InternalFunction(isHidden=true)
    public String toStringD(Date value)
    {
        return String.valueOf(value);
    }

}

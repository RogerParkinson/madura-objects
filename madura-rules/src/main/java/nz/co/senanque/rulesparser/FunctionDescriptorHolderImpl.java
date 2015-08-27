/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package nz.co.senanque.rulesparser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Roger Parkinson
 *
 */
public class FunctionDescriptorHolderImpl implements FunctionDescriptorHolder {
	
	private final List<Class<?>> m_externalFunctionClasses;
    private Map<String,FunctionDescriptor> m_functionMap = new HashMap<String,FunctionDescriptor>();
    private Map<String,FunctionDescriptor> m_operatorMap = new HashMap<String,FunctionDescriptor>();

    public FunctionDescriptorHolderImpl(List<Class<?>> externalFunctionClasses) {
    	m_externalFunctionClasses = externalFunctionClasses;
    }
    /* (non-Javadoc)
	 * @see nz.co.senanque.rulesparser.FunctionDescriptorHolder#addFunction(nz.co.senanque.rulesparser.FunctionDescriptor)
	 */
    @Override
	public void addFunction(FunctionDescriptor functionDescriptor)
    {
        m_functionMap.put(functionDescriptor.getName(),functionDescriptor);
        if (functionDescriptor.getName().equals("logicalNot"))
        {
            m_functionMap.put("!",functionDescriptor);
        }
        
    }
    /* (non-Javadoc)
	 * @see nz.co.senanque.rulesparser.FunctionDescriptorHolder#addOperator(java.lang.String, nz.co.senanque.rulesparser.FunctionDescriptor)
	 */
    @Override
	public void addOperator(String operator, FunctionDescriptor fd)
    {
        m_operatorMap.put(operator, fd);
    }
    /* (non-Javadoc)
	 * @see nz.co.senanque.rulesparser.FunctionDescriptorHolder#getOperators()
	 */
    @Override
	public Set<Entry<String, FunctionDescriptor>> getOperators()
    {
        return m_operatorMap.entrySet();
    }
    /* (non-Javadoc)
	 * @see nz.co.senanque.rulesparser.FunctionDescriptorHolder#getFunctions()
	 */
    @Override
	public Set<Entry<String, FunctionDescriptor>> getFunctions()
    {
        return m_functionMap.entrySet();
    }
    public List<Class<?>> getExternalFunctionClasses()
    {
        return m_externalFunctionClasses;
    }

}

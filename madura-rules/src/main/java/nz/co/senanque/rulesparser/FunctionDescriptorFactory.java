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
package nz.co.senanque.rulesparser;
import java.lang.reflect.Method;
import java.util.List;

import nz.co.senanque.rules.OperationsImpl;
import nz.co.senanque.rules.RuleSession;
import nz.co.senanque.rules.annotations.Function;
import nz.co.senanque.rules.annotations.InternalFunction;

import org.jdom.Document;
import org.jdom.Element;

/**
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */

public final class FunctionDescriptorFactory
{
	public FunctionDescriptorFactory()
	{
	}
	
	public void parse(Document doc, FunctionDescriptorHolder rtp)
	{
	    for (Element e: (List<Element>)doc.getRootElement().getChildren())
	    {
	        FunctionDescriptor fd = new FunctionDescriptor(e);
	        rtp.addFunction(fd);
	        if (fd.getOperator() != null)
	        {
	            rtp.addOperator(fd.getOperator(),fd);
	        }
	    }
	}
    public void loadOperators(FunctionDescriptorHolder rtp)
    {
        loadOperators(OperationsImpl.class,rtp);
        loadOperators(RuleSession.class,rtp);
        for (Class<?> clazz: rtp.getExternalFunctionClasses())
        {
            loadExternalOperators(clazz,rtp);
        }
    }
    private void loadOperators(Class<?> clazz, FunctionDescriptorHolder rtp)
    {
        for (Method method: clazz.getMethods())
        {
            InternalFunction function = method.getAnnotation(InternalFunction.class);
            if (function != null)
            {
                FunctionDescriptor fd = new FunctionDescriptor(method,function);
                rtp.addFunction(fd);
                if (fd.getOperator() != null)
                {
                    rtp.addOperator(fd.getOperator(),fd);
                }
            }
        }
    }
    private void loadExternalOperators(Class<?> clazz, FunctionDescriptorHolder rtp)
    {
        for (Method method: clazz.getMethods())
        {
            Function function = method.getAnnotation(Function.class);
            if (function != null)
            {
                FunctionDescriptor fd = new FunctionDescriptor(method,function);
                rtp.addFunction(fd);
                if (fd.getOperator() != null)
                {
                    rtp.addOperator(fd.getOperator(),fd);
                }
            }
        }
    }
}

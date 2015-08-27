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

import java.util.List;

import nz.co.senanque.rules.annotations.InternalFunction;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.ProxyField;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationSession;

public interface RuleSession
{

    public abstract void bind(final ValidationObject validationObject,
            final ProxyField proxyField, final FieldReference fieldReference,
            final ValidationObject owner);

    public abstract RuleProxyField getRuleProxyField(final ProxyField proxyField);

    public abstract Operations getOperations();

    public abstract ObjectMetadata getMetadata(ValidationObject object);

    public abstract ValidationSession getSession();

    public abstract void setValue(final RuleProxyField proxyField,
            final Object value);

    /**
     * Method evaluateReadOnly.
     * Evaluate all the readonly elements in the document using the backchainer.
     */
//    public abstract void evaluateReadOnly();

    public abstract List<RuleProxyField> clearDerived();

    /**
     * Method setValues. 
     * This is how you tell the engine about a new value (or change of value)
     * The list is a list of ProposedValue classes. These are attempted and
     * any errors generated are rolled back so a failure on any of these restores
     * the status quo.
     * @param values
     * @throws InferenceException
     */
    public abstract void setValues(List<ProposedValue> values)
            throws InferenceException;

    public abstract Indenter getIndenter();

    public abstract void removeRuleProxyField(ProxyField proxyField);

    @InternalFunction(operator = "=", precedence = 1, isAssign = true)
    public abstract void assign(RuleContext ruleContext, Object value,
            RuleProxyField target);

    public abstract void assign(RuleContext ruleContext, Object value,
            List<ProxyField> list);

    public abstract void assign(RuleProxyField target, Object value,
            RuleContext ruleContext, boolean dummy);

    public abstract void addAssignedField(ProxyField proxyField);

    public abstract boolean bind(ValidationObject validationObject);

    @InternalFunction(precedence = 1, isAssign = true)
    public abstract void activate(RuleContext ruleContext, RuleProxyField ruleProxyField);

    public abstract void setActivate(RuleProxyField proxyField,
            RuleContext ruleContext);

    @InternalFunction(precedence = 1, isAssign = true)
    public abstract void readonly(RuleContext ruleContext, RuleProxyField ruleProxyField);

    public abstract void setReadOnly(RuleProxyField proxyField,
            RuleContext ruleContext);

    @InternalFunction(precedence = 1, isAssign = true)
    public abstract void require(RuleContext ruleContext, RuleProxyField ruleProxyField);

    @InternalFunction(precedence=1,isAssign=false)
    public abstract boolean isNotKnown(RuleProxyField ruleProxyField);

    public abstract void setRequired(RuleProxyField proxyField,
            RuleContext ruleContext);

    @InternalFunction(precedence = 1, isAssign = true)
    public abstract void exclude(RuleContext ruleContext, String key,
            RuleProxyField proxyField);

    public abstract void exclude(RuleProxyField proxyField, String key,
            RuleContext ruleContext);

    public abstract void autoAssign(ProxyField proxyField,
            RuleContext ruleContext);

    public abstract String getConstant(String key);

    public abstract String getMessage(String message, Object[] args);

    public abstract ProxyField getLastProxyField();

	public abstract String getStats(RuleSession ruleSession);
	
	public boolean isUnbinding(ValidationObject validationObject);

}
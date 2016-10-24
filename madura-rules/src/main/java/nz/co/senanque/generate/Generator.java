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
package nz.co.senanque.generate;

import java.io.File;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nz.co.senanque.resourceloader.MessageResource;
import nz.co.senanque.rules.ConstraintViolationException;
import nz.co.senanque.rules.FieldReference;
import nz.co.senanque.rules.NotTrueException;
import nz.co.senanque.rules.Operations;
import nz.co.senanque.rules.Rule;
import nz.co.senanque.rules.RuleContext;
import nz.co.senanque.rules.RuleProxyField;
import nz.co.senanque.rules.RuleSession;
import nz.co.senanque.rules.UnKnownFieldValueException;
import nz.co.senanque.rulesparser.AbstractRule;
import nz.co.senanque.rulesparser.Constant;
import nz.co.senanque.rulesparser.Expression;
import nz.co.senanque.rulesparser.ExpressionElement;
import nz.co.senanque.rulesparser.ExternalConstant;
import nz.co.senanque.rulesparser.Operator;
import nz.co.senanque.rulesparser.Parameter;
import nz.co.senanque.schemaparser.EnumeratedConstant;
import nz.co.senanque.schemaparser.FieldDescriptor;
import nz.co.senanque.validationengine.ListeningArray;
import nz.co.senanque.validationengine.ProxyField;
import nz.co.senanque.validationengine.ValidationObject;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JArray;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JCommentPart;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpressionImpl;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

/**
 * 
 * This class is responsible for generating the Java for a rule.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.9 $
 */
public class Generator
{
	public void generate(String packageName, File destDir, PrintStream status) throws Exception {
        JCodeModel cm = new JCodeModel();
        JPackage jpackage = cm._package(packageName);
        JDefinedClass maduraResourceComponent = jpackage._class("MaduraResourceComponent");
        JAnnotationUse annotationUse = maduraResourceComponent.annotate(org.springframework.stereotype.Component.class);
        annotationUse.param("value", maduraResourceComponent.fullName());
        annotationUse = maduraResourceComponent.annotate(MessageResource.class);
        annotationUse.param("value", "messages");
//        JFieldVar serialVersionUIDField = maduraResourceComponent.field(JMod.FINAL|JMod.PRIVATE|JMod.STATIC, long.class, "serialVersionUID");
//        serialVersionUIDField.init(JExpr.lit(1L));
        cm.build(destDir, status);
	}

    public void generate(String packageName, AbstractRule rule, String objectPackage, File destDir, PrintStream status) throws Exception
    {
        JCodeModel cm = new JCodeModel();
        JPackage jpackage = cm._package(packageName);
        final JClass ruleDefinedClass = cm.ref(Rule.class);
        final JClass serializableClass = cm.ref(Serializable.class);
        final JClass ruleSessionDefinedClass = cm.ref(RuleSession.class);
        final JClass validationObjectDefinedClass = cm.ref(ValidationObject.class);
        final JClass ruleContextDefinedClass = cm.ref(RuleContext.class);
        final JClass proxyFieldDefinedClass = cm.ref(RuleProxyField.class);
        final JClass notTrueExceptionClass = cm.ref(NotTrueException.class);
        final JClass unknownFieldValueExceptionClass = cm.ref(UnKnownFieldValueException.class);
        final JClass constraintViolationExceptionClass = cm.ref(ConstraintViolationException.class);
        final JClass operationsDefinedClass = cm.ref(Operations.class);
        final JClass listDefinedClass = cm.ref(List.class);
        final JType proxyFieldListDefinedClass = listDefinedClass.narrow(ProxyField.class);
        final JClass listeningArrayDefinedClass = cm.ref(ListeningArray.class);
        
        JDefinedClass ruleClass = jpackage._class(rule.getName());
        JCommentPart comment = new JCommentPart();
        comment.add(rule.getComment());
        ruleClass.javadoc().add(comment);
        ruleClass._implements(ruleDefinedClass);
        ruleClass._implements(serializableClass);
        JAnnotationUse annotationUse = ruleClass.annotate(org.springframework.stereotype.Component.class);
        annotationUse.param("value", ruleClass.fullName());
        JFieldVar serialVersionUIDField = ruleClass.field(JMod.FINAL|JMod.PRIVATE|JMod.STATIC, long.class, "serialVersionUID");
        serialVersionUIDField.init(JExpr.lit(1L));
        JMethod evaluateMethod = ruleClass.method(JMod.PUBLIC, cm.VOID, "evaluate");
        JVar sessionVar = evaluateMethod.param(JMod.FINAL, ruleSessionDefinedClass, "session");
        JVar objectVar = evaluateMethod.param(JMod.FINAL, validationObjectDefinedClass, "object");
        JVar ruleContextVar = evaluateMethod.param(JMod.FINAL, ruleContextDefinedClass, "ruleContext");
        JBlock jblock = evaluateMethod.body();

        JVar operationsVar = jblock.decl(JMod.FINAL,operationsDefinedClass, "operations", sessionVar.invoke("getOperations"));
        Map<String,JVar> proxyMap = new HashMap<String,JVar>();
        for (Parameter parameter : rule.getAllFieldsReferredTo())
        {
            String parameterValue = parameter.getValue();
            if (proxyMap.get(parameterValue) != null)
            {
                continue;
            }
            if (parameter.isList())
            {
                int i = parameterValue.indexOf('.');
                if (i > -1)
                {
                    String localRef = parameterValue.substring(0,i);
                    String targetRef = parameterValue.substring(i+1,parameterValue.length());
                    JVar proxyVar = jblock.decl(JMod.FINAL,proxyFieldListDefinedClass, "proxyField"+parameterValue.replace('.', '_'),sessionVar.invoke("getMetadata").arg(objectVar).invoke("getProxyFields").arg(JExpr.lit(localRef)).arg(JExpr.lit(targetRef)));
                    proxyMap.put(parameterValue, proxyVar);
                }
                else
                {
                    String localRef = parameterValue;
                    JVar proxyVar = jblock.decl(JMod.FINAL,proxyFieldDefinedClass, "proxyField"+parameterValue.replace('.', '_'), sessionVar.invoke("getRuleProxyField").arg(sessionVar.invoke("getMetadata").arg(objectVar).invoke("getProxyField").arg(JExpr.lit(localRef))));
                    proxyMap.put(parameterValue, proxyVar);
                }
            }
            else
            {
                JVar proxyVar = jblock.decl(JMod.FINAL,proxyFieldDefinedClass, "proxyField"+parameter.getValue().replace('.', '_'), sessionVar.invoke("getRuleProxyField").arg(sessionVar.invoke("getMetadata").arg(objectVar).invoke("getProxyField").arg(JExpr.lit(parameterValue))));
                proxyMap.put(parameterValue, proxyVar);
            }
        }
        List<Expression> conditions = rule.getConditionList();
        List<Expression> actions = rule.getActionList();
        JTryBlock tryBlockUnknown = jblock._try();
        JCatchBlock catchBlockUnknown = tryBlockUnknown._catch(unknownFieldValueExceptionClass);
        catchBlockUnknown.body()._return();
        JBlock jBlockUnknown = tryBlockUnknown.body();
        
        if (!conditions.isEmpty())
        {
            JTryBlock tryBlock = jBlockUnknown._try();
            for (Expression expression:conditions)
            {
                JInvocation condition = makeExpression(expression.getExpressionElements(),proxyMap, sessionVar, operationsVar,ruleContextVar,cm, listeningArrayDefinedClass);
                tryBlock.body().add(operationsVar.invoke((actions.isEmpty())?"checkTrue":"checkTrue").arg(condition));
            }
            if (actions.isEmpty())
            {
                JCatchBlock catchBlock = tryBlock._catch(notTrueExceptionClass);
                catchBlock.body()._throw(JExpr._new(constraintViolationExceptionClass).arg(JExpr.invoke("getMessage").arg(sessionVar).arg(objectVar)));
            }
            else
            {
                JCatchBlock catchBlock = tryBlock._catch(notTrueExceptionClass);            
                catchBlock.body()._return();
            }
        }
        if (!actions.isEmpty())
        {
            for (Expression expression:actions)
            {
                JInvocation action = makeExpression(expression.getExpressionElements(),proxyMap, sessionVar, operationsVar,ruleContextVar,cm, listeningArrayDefinedClass);
                jBlockUnknown.add(action);
            }
        }
        
        JMethod getRuleNameMethod = ruleClass.method(JMod.PUBLIC, cm.ref(String.class), "getRuleName");
        getRuleNameMethod.body()._return(JExpr.lit(packageName+"."+rule.getName()+":"+rule.getMessage()));
        
//        JMethod getMessageMethod = ruleClass.method(JMod.PUBLIC, cm.ref(String.class), "getMessage");
//        getMessageMethod.body()._return(JExpr.lit(packageName+"."+rule.getName()));
        
        JMethod getMessageMethod1 = ruleClass.method(JMod.PUBLIC, cm.ref(String.class), "getMessage");
        JVar sessionVar1 = getMessageMethod1.param(JMod.FINAL, ruleSessionDefinedClass, "session");
        JVar objectVar1 = getMessageMethod1.param(JMod.FINAL, validationObjectDefinedClass, "object");
        JArray jarray1 = JExpr.newArray(cm.ref(Object.class));
        for (Parameter parameter : rule.getMessageArguments())
        {
            JExpressionImpl expression =null;
            String parameterValue = parameter.getValue();
            int i = parameterValue.indexOf('.');
            if (i > -1)
            {
                String localRef = parameterValue.substring(0,i);
                String targetRef = parameterValue.substring(i+1,parameterValue.length());
                expression = sessionVar.invoke("getMetadata").arg(objectVar).invoke("getProxyFields").arg(JExpr.lit(localRef)).arg(JExpr.lit(targetRef));
            }
            else
            {
                Class<?> clazz = parameter.getTypeAsClass();
                JType jtype = cm._ref(clazz);
                JInvocation invocation = sessionVar1.invoke("getMetadata").arg(objectVar1).invoke("getProxyField").arg(JExpr.lit(parameter.getValue()));
                if (jtype == null)
                {
                    expression = invocation.invoke("getValue");
                }
                else
                {
                    expression = JExpr.cast(jtype, invocation.invoke("getValue"));
                }
            }
            jarray1.add(expression);
        }
        getMessageMethod1.body()._return(sessionVar1.invoke("getMessage").arg(JExpr.lit(packageName+"."+rule.getName())).arg(jarray1));
        
        JMethod toStringMethod = ruleClass.method(JMod.PUBLIC, cm.ref(String.class), "toString");
        toStringMethod.body()._return(JExpr.lit(packageName+"."+rule.getName()+":"+rule.getMessage()));
        
        JMethod getClassNameMethod = ruleClass.method(JMod.PUBLIC, cm.ref(String.class), "getClassName");
        getClassNameMethod.body()._return(JExpr.lit(/*objectPackage+"."+*/rule.getClassName()));
        
        JClass retClass = cm.ref(objectPackage+"."+rule.getClassName());
        JClass c = cm.ref(Class.class).narrow(retClass);
        
//        JMethod getScopeMethod = ruleClass.method(JMod.PUBLIC, cm.ref(Class.class), "getScope");
//        getScopeMethod.body()._return(cm.ref(objectPackage+"."+rule.getClassName()).dotclass() );
        
        JMethod getScopeMethod = ruleClass.method(JMod.PUBLIC, c, "getScope");
        getScopeMethod.body()._return(retClass.dotclass() );
        
        JMethod listenersMethod = ruleClass.method(JMod.PUBLIC, cm.ref(FieldReference.class).array(), "listeners");
        Set<FieldDescriptor> readFields = rule.getAllFieldsRead();
        if (readFields.size()>0)
        {
            JArray jarray = JExpr.newArray(cm.ref(FieldReference.class));
            for (FieldDescriptor fd : readFields)
            {
                jarray.add(JExpr._new(cm.ref(FieldReference.class)).arg(JExpr.lit(fd.getClazz())).arg(JExpr.lit(rule.getClassName())).arg(JExpr.lit(fd.getName())));
            }
            listenersMethod.body()._return(jarray);
        }
        else
        {
            listenersMethod.body()._return(JExpr._null());
        }

        JMethod outputsMethod = ruleClass.method(JMod.PUBLIC, cm.ref(FieldReference.class).array(), "outputs");
        Set<FieldDescriptor> outputFields = rule.getAllFieldsOutput();
        if (outputFields.size()>0)
        {
            JArray jarray = JExpr.newArray(cm.ref(FieldReference.class));
            for (FieldDescriptor fd : outputFields)
            {
                jarray.add(JExpr._new(cm.ref(FieldReference.class)).arg(JExpr.lit(fd.getClazz())).arg(JExpr.lit(rule.getClassName())).arg(JExpr.lit(fd.getName())));
            }
            outputsMethod.body()._return(jarray);
        }
        else
        {
        	outputsMethod.body()._return(JExpr._null());
        }

        cm.build(destDir, status);
        
    }
    private JInvocation makeExpression(List<ExpressionElement> elementList, Map<String,JVar> proxyMap,JVar sessionVar,JVar operationsVar,JVar ruleContextVar,JCodeModel cm, JType listeningArrayDefinedClass)
    {
        JInvocation expr=null;
        for (int i=0;i<elementList.size();i++)
        {
            ExpressionElement expressionElement = elementList.get(i);
            if (expressionElement.isUsed())
            {
                continue;
            }
            if (expressionElement instanceof Operator)
            {
                Operator operator = (Operator)expressionElement;
                if (operator.isAssign())
                {
                    Parameter p = (Parameter)elementList.get(0);
                    p.setAssigned(true);
                    expr = sessionVar.invoke(operator.getMethodName());
                    expr.arg(ruleContextVar);
                }
                else if (operator.getArgumentCount() == 1 && operator.getArguments()[0] == RuleProxyField.class)
                {
                    expr = sessionVar.invoke(operator.getMethodName());
                }
                else if (operator.isNative())
                {
                    expr = operationsVar.invoke(operator.getMethodName());
                }
                else
                {
                    expr = cm.ref(operator.getTargetClassName()).staticInvoke(operator.getMethodName());
                }
                int count = 0;
                for (int j=i-1;count<operator.getArgumentCount();j--)
                {
                    ExpressionElement expressionElement2=null;
                    try
                    {
                        expressionElement2 = elementList.get(j);
                    }
                    catch (ArrayIndexOutOfBoundsException e2)
                    {
                        throw new RuntimeException(e2);
                    }
                    if (expressionElement2.isUsed())
                    {
                        continue;
                    }
                    count++;
                    expressionElement2.setUsed(true);
                    if (expressionElement2 instanceof Constant)
                    {
                        Object value = ((Constant)expressionElement2).getConstantValue();
                        if (value instanceof Double)
                        {
                            expr.arg(JExpr.lit((Double)value));
                            continue;
                        }
                        if (value instanceof Float)
                        {
                            expr.arg(JExpr.lit((Float)value));
                            continue;
                        }
                        if (value instanceof Long)
                        {
                            expr.arg(JExpr.lit((Long)value));
                            continue;
                        }
                        if (value instanceof Boolean)
                        {
                            expr.arg(JExpr.lit((Boolean)value));
                            continue;
                        }
                        if (value instanceof Integer)
                        {
                            expr.arg(JExpr.lit((Integer)value));
                            continue;
                        }
                        if (value instanceof String)
                        {
                            expr.arg(JExpr.lit((String)value));
                            continue;
                        }
                        if (value instanceof EnumeratedConstant)
                        {
                            EnumeratedConstant enumeratedConstant = (EnumeratedConstant)value;
                            expr.arg(JExpr.direct(enumeratedConstant.toString()));
                            continue;
                        }
                    }
                    if (expressionElement2 instanceof ExternalConstant)
                    {
                        expr.arg(sessionVar.invoke("getConstant").arg(JExpr.lit(((ExternalConstant)expressionElement2).getValue())));
                        continue;
                    }
                    if (expressionElement2 instanceof Parameter)
                    {
                        Parameter p = (Parameter)expressionElement2;
                        JVar v = proxyMap.get(((Parameter)expressionElement2).getValue());
                        if (p.isAssign() || p.getValue().indexOf('.')>-1)
                        {
                            expr.arg(v);
                        }
                        else if (operator.getArguments()[count-1] == RuleProxyField.class)
                        {
                        	expr.arg(v);
                        }
                        else if (p.isList())
                        {
                            expr.arg(JExpr.cast(listeningArrayDefinedClass, v.invoke("getValue")));
                        }
                        else
                        {
                            Class<?> clazz = p.getTypeAsClass();
                            JType jtype = cm._ref(clazz);
//                            String t = p.getFieldDescriptor().getType();
//                            JType jtype = null;
//                            try
//                            {
//                                Class<?> clazz = Class.forName(t);
//                                jtype = cm._ref(clazz);
//                            }
//                            catch (ClassNotFoundException e)
//                            {
//                                try
//                                {
//                                    jtype = JType.parse(cm, t);
//                                }
//                                catch (IllegalArgumentException e1)
//                                {
//                                    //e1.printStackTrace();
//                                }
//                            }
                            if (jtype == null)
                            {
                                expr.arg(v.invoke("getValue"));
                            }
                            else
                            {
                                expr.arg(JExpr.cast(jtype, v.invoke("getValue")));
                            }
                        }
                        continue;
                    }
                    if (expressionElement2 instanceof Operator)
                    {
                        expr.arg(((Operator)expressionElement2).getExpr());
                        continue;
                    }
                }
                operator.setExpr(expr);
            }
        }
        return expr;
    }
//    private JExpression getCastedParameter(Parameter parameter)
//    {
//        JExpression ret = null;
//        JVar v = proxyMap.get(((Parameter)expressionElement2).getValue());
//        if (parameter.isAssign() || parameter.getValue().indexOf('.')>-1)
//        {
//            expr.arg(v);
//        }
//        else if (parameter.isList())
//        {
//            expr.arg(JExpr.cast(listeningArrayDefinedClass, v.invoke("getValue")));
//        }
//        else
//        {
//            Class<?> clazz = p.getTypeAsClass();
//            JType jtype = cm._ref(clazz);
//            if (jtype == null)
//            {
//                expr.arg(v.invoke("getValue"));
//            }
//            else
//            {
//                expr.arg(JExpr.cast(jtype, v.invoke("getValue")));
//            }
//        }
//        return ret;
//    }
}

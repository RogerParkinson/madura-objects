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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import nz.co.senanque.parser.Parser;
import nz.co.senanque.parser.ParserException;
import nz.co.senanque.parser.TextProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses the source rules, extends generic parser.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.11 $
 */

public final class ParsePackage extends Parser
{
    private static final Logger log = LoggerFactory.getLogger(ParsePackage.class);
	public static final int TYPE_RULE = 1;
	public static final int TYPE_CONSTRAINT = 2;
	public static final int TYPE_FORMULA = 3;
	
	private FunctionDescriptor m_not;
	
	/**
	 * Constructor for ParsePackage.
	 * @param textProvider
	 */
	public ParsePackage()
	{
	}
    public void parse(TextProvider textProvider)
    {
        RulesTextProvider rtp = (RulesTextProvider)textProvider;
        m_not = figureNotOperator(rtp);
    	while(true)
    	{
        	textProvider.commit();
    		clearLeadingSpaces(textProvider);
			if (endOfData(textProvider)) break;
            if (exact("import",textProvider,false))
            {
                processImport(rtp);
                continue;
            }
            rtp.getAccumulate();
            if (exact("formula:",textProvider,false))
            {
                AbstractRule rule = rtp.addRule(processFormula(rtp));
                rule.setComment(rtp.getAccumulate());
                continue;
            }
			if (exact("rule:",textProvider,false))
			{
			    AbstractRule rule = rtp.addRule(processRule(rtp));
                rule.setComment(rtp.getAccumulate());
				continue;
			}
			if (exact("constraint:",textProvider,false))
			{
			    AbstractRule rule = rtp.addRule(processConstraint(rtp));
                rule.setComment(rtp.getAccumulate());
				continue;
			}
			if (endOfData(textProvider)) break;
			throw new ParserException("unexpected token: ",textProvider);
    	}
    }
    private FunctionDescriptor figureNotOperator(RulesTextProvider rtp)
    {
        for (Map.Entry<String,FunctionDescriptor> operator: rtp.getOperators())
        {
            if ("!".equals(operator.getKey()))
            {
                return operator.getValue();
            }
        }
        throw new ParserException("did not find the '!' operator: ",rtp);
        
    }
	/**
	 * Method getRuleName. The rule name is normally enclosed in quotes
	 * but it might be omitted in which case we generate a unique one.
	 * @return String
	 * @throws ParserException
	 */
    private String getRuleName(RulesTextProvider textProvider)
    {
		if (!quotedString('"',textProvider))
		{
			return "R"+(textProvider.incrementRuleCount());
		}
    	return textProvider.getLastToken();
    }
    private void LoadCommonRuleData(AbstractRule rule, RulesTextProvider textProvider)
    {
        rule.setName(getRuleName(textProvider));
        if (!CVariable(textProvider))
        {
            throw new ParserException("Missing class name",textProvider);
        }
        textProvider.accumulate(" ");
        rule.setClassName(textProvider.getLastToken());
        textProvider.setCurrentScope(rule.getClassName());
        String message = "";
        if (quotedString('"',textProvider))
        {
            message = textProvider.getLastToken();
        }
        rule.setMessage(message);
        if (getBracketedToken('[', ']', textProvider))
        {
           String args = textProvider.getLastToken();
           StringTokenizer st = new StringTokenizer(args,"[,]");
           while (st.hasMoreTokens())
           {
               String operand = st.nextToken();
               if (textProvider.findOperandInScope(operand))
               {
                   try
                {
                    rule.addMessageArgument(operand, textProvider.getLastResolvedField());
                }
                catch (Exception e)
                {
                    throw new ParserException(e.getMessage()+" <"+operand+">",textProvider);
                }
               }
               else
               {
                   throw new ParserException("Could not locate <"+operand+"> in scope",textProvider);
               }
               
           }
        }
        log.debug(rule.getName());
        textProvider.accumulate("\n");
    }
    /**
     * Method processImport.
     * Parse an import (defines a function)
     * @return Rule
     * @throws ParserException
     */
    private FunctionDescriptor processImport(RulesTextProvider textProvider)
    {
    	log.debug("processImport");
        textProvider.getPosition();
        String fullFunctionName = "";
        String functionName = null;
        while (alpha(textProvider))
        {
            functionName =textProvider.getLastToken();
            fullFunctionName += functionName;
            if (exact(".",textProvider))
            {
                fullFunctionName += ".";
                continue;
            }
            exactOrError("(",textProvider);
        }
        FunctionDescriptor functionDescriptor = new FunctionDescriptor(functionName, fullFunctionName);
        
        List<Class<?>> args = new ArrayList<Class<?>>();
        while (true)
        {
            if (exact(")",textProvider))
            {
                break;
            }
            if (!alpha(textProvider))
            {
                throw new ParserException("Expected argument type ",textProvider);
            }
            String argumentType = textProvider.getLastToken();
            try
            {
                args.add(Class.forName(argumentType));
            }
            catch (ClassNotFoundException e)
            {
                throw new ParserException("Expected valid class but got "+argumentType+" ",textProvider);
            }
            if (!alpha(textProvider))
            {
                throw new ParserException("Expected argument name ",textProvider);
            }
            exact(",",textProvider);
        }
        functionDescriptor.setArgTypes(args);
        textProvider.addFunction(functionDescriptor);
        return functionDescriptor;
    }
    /**
     * Method processFormula.
     * Parse a formula which is just one action expression followed by
     * a semicolon.
     * @return Rule
     * @throws ParserException
     */
    private AbstractRule processFormula(RulesTextProvider textProvider)
    {
    	log.debug("processFormula");
        Formula rule = new Formula();
        int start = textProvider.getPos();
        LoadCommonRuleData(rule,textProvider);
        textProvider.addTOCElement(null, rule.getDescription(), start, textProvider.getPos(), TYPE_FORMULA);
        exactOrError("{",textProvider);
        Expression expression = processAction(textProvider);
        exactOrError("}",textProvider);
        rule.addAction(expression);

        return rule;
    }
	/**
	 * Method processRule. Parse the rule. Rules have an if/then structure
	 * with conditions and actions
	 * @return Rule
	 * @throws ParserException
	 */
    private AbstractRule processRule(RulesTextProvider textProvider)
    {
    	log.debug("processRule");
    	int start = textProvider.getPos();
        Rule rule = new Rule();
        LoadCommonRuleData(rule,textProvider);
        textProvider.addTOCElement(null, rule.getDescription(), start, textProvider.getPos(), TYPE_RULE);
        exactOrError("{",textProvider);
        
        if (!exact("if",textProvider))
            throw new ParserException("expected 'if' in a rule: ",textProvider);
        // Condition
        Expression condition = processCondition(textProvider);
        rule.addCondition(condition);
        exactOrError("{",textProvider);
        while (!exact("}",textProvider))
        {
            Expression action = processAction(textProvider);
            exact(";",textProvider);
            rule.addAction(action);
        }
        exact("}",textProvider);

        return rule;
    }
	/**
	 * Method processConstraint.
	 * A constraint is a list of conditions and an explanation.
	 * @return Rule
	 * @throws ParserException
	 */
    private AbstractRule processConstraint(RulesTextProvider textProvider)
    	throws ParserException
    {
    	log.debug("processConstraint");
        Constraint rule = new Constraint();
        int start = textProvider.getPos();
        LoadCommonRuleData(rule,textProvider);
        textProvider.addTOCElement(null, rule.getDescription(), start, textProvider.getPos(), TYPE_CONSTRAINT);
        exactOrError("{",textProvider);
        Expression condition = processAction(textProvider);
        if (condition.isAssign())
        {
            throw new ParserException("Found an assignment in a condition ",textProvider);
        }
        exactOrError("}",textProvider);
        rule.addCondition(condition);

        return rule;
    }
    private Expression processCondition(RulesTextProvider textProvider)
    	throws ParserException
    {
    	log.debug("processCondition");
        textProvider.accumulate("\n");
    	Expression expression = processExpression(textProvider);
        if (expression.isAssign())
        {
            throw new ParserException("Found an assignment in a condition ",textProvider);
        }
    	return expression;
    }
    private Expression processAction(RulesTextProvider textProvider)
    	throws ParserException
    {
    	log.debug("processAction");
        textProvider.accumulate("\n");
    	Expression expression = processExpression(textProvider);
    	exactOrError(";",textProvider);
    	return expression;
    }
    private Expression processExpression(RulesTextProvider textProvider)
    	throws ParserException
    {
    	log.debug("processExpression");
    	Expression expression = new Expression();
        boolean not = false;
        boolean negative = false;
    	while (!exact(";",textProvider) && !exact(",",textProvider))
    	{
    	    not = false;
            if (exact("!",textProvider))
            {
                not = true;
            }
			if (exact("(",textProvider))
			{
				Expression e = processExpression(textProvider);
				exactOrError(")",textProvider);
				expression.expression(e);
				if (processOperator(expression,textProvider,not))
					continue;
    			break;
			}
            negative = false;
            if (exact("-",textProvider))
            {
                negative = true;
            }
    		if (number(textProvider))
    		{
    			// constant numeric found
				String constant = ((negative)?"-":"")+textProvider.getLastToken();
				java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
				nf.setParseIntegerOnly(false);
				Number n;
                try
                {
                    n = nf.parse(constant);
                    if (n instanceof Long)
                    {
                        Long l = (Long)n;
                        if (constant.endsWith("F"))
                        {
                            n = new Float(l);
                        }
                        if (constant.endsWith("D"))
                        {
                            n = new Double(l);
                        }
                    }
                }
                catch (ParseException e)
                {
                    throw new ParserException(e.getMessage()+": ",textProvider);
                }
				expression.constant(n);
				if (processOperator(expression,textProvider,not))
					continue;
    			break;
    		}
			if (negative)
				throw new ParserException("Unexpected '-' ",textProvider);
            if (this.quotedString('"',textProvider))
            {
                // constant string found
                String constant = textProvider.getLastToken();
                expression.constant(constant);
                if (processOperator(expression,textProvider,not))
                    continue;
                break;
            }
            if (exact("true",textProvider))
            {
                // constant boolean found
                expression.constant(Boolean.TRUE);
                if (processOperator(expression,textProvider,not))
                    continue;
                break;
            }
            if (exact("false",textProvider))
            {
                // constant boolean found
                expression.constant(Boolean.FALSE);
                if (processOperator(expression,textProvider,not))
                    continue;
                break;
            }
            if (exact("$",textProvider))
            {
                if (this.getBracketedToken('{', '}', textProvider))
                {
                    // external constant string found
                    String constant = textProvider.getLastToken();
                    constant = constant.substring(1,constant.length()-1);
                    expression.externalConstant(constant);
                    if (processOperator(expression,textProvider,not))
                        continue;
                    break;
                }
            }
            if (xpath(textProvider))
            {
                String lastToken = textProvider.getLastToken();
                // might be a variable, might be a function, depends what comes next
                if (nexact("(",true,textProvider))
                {
                    // must be a function
                    FunctionDescriptor fd = textProvider.getFunction(lastToken);
                    if (fd == null)
                    {
                        throw new ParserException("Unknown function <"+lastToken+">",textProvider);
                    }
                    processFunction(expression,fd,textProvider);
                    if (processOperator(expression,textProvider,not))
                        continue;
                    break;
                }
                else
                {
                    // must be a variable
                    String operand = lastToken;
                    if (textProvider.findConstantInScope(operand))
                    {
                        expression.constant(textProvider.getLastConstant());
                    }
                    else if (textProvider.findOperandInScope(operand))
                    {
                        expression.operand(operand,textProvider.getLastResolvedField());
                    }
                    else
                    {
                        throw new ParserException("Could not locate <"+operand+"> in scope",textProvider);
                    }
                    if (processOperator(expression,textProvider,not))
                        continue;
                    break;
                }
            }
			throw new ParserException("Expected expression: ",textProvider);
    	}
    	expression.tidyExpression(textProvider);
    	expression.validateExpression(textProvider);
    	return expression;
    }
    
	/**
	 * This puts the function and its arguments into the expression. It figures out
	 * the number of args and their type and makes sure they are all there (and the right type)
	 * 
	 * @param expression
	 * @param od
	 * @throws ParserException
	 */
	private void processFunction(final Expression expression, final FunctionDescriptor od, RulesTextProvider textProvider) throws ParserException
	{
    	log.debug("processFunction");
		if (!exact("(",textProvider))
			throw new ParserException("Expected ')' after '"+od.getName()+"': ",textProvider);
		Class<?> args[] = od.getParameterTypes();
		for (int arg=1;arg <= args.length;arg++)
		{
			if (args[arg-1].equals(Expression.class))
			{
				Expression e = processExpression(textProvider);
				expression.expression(e);
			}
			if (args[arg-1].equals(List.class))
			{	
				if (!xpath(textProvider))
					throw new ParserException("expected list operand: ",textProvider);
				// operand string found
				String operand = textProvider.getLastToken();
				if (!textProvider.findOperandInScope(operand))
				{
					throw new ParserException("Could not locate "+operand+" in scope",textProvider);
				}
				expression.list(textProvider.getLastToken(),textProvider.getLastResolvedField());
			}
			else
			{
	             Expression e = processExpression(textProvider);
	             expression.expression(e);
			}
			if (arg < args.length)
			{
				// ensure arguments are separated by a comma
			    exactOrError(",",textProvider);
			}
		}
		exactOrError(")",textProvider);
		expression.function(od);
	}
	private boolean processOperator(Expression expression, RulesTextProvider textProvider, boolean not)
		throws ParserException
	{
    	log.debug("processOperator");
        if (not)
        {
            expression.operator(m_not);
        }
        for (Map.Entry<String,FunctionDescriptor> operator: textProvider.getOperators())
        {
            if (exact(operator.getKey(),textProvider))
            {
                expression.operator(operator.getValue());
                return true;
            }
        }
        return false;
	}
}

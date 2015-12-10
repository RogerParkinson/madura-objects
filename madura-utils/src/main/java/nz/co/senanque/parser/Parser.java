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
package nz.co.senanque.parser;

import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic Parser. You extend this to build a specific parser
 * and you use the core methods in here. It uses the TextProvider interface to fetch the 
 * input and this can be implemented in various ways.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
public class Parser
{
    private static final Logger log = LoggerFactory.getLogger(Parser.class);

    protected boolean m_debug = false;
    private Stack<TextProvider> m_textProviderStack = new Stack<TextProvider>();
	private int m_indent = 0;

    /**
     * Set the indent value.
     * This is used for formatting the diagnostics
     * @param i
     */
    public void indent(int i)
    {
    	m_indent += i;
    }

	public Parser()
	{
	}

	/**
	 * Report the debug information to the logger
	 */
	protected void debug(TextProvider textProvider)
	{
		if (!m_debug) return;
		Throwable t = new Throwable();
		StackTraceElement e[] = t.getStackTrace();
		StackTraceElement ste = e[1];
		String indent = "";
		for (int i=0;i<m_indent;i++)
			indent +="  ";
		log.debug("{}SUCCESS {}:{}",indent,ste.getMethodName(),textProvider.debug());
	}
	/**
	 * Report the debug information with a message to the logger
	 * @param message
	 */
	protected void debug(String message, TextProvider textProvider)
	{
		if (!m_debug) return;
		Throwable t = new Throwable();
		StackTraceElement e[] = t.getStackTrace();
		StackTraceElement ste = e[1];
		String indent = "";
		for (int i=0;i<m_indent;i++)
			indent +="  ";
		log.debug(indent+ste.getMethodName()+" \""+message+"\" : "+textProvider.debug());
	}
	/**
	 * Used when a syntac error is detected and you need to indicate where
	 * the problem is.
	 * @return text containing diagnotics
	 */
	public String getDiagnostics(TextProvider textProvider)
	{
		return textProvider.debug();
	}

    /**
     * Turns on the debug diagnostics to help you debug your parser
     * @param debug
     */
    public void setDebug(boolean debug)
    {
        this.m_debug = debug;
    }

    @SuppressWarnings("unused")
	private String getSource(TextProvider textProvider)
    {
        return textProvider.getSource();
    }

    /**
     * Get the next character from the text provider
     * @return one character
     * @param textProvider
     */
    protected char getNextChar(TextProvider textProvider)        
    {
		try
		{
			return textProvider.getNextChar();
		}
		catch (EndOfDataException e)
		{
			return 0;
		}
    }

    /**
     * Go back to the last mark()ed position
     * @param textProvider
     */
    protected void reset(TextProvider textProvider)
    {
        textProvider.reset();
    }

    /**
     * Mark the current position so that we can reset() back to it
     * @param textProvider
     */
    protected void mark(TextProvider textProvider)        
    {
        textProvider.mark();
    }
    protected void unmark(TextProvider textProvider)        
    {
        textProvider.unmark();
    }
    protected void remark(TextProvider textProvider)        
    {
        textProvider.unmark();
        textProvider.mark();
    }
    
   protected void commit(TextProvider textProvider) {
	   textProvider.commit();
   }

    /**
     * When a match is found this method returns what was matched
     * @return String containing last token
     */
    public String getLastToken(TextProvider textProvider)
    {
        return textProvider.getLastToken();
    }

    /**
     * Clears the previous token when you have finished with it.
     * Custom match methods should call this first
     * @param textProvider
     */
    public void clearLastToken(TextProvider textProvider)
    {
        textProvider.clearLastToken();
    }

    /**
     * Jump over leading spaces
     * Custom match methods normally call this before trying to match anything
     * @param textProvider
     */
    public void clearLeadingSpaces(TextProvider textProvider)
        
    {
        while (true)
        {
            mark(textProvider);
			char c;
			try
			{
				c = getNextChar(textProvider);
			}
			catch (ExceededBufferSizeException e)
			{
				return;
			}
			catch (ParserException e)
			{
				// ignore problems at this point
				return;
			}

            if (c == 0)
            {
            	if (m_textProviderStack.size() > 0)
            	{
					textProvider.close();            		
					textProvider = (TextProvider)m_textProviderStack.pop();
            	}
            	break;
            } 
            if (" \t\r\n".indexOf(c) == -1)
            {
                reset(textProvider);
                break;
            }
            unmark(textProvider);
        }
    }

    /**
     * Match against the passed string.
     * Case is ignored if the ignoreCase flag is set.
     * @param match
     * @param textProvider
     * @return true if matched
     */
    public boolean exactOrError(String match,TextProvider textProvider) 
    {
        if (!exact(match,textProvider,false))
        {
            throw new ParserException("Expected "+match,textProvider);
        }
        return true;
    }
    public boolean exact(String match,TextProvider textProvider) 
    {
        return exact(match,textProvider,false);
    }
    public boolean exact(String match,TextProvider textProvider, boolean ignoreCase)    
    {
        clearLastToken(textProvider);
        clearLeadingSpaces(textProvider);
        mark(textProvider);
        if (m_debug)
	        debug("testing "+ match,textProvider);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < match.length(); i++)
        {
            char c = getNextChar(textProvider);

            if (ignoreCase) c = Character.toLowerCase(c);

            if (c != match.charAt(i))
            {
                reset(textProvider);
                return false;
            }
            sb.append(c);
        }
    	unmark(textProvider);
        String s = sb.toString().trim();
        if (s.length() == 0) {
        	return false;
        }
        textProvider.setLastToken(s);
        
        debug(textProvider);
        return true;
    }

    /**
     * Match against the exact string
     * Ignore case if the ignoreCase flag is on
     * Don't move the pointer to the end of the matched string
     * Leave it at the start. You can then test for this same string again.
     * @param match
     * @param textProvider
     * @return true if matched
     */
    public boolean nexact(String match, boolean ignoreCase, TextProvider textProvider)        
    {
        clearLastToken(textProvider);
        clearLeadingSpaces(textProvider);
        mark(textProvider);
        if (m_debug)
	        debug("testing "+ match,textProvider);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < match.length(); i++)
        {
            char c = getNextChar(textProvider);

            if (ignoreCase) c = Character.toLowerCase(c);

            if (c != match.charAt(i))
            {
                reset(textProvider);
                return false;
            }
            sb.append(c);
        }
        String s = sb.toString().trim();
        if (s.length() == 0) {
        	unmark(textProvider);
        	return false;
        }
        textProvider.setLastToken(s);
        debug(textProvider);
        reset(textProvider);
        return true;
    }
    
    /**
     * Test to see if we are at the end of the data
     * @param textProvider
     * @return true if matches
     */
    public boolean endOfData(TextProvider textProvider)        
    {
    	clearLastToken(textProvider);
    	clearLeadingSpaces(textProvider);
    	mark(textProvider);
    	if (m_debug)
    		debug("testing",textProvider);
    	try
    	{
    		char c = getNextChar(textProvider);
    		if (c == 0) {
    			unmark(textProvider);
    			return true;
    		}
    	}
    	catch (Exception e)
    	{
			unmark(textProvider);
        	debug(textProvider);
    		return true;
    	}
    	reset(textProvider);
    	return false;
    }

    /**
     * See if there is a number (all digits)
     * @param textProvider
     * @return true if matched
     */
    public boolean number(TextProvider textProvider)
        
    {
        clearLastToken(textProvider);
        clearLeadingSpaces(textProvider);
        mark(textProvider);
        if (m_debug)
	        debug("testing",textProvider);
        StringBuilder sb = new StringBuilder();
        boolean hasDecimal = false;
        while (true)
        {
            char c = getNextChar(textProvider);
            if (c == 'L')
            {
                remark(textProvider);
                sb.append(c);
                break;
            }
            if (c == 'D')
            {
                remark(textProvider);
                sb.append(c);
                break;
            }
            if (c == 'F')
            {
                remark(textProvider);
                sb.append(c);
                break;
            }
            if (!Character.isDigit(c) && c != '.')
            {
                break;
            }
            if (c == '.')
            {
                if (hasDecimal)
                {
                    break;
                }
                else
                {
                    hasDecimal = true;
                }
            }
            remark(textProvider);
            sb.append(c);
        }
        reset(textProvider); // removes last char
        String s = sb.toString().trim();
        if (s.length() == 0) return false;
        textProvider.setLastToken(s);
        debug(textProvider);
        return true;
    }

    /**
     * See if there is a word (all letters)
     * @param textProvider
     * @return true if matched
     */
    public boolean alpha(TextProvider textProvider)
        
    {
        clearLastToken(textProvider);
        clearLeadingSpaces(textProvider);
        mark(textProvider);
        if (m_debug)
            debug("testing",textProvider);
        StringBuilder sb = new StringBuilder();
        while (true)
        {
            char c = getNextChar(textProvider);

            if (!Character.isLetter(c)) break;
            remark(textProvider);
            sb.append(c);
        }
        reset(textProvider); // removes last char
        String s = sb.toString().trim();
        if (s.length() == 0) return false;
        textProvider.setLastToken(s);
        debug(textProvider);
        return true;
    }

    /**
     * See if there is an alphanumeric
     * @param textProvider
     * @return true if matched
     */
    public boolean alphaNum(TextProvider textProvider)
        
    {
        clearLastToken(textProvider);
        clearLeadingSpaces(textProvider);
        mark(textProvider);
        if (m_debug)
			debug("testing",textProvider);
        StringBuilder sb = new StringBuilder();
        while (true)
        {
            char c = getNextChar(textProvider);

            if (!Character.isLetterOrDigit(c)) break;
            remark(textProvider);
            sb.append(c);
        }
        reset(textProvider); // removes last char
        if (sb.toString().length() == 0) return false;
        textProvider.setLastToken(sb.toString());
        debug(textProvider);
        return true;
    }

    /**
     * Look for a cobol style variable
     * This contains alphanums and dashes but must start with a letter
     * @param textProvider
     * @return true if matched
     */
    public boolean CobolVariable(TextProvider textProvider)
        
    {
        clearLastToken(textProvider);
        clearLeadingSpaces(textProvider);
        mark(textProvider);
        if (m_debug)
			debug("testing",textProvider);
        int count=0;
        StringBuilder sb = new StringBuilder();
        while (true)
        {
            char c = getNextChar(textProvider);
            if (!Character.isLetterOrDigit(c) && c != '-') break;
            if (count == 0 && !Character.isLetter(c)) break;
            remark(textProvider);
            sb.append(c);
            count++;
        }
        reset(textProvider); // removes last char
        String s = sb.toString().trim();
        if (s.length() == 0) return false;
        textProvider.setLastToken(s);
        debug(textProvider);
        return true;
    }

    /**
     * Look for a C style variable
     * alphanum and underbars are allowed but must start with a letter
     * @param textProvider
     * @return true if matched
     */
    public boolean CVariable(TextProvider textProvider)
        
    {
        clearLastToken(textProvider);
        clearLeadingSpaces(textProvider);
        mark(textProvider);
        if (m_debug)
			debug("testing",textProvider);
        int count=0;
        StringBuilder sb = new StringBuilder();
        while (true)
        {
            char c = getNextChar(textProvider);
            if (!Character.isLetterOrDigit(c) && c != '_') break;
            if (count == 0 && !Character.isLetter(c)) break;
            remark(textProvider);
            sb.append(c);
            count++;
        }
        reset(textProvider); // removes last char
        String s = sb.toString().trim();
        if (s.length() == 0) return false;
        textProvider.setLastToken(s);
        debug(textProvider);
        return true;
    }

    /**
     * Look for an XPath expression
     * alphanum and underbars and slashes are allowed but must start with a letter
     * @param textProvider
     * @return true if matched
     */
    public boolean xpath(TextProvider textProvider)
        
    {
        clearLastToken(textProvider);
        clearLeadingSpaces(textProvider);
        mark(textProvider);
        if (m_debug)
			debug("testing",textProvider);
        int count=0;
        StringBuilder sb = new StringBuilder();
        while (true)
        {
            char c = getNextChar(textProvider);
            if (!Character.isLetterOrDigit(c) && c != '_' && c !='/' && c !='.') break;
            if (count == 0 && !Character.isLetter(c)) break;
            remark(textProvider);
            sb.append(c);
            count++;
        }
        reset(textProvider); // removes last char
        String s = sb.toString().trim();
        if (s.length() == 0) return false;
        textProvider.setLastToken(s);
        return true;
    }

	/**
     * Match the next space bounded string
     * @param textProvider
     * @return true if matched
     */
    public boolean any(TextProvider textProvider)        
    {
        clearLastToken(textProvider);
        clearLeadingSpaces(textProvider);
        mark(textProvider);
        if (m_debug)
			debug("testing",textProvider);
        StringBuilder sb = new StringBuilder();
        while (true)
        {
            char c = getNextChar(textProvider);

            if (c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == 0)
            {
            	break;
            }
            sb.append(c);
        }
        unmark(textProvider);
        String s = sb.toString().trim();
        if (s.length() == 0) return false;
        textProvider.setLastToken(s);
        debug(textProvider);
        return true;
    }

    /**
     * get the next delimited bounded string
     * This searches up to but not including the delimit string
     * @param delimit
     * @param textProvider
     * @return always true
     */
    public boolean any(String delimit,TextProvider textProvider)
        
    {
        clearLastToken(textProvider);
        clearLeadingSpaces(textProvider);
        mark(textProvider);
        if (m_debug)
			debug("testing "+delimit,textProvider);
        StringBuilder sb = new StringBuilder();
        while (true)
        {
            char c = getNextChar(textProvider);

            if (delimit.indexOf(c) > -1) break;
            if (c == 0) break;
            remark(textProvider);
            if (c == '\t' || c == '\r' || c == '\n') 
            {
            	continue;
            }
            sb.append(c);
        }
        reset(textProvider);
        String s = sb.toString().trim();
        if (s.length() == 0) return false;
        textProvider.setLastToken(s);
        debug(textProvider);
        return true;
    }

    /**
     * Match a quoted string. The quotes have to be present but they
     * are removed from the result. Quotes inside the string can be
     * 'quoted' using the '\' character eg \" 
     * @param quote style
     * @param textProvider
     * @return true if matched
     */
    public boolean quotedString(char quote,TextProvider textProvider)        
    {
        clearLastToken(textProvider);
        clearLeadingSpaces(textProvider);
        mark(textProvider);
        if (m_debug)
			debug("testing "+quote,textProvider);

        if (getNextChar(textProvider) != quote)
        {
            reset(textProvider);
            return false;
        }
        char lastChar = 0;
        char c = 0;
        StringBuilder sb = new StringBuilder();
        while (true)
        {
            lastChar = c;
            c = getNextChar(textProvider);
			if (c == 0) {
		        unmark(textProvider);
				return false;
			}
            if (c == quote && lastChar != '\\') break;
            sb.append(c);
        }
        unmark(textProvider);
        String s = sb.toString().trim();
        if (s.length() == 0) return false;
        textProvider.setLastToken(s);
        debug(textProvider);
        return true;
    }

    /**
     * Match a bracketed token given the start bracket and the end bracket
     * The result includes the brackets.
     * @param start
     * @param end
     * @param textProvider
     * @return true if match
     */
    public boolean getBracketedToken(char start, char end, TextProvider textProvider)
    {
        clearLastToken(textProvider);
        clearLeadingSpaces(textProvider);
        mark(textProvider);
        if (m_debug)
			debug("testing " + start + " " + end,textProvider);
        StringBuilder sb = new StringBuilder();
        char c = getNextChar(textProvider);

        if (c != start)
        {
            reset(textProvider);
            return false;
        }
        int brackets = 0;

        while (true)
        {
            if (c == start) brackets++;
            if (c == end) brackets--;
            sb.append(c);
            if (brackets < 1) break;
            c = getNextChar(textProvider);
            if (c == 0)
            {
            	reset(textProvider);
				return false;
            }
        }
        unmark(textProvider);
        String s = sb.toString().trim();
        if (s.length() == 0) return false;
        textProvider.setLastToken(s);
        debug(textProvider);
        return true;
    }

}

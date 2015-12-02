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

abstract public class AbstractTextProvider implements TextProvider
{
    private String m_lastToken = "";
    private final ParserSource m_parserSource;
    
    public AbstractTextProvider(ParserSource parserSource) {
    	m_parserSource = parserSource;
    }

    public String getSource()
    {
        return m_parserSource.getSource();
    }

    public char getNextChar() throws ParserException
    {
        return m_parserSource.getNextChar();
    }

    public void close()
    {
    }
    public String debug()
    {
        return m_parserSource.debug();
    }
    public void mark()
    {
        m_parserSource.mark();
    }
    public void unmark()
    {
        m_parserSource.unmark();
    }
    public void commit()
    {
        m_parserSource.commit();
    }
    public void reset()
    {
        m_parserSource.reset();
    }
    public void setLastToken(String lastToken)
    {
        m_lastToken = lastToken;
    }
    public void clearLastToken()
    {
        m_lastToken = "";
    }

    public String getLastToken()
    {
        return m_lastToken;
    }
    public String getPosition()
    {
    	return m_parserSource.getPosition();
    }
    public String getLastLine() {
    	return m_parserSource.getLastLine();
    }

    public int getLineCount() {
    	return m_parserSource.getLineCount();
    }
    
    public int getPos() {
    	return m_parserSource.getPos();
    }
}

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

public class ParserException extends RuntimeException
{
	private int m_lineCount = -1;
    public ParserException(String message)
    {
        super(message);
    }
    public ParserException(String message, TextProvider txt)
    {
    	super(message+" "+txt.getPosition()+":"+txt.getLastLine());
    	m_lineCount = txt.getLineCount();
    }
	public int getLineCount() {
		return m_lineCount;
	}
    
}

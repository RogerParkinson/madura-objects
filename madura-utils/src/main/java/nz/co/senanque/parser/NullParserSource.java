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
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A dummy parser source that does not actually read a source.
 * It is used to get the parser to perform some side effect tasks that are not actually parsing.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.3 $
 */
public class NullParserSource implements ParserSource
{
    @SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(NullParserSource.class);

    private final String m_source;
	private int m_lineCount = 0;
    
    public NullParserSource(String source, int bufferSize) {
    	m_source = source;
    }
	public String debug()
	{
		StringBuilder ret = new StringBuilder();
        ret.append(" near line: ");
        ret.append(m_lineCount+1);
        ret.append(" ");
        try {
			ret.append(getLastLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret.toString();
	}

	public void reset() {
		throw new NotImplementedException();
	}

	public void unmark() {
		throw new NotImplementedException();
	}

	public void commit() {
		throw new NotImplementedException();
	}

	public void mark() {
		throw new NotImplementedException();
	}

	public String getPosition() {
		return m_source+" at line "+(m_lineCount+1);
	}

	public void close() {
	}

	public String getSource() {
		return m_source;
	}
	
	public String getLastLine() {
		return "";
	}

	public char getNextChar() throws ParserException {
		throw new EndOfDataException("");
    }
	@Override
	public int getLineCount() {
		return m_lineCount;
	}
	public int getPos() {
		return 0;
	}

}

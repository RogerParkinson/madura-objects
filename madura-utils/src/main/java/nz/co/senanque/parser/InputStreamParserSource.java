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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * Provides parseable data from an inputstream. It uses two buffers to avoid overwriting one buffer.
 * The key point about this is it allows multiple mark points and resets are stored in a stack.
 * The getNextChar() method strips comments and if you have a different comment format you should
 * extend this class and override that method.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.3 $
 */
public class InputStreamParserSource implements ParserSource
{
    @SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(InputStreamParserSource.class);

	private class Mark {
		final int m_position;
		final int m_lineStart;
		final int m_lineCount;
		final int m_bufferId;

		public int getBufferId() {
			return m_bufferId;
		}

		public int getPosition() {
			return m_position;
		}

		public int getLineStart() {
			return m_lineStart;
		}

		public int getLineCount() {
			return m_lineCount;
		}

		public Mark(int position, int lineStart, int lineCount, int bufferId) {
			m_position = position;
			m_lineStart = lineStart;
			m_lineCount = lineCount;
			m_bufferId = bufferId;
		}
	}
	private final InputStreamReader m_inputStream;
    private final String m_source;
    private int m_position=0;
    private int m_pos=0;
	private int m_lineCount = 0;
	private int m_lineStart = 0;
	private int m_bufferId = 0;
	private char[][] m_internalBuffers;
	private int[] m_internalBufferBytes = new int[2];
	private Stack<Mark> marks = new Stack<Mark>();
	private boolean m_suppressNextRead[] = new boolean[2];
    
    public InputStreamParserSource(InputStream inputStream, String source, int bufferSize) {
    	m_inputStream = new InputStreamReader(inputStream);
    	m_source = source;
    	m_internalBuffers = new char[2][bufferSize];
    	loadBuffer();
    }
    public InputStreamParserSource(InputStream inputStream, String source) {
    	this(inputStream, source, 3000);
    }
    public InputStreamParserSource(Resource resource) throws IOException {
    	this(resource.getInputStream(), resource.getURI().toString(), 3000);
    }

    public InputStreamParserSource(Resource resource, int bufferSize) throws IOException {
    	this(resource.getInputStream(), resource.getURI().toString(), bufferSize);
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
		Mark mark = marks.pop();
		m_position = mark.getPosition();
		m_lineStart = mark.getLineStart();
		m_lineCount = mark.getLineCount();
		m_bufferId = mark.getBufferId();
	}

	public void unmark() {
		marks.pop();
	}

	public void commit() {
		marks.clear();
		m_suppressNextRead[0] = false;
		m_suppressNextRead[1] = false;
	}

	public void mark() {
		marks.push(new Mark(m_position, m_lineStart, m_lineCount, m_bufferId));
	}

	public String getPosition() {
		return " at line "+(m_lineCount+1)+" "+m_source;
	}

	public int getPos() {
		return m_pos+m_position;
	}

	public void close() {
		try {
			m_inputStream.close();
		} catch (IOException e) {
			throw new ParserException(e.getMessage());
		}
	}

	public String getSource() {
		return m_source;
	}
	
	private void loadBuffer() {
		int oldBufferId = m_bufferId;
		m_bufferId = (m_bufferId==0)?1:0;
		m_pos += m_position;
		m_position = 0;
		if (m_suppressNextRead[m_bufferId]) {
			if (m_suppressNextRead[oldBufferId]) {
				throw new ExceededBufferSizeException("Exceeded buffer size, use a larger buffer. Current is "+m_internalBuffers[m_bufferId].length);
			}
		} else {
//			log.debug("Loading new buffer");
			m_suppressNextRead[m_bufferId] = true;
			try {
				m_internalBufferBytes[m_bufferId]  = m_inputStream.read(m_internalBuffers[m_bufferId]);
			} catch (IOException e) {
				throw new ParserException(e.getMessage());
			}
			if (m_internalBufferBytes[m_bufferId] == -1) {
				throw new EndOfDataException("");
			}
		}
	}

	private char getNextCharInternal(boolean adjustLineCount) throws ParserException {
//		log.debug("current position {} current buffer {} length {}",m_position,m_bufferId, m_internalBufferBytes[m_bufferId]);
		if (m_internalBufferBytes[m_bufferId] == -1) {
			return 0;
		}
		if (m_position >= m_internalBufferBytes[m_bufferId]) {
			loadBuffer();
		}
		char c = m_internalBuffers[m_bufferId][m_position++];

		if (adjustLineCount && (c == '\n')) {
			m_lineCount++;
			m_lineStart = m_position;
		}
		return c;
	}

	public String getLastLine() {
		StringBuilder sb = new StringBuilder();
		if (m_position < m_lineStart) {
			int altBuffer = (m_bufferId==0)?1:0;
			sb.append(new String(m_internalBuffers[altBuffer],m_lineStart,m_internalBuffers[altBuffer].length-m_lineStart).trim());
			sb.append(new String(m_internalBuffers[m_bufferId],0,m_position).trim());
		} else {
			sb.append(new String(m_internalBuffers[m_bufferId], m_lineStart, m_position-m_lineStart).trim());
		}
		String t = sb.toString();
		if (t.isEmpty()) {
			return "";
		}
		else {
			return "\n"+t;
		}
	}

	public char getNextChar() throws ParserException {
		char c = getNextCharInternal(true);
		if (c == '/')
		{
			mark();
			c = getNextCharInternal(false);
			if (c == '/')
			{
				while (c != '\n') {
					c = getNextCharInternal(true);
				}
			} 
			else if (c == '*')
			{
//				log.debug("found a '/*'");
				StringBuilder sb = new StringBuilder();
				while (true)
				{
					c = getNextCharInternal(true);
					sb.append(c);
//					log.debug("found a {} {}",c, (int)c);
					if (c == '*')
					{
						c = getNextCharInternal(true);
						if (c == '/')
						{
							c = getNextCharInternal(true);
							break;
						}
					}
				}
			}
			else
			{
				reset();
				c = '/';
			}
		}
        return c;
    }
	@Override
	public int getLineCount() {
		return m_lineCount;
	}

}

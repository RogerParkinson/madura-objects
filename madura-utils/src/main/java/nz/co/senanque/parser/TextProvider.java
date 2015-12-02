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
public interface TextProvider
{

    abstract char getNextChar()
        throws ParserException;

    abstract void reset()
        throws ParserException;

    abstract void mark();
    abstract String getSource();
    abstract String debug();
    abstract String getPosition();
	abstract void close();
    abstract void clearLastToken();
    abstract String getLastToken();
    abstract void setLastToken(String trim);
    abstract public String getLastLine();
	abstract void unmark();
	abstract void commit();
	abstract int getLineCount();
	abstract int getPos();
}


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

/**
 * 
 * Support for indentation when logging
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.1 $
 */
public class Indenter
{
    private transient int m_indent = 0;
    private static final String s_spaces = "                                                                    ";
    protected void increment()
    {
        m_indent++;
    }
    protected void decrement()
    {
        m_indent--;
    }
    public String toString()
    {
        return s_spaces.substring(0,(m_indent>=0)?m_indent:0);
    }
    protected void clear()
    {
        m_indent = 0;
    }

}

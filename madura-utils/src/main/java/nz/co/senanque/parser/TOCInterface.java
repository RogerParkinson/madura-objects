/*******************************************************************************
 * Copyright (c)8/05/2014 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

/**
 * Table Of Contents Interface.
 * Not all parsers have to support this but those that do use it to note where TOC items are in the
 * input file.
 * 
 * @author Roger Parkinson
 *
 */
public interface TOCInterface {
	public Object addTOCElement(Object parent, String name, long start,
			long end, int type);

	public void addErrorElement(String name, int line);

}

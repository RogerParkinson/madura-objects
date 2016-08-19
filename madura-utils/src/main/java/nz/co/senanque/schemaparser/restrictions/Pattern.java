/*******************************************************************************
 * Copyright (c)2016 Prometheus Consulting
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
package nz.co.senanque.schemaparser.restrictions;

import java.util.ArrayList;
import com.mifmif.common.regex.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.mifmif.common.regex.Generex;

/**
 * @author Roger Parkinson
 *
 */
public class Pattern extends Restriction {

	String value;
	
	List<CandidateValue> candidateValues = new ArrayList<>();
	public List<CandidateValue> getCandidateValues() {
		if (candidateValues.isEmpty()) {
			Generex generex = new Generex(value);
	        Iterator iterator = generex.iterator();
	        int i=0;
	        while (iterator.hasNext() && (i++ < 10)) {
	            candidateValues.add(new CandidateValue(iterator.next(),true,this));
	        }
		}
		return candidateValues;
	}
	public Pattern(Element restriction) {
		value = restriction.getAttributeValue("value");
	}
	public Pattern(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
}

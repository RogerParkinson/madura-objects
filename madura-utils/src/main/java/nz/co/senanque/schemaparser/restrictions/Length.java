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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

/**
 * @author Roger Parkinson
 *
 */
public class Length implements Restriction {

	int length;

	List<CandidateValue> candidateValues = new ArrayList<>();
	public Length(Element restriction) {
		length = Integer.parseInt(restriction.getAttributeValue("value"));
	}
	public List<CandidateValue> getCandidateValues() {
		if (candidateValues.isEmpty()) {
			candidateValues.add(new CandidateValue(StringUtils.rightPad("", length-1, '.'),false));
			candidateValues.add(new CandidateValue(StringUtils.rightPad("", length+1, '.'),false));
			candidateValues.add(new CandidateValue(StringUtils.rightPad("", length, '.'),true));
		}
		return candidateValues;
	}
}

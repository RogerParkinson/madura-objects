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
package nz.co.senanque.locking.sql;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Roger Parkinson
 *
 */
public class LockedObject implements Comparable<LockedObject> {
	
	private final String m_objectName;
	private final String m_ownerName;
	private final Timestamp m_timestamp;
	private final String m_comment;
	
	public String getObjectName() {
		return m_objectName;
	}

	public String getOwnerName() {
		return m_ownerName;
	}

	public Timestamp getTimestamp() {
		return m_timestamp;
	}

	protected LockedObject(String objectName, String ownerName, String comment)
	{
		m_objectName = objectName;
		m_ownerName = ownerName;
		m_timestamp = new Timestamp(new Date().getTime());
		m_comment = comment;
	}
	
	public String toString()
	{
		return m_objectName + " " + m_ownerName + " "+m_comment + " " + m_timestamp;
	}

	public int compareTo(LockedObject o) {
		return this.m_objectName.compareTo(o.getObjectName());
	}


}

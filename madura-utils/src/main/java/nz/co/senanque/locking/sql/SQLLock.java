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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import nz.co.senanque.locking.LockFactory;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Oracle Lock: this can be used across multiple JVMs
 * Incurs some database overhead.
 * It clears all the locks for this JVM when it restarts.
 * 
 * @author Roger Parkinson
 *
 */
public class SQLLock implements Lock {

	static Logger logger = LoggerFactory.getLogger(SQLLock.class);
	

	private final String m_lockName;
	public String getLockName() {
		return m_lockName;
	}

	public String getComment() {
		return m_comment;
	}
	private final String m_comment;
	@SuppressWarnings("unused")
	private final LockFactory.LockType m_type;
	private final int m_sleepTime;
	private final SQLLockFactory m_factory;


	private int m_lockedObjectId;

	public SQLLock(String lockName, LockFactory.LockType type, String comment, int sleepTime,SQLLockFactory factory) {
		m_lockName = lockName;
		m_comment = comment;
		m_type = type;
		m_sleepTime = sleepTime;
		m_factory = factory;
	}

	public void lock() {
		int i = 0;
		while (!tryLock())
		{
			try {
				Thread.sleep(m_sleepTime);
			} catch (InterruptedException e) {
			}
			if (m_factory.getMaxRetries() > -1 && m_factory.getMaxRetries()<i)
			{
				throw new SQLLockException("failed to lock "+this.toString());
			}
			i++;
		}
	}

	public void lockInterruptibly() throws InterruptedException {
		int i = 0;
		while (!tryLock())
		{
			try {
				Thread.sleep(m_sleepTime);
			} catch (InterruptedException e) {
				throw e;
			}
			if (m_factory.getMaxRetries() > -1 && m_factory.getMaxRetries()<i)
			{
				throw new SQLLockException("failed to lock "+this.toString());
			}
			i++;
		}
	}

	public Condition newCondition() {
		throw new NotImplementedException();
	}

	public boolean tryLock() {
		
		return m_factory.lock(this);
	}

	public boolean tryLock(long arg0, TimeUnit arg1)
			throws InterruptedException {
		if (!tryLock())
		{
			arg1.sleep(arg0);
			return tryLock();
		}
		return true;
	}

	public void unlock() {
		m_factory.unlock(this);
	}
	
	public String toString()
	{
		return m_lockName+" "+m_comment ;
	}

	protected void setLockedObjectId(int lockedObjectId) {
		m_lockedObjectId = lockedObjectId;
		
	}

	protected int getLockedObjectId() {
		return m_lockedObjectId;
	}

}

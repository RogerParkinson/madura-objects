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
package nz.co.senanque.locking.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import nz.co.senanque.locking.LockFactory;
import nz.co.senanque.locking.LockWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;


/**
 * @author Roger Parkinson
 *
 */
@ManagedResource(objectName = "nz.co.senanque.locking:name=SimpleLockFactory")
public class SimpleLockFactory implements LockFactory {
	
	static Logger logger = LoggerFactory.getLogger(SimpleLockFactory.class);

	
	private static List<LockedObject> m_locks = new ArrayList<LockedObject>();
    ThreadLocal<List<LockedObject>> m_currentLocks = new ThreadLocal<List<LockedObject>>();

    private int m_sleepTime = 1000;
    private String m_prefix;


	private int m_maxRetries=-1;

	@ManagedAttribute
	public int getSleepTime() {
		return m_sleepTime;
	}

	@ManagedAttribute
	public void setSleepTime(int sleepTime) {
		m_sleepTime = sleepTime;
	}
	
	@ManagedAttribute(description="The current locks")
	public String getAllLocksJMX() {
		return toString();
	}
	
	private String getThreadid()
	{
		return Thread.currentThread().getName()+"-"+Thread.currentThread().getId();
	}

	/* (non-Javadoc)
	 * @see com.aldous.locking.LockFactory#getLock(java.lang.String, com.aldous.locking.LockFactory.LockType)
	 */
	public Lock getLock(String lockName, LockType type, String comment) {
		Lock lock = new SimpleLock(lockName,type,comment, getSleepTime(), this);
		return lock;
	}

	public Lock getWrappedLock(String lockName, LockType type, String comment) {
		Lock lock = getLock(lockName,type,comment);
		String me = getThreadid();
		synchronized (m_locks)
		{
			for (LockedObject lo: m_locks)
			{
				if (lo.getObjectName().equals(lockName))
				{
					if (lo.getOwnerName().equals(me))
					{
						return new LockWrapper(lock,true);
					}
				}
			}
		}
		return new LockWrapper(lock,false);
	}

	public boolean lock(SimpleLock simpleLock) {
		String lockName = (m_prefix==null)?simpleLock.getLockName():m_prefix+simpleLock.getLockName();
		String me = getThreadid();
		synchronized (m_locks)
		{
			for (LockedObject lo: m_locks)
			{
				if (lo.getObjectName().equals(lockName))
				{
					if (lo.getOwnerName().equals(me))
					{
						// we found a lock but we own it so that's okay
						logger.debug("Lock {} already secured for {}",lockName,getThreadid());
						return true;
					}
					// we found a lock that we don't own.
					logger.debug("Lock {} already secured for {} rejected request from {}",new Object[]{lockName,lo.getOwnerName(),getThreadid()});
					return false;
				}
			}
			// No one owns this lock so create a new one.
			LockedObject l = new LockedObject(lockName,me,simpleLock.getComment());
			m_locks.add(l);
			getCurrentLocks().add(l);
			logger.debug("Lock {} secured for {}",lockName,getThreadid());
			return true;
		}
	}
	public List<LockedObject> getCurrentLocks() {
		List<LockedObject> ret = m_currentLocks.get();
		if (ret == null)
		{
			ret = new ArrayList<LockedObject>();
			m_currentLocks.set(ret);
		}
		ret = new ArrayList<LockedObject>();
		ret.addAll(m_currentLocks.get());
		return ret;
	}

	public void unlock(SimpleLock simpleLock) {
		unlock(simpleLock.getLockName());
	}

	@ManagedOperation
    @ManagedOperationParameters ({
        @ManagedOperationParameter(description="Name of the lock to kill", name="lockName")   
    })
	public void unlock(String plockName) {
		String lockName = (m_prefix==null)?plockName:m_prefix+plockName;
		String me = getThreadid();
		synchronized (m_locks)
		{
			LockedObject kill = null;
			for (LockedObject lo: m_locks)
			{
				if (lo.getObjectName().equals(lockName))
				{
					if (lo.getOwnerName().equals(me))
					{
						kill = lo;
						break;
					}
				}
			}
			if (kill != null)
			{
				m_locks.remove(kill);
				logger.debug("Lock {} released for {}",lockName,getThreadid());
			}
			else
			{
				logger.debug("Lock {} unreleased for {}",lockName,getThreadid());
				throw new SimpleLockException("attempt to release a lock you don't own");
			}
		}
	}
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (LockedObject lo : m_locks)
		{
			sb.append(lo.toString()+"\n");
		}
		return sb.toString();
	}

	public void clearCurrentLocks() {
		for (LockedObject lock :getCurrentLocks())
		{
			unlock(lock.getObjectName());
		}
	}

	@ManagedAttribute
	public String getPrefix() {
		return m_prefix;
	}

	@ManagedAttribute
	public void setPrefix(String prefix) {
		m_prefix = prefix;
	}

	@ManagedOperation
	public long countAllLocks() {
		synchronized (m_locks)
		{
			return m_locks.size();
		}
	}

	public List<String> getAllLocks() {
		List<String> ret = new ArrayList<String>();
		synchronized (m_locks)
		{
			for (LockedObject lo: m_locks)
			{
				ret.add(lo.toString());
			}
		}
		return ret;
	}

	@ManagedAttribute
	public int getMaxRetries() {
		return m_maxRetries;
	}

	@ManagedAttribute
	public void setMaxRetries(int maxRetries) {
		m_maxRetries = maxRetries;
	}

}

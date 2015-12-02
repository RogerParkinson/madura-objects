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
package nz.co.senanque.locking;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps a lock so we can modify the behaviour slightly without changing the interface.
 * Specifically if we set the ignore flag then it assumes we already have the lock and does nothing,
 * that includes not unlocking, which is helpful if we are locking and unlocking and the surrounding
 * code is already managing the lock.
 * 
 * @author Roger Parkinson
 *
 */
public class LockWrapper implements Lock {

	static Logger logger = LoggerFactory.getLogger(LockWrapper.class);

	private final Lock m_lock;
	private final boolean m_ignore;

	public LockWrapper(Lock lock, boolean ignore) {
		m_lock = lock;
		m_ignore = ignore;
	}

	public void lock() {
		if (!m_ignore) {
			m_lock.lock();
		}
	}

	public void lockInterruptibly() throws InterruptedException {
		if (!m_ignore) {
			m_lock.lockInterruptibly();
		}
	}

	public Condition newCondition() {
		return m_lock.newCondition();
	}

	public boolean tryLock() {
		if (!m_ignore) {
			return m_lock.tryLock();
		}		
		return true;
	}

	public boolean tryLock(long arg0, TimeUnit arg1)
			throws InterruptedException {
		if (!m_ignore) {
			return m_lock.tryLock(arg0, arg1);
		}
		return true;
	}

	public void unlock() {
		if (!m_ignore) {
			m_lock.unlock();
		}
	}
	
	public String toString()
	{
		return m_lock.toString();
	}

}

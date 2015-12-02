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

/**
 * @author Roger Parkinson
 *
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Jacket class so that you don't have to remember to unlock
 * after your locks. This accepts multiple locks and acquires the locks in the order
 * they are given in the list. The unlock is done in the reverse order.
 * 
 * @author Roger Parkinson
 *
 */
public class LockTemplate {
	
	private final List<Lock> m_locks;
	private final LockAction m_lockAction;
	
	public LockTemplate(List<Lock> locks, LockAction lockAction)
	{
		m_lockAction = lockAction;
		m_locks = locks;
	}
	public LockTemplate(Lock[] locks, LockAction lockAction)
	{
		m_lockAction = lockAction;
		m_locks = Arrays.asList(locks);
	}
	public LockTemplate(Lock lock, LockAction lockAction)
	{
		m_lockAction = lockAction;
		m_locks = new ArrayList<Lock>();
		m_locks.add(lock);
	}
	
	public boolean doAction()
	{
		Stack<Lock> lockedLocks = LockUtils.lockAll(m_locks);
		try {
			if (lockedLocks.size() == m_locks.size()) {
				m_lockAction.doAction();
				return true;
			} else {
				return false;
			}
		}
		finally
		{
			LockUtils.unlockAll(lockedLocks);
		}
	}
	
	public boolean doAction(long time, TimeUnit unit) throws InterruptedException
	{
		Stack<Lock> lockedLocks = LockUtils.lockAll(m_locks);
		try {
			if (lockedLocks.size() == m_locks.size()) {
				m_lockAction.doAction();
				return true;
			} else {
				return false;
			}
		}
		finally
		{
			LockUtils.unlockAll(lockedLocks);
		}
	}

}

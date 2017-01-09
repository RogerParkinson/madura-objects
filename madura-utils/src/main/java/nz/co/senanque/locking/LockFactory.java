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

import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * Interface describes a lock factory. Different lock factories have different
 * locking capabilities (eg across multiple JVMs etc). All support the 
 * java.util.concurrent.locks.Lock mechanism
 * @author Roger Parkinson
 *
 */
public interface LockFactory {
	
	enum LockType {READ, WRITE};
	
	/**
	 * Get a lock object that can be locked when we want.
	 * @param lockName
	 * @param type
	 * @param comment
	 * @return Lock
	 */
	Lock getLock(String lockName, LockType type, String comment);
	/**
	 * Get a lock object that can be locked when we want.
	 * @param lockName
	 * @param type
	 * @param comment
	 * @return Lock
	 */
	Lock getWrappedLock(String lockName, LockType type, String comment);
	/**
	 * clear all the locks for this thread
	 */
	void clearCurrentLocks();
	
//	/**
//	 * Count the number of locks currently locked by all lockers
//	 * @return number of active locks
//	 */
//	long countAllLocks();
//	
//	/**
//	 * Get the locks currently locked by all lockers
//	 * String representation to avoid dependence on internal lock structures.
//	 * @return the names of all the currently active locks
//	 */
//	List<String> getAllLocks();

}

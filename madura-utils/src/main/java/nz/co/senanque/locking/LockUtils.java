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
import java.util.Stack;
import java.util.concurrent.locks.Lock;

/**
 * @author Roger Parkinson
 *
 */
public class LockUtils {
	
	public static Stack<Lock> lockAll(List<Lock> locks) {
		Stack<Lock> lockedLocks = new Stack<Lock>();
		for (Lock lock:locks) {
			if (lock.tryLock()) {
				lockedLocks.push(lock);
			} else {
				break;
			}
		}
		return lockedLocks;
	}
	public static void unlockAll(Stack<Lock> locks) {
		while (locks.size()>0) {
			Lock lock = locks.pop();
			lock.unlock();
		}
	}
}

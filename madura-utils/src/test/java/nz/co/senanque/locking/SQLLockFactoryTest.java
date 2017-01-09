package nz.co.senanque.locking;

import java.util.concurrent.locks.Lock;

import nz.co.senanque.locking.LockFactory.LockType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Connects to a MySQL database and tests locking.
 * This assumes there is a MySQL database available. 
 * See the Spring context for connection details.
 * 
 * @author Roger Parkinson
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SQLLockFactoryTest {

	@Autowired
	LockFactory m_lockFactory;

	@Test
	public void testGetLock() {
		Lock lock = m_lockFactory.getLock("A", LockType.WRITE, "hello there");
		LockTemplate lockTemplate = new LockTemplate(lock, new LockAction() {
			public void doAction() {
				String s = "got a lock";
				s.toString();
			}
		});
		if (!lockTemplate.doAction()) {
			throw new RuntimeException("Failed to get a lock");
		};
	}

}

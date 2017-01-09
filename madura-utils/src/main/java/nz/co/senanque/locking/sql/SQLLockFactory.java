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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import nz.co.senanque.locking.LockFactory;
import nz.co.senanque.locking.LockWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;


/**
 * The SQL Lock Factory uses a db table to hold the locks
 * To get an exclusive it creates an entry for the lock in a table
 * constrained to be unique, so that will either give us the lock or fail.
 * To release the lock we just have to remove the record.
 * 
 * Old locks are cleared when we restart. This means we can use this across multiple
 * JVMs and if one lock goes bad we can restart just one JVM to clear the bad lock.
 * 
 * @author Roger Parkinson
 *
 */
@ManagedResource(objectName = "nz.co.senanque.locking:name=SQLLockFactory")
public class SQLLockFactory implements LockFactory, InitializingBean {
	
	static Logger logger = LoggerFactory.getLogger(SQLLockFactory.class);

	
    ThreadLocal<List<LockedObject>> m_currentLocks = new ThreadLocal<List<LockedObject>>();

    protected int m_sleepTime = 1000;
    private String m_prefix="";
    protected String m_hostAddress;
    protected DataSource m_dataSource;
	private int m_maxRetries=-1;

	protected String getThreadid()
	{
		return m_hostAddress+Thread.currentThread().getName()+"-"+Thread.currentThread().getId();
	}

	/* (non-Javadoc)
	 * @see com.aldous.locking.LockFactory#getLock(java.lang.String, com.aldous.locking.LockFactory.LockType)
	 */
	public Lock getLock(String lockName, LockType type, String comment) {
		return new SQLLock(lockName,type,comment, getSleepTime(), this);
	}
	
	public Lock getWrappedLock(String lockName, LockType type, String comment) {
		Lock lock = getLock(lockName,type,comment);
		String ownerName = getThreadid();
		Connection connection = null;
		try {
			connection = m_dataSource.getConnection();
			PreparedStatement read = null;
			ResultSet rs = null;
			
			try {
				read = connection.prepareStatement("select * from SQL_LOCK where lockName = ?");
				read.setString(1,lockName);
				rs = read.executeQuery();
				if (rs.next())
				{
					String lockOwner = rs.getString("ownerName");
					if (lockOwner.equals(ownerName))
					{
						// We own this lock so we are okay
						return new LockWrapper(lock,true);				
					}
				}
			} catch (SQLException e1) {
				logger.error("locking error: {}",e1.getLocalizedMessage());
				throw new SQLLockException(e1);
			}
			finally
			{
				try {
					if (rs != null)
					{
						rs.close();
					}
					if (read != null)
					{
						read.close();
					}
				} catch (SQLException e) {
					logger.error("locking error: {}",e.getLocalizedMessage());
					throw new SQLLockException(e);
				}
			}
		} catch (SQLException e) {
			logger.error("locking error: {}",e.getLocalizedMessage());
			throw new SQLLockException(e);
		}
		
		return new LockWrapper(lock,false);				
	}

	public boolean lock(SQLLock plock) {
		String lockName = plock.getLockName();
		String ownerName = getThreadid();
		Connection connection = null;
		try {
			connection = m_dataSource.getConnection();
			PreparedStatement read = null;
			ResultSet rs = null;
			
			try {
				read = connection.prepareStatement("select * from SQL_LOCK where lockName = ?");
				read.setString(1,lockName);
				rs = read.executeQuery();
				if (rs.next())
				{
					String lockOwner = rs.getString("ownerName");
					if (lockOwner.equals(ownerName))
					{
						// We own this lock so we are okay
						logger.debug("Lock {} already secured for us: {}",lockName,ownerName);
						return true;				
					}
					// we found a lock that we don't own.
					logger.debug("Lock {} already secured for {} rejected request from {}",new Object[]{lockName,lockOwner,ownerName});
					return false;
				}
			} catch (SQLException e1) {
				logger.error("locking error: {}",e1.getLocalizedMessage());
				throw new RuntimeException(e1);
			}
			finally
			{
				if (rs != null)
				{
					rs.close();
				}
				if (read != null)
				{
					read.close();
				}
			}
			
			// The item is probably not already locked (ie it might get locked between the time we checked and now)
			// So attempt to lock it by creating a record with that unique key
			PreparedStatement insert = null;
			try {
				insert = connection.prepareStatement("insert into SQL_LOCK (lockName,ownerName,started,comments,hostAddress) values (?,?,?,?,?)");
				insert.setString(1, lockName);
				insert.setString(2, ownerName);
				Date d = new Date();
				insert.setTimestamp(3, new Timestamp(d.getTime()));
				insert.setString(4, plock.getComment());
				insert.setString(5, m_hostAddress);
			
				int count = insert.executeUpdate();
				return (count == 1);
			} catch (SQLException e1) {
				return false; // failure to insert the record means it must be locked.
//			throw new RuntimeException(e1);
			}
			finally
			{
				if (insert != null)
				{
					insert.close();
				}
			}
		} catch (SQLException e) {
			logger.error("locking error: {}",e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
		finally
		{
			close(connection);
		}
	}
	protected void close(Connection connection)
	{
		try {
			if (connection != null)
			{
				connection.close();
			}
		} catch (SQLException e) {
			logger.error("locking error: {}",e.getLocalizedMessage());		
		}
	}
	public void unlock(SQLLock lock) {
		unlock(lock.getLockName(), true);
	}

	@ManagedOperation
    @ManagedOperationParameters ({
        @ManagedOperationParameter(description="Name of the lock to kill", name="lockName")   
    })
	public void unlock(String plockName) {
		unlock(plockName, false);
	}
	private void unlock(String plockName, boolean checkOwner) {
		String lockName = plockName;
		String ownerName = getThreadid();
		Connection connection = null;
		try {
			connection = m_dataSource.getConnection();
			PreparedStatement read = null;
			ResultSet rs = null;
			
			try {
				read = connection.prepareStatement("select * from SQL_LOCK where lockName = ?");
				read.setString(1,lockName);
				rs = read.executeQuery();
				if (rs.next())
				{
					String lockOwner = rs.getString("ownerName");
					if (checkOwner && !lockOwner.equals(ownerName))
					{
						// We don't own this lock
						logger.debug("Lock {} unreleased for {}",lockName,ownerName);
						throw new SQLLockException("attempt to release a lock you don't own");
					}
					// we found a lock that we do own so we are okay
				}
				else
				{
					logger.debug("Lock {} unreleased for {}",lockName,ownerName);
					throw new SQLLockException("attempt to release a lock you don't own");
				}
			} catch (SQLException e1) {
				logger.error("locking error: {}",e1.getLocalizedMessage());		
				throw new RuntimeException(e1);
			}
			finally
			{
				if (rs != null)
				{
					rs.close();
				}
				if (read != null)
				{
					read.close();
				}
			}
			
			// The item is probably not already locked (ie it might get locked between the time we checked and now)
			// So attempt to lock it by creating a record with that unique key
			PreparedStatement delete = null;
			try {
				delete = connection.prepareStatement("delete from SQL_LOCK where lockName = ?");
				delete.setString(1, lockName);
			
				int count = delete.executeUpdate();
				logger.debug("updated lock rows: {}",count);
				logger.debug("Lock {} released for {}",lockName,ownerName);
			} catch (SQLException e1) {
				logger.error("locking error: {}",e1.getLocalizedMessage());		
				throw new SQLLockException(e1);
			}
			finally
			{
				if (delete != null)
				{
					delete.close();
				}
			}
		} catch (SQLException e) {
			logger.error("locking error: {}",e.getLocalizedMessage());		
			throw new SQLLockException(e);
		}
		finally
		{
			close(connection);
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

	public void afterPropertiesSet() throws Exception {
		
		try {
		    InetAddress addr = InetAddress.getLocalHost();

		    // Get IP Address
		    m_hostAddress = getPrefix()+addr.getHostAddress();
		} catch (UnknownHostException e) {
			logger.error("locking setup error: {}",e.getLocalizedMessage());		
			throw new SQLLockException(e);
		}
		checkTable();
	}
	private void checkTable() {
		Statement statement = null;
		try {
			DatabaseMetaData metadata = m_dataSource.getConnection().getMetaData();
			ResultSet rs = metadata.getTables(null, null, "SQL_LOCK",null);
			if (rs.next()) {
				// table must be already there so we're done
				rs.close();
				return;
			}
			String dbName = metadata.getDatabaseProductName();
			String createTableString = getCreateTableString(dbName);
			statement = m_dataSource.getConnection().createStatement();
			logger.debug("creating SQL_LOCK table");
			statement.executeUpdate(createTableString);
			statement.executeUpdate("commit;");
			logger.debug("created SQL_LOCK table");
		} catch (SQLException e1) {
			logger.error("Error creating table: {}",e1.getLocalizedMessage());		
			throw new SQLLockException(e1);
		}
		finally
		{
			if (statement != null)
			{
				try {
					statement.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	private String getCreateTableString(String dbName) {
		InputStream inputStream = null;
		inputStream = this.getClass().getResourceAsStream("/sql_lock-"+dbName+".sql");
		if (inputStream == null) {
			inputStream = this.getClass().getResourceAsStream("/sql_lock.sql");
		}
	    try
	    (
	        final BufferedReader br
	           = new BufferedReader(new InputStreamReader(inputStream))
	    ) {
	        // parallel optional
	        return br.lines().parallel().collect(Collectors.joining("\n"));
	    } catch (final IOException e) {
	        throw new RuntimeException(e);
	        // whatever.
	    } finally {
	    	try {
				inputStream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
	    }
	}

	@SuppressWarnings("unused")
	private void clearOldLocks()
	{
		Connection connection = null;
		try {
			connection = m_dataSource.getConnection();
			PreparedStatement delete = null;
			try {
				delete = connection.prepareStatement("delete from SQL_LOCK where hostAddress = ?");
				delete.setString(1, m_hostAddress);
			
				int count = delete.executeUpdate();
				logger.debug("updated lock rows: {}",count);
			} catch (SQLException e1) {
				logger.error("locking error: {}",e1.getLocalizedMessage());		
				throw new RuntimeException(e1);
			}
			finally
			{
				if (delete != null)
				{
					delete.close();
				}
			}
		} catch (SQLException e) {
			logger.error("locking error: {}",e.getLocalizedMessage());		
			throw new SQLLockException(e);
		}
		finally
		{
			close(connection);
		}
		
	}
	@ManagedAttribute
	public int getSleepTime() {
		return m_sleepTime;
	}
	@ManagedAttribute
	public void setSleepTime(int sleepTime) {
		m_sleepTime = sleepTime;
	}

	public DataSource getDataSource() {
		return m_dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		m_dataSource = dataSource;
	}
	@ManagedOperation(description="The current locks")
	public String getAllLocksJMX() {
		Connection connection = null;
		StringBuilder sb = new StringBuilder("current locks:\n");
		try {
			connection = m_dataSource.getConnection();
			PreparedStatement read = null;
			ResultSet rs = null;
			
			try {
				read = connection.prepareStatement("select * from SQL_LOCK");
				rs = read.executeQuery();
				if (rs.next())
				{
					String lock = new LockedObject(rs.getString("lockName"), rs.getString("ownerName"), rs.getString("comments"), rs.getTimestamp("started")).toString();
					sb.append(lock);
					sb.append('\n');
				}
			} catch (SQLException e1) {
				logger.error("locking error: {}",e1.getLocalizedMessage());
				throw new SQLLockException(e1);
			}
			finally
			{
				try {
					if (rs != null)
					{
						rs.close();
					}
					if (read != null)
					{
						read.close();
					}
				} catch (SQLException e) {
					logger.error("locking error: {}",e.getLocalizedMessage());
					throw new SQLLockException(e);
				}
			}
		} catch (SQLException e) {
			logger.error("locking error: {}",e.getLocalizedMessage());
			throw new SQLLockException(e);
		}
		return sb.toString();
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
	public void clearCurrentLocks() {
		for (LockedObject lock :getCurrentLocks())
		{
			unlock(lock.getObjectName());
		}
	}

//	public long countAllLocks() {
//		return getAllLocks().size();
//	}
//
//	public List<String> getAllLocks() {
//		List<String> ret = new ArrayList<String>();
//		Connection connection = null;
//		try {
//			connection = m_dataSource.getConnection();
//			PreparedStatement read = null;
//			ResultSet rs = null;
//			
//			try {
//				read = connection.prepareStatement("select * from SQL_LOCK");
//				rs = read.executeQuery();
//				while (rs.next())
//				{
//					StringBuilder sb = new StringBuilder();
//					sb.append(rs.getString("lockName"));
//					sb.append(" ");
//					sb.append(rs.getString("ownerName"));
//					sb.append(" ");
//					String ts = rs.getString("started");
//					sb.append(String.valueOf(ts));
//					sb.append(" ");
//					sb.append(rs.getString("comments"));
//					ret.add(sb.toString());
//				}
//			} catch (SQLException e1) {
//				throw new RuntimeException(e1);
//			}
//			finally
//			{
//				if (rs != null)
//				{
//					rs.close();
//				}
//				if (read != null)
//				{
//					read.close();
//				}
//			}
//		} catch (SQLException e) {
//			throw new RuntimeException(e);
//		}
//		finally
//		{
//			close(connection);
//		}
//		return ret;
//	}
	@ManagedAttribute
	public int getMaxRetries() {
		return m_maxRetries;
	}
	@ManagedAttribute
	public void setMaxRetries(int maxRetries) {
		m_maxRetries = maxRetries;
	}
}

package com.allpago.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

import com.allpago.config.PropUtil;

public class DBConnectionPool {
	
	private static DBConnectionPool pool = new DBConnectionPool();
	private static BasicDataSource connectionPool;
	
	private DBConnectionPool(){
		
		String url = PropUtil.getInstance().getProperty("db.url");
		String user = PropUtil.getInstance().getProperty("db.user");
		String passwd = PropUtil.getInstance().getProperty("db.password");
		String driver = PropUtil.getInstance().getProperty("db.driver");
		String size = PropUtil.getInstance().getProperty("db.pool.size");
		connectionPool = new BasicDataSource();
		connectionPool.setUsername(user);
		connectionPool.setPassword(passwd);
		connectionPool.setDriverClassName(driver);
		connectionPool.setUrl(url);
		connectionPool.setInitialSize(Integer.parseInt(size));
	}

	public static DBConnectionPool getInstance(){
		return pool;
	}
	
	public static Connection getConnection() throws SQLException{
		return connectionPool.getConnection();
	}
}

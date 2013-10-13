package com.haiming.messageboard.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DaoUtils {
	//这里要使用完整路径
	private static final String url="jdbc:sqlite:F:/GitRepo/messageboard/MessageBoard/test.db";
	private static final String className = "org.sqlite.JDBC";
	private static Connection conn = null;
	
	/**
	 * 获得一个Connection对象
	 * @return
	 */
	public static Connection getConnection(){ 
		try {
			Class.forName(className);
			conn = DriverManager.getConnection(url);
			return conn;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 关闭连接，释放资源 
	 * @param st
	 * @param rs
	 */
	public static void close( Statement st,ResultSet rs){ 
		try{ 
			if(conn != null){ 
				conn.close();
				conn = null;
			}
			if(st != null){ 
				st.close();
				st = null;
			}
			if(rs != null){ 
				rs.close();
				rs = null;
			}
		}catch(SQLException e){ 
			throw new RuntimeException(e);
		}
	}
	
	 
}

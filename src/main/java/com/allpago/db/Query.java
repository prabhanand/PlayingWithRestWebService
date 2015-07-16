package com.allpago.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allpago.resource.Office;

/**
 * 
 * The purpose of this call is to interact with DB for all required purposes.
 *
 */
public class Query {

	private static Logger logger = LoggerFactory.getLogger(Query.class);
	
	
	/**
	 * List all the office from db
	 * @return list of offices
	 */
	public static Collection<Office> getAllOfficeList(){
		
		final Collection<Office> list = new ArrayList< Office >();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prepstmt = null;
		Office off = null;
		try {
			//get connection
			conn = DBConnectionPool.getConnection();
			String query = "select name, address, opentm, closetm, utcdiff from office";
			prepstmt = conn.prepareStatement(query);
			rs = prepstmt.executeQuery();
			while(rs.next()){
				off = new Office(rs.getString("name"), rs.getString("address"), rs.getTime("opentm"), rs.getTime("closetm"), rs.getInt("utcdiff"));
				list.add(off);
			}
			logger.debug("all Offices listed "+list.size());
		} catch (Exception e) {
			System.out.println("Error: while getting office "+ e.getMessage());
		}finally{
			close(prepstmt);
			close(rs);
			close(conn);
		}
		return list;	
	}
	
	
	/**
	 * List all open offices with respect to current UTC time.
	 * @return
	 */
	public static Collection<Office> getOpenOfficeList(){

		final Collection<Office> list = new ArrayList< Office >();
		Connection conn = null;
		ResultSet rs1 = null, rs2 = null, rs3 = null, rs4 = null;
		PreparedStatement prepstmt1 = null, prepstmt2 = null, prepstmt3 = null, prepstmt4 = null;
		Office off = null;
		try {
			//get connection
			conn = DBConnectionPool.getConnection();
			//Query to get offices open between given start time and end time where UTC time is in -ve, means substract it from UTC time to get local time
			String query = "SELECT name, address, opentm, closetm, utcdiff FROM office WHERE utcdiff < 0 AND SUBTIME(UTC_TIME(), SEC_TO_TIME(- utcdiff*60*60)) BETWEEN opentm AND closetm";
			
			prepstmt1 = conn.prepareStatement(query);
			rs1 = prepstmt1.executeQuery();
			while(rs1.next()){
				off = new Office(rs1.getString("name"), rs1.getString("address"), rs1.getTime("opentm"), rs1.getTime("closetm"), rs1.getInt("utcdiff"));
				list.add(off);
			}
			
			//Query to get offices open between given start time and end time where UTC time is in +ve means addup to the UTC time to get local time
			query = "SELECT name, address, opentm, closetm, utcdiff  FROM office WHERE utcdiff >= 0 AND ADDTIME(UTC_TIME(), SEC_TO_TIME(utcdiff*60*60)) BETWEEN opentm AND closetm";
			
			prepstmt2 = conn.prepareStatement(query);
			rs2 = prepstmt2.executeQuery();
			while(rs2.next()){
				off = new Office(rs2.getString("name"), rs2.getString("address"), rs2.getTime("opentm"), rs2.getTime("closetm"), rs2.getInt("utcdiff"));
				list.add(off);
			}

			
			//Query to get all open offices where they start on earlier day and close on the next day where the start time is greater than close time and UTC time difference is in -ve
			query = "SELECT name, address, opentm, closetm, utcdiff FROM office WHERE opentm > closetm AND utcdiff < 0 AND "
					+ "(SUBTIME(UTC_TIME(), SEC_TO_TIME(- utcdiff*60*60)) BETWEEN opentm  AND '23:59:59' OR "
					+ "SUBTIME(UTC_TIME(), SEC_TO_TIME(- utcdiff*60*60)) BETWEEN '00:00:00' AND closetm)";
			
			prepstmt3 = conn.prepareStatement(query);
			rs3 = prepstmt3.executeQuery();
			while(rs3.next()){
				off = new Office(rs3.getString("name"), rs3.getString("address"), rs3.getTime("opentm"), rs3.getTime("closetm"), rs3.getInt("utcdiff"));
				list.add(off);
			}
			//As above but handle a case where UTC time difference is in +ve
			query = "SELECT name, address, opentm, closetm, utcdiff FROM office  WHERE opentm > closetm AND utcdiff >= 0 AND "
					+ "(ADDTIME(UTC_TIME(), SEC_TO_TIME(utcdiff*60*60)) BETWEEN opentm  AND '23:59:59' OR  "
					+ "ADDTIME(UTC_TIME(), SEC_TO_TIME(utcdiff*60*60)) BETWEEN '00:00:00' AND closetm)";
			
			prepstmt4 = conn.prepareStatement(query);
			rs4 = prepstmt4.executeQuery();
			
			while(rs4.next()){
				off = new Office(rs4.getString("name"), rs4.getString("address"), rs4.getTime("opentm"), rs4.getTime("closetm"), rs4.getInt("utcdiff"));
				list.add(off);
			}
			logger.debug("all Offices listed "+list.size());
			
		} catch (Exception e) {
			System.out.println("Error: while getting office "+ e.getMessage());
		}finally{
			
			close(prepstmt1);
			close(rs1);

			close(prepstmt2);
			close(rs2);

			close(prepstmt3);
			close(rs3);

			close(prepstmt4);
			close(rs4);

			close(conn);
		}
		return list;	
	}
	
	/**
	 * Get office details by name
	 * @param name
	 * @return
	 */
	
	public static Office getOffice(String name){
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prepstmt = null;
		try {
			//get connection
			conn = DBConnectionPool.getConnection();
			String query = "SELECT * FROM office WHERE name=?";
			prepstmt = conn.prepareStatement(query);
			prepstmt.setString(1, name);
			rs = prepstmt.executeQuery();
			if(rs.next()){
				return new Office(rs.getString("name"), rs.getString("address"), rs.getTime("opentm"), rs.getTime("closetm"), rs.getInt("utcdiff"));
			}
			logger.debug("Office with this NOT name exists");
		} catch (Exception e) {
			System.out.println("Error: while getting office "+ e.getMessage());
		}finally{
			close(prepstmt);
			close(rs);
			close(conn);
		}
		return null;
	}
	
	/**
	 * This class directly interacting with DB. Saves the given content to database.
	 * @param name
	 * @param address
	 * @param opentm
	 * @param closetm
	 * @param utcdiff
	 * @return
	 */
	public static boolean saveOffice(final String name, final String address, final Time opentm, final Time closetm, final int utcdiff){
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prepstmt = null;
		int result = 0;
		try{
			conn = DBConnectionPool.getConnection();
			String query = "INSERT INTO office(name, address, opentm, closetm, utcdiff) VALUES (?,?,?,?,?)";
			prepstmt = conn.prepareStatement(query);
			prepstmt.setString(1, name);
			prepstmt.setString(2, address);
			prepstmt.setTime(3, opentm);
			prepstmt.setTime(4, closetm);
			prepstmt.setInt(5, utcdiff);
			result = prepstmt.executeUpdate();
			logger.debug("Result of query to insert office:"+result);
			return true;
		}catch (SQLException e) {
			logger.error("Error: while storing office "+ e.getMessage());
		}finally{
			close(prepstmt);
			close(rs);
			close(conn);
		}
		return false;
	}

	/**
	 * Checks for existence of office in the system
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isOfficeExists(String name){
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prepstmt = null;
		try {
			//get connection
			conn = DBConnectionPool.getConnection();
			String query = "SELECT id FROM office WHERE name=?";
			prepstmt = conn.prepareStatement(query);
			prepstmt.setString(1, name);
			rs = prepstmt.executeQuery();
			if(rs.next()){
				long id = rs.getLong("id");
				logger.debug("Office with this name exists - "+id);
				return true;
			}
			logger.debug("Office with this NOT name exists");
		} catch (Exception e) {
			System.out.println("Error: while getting office "+ e.getMessage());
		}finally{
			close(prepstmt);
			close(rs);
			close(conn);
		}
		return false;
	}


	/**
	 * Delete office with given name
	 * @param name
	 * @return
	 */
	public static boolean deleteOffice(String name){
		Connection conn = null;

		PreparedStatement prepstmt = null;
		try {
			//get connection
			conn = DBConnectionPool.getConnection();      
			String query = "DELETE FROM office WHERE name=?";
			prepstmt = conn.prepareStatement(query);
			prepstmt.setString(1, name);
			prepstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			System.out.println("Error: while getting office "+ e.getMessage());
		}finally{
			close(prepstmt);
			close(conn);
		}
		return false;
	}
	
	
	/**
	 * Try to ping database and get data. if able to fire query then db is working fine
	 * @return
	 */
	
	public static boolean pingDB(){
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prepstmt = null;
		try {
			//get connection
			conn = DBConnectionPool.getConnection();      
			String query = "SELECT id FROM office";
			prepstmt = conn.prepareStatement(query);
			rs = prepstmt.executeQuery();
			if(rs.next()){
			}
			return true;
		} catch (Exception e) {
			System.out.println("Error: while getting office "+ e.getMessage());
		}finally{
			close(prepstmt);
			close(rs);
			close(conn);
		}
		return false;
	}
	
	
	private static void close(PreparedStatement prestmt){
		if(null != prestmt){
			try {
				prestmt.close();
			} catch (SQLException e) {
				logger.warn("database PreparedStatement close exception "+ e.getMessage());
			}
		}
	}

	private static void close(ResultSet rs){
		if(null != rs){
			try {
				rs.close();
			} catch (SQLException e) {
				logger.warn("database resultset close exception "+ e.getMessage());	
			}
		}

	}
	private static void close(Connection conn){
		if(null != conn){
			try {
				conn.close();
			} catch (SQLException e) {
				logger.warn("database connetion close exception "+ e.getMessage());
			}
		}

	}
}

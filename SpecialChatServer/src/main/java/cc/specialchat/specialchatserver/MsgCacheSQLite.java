package cc.specialchat.specialchatserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database:    msg_cache.db
 * table:       msg_cache
 * columns:
 *      msg_index       INTEGER,primary key,autoincrement   //index
 *      from_id         INTEGER,NOT NULL
 *      to_id           INTEGER,NOT NULL
 *      msg_content     TEXT,NOT NULL
 *      send_time       INTEGER,NOT NULL
 *      is_read         INTEGER
 *
 * */


public class MsgCacheSQLite{
	private static Connection getConnection() throws SQLException, ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		//c.setAutoCommit(false);
		return DriverManager.getConnection("jdbc:sqlite:user_info.db");
	}
	
	/**
	 * Table init method, create table user_info
	 * @throws SQLException ...
	 * @throws ClassNotFoundException ...
	 */
	//todo: init
	void init() throws SQLException, ClassNotFoundException{
		Connection connection=getConnection();
		Statement statement=connection.createStatement();
		String CREATE_TABLE_SQL="create table msg_cache (" +
				"" + //todo: here.
				")";
	}
	
	//todo: insert new message
	void insertNewMsg(){
	
	}
	
	//todo: fetch all his/her messages by user_id
	String[][] fetchMsg(String user_id) throws SQLException, ClassNotFoundException{
		Connection connection=getConnection();
		Statement statement=connection.createStatement();
		String QUERY_SQL="select * from msg_cache where to_id="+user_id+";";
		ResultSet resultSet=statement.executeQuery(QUERY_SQL);
		while(resultSet.next()){
			//todo: fetch
		}
		return null;//not this
	}
}

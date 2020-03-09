package cc.specialchat.specialchatserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database:    user_info.db
 * table:       user_info
 * columns:
 *      user_index      INTEGER,primary key,autoincrement   //index
 *      user_id         INTEGER
 *      user_name       TEXT
 *      password        TEXT
 *      login_time      INTEGER
 *      token_key       TEXT
 *
 * */

public class UserInfoSQLite{
	
	public static void init() throws SQLException, ClassNotFoundException{
		Connection c=getConnection();
		Statement st=c.createStatement();
		String CREATE_TABLE_SQL=
				"create table user_info (" +
						"user_index INTEGER primary key autoincrement," +
						"user_id INTEGER NOT NULL," +
						"user_name TEXT," +
						"password TEXT," +
						"login_time INTEGER," +
						"token_key TEXT" +
						")";
						
		st.executeUpdate(CREATE_TABLE_SQL);
		st.close();
		c.close();
		System.out.println("done");
	}
	
	public static Connection getConnection() throws SQLException, ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		Connection c=DriverManager.getConnection("jdbc:sqlite:user_info.db");
		//c.setAutoCommit(false);
		return c;
	}
	
	public void addNewUser(){
		//todo: complete this
	}
	
	/**
	 * Fetch user's information by user_id
	 * @param user_id user's id (String)
	 * @return user's information
	 * @throws SQLException ...
	 * @throws ClassNotFoundException ...
	 */
	public String[] fetchUserInfo(String user_id) throws SQLException, ClassNotFoundException{
		Connection connection=getConnection();
		Statement statement=connection.createStatement();
		String QUERY_SQL="select * from user_info where user_id="+user_id+";";
		ResultSet resultSet=statement.executeQuery(QUERY_SQL);
		String userInfo[]=new String[6];
		if(resultSet.first()){
			userInfo[0]=resultSet.getInt("user_index")+"";
			userInfo[1]=resultSet.getInt("user_id")+"";
			userInfo[2]=resultSet.getString("user_name");
			userInfo[3]=resultSet.getString("password");
			userInfo[4]=resultSet.getInt("login_time")+"";
			userInfo[5]=resultSet.getString("token_key");
		}else{
			userInfo=null;
		}
		resultSet.close();
		statement.close();
		connection.close();
		return userInfo;
	}
	
	/**
	 * Fetch all users information.
	 * @return return a String[50][6]
	 * @throws SQLException ..
	 * @throws ClassNotFoundException ..
	 */
	public String[][] fetchAllUsersInfo() throws SQLException, ClassNotFoundException{
		Connection connection=getConnection();
		Statement statement=connection.createStatement();
		String QUERY_SQL="select * from user_info;";
		ResultSet resultSet=statement.executeQuery(QUERY_SQL);
		String[][] allUsersInfo=new String[50][6];
		int index=0;
		while(resultSet.next()){
			allUsersInfo[index][0]=resultSet.getInt("user_index")+"";
			allUsersInfo[index][1]=resultSet.getInt("user_id")+"";
			allUsersInfo[index][2]=resultSet.getString("user_name");
			allUsersInfo[index][3]=resultSet.getString("password");
			allUsersInfo[index][4]=resultSet.getInt("login_time")+"";
			allUsersInfo[index][5]=resultSet.getString("token_key");
			index++;
		}
		resultSet.close();
		statement.close();
		connection.close();
		return allUsersInfo;
	}
}

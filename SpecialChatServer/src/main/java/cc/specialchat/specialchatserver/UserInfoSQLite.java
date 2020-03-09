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
 *      user_id         INTEGER,NOT NULL,UNIQUE
 *      user_name       TEXT,NOT NULL
 *      password        TEXT,NOT NULL
 *      login_time      INTEGER
 *      token_key       TEXT
 *
 * */

class UserInfoSQLite{
	
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
	static void init() throws SQLException, ClassNotFoundException{
		Connection c=getConnection();
		Statement st=c.createStatement();
		String CREATE_TABLE_SQL=
				"create table user_info (" +
						"user_index INTEGER primary key autoincrement," +
						"user_id INTEGER NOT NULL UNIQUE," +
						"user_name TEXT NOT NULL," +
						"password TEXT NOT NULL," +
						"login_time INTEGER," +
						"token_key TEXT" +
						")";
		
		st.executeUpdate(CREATE_TABLE_SQL);
		st.close();
		c.close();
		System.out.println("init table user_info done! \n");
	}
	
	/**
	 * Start Login use user_id , password
	 * @param user_id String
	 * @param password String
	 * @return token_key, String
	 * @throws SQLException ...
	 * @throws ClassNotFoundException ...
	 */
	static String[] goLogin(String user_id,String password) throws SQLException,
			ClassNotFoundException{
		Connection connection=getConnection();
		Statement statement=connection.createStatement();
		String QUERY_SQL="select * from user_info where user_id="+user_id+";";
		ResultSet resultSet=statement.executeQuery(QUERY_SQL);
		if(resultSet.next() && resultSet.getString("password").equals(password)){
			String[] user_info=new String[4];
			user_info[0]=resultSet.getInt("user_id")+"";
			user_info[1]=resultSet.getString("user_name");
			user_info[2]=MyTools.createANewTokenKey();
			user_info[3]=MyTools.getCurrentTime()+"";
			String UPDATE_INFO_SQL="update user_info set " +
					"token_key='"+user_info[2]+"', " +
					"login_time="+user_info[3]+" "+
					"where user_id="+user_id+";";
			statement.executeUpdate(UPDATE_INFO_SQL);
			resultSet.close();
			statement.close();
			connection.close();
			return user_info;
		}
		resultSet.close();
		statement.close();
		connection.close();
		return new String[]{""};
	}
	
	/**
	 * Verify User's token_key
	 * @param user_id String user_id
	 * @param token_key String token_key
	 * @return A boolean value
	 * @throws SQLException ...
	 * @throws ClassNotFoundException ...
	 */
	static boolean verifyUserTokenKey(String user_id,String token_key) throws SQLException,
			ClassNotFoundException{
		Connection connection=getConnection();
		Statement statement=connection.createStatement();
		String QUERY_SQL="select token_key from user_info where user_id="+user_id+";";
		ResultSet resultSet=statement.executeQuery(QUERY_SQL);
		if(resultSet.next() && resultSet.getString("token_key").equals(token_key)){
			resultSet.close();
			statement.close();
			connection.close();
			return true;
		}else{
			resultSet.close();
			statement.close();
			connection.close();
			return false;
		}
	}
	
	/**
	 * Add a new user into user_info
	 * @param user_id integer, user_id
	 * @param user_name String, user_name
	 * @param password String, user password
	 * @throws SQLException ...
	 * @throws ClassNotFoundException ...
	 */
	static void addNewUser(int user_id,String user_name,String password)
			throws SQLException, ClassNotFoundException{
		Connection connection=getConnection();
		Statement statement=connection.createStatement();
		String ADD_NEW_USER_SQL="insert into user_info " +
				"(user_index,user_id,user_name,password,login_time,token_key) " +
				"values (null," +
						user_id+"," +
						"'"+user_name+"'," +
						"'"+password+"'," +
						MyTools.getCurrentTime()+"," +
						"null" +
						")";
		statement.executeUpdate(ADD_NEW_USER_SQL);
		statement.close();
		connection.close();
	}
	
	/**
	 * Fetch user's information by user_id
	 * @param user_id user's id (String)
	 * @return user's information
	 * @throws SQLException ...
	 * @throws ClassNotFoundException ...
	 */
	static String[] fetchUserInfo(String user_id) throws SQLException, ClassNotFoundException{
		Connection connection=getConnection();
		Statement statement=connection.createStatement();
		String QUERY_SQL="select * from user_info where user_id="+user_id+";";
		ResultSet resultSet=statement.executeQuery(QUERY_SQL);
		String[] userInfo=new String[6];
		if(resultSet.next()){
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
	static String[][] fetchAllUsersInfo() throws SQLException, ClassNotFoundException{
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

package cc.specialchat.specialchatserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * User Info. SQLite Manager
 *
 * Database:    user_info.db
 * table:       user_info
 * columns:
 *      user_index      INTEGER,primary key,autoincrement   //index
 *      user_id         INTEGER,NOT NULL,UNIQUE
 *      user_phone          TEXT,NOT NULL    // changed to string!!
 *      user_name       TEXT,NOT NULL   // string filtered by MyTools.filterSpecialChar()
 *      password        TEXT,NOT NULL
 *      login_time      INTEGER
 *      token_key       TEXT
 *
 * */

class UserInfoSQLite{
	
	// Create a SQLite connection.
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
		Connection connection=getConnection();
		Statement statement=connection.createStatement();
		String CREATE_TABLE_SQL=
				"create table user_info (" +
						"user_index INTEGER primary key autoincrement," +
						"user_id INTEGER NOT NULL UNIQUE," +
						"user_phone TEXT NOT NULL," +
						"user_name TEXT NOT NULL," +
						"password TEXT NOT NULL," +
						"login_time INTEGER," +
						"token_key TEXT" +
						")";
		
		statement.executeUpdate(CREATE_TABLE_SQL);
		statement.close();
		connection.close();
		System.out.println("---- init table user_info done! ----\n");
	}
	
	/**
	 * Start Login use user_IdOrPhone , password
	 * 20.04.02 update, now can login by phone
	 * @param user_IdOrPhone String . phone or user_IdOrPhone
	 * @param password String
	 * @return token_key, String
	 */
	static String[] goLogin(String user_IdOrPhone,String password) {
		try{
			Connection connection=getConnection();
			Statement statement=connection.createStatement();
			
			String QUERY_SQL="select * from user_info where user_id="+user_IdOrPhone+";";
			ResultSet resultSet=statement.executeQuery(QUERY_SQL);
			if(resultSet.next() && resultSet.getString("password").equals(password)){
				String[] user_info=new String[5];
				user_info[0]=resultSet.getInt("user_id")+"";
				user_info[1]=resultSet.getString("user_name");
				user_info[2]=MyTools.createANewTokenKey();
				user_info[3]=MyTools.getCurrentTime()+"";
				user_info[4]=resultSet.getString("user_phone");
				String UPDATE_INFO_SQL="update user_info set "+
										"token_key='"+user_info[2]+"', "+
										"login_time="+user_info[3]+" "+
										"where user_id="+user_IdOrPhone+";";
				statement.executeUpdate(UPDATE_INFO_SQL);
				resultSet.close();
				statement.close();
				connection.close();
				return user_info;
			}
			resultSet.close();
			
//			String QUERY_SQL2="select * from user_info where user_phone="+user_IdOrPhone+";";
//			ResultSet resultSet2=statement.executeQuery(QUERY_SQL2);
//			if(resultSet2.next() && resultSet2.getString("password").equals(password)){
//				String[] user_info=new String[5];
//				user_info[0]=resultSet2.getInt("user_id")+"";
//				user_info[1]=resultSet2.getString("user_name");
//				user_info[2]=MyTools.createANewTokenKey();
//				user_info[3]=MyTools.getCurrentTime()+"";
//				user_info[4]=resultSet2.getInt("user_phone")+"";
//				String UPDATE_INFO_SQL="update user_info set "+
//						"token_key='"+user_info[2]+"', "+
//						"login_time="+user_info[3]+" "+
//						"where user_phone="+user_IdOrPhone+";";
//				statement.executeUpdate(UPDATE_INFO_SQL);
//				resultSet2.close();
//				statement.close();
//				connection.close();
//				return user_info;
//			}

//			resultSet2.close();
			statement.close();
			connection.close();
			return new String[]{""};
		}catch(SQLException|ClassNotFoundException e){
			e.printStackTrace();
			return new String[]{""};
		}
	}
	
	/**
	 * Check a user id whether can be use (whether existed)
	 * @param user_id String user_id to be check
	 * @return available true or false
	 */
	static boolean checkIDUsability(String user_id){
		try{
			Connection co=getConnection();
			Statement st=co.createStatement();
			String QUERY_SQL="select user_index from user_info where user_id="+user_id;
			ResultSet re=st.executeQuery(QUERY_SQL);
			if(re.next()){
				re.close();
				st.close();
				co.close();
				return false;
			}else{
				return true;
			}
		}catch(SQLException|ClassNotFoundException e){
			e.printStackTrace();
			return false;
		}
	}
	
	//todo: check phone usability
	
	/**
	 * Verify User's token_key
	 * @param user_id String user_id
	 * @param token_key String token_key
	 * @return A boolean value
	 */
	static boolean verifyUserTokenKey(String user_id,String token_key){
		try{
			Connection connection=getConnection();
			Statement statement=connection.createStatement();
			String QUERY_SQL="select token_key from user_info where user_id="+user_id;
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
		}catch(SQLException|ClassNotFoundException e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Add a new user into user_info
	 * @param user_id string, user_id
	 * @param user_phone string user's phone
	 * @param user_name String, user_name
	 * @param password String, user password
	 */
	static boolean addNewUser(String user_id,String user_phone,String user_name,String password){
		try{
			Connection connection=getConnection();
			Statement statement=connection.createStatement();
			String ADD_NEW_USER_SQL="insert into user_info "+
					"(user_index,user_id,user_phone,user_name,password,login_time,token_key) "+
					"values (null,"+
					user_id+"," +
					"'"+user_phone+"',"+
					"'"+user_name+"',"+
					"'"+password+"',"+
					MyTools.getCurrentTime()+","+
					"null"+
					")";
			
			statement.executeUpdate(ADD_NEW_USER_SQL);
			statement.close();
			connection.close();
			System.out.println("---- New added user:"+user_id+","+user_name+"! ----\n");
			
			// Create contact table for new user
			ContactListSQLite.init(user_id);
			
			return true;
		}catch(SQLException|ClassNotFoundException e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Update user info (When edit user profile)
	 * @param user_id uid
	 * @param new_user_name new user name
	 * @param new_user_phone new user phone
	 * @return boolean
	 */
	static boolean updateUserInfo(String user_id,String new_user_name,String new_user_phone){
		try{
			Connection con=getConnection();
			Statement sta=con.createStatement();
			String SQL="update user_info set user_name='"+new_user_name+"', user_phone='"+new_user_phone+"' " +
					"where user_id="+user_id;
			sta.executeUpdate(SQL);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Fetch user's information by user_id
	 * @param user_id user's id (String)
	 * @return user's information
	 */
	static String[] fetchUserInfo(String user_id){
		try{
			Connection connection=getConnection();
			Statement statement=connection.createStatement();
			String QUERY_SQL="select * from user_info where user_id="+user_id+";";
			ResultSet resultSet=statement.executeQuery(QUERY_SQL);
			String[] userInfo=new String[7];
			if(resultSet.next()){
				userInfo[0]=resultSet.getInt("user_index")+"";
				userInfo[1]=resultSet.getInt("user_id")+"";
				userInfo[2]=resultSet.getString("user_phone");
				userInfo[3]=resultSet.getString("user_name");
				userInfo[4]=resultSet.getString("password");
				userInfo[5]=resultSet.getInt("login_time")+"";
				userInfo[6]=resultSet.getString("token_key");
			}else{
				userInfo=null;
			}
			resultSet.close();
			statement.close();
			connection.close();
			return userInfo;
		}catch(SQLException|ClassNotFoundException e){
			return null;
		}
	}
	
	/**
	 * Fetch user_id by ta's phone
	 * @param phone user_phone
	 * @return user_id
	 */
	static String fetchUserID(String phone){
		try{
			Connection con=getConnection();
			Statement sta=con.createStatement();
			String SQL="select user_id from user_info where user_phone='"+phone+"'";
			ResultSet res=sta.executeQuery(SQL);
			String user_id="";
			if(res.next()){
				user_id=res.getString("user_id");
			}
			res.close();
			sta.close();
			con.close();
			return user_id;
		}catch(Exception e){
			//
			return "";
		}
	}
	
	/**
	 * Fetch all users information. note: max number is 50
	 * @return return a String[50][6]
	 */
	static String[][] fetchAllUsersInfo(){
		try{
			Connection connection=getConnection();
			Statement statement=connection.createStatement();
			String QUERY_SQL="select * from user_info;";
			ResultSet resultSet=statement.executeQuery(QUERY_SQL);
			String[][] allUsersInfo=new String[51][6];
			int index=0;
			while(resultSet.next() && index<50){
				index++;
				allUsersInfo[index][0]=resultSet.getInt("user_index")+"";
				allUsersInfo[index][1]=resultSet.getInt("user_id")+"";
				allUsersInfo[index][2]=resultSet.getString("user_name");
				allUsersInfo[index][3]=resultSet.getString("password");
				allUsersInfo[index][4]=resultSet.getInt("login_time")+"";
				allUsersInfo[index][5]=resultSet.getString("token_key");
			}
			allUsersInfo[0][0]=index+"";
			resultSet.close();
			statement.close();
			connection.close();
			return allUsersInfo;
		}catch(SQLException|ClassNotFoundException e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Search (new) contacts
	 * @param id String
	 * @return String[][]
	 */
	static String[][] searchUsers(String id){
		try{
			String[][] userInfo=new String[51][4];
			int index=0;
			
			Connection co=getConnection();
			Statement st=co.createStatement();
			
			// Search by user_id.
			String QUERY_SQL="select user_index,user_id,user_phone,user_name from user_info where user_id like '%"+id+"%';"; // search
			ResultSet re=st.executeQuery(QUERY_SQL);
			if(re.next()){
				do{
					index++;
					userInfo[index][0]=re.getInt("user_index")+"";
					userInfo[index][1]=re.getInt("user_id")+"";
					userInfo[index][2]=re.getString("user_phone");
					userInfo[index][3]=re.getString("user_name");
//					userInfo[index][4]=re.getString("password");
//					userInfo[index][5]=re.getInt("login_time")+"";
//					userInfo[index][6]=re.getString("token_key");
				}while(re.next() && index<50);
			}
			re.close();
			
			// Search by user_phone
			QUERY_SQL="select user_index,user_id,user_phone,user_name from user_info where user_phone like '%"+id+"%';"; // search
			re=st.executeQuery(QUERY_SQL);
			if(re.next()){
				do{
					index++;
					userInfo[index][0]=re.getInt("user_index")+"";
					userInfo[index][1]=re.getInt("user_id")+"";
					userInfo[index][2]=re.getString("user_phone");
					userInfo[index][3]=re.getString("user_name");
//					userInfo[index][4]=re.getString("password");
//					userInfo[index][5]=re.getInt("login_time")+"";
//					userInfo[index][6]=re.getString("token_key");
				}while(re.next() && index<50);
			}
			re.close();
			st.close();
			co.close();
			
			userInfo[0][0]=index+""; // data counts
			return userInfo;
		}catch(SQLException|ClassNotFoundException e){
			e.printStackTrace();
			return null;
		}
	}
	
}

package cc.specialchat.specialchatserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Contact List SQLite Manager
 *
 * Databases:   contact_list.db
 * Table(s):    contact_list_[user_id]    // [user_id] link to user_info>user_id
 * Columns:
 *      index_num       INTEGER,primary key,autoincrement    // index
 *      ta_id           INTEGER,NOT NULL,UNIQUE // user_id
 *      nickname        TEXT // def sets user_name
 *      status          INTEGER // no usage , reserved
 *      add_time        INTEGER
 *
 */

class ContactListSQLite{
	
	private static Connection getConnection() throws SQLException, ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		//c.setAutoCommit(false);
		return DriverManager.getConnection("jdbc:sqlite:contact_list.db");
	}
	
	static void init(String user_id) throws SQLException, ClassNotFoundException{
		Connection connection=getConnection();
		Statement statement=connection.createStatement();
		String CREATE_TABLE_SQL=
				"create table contact_list_"+user_id+" (" +
						"index_num INTEGER primary key autoincrement," +
						"ta_id INTEGER NOT NULL UNIQUE," +
						"nickname TEXT," +
						"status INTEGER," +
						"add_time INTEGER" +
						")";
		
		statement.executeUpdate(CREATE_TABLE_SQL);
		statement.close();
		connection.close();
		System.out.println("---- init table contact_list_"+user_id+" done! ----\n");
	}
	
	/**
	 * Check two users whether is friend
	 * @param user_a a
	 * @param user_b b
	 * @return boolean
	 */
	static boolean checkIsFriend(String user_a,String user_b){
		try{
			Connection con=getConnection();
			Statement st=con.createStatement();
			String QUERY_SQL_A="select index_num from contact_list_"+user_a+" where ta_id="+user_b;
			String QUERY_SQL_B="select index_num from contact_list_"+user_b+" where ta_id="+user_a;
			ResultSet re_a=st.executeQuery(QUERY_SQL_A);
			ResultSet re_b=st.executeQuery(QUERY_SQL_B);
			if(re_a.next() && re_b.next()){
				re_a.close();
				re_b.close();
				st.close();
				con.close();
				return true;
			}
			re_a.close();
			re_b.close();
			st.close();
			con.close();
			return false;
		}catch(ClassNotFoundException|SQLException e){
			e.printStackTrace();
			return true;
		}
	}
	
	// edit two tables
	/**
	 * Insert two tables to make friend
	 * @param user_a a
	 * @param user_b b
	 * @param nickname_a a
	 * @param nickname_b b
	 * @return boolean
	 */
	static boolean addNewContact(String user_a,String user_b,String nickname_a,String nickname_b){
		try{
			Connection co=getConnection();
			Statement st=co.createStatement();
			String ADD_SQL_A="insert into contact_list_"+user_a+" " +
					"(ta_id,nickname,add_time) values (" +
					user_b+"," +
					"'"+nickname_b+"'," +
					MyTools.getCurrentTime()+"" +
					")";
			
			String ADD_SQL_B="insert into contact_list_"+user_b+" " +
					"(ta_id,nickname,add_time) values (" +
					user_a+"," +
					"'"+nickname_a+"'," +
					MyTools.getCurrentTime()+"" +
					")";
			
			st.executeUpdate(ADD_SQL_A);
			st.executeUpdate(ADD_SQL_B);
			
			st.close();
			co.close();
			
			return true;
		}catch(SQLException|ClassNotFoundException e){
			e.printStackTrace();
			return false;
		}
	}
	
	// fetch user's contacts list
	static String[] fetchContacts(String user_id){
		try{
			Connection con=getConnection();
			Statement st=con.createStatement();
			String QUERY_SQL="";
			ResultSet re=st.executeQuery(QUERY_SQL);
			String[] contacts=new String[50];
			while(re.next()){
				//todo fetch
			}
			return contacts;
		}catch(ClassNotFoundException|SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
}

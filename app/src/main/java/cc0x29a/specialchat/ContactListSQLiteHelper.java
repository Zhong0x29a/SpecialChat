package cc0x29a.specialchat;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.NotNull;


/**
 *
 *      database    :contact_list.db
 *      table       :contact_list
 *      column(6)   :
 *          index_num       INTEGER,primary key,autoincrement   index
 *          user_id         INTEGER,NOT NULL,UNIQUE
 *          user_name       TEXT
 *          nickname        TEXT
 *          user_phone      INTEGER //new add
 *          status          INTEGER,(def: 0)    // no usage, but reserved
 *
 */

public class ContactListSQLiteHelper extends SQLiteOpenHelper{
	
	/**
	 * Fetch contact list , max num is 50 each time //total just 50, need complete!!
	 * @param db SQLite database
	 * @return a String[][]
	 */
	String[][] fetchContactsList(SQLiteDatabase db){
		String[][] contactsList=new String[51][4];
		int index=0;
		try{
			Cursor cursor=db.query("contact_list",new String[]{"user_id","user_name","nickname","user_phone"},
					null,null,null,null,
					"user_name asc");
			while(cursor.moveToNext() && index<50){
				index++;
				contactsList[index][0]=cursor.getInt(cursor.getColumnIndex("user_id"))+"";
				contactsList[index][1]=cursor.getString(cursor.getColumnIndex("user_name"));
				contactsList[index][2]=cursor.getString(cursor.getColumnIndex("nickname"));
				contactsList[index][3]=cursor.getInt(cursor.getColumnIndex("user_phone"))+"";
			}
			cursor.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		contactsList[0][0]=index+"";
		
		db.close();
		return contactsList;
	}
	
	/**
	 * Fetch user's nickname
	 * @param db db
	 * @param user_id uid
	 * @return nickname in database or user_id
	 */
	String fetchNickname(SQLiteDatabase db,String user_id){
		try{
			Cursor cursor=db.query("contact_list",new String[]{"nickname"},
					"user_id="+user_id,null,null,null,null);
			String nickname;
			cursor.moveToNext();
			nickname=cursor.getString(cursor.getColumnIndex("nickname"));
			cursor.close();
			return nickname;
		}catch(Exception e){
			return user_id;
		}
	}
	
	/**
	 * Insert new contact into Contacts list
	 * @param db SQLite database
	 * @param user_id string user_id
	 * @param user_name string user_name
	 * @param nickname string nickname ,can be null, if is null, nickname=user_name
	 * @param user_phone string user's phone
	 */
	void insertNewContact(SQLiteDatabase db,String user_id,String user_name,String nickname,String user_phone){
		try{
			if(nickname==null||nickname.equals("")){
				nickname=user_name;
			}
			String INSERT_SQL="insert into contact_list (user_id,user_name,nickname,user_phone) " +
					"values ("+
					user_id+",'"+
					user_name+"'," +
					"'"+nickname+"'," +
					user_phone+
					")";
			db.execSQL(INSERT_SQL);
		}catch(SQLException|NullPointerException e){
			e.printStackTrace();
		}
		db.close();
	}
	
	/**
	 * Update contact list (update nickname only , not insert data)
	 * @param db database
	 * @param user_id String
	 * @param nickname String
	 */
	void updateContactList(@NotNull SQLiteDatabase db,String user_id, String nickname){
		Cursor cursor=db.query("contact_list",new String[]{"user_id"},
				"user_id="+user_id,null,null,null,null);
		if(cursor.moveToFirst()){
			cursor.close();
			// update
			String SQL="update contact_list set " +
					"nickname='"+nickname+"' "+
					"where user_id="+user_id;
			db.execSQL(SQL);
		}else{
			cursor.close();
			// insert
			insertNewContact(db,user_id,nickname,nickname,"null");
		}
		db.close();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		String CREATE_TABLE_SQL=
				"create table contact_list(" +
						"index_num INTEGER primary key autoincrement," +
						"user_id INTEGER NOT NULL UNIQUE," +
						"user_name TEXT NOT NULL," +
						"nickname TEXT," +
						"user_phone INTEGER," +
						"status INTEGER" +
						");";
		db.execSQL(CREATE_TABLE_SQL);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
	
	}
	
	ContactListSQLiteHelper(Context context,String name,int version){
		super(context,name,null,version);
	}
	
}

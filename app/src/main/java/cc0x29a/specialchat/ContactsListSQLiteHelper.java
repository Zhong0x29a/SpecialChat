package cc0x29a.specialchat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 *      database    :contacts_list.db3
 *      table       :contacts_list
 *      column(5)   :
 *          index_num       INTEGER,primary key,autoincrement   index
 *          user_id         INTEGER,NOT NULL,UNIQUE
 *          user_name       TEXT
 *          nickname        TEXT
 *          status          INTEGER,(def: 0)    // no usage, but reserved
 *
 */

public class ContactsListSQLiteHelper extends SQLiteOpenHelper{
	ContactsListSQLiteHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
		super(context,name,factory,version);
	}
	
	/**
	 * Fetch contacts list , max num is 49 each time //todo: but total just 49,need complete!!
	 * @param db SQLite database
	 * @return a String[][]
	 */
	String[][] fetchContactsList(SQLiteDatabase db){
		String[][] contactsList=new String[50][3];
		int index=0;
		Cursor cursor=db.query("contacts_list",new String[]{"user_id","user_name","nickname"},
				null,null,null,null,"user_name asc");
		while(cursor.moveToNext()){
			index++;
			contactsList[index][0]=cursor.getInt(cursor.getColumnIndex("user_id"))+"";
			contactsList[index][0]=cursor.getString(cursor.getColumnIndex("user_name"));
			contactsList[index][0]=cursor.getString(cursor.getColumnIndex("nickname"));
		}
		contactsList[0][0]=index+"";
		
		cursor.close();
		return contactsList;
	}
	
	//todo complete
	void insertNewContact(){
	
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		String CREATE_TABLE_SQL=
				"create table contacts_list(" +
						"index_num INTEGER primary key autoincrement," +
						"user_id INTEGER NOT NULL UNIQUE," +
						"user_name TEXT NOT NULL," +
						"nickname TEXT," +
						"status INTEGER" +
						");";
		db.execSQL(CREATE_TABLE_SQL);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
	
	}
}

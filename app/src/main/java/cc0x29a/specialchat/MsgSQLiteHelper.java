package cc0x29a.specialchat;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Message SQLite helper
 * Manager of local cached messages
 *
 *  database    :msg_[user_id].db
 *  table       :msg
 *  column      :
 *      msg_index       INTEGER,primary key,autoincrement   //index
 *      msg_by          INTEGER,NOT NULL
 *      is_read         INTEGER        // read->1, unread->0;
 *      send_time       INTEGER,NOT NULL
 *      msg_content     TEXT,   NOT NULL
 *
 */

public class MsgSQLiteHelper extends SQLiteOpenHelper{
	
	/**
	 * Init the table at first time
	 * @param db SQLite database
	 */
	@Override
	public void onCreate(@NotNull SQLiteDatabase db){
		String CREATE_TABLE_SQL=
				"create table msg("+
				"msg_index INTEGER primary key autoincrement," +
				"msg_by INTEGER NOT NULL,"+    // user_id
				"is_read INTEGER,"+
				"send_time INTEGER NOT NULL,"+
				"msg_content TEXT NOT NULL" +
				")";
		db.execSQL(CREATE_TABLE_SQL);
	}
	
	/**
	 * Insert new message into SQLite.
	 * @param db , the database
	 * @param msg_by , integer, sender
	 * @param send_time , integer, msg send time
	 * @param msg_content , string, msg content
	 */
	void insertNewMsg(@NotNull SQLiteDatabase db,String msg_by,String send_time,
	                  String msg_content){
		String INSERT_NEW_MSG_SQL=
				"insert into msg (msg_index,msg_by,is_read,send_time,msg_content) values(" +
						"null," +
						msg_by+"," +
						"0," +
						send_time+"," +
						"'"+msg_content+"'" +
						")";
		try{
			db.execSQL(INSERT_NEW_MSG_SQL);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Get chat record, start from position ,
	 * 50 pieces is max each time.
	 * @param db , the database.
	 * @param position , count start from 0, so it should be 50*n (n>=0).
	 * @return String[][] record
	 */
	List<String[]> getChatRecord(@NotNull SQLiteDatabase db,int position){
		try{
			Cursor cursor=db.query("msg",
					new String[]{"msg_index","msg_by","is_read","send_time","msg_content"},
					null,null,null,null,
					"msg_index desc");
			List<String[]> data=new ArrayList<>();
			if(cursor.moveToPosition(position)){
				int i=0;
				do{
					String[] temp=new String[5];
					temp[0]=cursor.getInt(cursor.getColumnIndex("msg_index"))+"";
					temp[1]=cursor.getInt(cursor.getColumnIndex("msg_by"))+"";
					temp[2]=cursor.getInt(cursor.getColumnIndex("is_read"))+"";
					temp[3]=cursor.getInt(cursor.getColumnIndex("send_time"))+"";
					temp[4]=MyTools.resolveSpecialChar(cursor.getString(cursor.getColumnIndex("msg_content")));
					data.add(temp);
					i++;
				}while(i < 50 && cursor.moveToNext());
			}
			cursor.close();
			return data;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Fetch latest one message
	 * @param db SQLiteDatabase
	 * @return a String[]
	 */
	String[] getLatestMsg(@NotNull SQLiteDatabase db){
		Cursor cursor=db.query("msg",
				new String[]{"msg_content","send_time"},
				null,null,null,null,
				"send_time desc");
		if(cursor.moveToFirst()){
			String[] temp=new String[2];
			temp[0]=MyTools.resolveSpecialChar(cursor.getString(cursor.getColumnIndex("msg_content")));
			temp[1]=cursor.getInt(cursor.getColumnIndex("send_time"))+"";
			cursor.close();
			return temp;
		}
		return new String[]{"",""};
	}
	
	/**
	 * Delete a message by msg_index
	 * @param db SQLite db
	 * @param msg_index String msg_index
	 */
	void deleteMsg(@NotNull SQLiteDatabase db,String msg_index){
		String DELETE_MSG_SQL=
				"DELETE FROM msg WHERE msg_index="+msg_index;
		
		try{
			db.execSQL(DELETE_MSG_SQL);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	MsgSQLiteHelper(Context context,String name,int version){
		super(context,name,null,version);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
	
	}
	
}


//			lastMsg[0]=cursor.getInt(cursor.getColumnIndex("msg_index"))+"";
//			lastMsg[1]=cursor.getInt(cursor.getColumnIndex("msg_by"))+"";
//			lastMsg[2]=cursor.getInt(cursor.getColumnIndex("is_read"))+"";
//			lastMsg[3]=cursor.getInt(cursor.getColumnIndex("send_time"))+"";
//			lastMsg[4]=cursor.getString(cursor.getColumnIndex("msg_content"))+"";

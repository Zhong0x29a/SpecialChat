package cc0x29a.specialchat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.NotNull;

/**
 *
 *  database    :msg_[user_id].db3
 *  table       :msg
 *  column      :
 *      msg_index       INTEGER,primary key,autoincrement   //index
 *      msg_by          INTEGER,NOT NULL
 *      is_read         INTEGER,NOT NULL
 *      send_time       INTEGER,NOT NULL
 *      msg_content     TEXT,   NOT NULL
 *
 */

public class MsgSQLiteHelper extends SQLiteOpenHelper{
	@Override
	public void onCreate(@NotNull SQLiteDatabase db){
		String CREATE_TABLE_SQL=
				"create table msg("+
				"msg_index INTEGER primary key autoincrement," +
				"msg_by INTEGER NOT NULL,"+    // user_id
				"is_read INTEGER NOT NULL,"+
				"send_time INTEGER NOT NULL,"+
				"msg_content TEXT NOT NULL" +
				")";
		db.execSQL(CREATE_TABLE_SQL);
	}
	
	//todo: a warning here
	/**
	 * Get chat record, 20 pieces is max each time.
	 * @param db , the database.
	 * @param position , count start from 0, so it should be 20*n (n>=0).
	 * @return String[][] record
	 */
	String[][] getChatRecord(@NotNull SQLiteDatabase db,int position){
		String[][] record=new String[21][5];
		int index=0;
		Cursor cursor=db.query("msg",
				new String[]{"msg_index","msg_by","is_read","send_time","msg_content"},
				null,null,null,null,
				"msg_index desc");
		if(cursor.moveToPosition(position)){
			do{
				index++;
				record[index][0]=cursor.getInt(cursor.getColumnIndex("msg_index"))+"";
				record[index][1]=cursor.getInt(cursor.getColumnIndex("msg_by"))+"";
				record[index][2]=cursor.getInt(cursor.getColumnIndex("is_read"))+"";
				record[index][3]=cursor.getInt(cursor.getColumnIndex("send_time"))+"";
				record[index][4]=cursor.getString(cursor.getColumnIndex("msg_content"));
			}while(index <= 20 && cursor.moveToNext());
		}
		cursor.close();
		record[0][0]=index+"";
		return record;
	}
	
	/**
	 * Insert new message into SQLite.
	 * @param db , the database
	 * @param msg_by , integer, sender
	 * @param is_read , integer, read->1, unread->0;
	 * @param send_time , integer, msg send time
	 * @param msg_content , string, msg content
	 */
	void insertNewMsg(@NotNull SQLiteDatabase db,int msg_by,int is_read,int send_time,
	                  String msg_content){
		String INSERT_NEW_MSG_SQL=
				"insert into msg (msg_index,msg_by,is_read,send_time,msg_content) values(" +
						"null," +
						msg_by+"," +
						is_read+"," +
						send_time+"," +
						"'"+msg_content+"'" +
						")";
		db.execSQL(INSERT_NEW_MSG_SQL);
	}
	
	/**
	 * Fetch last message
	 * @param db SQLiteDatabase
	 * @return a String
	 */
	String getLastMsg(@NotNull SQLiteDatabase db){
		Cursor cursor=db.query("msg",
				new String[]{"msg_content"},
				null,null,null,null,
				"send_time desc");
		if(cursor.moveToFirst()){
			String temp=cursor.getString(cursor.getColumnIndex("msg_content"));
			cursor.close();
			return temp;
		}
		return "";
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
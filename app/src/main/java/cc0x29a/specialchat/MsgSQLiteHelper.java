package cc0x29a.specialchat;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.NotNull;

/**
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
	 * Get chat record, 50 pieces is max each time.
	 * @param db , the database.
	 * @param position , count start from 0, so it should be 50*n (n>=0).
	 * @return String[][] record
	 */
	String[][] getChatRecord(@NotNull SQLiteDatabase db,int position){
		String[][] record=new String[51][5];
		int index=0;
		try{
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
					record[index][4]=MyTools.resolveSpecialChar(cursor.getString(cursor.getColumnIndex("msg_content")));
				}while(index < 50 && cursor.moveToNext());
			}
			
		
			cursor.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		record[0][0]=(index)+"";
		return record;
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
		return new String[]{""};
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
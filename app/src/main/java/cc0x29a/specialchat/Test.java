package cc0x29a.specialchat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


//System.currentTimeMillis()/1000

public class Test extends SQLiteOpenHelper{
	public Test(Context context,String name,int version){
		super(context,name,null,version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		String CREATE_TABLE_SQL=""+
				"create table chat_list("+
				"index_num INTEGER primary key "+
				"autoincrement,"+
				"user_id INTEGER NOT NULL UNIQUE,"+
				"nickname TEXT,"+
				"last_chat_time INTEGER NOT NULL"+
				")";
		
		db.execSQL(CREATE_TABLE_SQL);
	}
	
	public String[][] getChatList(SQLiteDatabase db){
		String[][] chatList=new String[50][5];
		chatList[0][4]="0";
		int index=0;
		
		Cursor cursor=db.query("chat_list",
				new String[]{"index_num","user_id","nickname","last_chat_time",},
				null,null,null,null,
				"last_chat_time desc");
		
		while(cursor.moveToNext()){
			chatList[index][0]=cursor.getInt(cursor.getColumnIndex("index_num"))+"";
			chatList[index][1]=cursor.getInt(cursor.getColumnIndex("user_id"))+"";
			chatList[index][2]=cursor.getString(cursor.getColumnIndex("nickname"));
			chatList[index][3]=cursor.getInt(cursor.getColumnIndex("last_chat_time"))+"";
			index++;
		}
		
		chatList[0][4]=index+"";
		
		cursor.close();
		return chatList;
	}
	
	public void insertNewChatListItem(SQLiteDatabase db,int user_id,String nickname,int last_chat_time){
		String SQL_INSERT_NEW_CHAT_LIST_ITEM="insert into chat_list values(" +
				"null," +
				user_id+"," +
				"'"+nickname +"',"+
				last_chat_time+
				")";
		
		db.execSQL(SQL_INSERT_NEW_CHAT_LIST_ITEM);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
	
	}
}

package cc0x29a.specialchat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.NotNull;

/**
 *
 *      database    :chat_list.db3
 *      table       :chat_list
 *      column(5)   :
 *          index_num       INTEGER,primary key,autoincrement   index
 *          user_id         INTEGER,NOT NULL,UNIQUE
 *          nickname        TEXT
 *          last_chat_time  INTEGER,NOT NULL
 *          status          INTEGER,(def: 0)    // new msg num?
 *
 */

public class ChatListSQLiteHelper extends SQLiteOpenHelper{
	@Override
	public void onCreate(@NotNull SQLiteDatabase db){
		String CREATE_TABLE_SQL=
				"create table chat_list("+
				"index_num INTEGER primary key autoincrement,"+
				"user_id INTEGER NOT NULL UNIQUE,"+
				"nickname TEXT,"+
				"last_chat_time INTEGER NOT NULL," +
				"status INTEGER"+
				")";
		db.execSQL(CREATE_TABLE_SQL);
	}
	
	/**
	 * Refresh chat list
	 * @param db    writeable SQLiteDatabase
	 * @param user_id   user_id(friend_id)
	 * @param last_chat_time    last chat time with this friend
	 */
	void refreshChatList(@NotNull SQLiteDatabase db,int user_id,int last_chat_time){
		Cursor cursor=db.query("chat_list",new String[]{"user_id"},
				"user_id="+user_id+"",null,null,null,null);
		if(cursor.moveToFirst()){
			cursor.close();
			String SQL="update chat_list "+
					"set last_chat_time="+last_chat_time+" "+
					"where user_id="+user_id;
			db.execSQL(SQL);
		}else{
			insertNewChatListItem(db,user_id,user_id+"",last_chat_time);
		}
	}
	
	/**
	 * Fetch chat list items.
	 * @param db SQLiteDataBase
	 * @return chat list, String[item number][4]
	 *         [0]->index_num
	 *         [1]->user_id
	 *         [2]->nickname
	 *         [3]->last_chat_time
	 */
	String[][] getChatList(@NotNull SQLiteDatabase db){
		String[][] chatList=new String[50][4];
		int index=0;
		
		Cursor cursor=db.query("chat_list",
				new String[]{"index_num","user_id","nickname","last_chat_time",},
				null,null,null,null,
				"last_chat_time desc");
		if(cursor.moveToFirst()){
			do{
				index++;
				chatList[index][0]=cursor.getInt(cursor.getColumnIndex("index_num"))+"";
				chatList[index][1]=cursor.getInt(cursor.getColumnIndex("user_id"))+"";
				chatList[index][2]=cursor.getString(cursor.getColumnIndex("nickname"));
				chatList[index][3]=cursor.getInt(cursor.getColumnIndex("last_chat_time"))+"";
			}while(cursor.moveToNext());
		}
		chatList[0][0]=index+"";
		
		cursor.close();
		return chatList;
	}
	
	/**
	 * Insert a new chat list item into SQLite
	 * @param db SQLiteDatabase
	 * @param user_id   user id, integer
	 * @param nickname  nickname, String
	 * @param last_chat_time last chat time , integer
	 */
	private void insertNewChatListItem(@NotNull SQLiteDatabase db,int user_id,String nickname,
	                                   int last_chat_time){
		String INSERT_NEW_CHAT_LIST_ITEM_SQL=
				"insert into chat_list (index_num,user_id,nickname,last_chat_time) values(" +
					"null," +
					user_id+"," +
					"'"+nickname +"',"+
					last_chat_time+
					")";
		db.execSQL(INSERT_NEW_CHAT_LIST_ITEM_SQL);
	}
	
	/**
	 * Delete a chat list item by user_id
	 * @param db SQLiteDatabase
	 * @param user_id user's id
	 */
	void deleteChatListItem(@NotNull SQLiteDatabase db,int user_id){
		String DELETE_CHAT_LIST_ITEM_SQL=
				"DELETE FROM chat_list WHERE user_id="+user_id;
		db.execSQL(DELETE_CHAT_LIST_ITEM_SQL);
	}
	
	/**
	 * Delete all items in chat list, equals clear chat list.
	 * @param db SQLiteDatabase
	 */
	void deleteAllChatListItem(@NotNull SQLiteDatabase db){
		String DELETE_CHAT_LIST_ITEM_SQL=
				"DELETE FROM chat_list";
		db.execSQL(DELETE_CHAT_LIST_ITEM_SQL);
	}
	
	ChatListSQLiteHelper(Context context,String name,int version){
		super(context, name, null,version);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
	
	}
	
}

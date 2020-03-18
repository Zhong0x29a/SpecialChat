package cc0x29a.specialchat;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.NotNull;

/**
 *
 * Chat list SQLite cached in local
 *
 *      database    :chat_list.db
 *      table       :chat_list
 *      column(5->6)   :
 *          index_num       INTEGER,primary key,autoincrement   index
 *          user_id         INTEGER,NOT NULL,UNIQUE
 *          nickname        TEXT
 *          last_chat_time  INTEGER
 *          status          INTEGER,(def: 0)    // new msg num?
 *          last_msg        TEXT    //new add 03.17
 *
 */

public class ChatListSQLiteHelper extends SQLiteOpenHelper{
	/**
	 * Init the table at first time
	 * @param db SQLite database
	 */
	@Override
	public void onCreate(@NotNull SQLiteDatabase db){
		String CREATE_TABLE_SQL=
				"create table chat_list("+
				"index_num INTEGER primary key autoincrement,"+
				"user_id INTEGER NOT NULL UNIQUE,"+
				"nickname TEXT,"+
				"last_chat_time INTEGER NOT NULL," +
				"status INTEGER," +
				"last_msg TEXT"+
				")";
		try{
			db.execSQL(CREATE_TABLE_SQL);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Update chat list or add new row
	 * @param db    writeable SQLiteDatabase
	 * @param user_id   user_id(friend_id)
	 * @param last_chat_time    last chat time with this friend
	 * @param last_msg last one msg
	 */
	void updateChatList(@NotNull SQLiteDatabase db,String user_id,String last_chat_time,String last_msg){
		try{
			Cursor cursor=db.query("chat_list",new String[]{"user_id"},
					"user_id="+user_id+"",null,null,null,null);
			if(cursor.moveToFirst()){
				cursor.close();
				String SQL="update chat_list "+
						"set ";
				if(!"".equals(last_chat_time)){
					SQL+="last_chat_time="+last_chat_time+" ";
				}else{
					SQL+="last_chat_time=0 ";
				}
				if(last_msg!=null){
					SQL+=", last_msg='"+last_msg+"' ";
				}
				SQL+="where user_id="+user_id;
				db.execSQL(SQL);
			}else{
				insertNewChatListItem(db,user_id,user_id+"",last_chat_time);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Fetch chat list items. 50 for max each time.
	 * @param db SQLiteDataBase
	 * @return chat list, String[item number][4]
	 *         [0]->index_num
	 *         [1]->user_id
	 *         [2]->nickname
	 *         [3]->last_chat_time
	 *         [4]->last_msg
	 */
	String[][] fetchChatList(@NotNull SQLiteDatabase db,int position){
		String[][] chatList=new String[51][5];
		int index=0;
		try{
			Cursor cursor=db.query("chat_list",
					new String[]{"index_num","user_id","nickname","last_chat_time","last_msg"},
					null,null,null,null,"last_chat_time desc");
			if(cursor.moveToPosition(position)){
				do{
					index++;
					chatList[index][0]=cursor.getInt(cursor.getColumnIndex("index_num"))+"";
					chatList[index][1]=cursor.getInt(cursor.getColumnIndex("user_id"))+"";
					chatList[index][2]=MyTools.resolveSpecialChar(cursor.getString(cursor.getColumnIndex("nickname")));
					chatList[index][3]=cursor.getInt(cursor.getColumnIndex("last_chat_time"))+"";
					chatList[index][4]=MyTools.resolveSpecialChar(cursor.getString(cursor.getColumnIndex("last_msg")));
					
				}while(index<50 && cursor.moveToNext());
			}
			cursor.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		chatList[0][0]=index+"";
		
		return chatList;
	}
	
	/**
	 * Insert a new chat list item into SQLite
	 * SHOULD BE CAREFUL NICKNAME!!!
	 * @param db SQLiteDatabase
	 * @param user_id   user id, integer
	 * @param nickname  nickname, String
	 * @param last_chat_time last chat time , integer
	 */
	void insertNewChatListItem(@NotNull SQLiteDatabase db,String user_id,String nickname,String last_chat_time){
		String INSERT_NEW_CHAT_LIST_ITEM_SQL=
				"insert into chat_list (index_num,user_id,nickname,last_chat_time) values(" +
					"null," +
					user_id+"," +
					"'"+nickname +"',"+
					last_chat_time+
					")";
		try{
			db.execSQL(INSERT_NEW_CHAT_LIST_ITEM_SQL);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Delete a chat list item by user_id
	 * @param db SQLiteDatabase
	 * @param user_id user's id
	 */
	void deleteChatListItem(@NotNull SQLiteDatabase db,String user_id){
		String DELETE_CHAT_LIST_ITEM_SQL=
				"DELETE FROM chat_list WHERE user_id="+user_id;
		
		try{
			db.execSQL(DELETE_CHAT_LIST_ITEM_SQL);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Delete all items in chat list, equals clear chat list.
	 * @param db SQLiteDatabase
	 */
	void deleteAllChatListItem(@NotNull SQLiteDatabase db){
		String DELETE_CHAT_LIST_ITEM_SQL=
				"DELETE FROM chat_list";
		try{
			db.execSQL(DELETE_CHAT_LIST_ITEM_SQL);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
	
	}
	
	ChatListSQLiteHelper(Context context,String name,int version){
		super(context, name, null,version);
	}
	
}

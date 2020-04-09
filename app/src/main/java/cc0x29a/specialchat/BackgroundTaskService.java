package cc0x29a.specialchat;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundTaskService extends Service{
	
	static Timer syncLM;
	static Timer syncCL;
	
	static String user_id;
	static String token_key;
	
	public BackgroundTaskService(){
	}
	
	@Override
	public void onCreate(){
		
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		user_id=preferences.getString("user_id",null);
		token_key=preferences.getString("token_key",null);
		
		syncLM=new Timer();
		syncCL=new Timer();
		
		syncLM.schedule(new TimerTask(){
			@Override
			public void run(){
				syncLatestMsg();
			}
		},20,3000);
		
		syncCL.schedule(new TimerTask(){
			@Override
			public void run(){
				try{
					syncContactsList();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		},8888,25000);
		
		
	}
	
	@Override
	public IBinder onBind(Intent intent){
		// TO DO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public void onDestroy(){
		if(syncCL!=null){syncCL.cancel();}
		if(syncLM!=null){syncLM.cancel();}
	}
	
	/**
	 * Sync the last_msg under chat_list.db
	 */
	private void syncLatestMsg(){
		try{
			ChatListSQLiteHelper cListSQLH=new ChatListSQLiteHelper(this,"chat_list.db",1);
			List<String[]> chatList=cListSQLH.fetchChatList(cListSQLH.getReadableDatabase());
			
			// Fetch last one message then update
			String[] lastMsg;
			String[] temp_d;
			for(int i=0;i<chatList.size();i++){
				temp_d=chatList.get(i);
				MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(this,"msg_"+temp_d[1]+".db",1);
				lastMsg=msgSQLiteHelper.getLatestMsg(msgSQLiteHelper.getReadableDatabase());
				cListSQLH.updateChatList(cListSQLH.getReadableDatabase(),temp_d[1],lastMsg[1],lastMsg[0]);
				msgSQLiteHelper.close();
			}
			
			Intent intent = new Intent();
			intent.putExtra("todo_action", "reLoadChatList");
			intent.setAction("backgroundTask.action");
			sendBroadcast(intent);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Fetch contacts list (sync from server)
	 */
	private void syncContactsList() throws Exception{
		SocketWithServer socket=new SocketWithServer();
		if(user_id==null || token_key==null){
			return;
		}
		
		String DataSend="{" +
				"'client':'SCC-1.0'," +
				"'action':'0010'," +
				"'user_id':'"+user_id+"'," +
				"'token_key':'"+token_key+"'," +
				"\"timestamp\":\""+MyTools.getCurrentTime()+"\"" +
				"}";
		
		@SuppressLint("HandlerLeak")
		Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg){
				try{
					JSONObject data=new JSONObject(msg.obj.toString());
					
					ContactListSQLiteHelper helper=new ContactListSQLiteHelper(BackgroundTaskService.this,"contact_list.db",1);
					ChatListSQLiteHelper helper2=new ChatListSQLiteHelper(BackgroundTaskService.this,"chat_list.db",1);
					
					// parse data;
					if(data.getString("status").equals("true")){
						for(int i=1;i<=Integer.parseInt(data.getString("number"));i++){
							// Save/update SQLite data
							JSONObject temp=new JSONObject(data.getString("index_"+i));
							helper.updateContactList(helper.getReadableDatabase(),temp.getString("user_id"),temp.getString("nickname"));
							helper2.fixNickname(helper2.getReadableDatabase(),temp.getString("user_id"),temp.getString("nickname"));
						}
						
						// Send broadcast to MainActivity.
						Intent intent = new Intent();
						intent.putExtra("todo_action", "reLoadContactList");
						intent.setAction("backgroundTask.action");
						sendBroadcast(intent);
						
						Intent intent2 = new Intent();
						intent2.putExtra("todo_action", "reLoadChatList");
						intent2.setAction("backgroundTask.action");
						sendBroadcast(intent2);
					}
					
				}catch(JSONException e){
					e.printStackTrace();
				}
				
			}
		};
		
		// Start socket.
//		JSONObject data=socket.startSocket(DataSend);
		socket.startSocket(DataSend,handler);
		
//		ContactListSQLiteHelper helper=new ContactListSQLiteHelper(this,"contact_list.db",1);
//		ChatListSQLiteHelper helper2=new ChatListSQLiteHelper(this,"chat_list.db",1);
//
//		// parse data;
//		if(data!=null && data.getString("status").equals("true")){
//			for(int i=1;i<=Integer.parseInt(data.getString("number"));i++){
//				// Save/update SQLite data
//				JSONObject temp=new JSONObject(data.getString("index_"+i));
//				helper.updateContactList(helper.getReadableDatabase(),temp.getString("user_id"),temp.getString("nickname"));
//				helper2.fixNickname(helper2.getReadableDatabase(),temp.getString("user_id"),temp.getString("nickname"));
//			}
//
//			// Send broadcast to MainActivity.
//			Intent intent = new Intent();
//			intent.putExtra("todo_action", "reLoadContactList");
//			intent.setAction("backgroundTask.action");
//			sendBroadcast(intent);
//
//			Intent intent2 = new Intent();
//			intent2.putExtra("todo_action", "reLoadChatList");
//			intent2.setAction("backgroundTask.action");
//			sendBroadcast(intent2);
//
//		}
		
	}
	
}

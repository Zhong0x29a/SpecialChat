package cc0x29a.specialchat;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import org.json.JSONObject;

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
//		Toast.makeText(this,"service started",Toast.LENGTH_SHORT).show();
		
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
		},200,5000);
		
		syncCL.schedule(new TimerTask(){
			@Override
			public void run(){
				try{
					syncContactsList();
				}catch(Exception e){
//					e.printStackTrace();
				}
			}
		},6666,60000);
		
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
			String[][] chatList=cListSQLH.fetchChatList(cListSQLH.getReadableDatabase(),0);
			
			// Fetch last one message then update
			String[] lastMsg;
			for(int i=1;i<=(Integer.parseInt(chatList[0][0]))&&i<50;i++){
				MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(this,"msg_"+chatList[i][1]+".db",1);
				lastMsg=msgSQLiteHelper.getLatestMsg(msgSQLiteHelper.getReadableDatabase());
				cListSQLH.updateChatList(cListSQLH.getReadableDatabase(),chatList[i][1],lastMsg[1],lastMsg[0]);
			}
			
			Intent intent = new Intent();
			intent.putExtra("todo_action", "reLoadChatList");
			intent.setAction("location.backgroundTask.action");
			sendBroadcast(intent);
			
			
//			ChatListSQLiteHelper chatListSQLiteHelper=
//					new ChatListSQLiteHelper(BackgroundTaskService.this,"chat_list.db",1);
			/*
			 * chatList[0][0]    -> total number
			 * chatList[index][0] -> index (index>0)
			 * chatList[index][1] -> user_id
			 * chatList[index][2] -> nickname
			 * chatList[index][3] -> last_chat_time
			 * */
//			chatList=chatListSQLiteHelper.fetchChatList(chatListSQLiteHelper.getReadableDatabase(),0);
			
//			MainActivity.adapter.updateData(chatList);
			//		System.out.println("synced");
//			MainActivity.reloadChatList();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Fetch contacts list (sync from server)
	 */
	private void syncContactsList() throws Exception{
		SocketWithServer socket=new SocketWithServer();
		socket.DataSend="{" +
				"'client':'SCC-1.0'," +
				"'action':'0010'," +
				"'user_id':'"+user_id+"'," +
				"'token_key':'"+token_key+"'" +
				"\"timestamp\":\""+MyTools.getCurrentTime()+"\"" +
				"}";
		
		JSONObject data=socket.startSocket();
		
		ContactListSQLiteHelper helper=new ContactListSQLiteHelper(this,"contact_list.db",1);
		
		// parse data;
		if(data!=null && data.getString("status").equals("true")){
			for(int i=1;i<=Integer.parseInt(data.getString("number"));i++){
				helper.updateContactList(helper.getReadableDatabase(),data.getString("user_id"),data.getString("nickname"));
				
			}
			
			Intent intent = new Intent();
			intent.putExtra("todo_action", "reLoadContactList");
			intent.setAction("location.backgroundTask.action");
			sendBroadcast(intent);
		}
		
//		for(int i=0;i<=10;i++){
//
//		}
		
	}

}

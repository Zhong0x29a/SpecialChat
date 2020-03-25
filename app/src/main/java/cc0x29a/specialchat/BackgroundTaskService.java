package cc0x29a.specialchat;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundTaskService extends Service{
	
	static Timer syncLM;
	static Timer syncCL;
	
	static Timer refreshMsgTimer;
	
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
		
		refreshMsgTimer=new Timer();
		
		// Refresh new message(s) per 5.888 seconds.
		refreshMsgTimer.schedule(new TimerTask(){
			@Override
			public void run(){
				try{
					if(refreshNewMsg()==1){
						// if network is not so fine...
						//showToast("!Poor Network... :(",Toast.LENGTH_SHORT);
					}
				}catch(JSONException e){
					//e.printStackTrace();
				}
			}
		},1700,5000);
		
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
		if(refreshMsgTimer!=null){refreshMsgTimer.cancel();}
	}
	
	/**
	 * Refresh New Message(s)
	 * send{
	 *     client:SCC-1.0,
	 *     action:0003,
	 *     user_id:[user_id],
	 *     token_key:[token_key]
	 * }
	 *
	 * return{
	 *     is_new_msg:[true|false],
	 *     new_msg_num:[new_message_number],    // 50 pieces MAX !
	 *     // below data sort by time, the oldest on top !!
	 *     index_1:{      //里面用单引号！！
	 *         user_id:[user_id],
	 *         send_time:[send_time],
	 *         msg_content:[msg_content]
	 *     }
	 *     index_2:{
	 *         user_id:[user_id],
	 *         send_time:[send_time],
	 *         msg_content:[msg_content]
	 *     }
	 *     index_...:{
	 *         ...
	 *     }
	 *     ...
	 * }
	 *
	 * @return int,
	 *      0->Have new msg
	 *      1->Network error
	 *      2->No new msg
	 *      3->Unknown Error..
	 *
	 */
	private int refreshNewMsg() throws JSONException{
		String jsonMsg;
		if(user_id != null && token_key != null){
			jsonMsg="{" +
					"\"client\":\"SCC-1.0\"," +
					"\"action\":\"0003\"," +
					"\"user_id\":\""+user_id+"\"," +
					"\"token_key\":\""+token_key+"\"," +
					"\"timestamp\":\""+MyTools.getCurrentTime()+"\"" +
					"}";
		}else{
			jsonMsg="{}";
		}
		SocketWithServer SWS=new SocketWithServer();
		SWS.DataSend=jsonMsg;
		JSONObject data=SWS.startSocket();
		
		if(data==null){
			System.out.println("Network ERROR! ");
			return 1;
		}else if(data.getString("is_new_msg").equals("true")){
			int new_msg_num=Integer.parseInt(data.getString("new_msg_num"));
			for(int i=1;i<=new_msg_num;i++){
				JSONObject jsonTemp=new JSONObject(data.getString("index_"+i));
				String friend_id=jsonTemp.getString("user_id");
				String send_time=jsonTemp.getString("send_time");
				
				MsgSQLiteHelper mh=new MsgSQLiteHelper(BackgroundTaskService.this,"msg_"+friend_id+".db",1);
				mh.insertNewMsg(mh.getReadableDatabase(),friend_id,send_time,jsonTemp.getString("msg_content"));
				
				ChatListSQLiteHelper clh=new ChatListSQLiteHelper(BackgroundTaskService.this,"chat_list.db",1);
				clh.updateChatList(clh.getReadableDatabase(),friend_id,send_time,jsonTemp.getString("msg_content"));
			}
			
			Intent intent = new Intent();
			intent.putExtra("todo_action", "reLoadChatList");
			intent.setAction("location.backgroundTask.action");
			sendBroadcast(intent);
			
			return 0;
		}else if(data.getString("is_new_msg").equals("false")){
			return 2;
		}else{
			return 3;
		}
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
			}
			
			Intent intent = new Intent();
			intent.putExtra("todo_action", "reLoadChatList");
			intent.setAction("location.backgroundTask.action");
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
		socket.DataSend="{" +
				"'client':'SCC-1.0'," +
				"'action':'0010'," +
				"'user_id':'"+user_id+"'," +
				"'token_key':'"+token_key+"'," +
				"\"timestamp\":\""+MyTools.getCurrentTime()+"\"" +
				"}";
		
		// Start socket.
		JSONObject data=socket.startSocket();
		
		ContactListSQLiteHelper helper=new ContactListSQLiteHelper(this,"contact_list.db",1);
		
		// parse data;
		if(data!=null && data.getString("status").equals("true")){
			for(int i=1;i<=Integer.parseInt(data.getString("number"));i++){
				// Save to/update SQLite data
				JSONObject temp=new JSONObject(data.getString("index_"+i));
				helper.updateContactList(helper.getReadableDatabase(),temp.getString("user_id"),temp.getString("nickname"));
			}
			
			// Send broadcast to MainActivity.
			Intent intent = new Intent();
			intent.putExtra("todo_action", "reLoadContactList");
			intent.setAction("location.backgroundTask.action");
			sendBroadcast(intent);
		}
		
	}
	
	
}

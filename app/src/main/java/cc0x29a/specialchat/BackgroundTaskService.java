package cc0x29a.specialchat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

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
						MyTools.showToast(BackgroundTaskService.this,"!Poor Network... :(",Toast.LENGTH_SHORT);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		},1700,3666);
		
		syncLM.schedule(new TimerTask(){
			@Override
			public void run(){
				syncLatestMsg();
			}
		},20,2888);
		
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
	
	// notification values
	private static final int PUSH_NOTIFICATION_ID = (0x001);
	private static final String PUSH_CHANNEL_ID = "PUSH_NOTIFY_ID";
	private static final String PUSH_CHANNEL_NAME = "PUSH_NOTIFY_NAME";
	/**
	 * Show a new message notification.
	 * @param user_id uid
	 * @param nickname nickname
	 * @param msg_content content
	 */
	private void showNewMsgNotification(String user_id,String nickname,String msg_content,String send_time) {
		NotificationManager notificationManager = (NotificationManager) BackgroundTaskService.this.getSystemService(NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
			if (notificationManager != null) {
				notificationManager.createNotificationChannel(channel);
			}
		}
		
		Intent intent = new Intent(this, ChatActivity.class);
		
		// Start chat activity, send user_id and ta's nickname by bundle
		Bundle bundle=new Bundle();
		bundle.putString("user_id", user_id);
		bundle.putString("nickname",nickname);
		intent.putExtras(bundle);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT,bundle);
		
		RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.new_msg_notification);
		
		remoteViews.setImageViewResource(R.id.new_msg_notification_profile, R.mipmap.ic_launcher);
		remoteViews.setTextViewText(R.id.new_msg_notification_title, nickname);
		remoteViews.setTextViewText(R.id.new_msg_notification_msg, msg_content);
		remoteViews.setTextViewText(R.id.new_msg_notification_time,send_time);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		Intent notificationIntent = new Intent(this, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		builder.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.mipmap.ic_launcher)
				.setChannelId(PUSH_CHANNEL_ID)
				.setDefaults(Notification.DEFAULT_ALL)
				.setContent(remoteViews)
				.setContentIntent(contentIntent)
				.setCustomBigContentView(remoteViews);
		
		Notification notification = builder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		if (notificationManager != null) {
			notificationManager.notify(PUSH_NOTIFICATION_ID, notification);
		}
		
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
	 *      3->No login info
	 *      4->Unknown Error..
	 *
	 */
	private int refreshNewMsg() throws JSONException{
		String jsonMsg;
		JSONObject data;
		SocketWithServer SWS=new SocketWithServer();
		
		if(user_id != null && token_key != null){
			jsonMsg="{" +
					"\"client\":\"SCC-1.0\"," +
					"\"action\":\"0003\"," +
					"\"user_id\":\""+user_id+"\"," +
					"\"token_key\":\""+token_key+"\"," +
					"\"timestamp\":\""+MyTools.getCurrentTime()+"\"" +
					"}";
			SWS.DataSend=jsonMsg;
			data=SWS.startSocket();
			
		}else{
			return 3;
		}
		
		if(data==null){
			System.out.println("Network ERROR! ");
			return 1;
		}else if(data.getString("is_new_msg").equals("true")){
			int new_msg_num=Integer.parseInt(data.getString("new_msg_num"));
			for(int i=1;i<=new_msg_num;i++){
				JSONObject jsonTemp=new JSONObject(data.getString("index_"+i));
				String friend_id=jsonTemp.getString("user_id");
				String send_time=jsonTemp.getString("send_time");
				String msg_content=jsonTemp.getString("msg_content");
				
				MsgSQLiteHelper mh=new MsgSQLiteHelper(BackgroundTaskService.this,"msg_"+friend_id+".db",1);
				mh.insertNewMsg(mh.getReadableDatabase(),friend_id,send_time,msg_content);
				mh.close();
				
				ChatListSQLiteHelper clh=new ChatListSQLiteHelper(BackgroundTaskService.this,"chat_list.db",1);
				clh.updateChatList(clh.getReadableDatabase(),friend_id,send_time,msg_content);
				clh.close();
				
				String[] new_data=new String[]{"",friend_id,"",send_time,MyTools.resolveSpecialChar(msg_content)};
				
				// send broadcast to ChatActivity
				Intent intent2 = new Intent();
				intent2.putExtra("todo_action", "updateChatRecord"); // ChatActivity
				intent2.putExtra("new_record",new_data);
				intent2.setAction("backgroundTask.action.chatActivity."+friend_id);
				sendBroadcast(intent2);
				
				showNewMsgNotification(friend_id,friend_id,MyTools.resolveSpecialChar(msg_content),MyTools.formatTime(send_time)); // todo nickname not set
			}
			
			// send broadcast to MainActivity
			Intent intent = new Intent();
			intent.putExtra("todo_action", "reLoadChatList");
			intent.setAction("backgroundTask.action");
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
				msgSQLiteHelper.close();
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
		if(user_id==null || token_key==null){
			return;
		}
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
		ChatListSQLiteHelper helper2=new ChatListSQLiteHelper(this,"chat_list.db",1);
		
		// parse data;
		if(data!=null && data.getString("status").equals("true")){
			for(int i=1;i<=Integer.parseInt(data.getString("number"));i++){
				// Save to/update SQLite data
				JSONObject temp=new JSONObject(data.getString("index_"+i));
				helper.updateContactList(helper.getReadableDatabase(),temp.getString("user_id"),temp.getString("nickname"));
				helper2.fixNickname(helper2.getReadableDatabase(),temp.getString("user_id"),temp.getString("nickname"));
			}
			
			// Send broadcast to MainActivity.
			Intent intent = new Intent();
			intent.putExtra("todo_action", "reLoadContactList");
			intent.setAction("location.backgroundTask.action");
			sendBroadcast(intent);
			
			Intent intent2 = new Intent();
			intent2.putExtra("todo_action", "reLoadChatList");
			intent2.setAction("backgroundTask.action");
			sendBroadcast(intent2);
			
		}
		
	}
	
}

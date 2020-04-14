package cc0x29a.specialchat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class NetworkService extends Service{
	public NetworkService(){
	}
	
	
	static String user_id;
	static String token_key;
	
	static Timer refreshMsgTimer;
	
	@Override
	public void onCreate(){
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		user_id=preferences.getString("user_id",null);
		token_key=preferences.getString("token_key",null);
		
		// Refresh new message(s) per 3.666 seconds.
		refreshMsgTimer=new Timer();
		refreshMsgTimer.schedule(new TimerTask(){
			@Override
			public void run(){
				try{
					refreshNewMsg();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		},1700,3666);
		
	}
	
	public void onDestroy(){
		if(refreshMsgTimer!=null){refreshMsgTimer.cancel();}
	}
	
	@Override
	public IBinder onBind(Intent intent){
		// TO DO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
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
	private void refreshNewMsg(){
		if(user_id==null || token_key==null){
			return;
		}
		
		new Thread(new Runnable(){
			@Override
			public void run(){
				String DataSend="{" +
						"\"client\":\"SCC-1.0\"," +
						"\"action\":\"0003\"," +
						"\"user_id\":\""+user_id+"\"," +
						"\"token_key\":\""+token_key+"\"," +
						"\"timestamp\":\""+MyTools.getCurrentTime()+"\"" +
						"}";
				final String dataStr=new__NetworkService.sendData(DataSend);
//				Looper.prepare();
//				new Handler().post(new Runnable(){
//					@Override
//					public void run(){
				try{
					JSONObject data=new JSONObject(dataStr);
					if(data.getString("is_new_msg").equals("true")){
						int new_msg_num=Integer.parseInt(data.getString("new_msg_num"));
						ChatListSQLiteHelper clh=new ChatListSQLiteHelper(NetworkService.this,"chat_list.db",1);
						for(int i=1;i<=new_msg_num;i++){
							JSONObject jsonTemp=new JSONObject(data.getString("index_"+i));
							String friend_id=jsonTemp.getString("user_id");
							String send_time=jsonTemp.getString("send_time");
							String msg_content=jsonTemp.getString("msg_content");
							
							// insert data to database
							MsgSQLiteHelper mh=new MsgSQLiteHelper(NetworkService.this,"msg_"+friend_id+".db",1);
							mh.insertNewMsg(mh.getReadableDatabase(),friend_id,send_time,msg_content);
							
							mh.close();
							
							SQLiteDatabase chat_list_db=clh.getReadableDatabase();
							// update info.
							clh.updateChatList(chat_list_db,friend_id,send_time,msg_content);
							// fetch nickname.
							String ta_nickname=clh.fetchNickname(chat_list_db,friend_id);
							// release resource
							chat_list_db.close();
							
							String[] new_data=new String[]{"",friend_id,"",send_time,MyTools.resolveSpecialChar(msg_content)};
							
							// Send broadcast to ChatActivity
							Intent intent2=new Intent();
							intent2.putExtra("todo_action","updateChatRecord"); // ChatActivity
							intent2.putExtra("new_record",new_data);
							intent2.setAction("backgroundTask.action.chatActivity."+friend_id);
							sendBroadcast(intent2);
							
							showNewMsgNotification(friend_id,ta_nickname,MyTools.resolveSpecialChar(msg_content),MyTools.formatTime(send_time));
							
						}
						
						clh.close();
						
						// Send broadcast to MainActivity
						Intent intent=new Intent();
						intent.putExtra("todo_action","reLoadChatList");
						intent.setAction("backgroundTask.action");
						sendBroadcast(intent);
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
//					}
//				});
//				Looper.loop();
			}
		}).start();
	}
	
	// Notification info
	private static final int PUSH_NOTIFICATION_ID = (0x001);
	private static final String PUSH_CHANNEL_ID = "PUSH_NOTIFY_NEW_MESSAGE_ID";
	private static final String PUSH_CHANNEL_NAME = "PUSH_NOTIFY_NEW_MESSAGE";
	/**
	 * Show a new message notification.
	 * @param user_id uid
	 * @param nickname nickname
	 * @param msg_content content
	 */
	private void showNewMsgNotification(String user_id,String nickname,String msg_content,String send_time) {
		NotificationManager notificationManager = (NotificationManager) NetworkService.this.getSystemService(NOTIFICATION_SERVICE);
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
		
		remoteViews.setImageViewResource(R.id.new_msg_notification_profile, R.mipmap.logo2);
		remoteViews.setTextViewText(R.id.new_msg_notification_title, nickname);
		remoteViews.setTextViewText(R.id.new_msg_notification_msg, msg_content);
		remoteViews.setTextViewText(R.id.new_msg_notification_time, send_time);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		Intent notificationIntent = new Intent(this, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		builder.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.mipmap.logo2)
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
	
}

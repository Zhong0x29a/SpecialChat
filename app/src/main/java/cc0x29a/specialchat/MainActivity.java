package cc0x29a.specialchat;

/*

  author:  Zhong Wenliang
  mail:    cuberwenliang@163.com
  date:    March, 2020
 
  **/

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{
	
	static Timer checkLoginTimer;
	static Timer refreshMsgTimer;
	
	Handler toastHandler=new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// todo: this can set a lunch page !!
		setContentView(R.layout.activity_main);
	
		//test code

		//test code
		
	}
	
	//todo what about this??
	/**
	 * 获取控件的高度或者宽度  isHeight=true则为测量该控件的高度，isHeight=false则为测量该控件的宽度
	 * @param view
	 * @param isHeight
	 * @return
	 */
	public static int getViewHeight(View view, boolean isHeight){
		int result;
		if(view==null)return 0;
		if(isHeight){
			int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
			view.measure(h,0);
			result =view.getMeasuredHeight();
		}else{
			int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
			view.measure(0,w);
			result =view.getMeasuredWidth();
		}
		return result;
	}
	
	// todo： this still empty
	@Override
	protected void onStart(){
		super.onStart();
		init();
		//changeView(1);
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
		normalMode();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		cancelRefreshTimers();
	}
	
	// todo : edit the text on the right(完善注释)    next method -> normalMode()
	private void init(){
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		if(preferences.getInt("is_login",0)!=1){
			changeView(1);
		}else if(preferences.getInt("is_login",0)==1){
			normalMode();
		}
	}
	
	// todo complete
	private void normalMode(){
		cancelRefreshTimers();
		
		// set timer tasks
		checkLoginTimer=new Timer();
		refreshMsgTimer=new Timer();
		
		// Check login status per 2 minutes.
		checkLoginTimer.schedule(new TimerTask(){
			@Override public void run(){
				try{
					if(checkLogin()==2){
						changeView(1);
					}else if(checkLogin()==1){
						showToast("Ohh! Poor Network... :(",Toast.LENGTH_LONG);
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
		},17,120000);
		
		// Refresh new message(s) per 5.888 seconds.
		refreshMsgTimer.schedule(new TimerTask(){
			@Override
			public void run(){
				try{
					if(refreshNewMsg()==1){
						// if network is not so ok...
						// 套娃就很皮..哈哈
						showToast("!Poor Network... :(",Toast.LENGTH_SHORT);
						refreshMsgTimer.cancel();
						refreshMsgTimer=new Timer();
						refreshMsgTimer.schedule(new TimerTask(){
							@Override
							public void run(){
								try{
									if(refreshNewMsg()==1){
										showToast("): ...Network Poor!",Toast.LENGTH_LONG);
									}else{
										normalMode();
									}
								}catch(JSONException e){
									e.printStackTrace();
								}
							}
						},1700,23333);
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
		},1700,5888);
		
		//loadChatList();
	}
	
	//todo little menu
	/**
	 * Load ListView by Adapter
	 * */
	private void loadChatList(){
		ChatListSQLiteHelper chatListSQLiteHelper=
				new ChatListSQLiteHelper(MainActivity.this,"chat_list.db3",1);
		final String[][] chatList=chatListSQLiteHelper.getChatList(chatListSQLiteHelper.getReadableDatabase());
		
		// Fetch last one message.
		String[] lastMsg=new String[50];
		for(int i=1;i<= (Integer.parseInt(chatList[0][0])) ;i++){
			MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(MainActivity.this,
					"msg_"+chatList[i][1]+".db3",1);
			lastMsg[i]=msgSQLiteHelper.getLastMsg(msgSQLiteHelper.getReadableDatabase());
		}
		
		ListView main_list_view=findViewById(R.id.main_list_view);
		ChatListItemAdapter chatListItemAdapter=new ChatListItemAdapter(MainActivity.this);
		chatListItemAdapter.chatListInfo=chatList;
		chatListItemAdapter.lastMsg=lastMsg;
		chatListItemAdapter.count=Integer.parseInt(chatList[0][0]); // item 数量
		main_list_view.setAdapter(chatListItemAdapter);
		
		main_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent,View view,int position,long id){
				Intent intent=new Intent(MainActivity.this,ChatActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("user_id", chatList[position][1]);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		main_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id){
				// todo: position (int) ,open a little menu,to delete chat or so on.
				return true;
			}
		});
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
	 *     index_[1]:{
	 *         user_id:[user_id],
	 *         send_time:[send_time]
	 *         msg_content:[msg_content]
	 *     }
	 *     index_[2]:{
	 *         user_id:[user_id],
	 *         send_time:[send_time]
	 *         msg_content:[msg_content]
	 *     }
	 *     index_[...]:{
	 *         ...
	 *     }
	 *     ...
	 * }
	 *
	 * @return int,
	 *      0->Have new msg
	 *      1->Network error
	 *      2->No new msg
	 *
	 * Todo: this can be optimized !! (1.combine msg)
 	 */
	private int refreshNewMsg() throws JSONException{
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		String user_id=preferences.getString("user_id",null);
		String token_key=preferences.getString("token_key",null);
		
		String jsonMsg="{" +
				"\"client\":\"SCC-1.0\"," +
				"\"action\":\"0003\"," +
				"\"user_id\":\""+user_id+"\"," +
				"\"token_key\":\""+token_key+"\"," +
				"\"timestamp\":\""+MyTools.getCurrentTime()+"\"" +
				"}";
		SocketWithServer SWS=new SocketWithServer();
		SWS.DataSend=jsonMsg;
		SWS.startSocket();
		
		if(SWS.DataJsonReturn==null){
			System.out.println("Network ERROR! ");
			return 1;
		}else if(SWS.DataJsonReturn.getString("is_new_msg").equals("true")){
			int new_msg_num=Integer.parseInt(SWS.DataJsonReturn.getString("new_msg_num"));
			for(int i=1;i<=new_msg_num;i++){
				JSONObject jsonTemp=SWS.DataJsonReturn.getJSONObject(SWS.DataJsonReturn.getString("index_"+i));
				int friend_id=jsonTemp.getInt("user_id");
				int send_time=jsonTemp.getInt("send_time");
				
				MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(MainActivity.this,
						"msg_"+friend_id+".db3",1);
				msgSQLiteHelper.insertNewMsg(msgSQLiteHelper.getReadableDatabase(),
						friend_id,0,
						send_time,jsonTemp.getString("msg_content"));
				
				ChatListSQLiteHelper chatListSQLiteHelper=new ChatListSQLiteHelper(
						MainActivity.this,"chat_list.db3",1);
				chatListSQLiteHelper.refreshChatList(chatListSQLiteHelper.getReadableDatabase(),
						friend_id,"",send_time);
			}
			loadChatList();
			return 0;
		}else{
			return 2;
		}
	}
	
	/**
	 * Check login status.
	 * @return int 0->good;1->network;2->bad.
	 *
	 * Server return:
	 *      {"status":"true"|"false"}
	 * **/
	private int checkLogin() throws JSONException{
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		String user_id=preferences.getString("user_id",null);
		String token_key=preferences.getString("token_key",null);
		
		String jsonMsg="{" +
				"\"client\":\"SCC-1.0\"," +
				"\"action\":\"0001\"," +
				"\"user_id\":\""+user_id+"\"," +
				"\"token_key\":\""+token_key+"\"," +
				"\"timestamp\":\""+MyTools.getCurrentTime()+"\"" +
				"}";
		SocketWithServer SWS=new SocketWithServer();
		SWS.DataSend=jsonMsg;
		SWS.startSocket();
		
		if(SWS.DataJsonReturn==null){
			return 1;
		}else if(SWS.DataJsonReturn.getString("status").equals("true")){
			if(preferences.getInt("is_login",0)!=1){
				SharedPreferences.Editor editor=preferences.edit();
				editor.putInt("is_login",1);
				editor.apply();
			}
			return 0;
		}else{
			SharedPreferences.Editor editor=preferences.edit();
			editor.putInt("is_login",0);
			editor.apply();
			return 2;
		}
	}
	
	/**
	 * Cancel unused timers.
	 */
	private void cancelRefreshTimers(){
		if(checkLoginTimer!=null){checkLoginTimer.cancel();}
		if(refreshMsgTimer!=null){refreshMsgTimer.cancel();}
	}
	
	/**
	 * Change content layout
	 * @param toPage
	 *        0 -> main page
	 *        1 -> font login page
	 * **/
	private void changeView(final int toPage){
		MainActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				if(toPage==0){ //main page
					findViewById(R.id.font_login_linear_layout).setVisibility(View.GONE);
					findViewById(R.id.main_linear_chat_list).setVisibility(View.VISIBLE);
				}else if(toPage==1){ //login
					findViewById(R.id.font_login_linear_layout).setVisibility(View.VISIBLE);
					findViewById(R.id.main_linear_chat_list).setVisibility(View.GONE);
					findViewById(R.id.btn_front_login).setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View v){
							goToLogin();
						}
					});
					cancelRefreshTimers();
					Toast.makeText(MainActivity.this,"Maybe you haven't login yet? ",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	/**
	 * Start LoginActivity
	 */
	private void goToLogin(){
		cancelRefreshTimers();
		startActivity(new Intent(MainActivity.this,LoginActivity.class));
	}
	
	/**
	 * make a simple toast
	 * @param info information to show
	 */
	public void showToast(final String info,final int duration) {
		toastHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), info,
						+duration).show();
			}
		});
	}
	
}


/*
* 		RSATools RSAT=new RSATools();
		byte[] raw_data=new byte[]{9,12,3};
		byte[] encrypted=RSAT.encryptData(raw_data);
		byte[] decrypted=RSAT.decryptData(encrypted);
		System.out.println("raw_data:"+raw_data+"\nencrypted:"+encrypted);
		System.out.println("\ndecrypted:"+decrypted);
* */
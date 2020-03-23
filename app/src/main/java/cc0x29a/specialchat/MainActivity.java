package cc0x29a.specialchat;

/*
* MainActivity.class
*
* Author:       Zhong Wenliang
* mail:         CuberWenliang@0x29a.cc
* start date:   March, 2020
*
* */


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{
	
	static String user_id;
	static String token_key;
	
	LocationReceiver locationReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// next ver to do: this can set a lunch page !!
		setContentView(R.layout.activity_main);
		
		// This would run at the very first lunch.
		welcomePage();
		
		// UI views init
		init();
		
		
		//test codes
/*		String a="{\"a1\":\"abc1\",\"b\":\"{'c1':'cde1'}\"}";
//		try{
//			JSONObject json=new JSONObject(a);
//			System.out.println(json.getString("a1"));
//			System.out.println(json.getString("b"));
//			String b=json.getString("b");
//			JSONObject json2=new JSONObject(b);
//			System.out.println(json2.getString("c1"));
//		}catch(JSONException e){
//			e.printStackTrace();
//		}
		//		MsgSQLiteHelper h=new MsgSQLiteHelper(this,"msg_1123592075.db",1);
//		for(int i=1;i<=23;i++){
//			h.insertNewMsg(h.getReadableDatabase(),1123592075+"",i+"",i+" I love you.");
//			ChatListSQLiteHelper c=new ChatListSQLiteHelper(this,"chat_list.db",1);
//			c.insertNewChatListItem(c.getReadableDatabase(),"4091"+i,"Little hao","2"+MyTools.getCurrentTime());
			
//			c.insertNewChatListItem(c.getReadableDatabase(),"1123592075","Little hao",""+MyTools.getCurrentTime());
//		}
		ContactListSQLiteHelper c=new ContactListSQLiteHelper(MainActivity.this,"contact_list.db",1);
		c.insertNewContact(c.getReadableDatabase(),"1123592075","Apple2","Haaaa pi","13360417480");
//		String[][] a=new String[][]{{"a","b","a","s"},{"a","b","c"},{"a","b","c"}};
//		System.out.println(a.length);
		finish();*/
		//test codes end
		
		
	}
	
	// choose whether to redirect page
	@Override
	protected void onStart(){
		super.onStart();
		 redirect();
		
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		user_id=preferences.getString("user_id",null);
		token_key=preferences.getString("token_key",null);
		 
		// listen messages from background task service
		locationReceiver = new LocationReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("location.backgroundTask.action");
		registerReceiver(locationReceiver, filter);
	}
	
	// run normalMode()
	@Override
	protected void onRestart(){
		super.onRestart();
		normalMode();
	}
	
	// stop background tasks service
	@Override
	protected void onStop(){
		super.onStop();
		stopService(new Intent(this,BackgroundTaskService.class));
		try{
			unregisterReceiver(locationReceiver);
		}catch(Exception e){
			//
		}
	}
	
	// clear timers & stop background tasks service
	@Override
	protected void onDestroy(){
		super.onDestroy();
		cancelRefreshTimers();
		stopService(new Intent(this,BackgroundTaskService.class));
		try{
			unregisterReceiver(locationReceiver);
		}catch(Exception e){
			//
		}
	}
	
	// Communicate with BackgroundTaskService.
	public class LocationReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context,Intent intent) {
			String intentAction = intent.getAction();
			if(null!=intentAction && intentAction.equals("location.backgroundTask.action")){
				if("reLoadChatList".equals(intent.getStringExtra("todo_action"))){
					reloadChatList();
				}else if("reLoadContactList".equals(intent.getStringExtra("todo_action"))){
					loadContactList();
				}
			}
		}
	}
	
	// set action menu.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.main_top_bar, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * the action menu.
	 *
	 * @param item menu item
	 * @return boolean
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.app_bar_search:
				startActivity(new Intent(MainActivity.this,SearchNewContact.class));
				cancelRefreshTimers();
				return true;
			case R.id.app_bar_stopRefresh:
				cancelRefreshTimers();
				Toast.makeText(this,"Stopped auto refresh.",Toast.LENGTH_LONG).show();
				return true;
			case R.id.app_bar_settings:
				Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
				return true;
			case R.id.app_bar_login:
				startActivity(new Intent(MainActivity.this,LoginActivity.class));
				cancelRefreshTimers();
//				finish();
				return true;
			case R.id.app_bar_about:
				Toast.makeText(this,"Special Chat-1.0\n" +
						"Developed by Zhong Wenliang. \n" +
						"Email: CuberWenliang@0x29a.cc",Toast.LENGTH_LONG).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * init views & some settings
	 */
	private void init(){
		loadChatList();
		
		// init buttons
		{
			findViewById(R.id.menu_btn_chats).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					findViewById(R.id.main_chat_recyclerView).setVisibility(View.VISIBLE);
					findViewById(R.id.main_contacts).setVisibility(View.GONE);
					findViewById(R.id.main_moments).setVisibility(View.GONE);
					findViewById(R.id.main_me).setVisibility(View.GONE);
					reloadChatList();
				}
			});
			findViewById(R.id.menu_btn_contacts).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					findViewById(R.id.main_chat_recyclerView).setVisibility(View.GONE);
					findViewById(R.id.main_contacts).setVisibility(View.VISIBLE);
					findViewById(R.id.main_moments).setVisibility(View.GONE);
					findViewById(R.id.main_me).setVisibility(View.GONE);
					loadContactList();
				}
			});
			findViewById(R.id.menu_btn_moments).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					findViewById(R.id.main_chat_recyclerView).setVisibility(View.GONE);
					findViewById(R.id.main_contacts).setVisibility(View.GONE);
					findViewById(R.id.main_moments).setVisibility(View.VISIBLE);
					findViewById(R.id.main_me).setVisibility(View.GONE);
					// next ver
				}
			});
			findViewById(R.id.menu_btn_me).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					findViewById(R.id.main_chat_recyclerView).setVisibility(View.GONE);
					findViewById(R.id.main_contacts).setVisibility(View.GONE);
					findViewById(R.id.main_moments).setVisibility(View.GONE);
					findViewById(R.id.main_me).setVisibility(View.VISIBLE);
					// next ver
				}
			});
		}
		
	}
	
	/**
	 * Whether redirect to front login page
 	 */
	private void redirect(){
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		if(preferences.getInt("is_login",0)!=1){
			changeViewToFontLogin();
		}else if(preferences.getInt("is_login",0)==1){
			normalMode();
		}
	}
	
	/**
	 * Load Welcome page at the first run of app.
	 */
	private void welcomePage(){
		SharedPreferences preferences=getSharedPreferences("init_info",MODE_PRIVATE);
		String is_firstRun=preferences.getString("first_run","yes");
		
		if(is_firstRun.equals("yes")){
			startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
			finish();
		}
	}
	
	// def 2 Timer(s)
	static Timer checkLoginTimer;
	static Timer refreshMsgTimer;
	/**
	 * Normal mode perform.
	 */
	private void normalMode(){
		startService(new Intent(this,BackgroundTaskService.class));
		
		// to clear timers in front
		cancelRefreshTimers();
		
		// set (or reset) timer tasks
		checkLoginTimer=new Timer();
		refreshMsgTimer=new Timer();
		
		// Check login status per 2 minutes.
		checkLoginTimer.schedule(new TimerTask(){
			@Override public void run(){
				try{
					int status=checkLogin();
					if(status==2){
						changeViewToFontLogin();
					}else if(status==1){
						showToast("Ohh! Poor Network... :(",Toast.LENGTH_LONG);
					}
				}catch(JSONException e){
//					e.printStackTrace();
				}
			}
		},17,120000);
		
		// Refresh new message(s) per 5.888 seconds.
		refreshMsgTimer.schedule(new TimerTask(){
			@Override
			public void run(){
				try{
					if(refreshNewMsg()==1){
						// if network is not so fine...
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
						},1888,23333);
					}
				}catch(JSONException e){
//					e.printStackTrace();
				}
			}
		},1700,5888);
		
	}
	
	static ChatListItemAdapter adapterChatList;
	/**
	 * Load Chat list ListView by Adapter
	 * */
	void loadChatList(){
		MainActivity.this.runOnUiThread(
			new Runnable(){
				public void run(){
					ChatListSQLiteHelper chatListSQLiteHelper=
							new ChatListSQLiteHelper(MainActivity.this,"chat_list.db",1);
					
					/*
					* chatList[0][0]    -> total number
					* chatList[index][0] -> index (index>0)
					* chatList[index][1] -> user_id
					* chatList[index][2] -> nickname
					* chatList[index][3] -> last_chat_time
					 * */
					final String[][] chatList=chatListSQLiteHelper.fetchChatList(chatListSQLiteHelper.getReadableDatabase(),0);
					
					final RecyclerView chatList_recycleView=findViewById(R.id.main_chat_recyclerView);
					LinearLayoutManager layoutManager=new LinearLayoutManager(MainActivity.this);
					chatList_recycleView.setLayoutManager(layoutManager);
					
					adapterChatList=new ChatListItemAdapter(chatList);
					adapterChatList.count=Integer.parseInt(chatList[0][0]);
					
					chatList_recycleView.setAdapter(adapterChatList);
					chatList_recycleView.setItemAnimator(new DefaultItemAnimator());
					
					// Fetch last one message.
//					String[] lastMsg=new String[51];
//					for(int i=1;i<= (Integer.parseInt(chatList[0][0])) && i<=50;i++){
//						MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(MainActivity.this,
//								"msg_"+chatList[i][1]+".db",1);
//						lastMsg[i]=msgSQLiteHelper.getLatestMsg(msgSQLiteHelper.getReadableDatabase());
//					}
					
					/*
					ChatListItemAdapter cli_adapter=new ChatListItemAdapter(MainActivity.this);
					cli_adapter.chatListInfo=chatList;
					cli_adapter.count=Integer.parseInt(chatList[0][0]); // item number
					
					final ListView ml_view=findViewById(R.id.main_chat_recyclerView);
					ml_view.setAdapter(cli_adapter);
					
					ml_view.setOnItemClickListener(new AdapterView.OnItemClickListener(){
						@Override
						public void onItemClick(AdapterView<?> parent,View view,int position,long id){
							position++;
							Intent intent=new Intent(MainActivity.this,ChatActivity.class);
							Bundle bundle=new Bundle();
							bundle.putString("user_id", chatList[position][1]);
							bundle.putString("nickname",chatList[position][2]);
							intent.putExtras(bundle);
							startActivity(intent);
							cancelRefreshTimers();
						}
					});
					
					ml_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
						@Override
						public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id){
							position++;
							final int finalPosition=position;
							AlertDialog alertDialog2 = new AlertDialog.Builder(MainActivity.this)
									.setTitle("Notices")
									.setMessage("Sure to delete this chat? \n'"+chatList[position][1]+"'")
									.setPositiveButton("Yeah", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialogInterface, int i) {
											//to do delete the chat item! by position
											ChatListSQLiteHelper chatListSQLiteHelper=
													new ChatListSQLiteHelper(MainActivity.this,"chat_list.db",1);
											chatListSQLiteHelper.deleteChatListItem(chatListSQLiteHelper.getReadableDatabase(),chatList[finalPosition][1]);
											loadChatList();
											ml_view.scrollTo(0,finalPosition*80); // bugs
											Toast.makeText(MainActivity.this, "Deleted. "+finalPosition, Toast.LENGTH_SHORT).show();
										}
									})
									.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialogInterface, int i){}
									})
									.create();
							alertDialog2.show();
							return true;
						}
					});*/
					
					
				}
			}
		);
	}
	
	/**
	 * Reload the chat list
	 */
	void reloadChatList(){
		MainActivity.this.runOnUiThread(
				new Runnable(){
					public void run(){
						ChatListSQLiteHelper chatListSQLiteHelper=
								new ChatListSQLiteHelper(MainActivity.this,"chat_list.db",1);
						/*
						 * chatList[0][0]    -> total number
						 * chatList[index][0] -> index (index>0)
						 * chatList[index][1] -> user_id
						 * chatList[index][2] -> nickname
						 * chatList[index][3] -> last_chat_time
						 * */
						final String[][] chatList=chatListSQLiteHelper.fetchChatList(chatListSQLiteHelper.getReadableDatabase(),0);
						adapterChatList.updateData(chatList);
					}
				}
		);
	}
	
	static ContactsListItemAdapter contactsListItemAdapter;
	/**
	 * Load Contacts List ListView by Adapter
	 */
	void loadContactList(){
		MainActivity.this.runOnUiThread(
			new Runnable(){
				public void run(){
					ContactListSQLiteHelper contactListSQLiteHelper=
							new ContactListSQLiteHelper(MainActivity.this,"contact_list.db",1);
					final String[][] contactsList=
							contactListSQLiteHelper.fetchContactsList(contactListSQLiteHelper.getReadableDatabase());
					
					ListView contacts_listView=findViewById(R.id.main_contacts_listView);
					contactsListItemAdapter=
							new ContactsListItemAdapter(MainActivity.this);
					contactsListItemAdapter.contactsInfo=contactsList;
					contactsListItemAdapter.count=Integer.parseInt(contactsList[0][0]);
					contacts_listView.setAdapter(contactsListItemAdapter);
					
					contacts_listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
						@Override
						public void onItemClick(AdapterView<?> parent,View view,int position,long id){
							position++;
							Intent intent=new Intent(MainActivity.this,ContactDetailActivity.class);
							Bundle bundle=new Bundle();
							bundle.putString("user_id",contactsList[position][0]);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					});
					
					contacts_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
						@Override
						public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id){
							position++;
							AlertDialog alertDialog2 = new AlertDialog.Builder(MainActivity.this)
									.setTitle("Notices")
//									.setMessage("Sure to delete this contact? \n'"+contactsList[position][0]+"'")
									.setMessage("Cannot delete contact yet. \n'"+contactsList[position][0]+"'")
									.setPositiveButton("Yeah", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialogInterface, int i) {
											// next ver to do delete the item! by index
											Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
										}
									})
									.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialogInterface, int i) {}
									})
									.create();
							alertDialog2.show();
							return true;
						}
					});
				}
			}
		);
	}
	
	//todo here
	void reloadContactList(){
		MainActivity.this.runOnUiThread(
				new Runnable(){
					public void run(){
//						ChatListSQLiteHelper chatListSQLiteHelper=
//								new ChatListSQLiteHelper(MainActivity.this,"chat_list.db",1);
//						/*
//						 * chatList[0][0]    -> total number
//						 * chatList[index][0] -> index (index>0)
//						 * chatList[index][1] -> user_id
//						 * chatList[index][2] -> nickname
//						 * chatList[index][3] -> last_chat_time
//						 * */
//						final String[][] chatList=chatListSQLiteHelper.fetchChatList(chatListSQLiteHelper.getReadableDatabase(),0);
//						contactsListItemAdapter.updateData(chatList);
					}
				}
		);
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
					"\"user_id\":\""+MyTools.filterSpecialChar(user_id)+"\"," +
					"\"token_key\":\""+MyTools.filterSpecialChar(token_key)+"\"," +
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
				JSONObject jsonTemp=data.getJSONObject(data.getString("index_"+i));
				String friend_id=jsonTemp.getString("user_id");
				String send_time=jsonTemp.getString("send_time");
				
				MsgSQLiteHelper mh=new MsgSQLiteHelper(MainActivity.this,"msg_"+friend_id+".db",1);
				mh.insertNewMsg(mh.getReadableDatabase(),friend_id,send_time,jsonTemp.getString("msg_content"));
				
				ChatListSQLiteHelper clh=new ChatListSQLiteHelper(MainActivity.this,"chat_list.db",1);
				clh.updateChatList(clh.getReadableDatabase(),friend_id,send_time,jsonTemp.getString("msg_content"));
			}
			reloadChatList();
			return 0;
		}else if(data.getString("is_new_msg").equals("false")){
			return 2;
		}else{
			return 3;
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

		if(token_key==null || user_id==null){
			return 2;
		}
		String jsonMsg="{" +
				"\"client\":\"SCC-1.0\"," +
				"\"action\":\"0001\"," +
				"\"user_id\":\""+user_id+"\"," +
				"\"token_key\":\""+token_key+"\"," +
				"\"timestamp\":\""+MyTools.getCurrentTime()+"\"" +
				"}";
		SocketWithServer SWS=new SocketWithServer();
		SWS.delay=6;
		SWS.DataSend=jsonMsg;
		JSONObject data=SWS.startSocket();
		
		if(data==null){
			return 1;
		}else if(data.getString("status").equals("true")){
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
	 * **/
	private void changeViewToFontLogin(){
		MainActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				//login
				findViewById(R.id.font_login_linear_layout).setVisibility(View.VISIBLE);
				findViewById(R.id.main_linear_layout).setVisibility(View.GONE);
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
		});
	}
	
	/**
	 * Start LoginActivity
	 */
	private void goToLogin(){
		cancelRefreshTimers();
		startActivity(new Intent(MainActivity.this,LoginActivity.class));
		finish();
	}
	
	Handler toastHandler=new Handler();
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
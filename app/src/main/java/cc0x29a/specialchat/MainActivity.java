package cc0x29a.specialchat;

/*
* MainActivity.class
*
* Author:       Zhong Wenliang
* mail:         CuberWenliang@0x29a.cc
* start date:   March, 2020
*
* */


import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity{
	
	static int version_number=20041801;
	
	static String user_id;
	static String token_key;
	static String user_name;
	static String user_phone;
	
	MainBroadcastReceiver receiver;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		// next ver to do: this can set a lunch page !!
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
		
		// check Application upgrade.
		checkAppUpgrade();
		
		// This would run at the very first lunch.
		welcomePage();
		
		// UI views init
		init();
		
		startService(new Intent(MainActivity.this,SocketWithServerService.class) );
		
		//test codes
		
		// nohup java -jar SpecialChatServer.jar > /dev/null 2> log &
		
//		String a="{\"a1\":\"abc1\",\"b\":\"{'c1':'cde1'}\"}";
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
//user_id="12365";
//		Bundle bundle=new Bundle();
//		bundle.putString("login_id",user_id);
//		Intent intent=new Intent(MainActivity.this,LoginActivity.class);
//		intent.putExtras(bundle);
//		startActivity(intent,bundle);
		//		MsgSQLiteHelper h=new MsgSQLiteHelper(this,"msg_1123592075.db",1);
//		for(int i=1;i<=23;i++){
//			h.insertNewMsg(h.getReadableDatabase(),1123592075+"",i+"",i+" I love you.");
//			ChatListSQLiteHelper c=new ChatListSQLiteHelper(this,"chat_list.db",1);
//			c.insertNewChatListItem(c.getReadableDatabase(),"4091"+i,"Little hao","2"+MyTools.getCurrentTime());
			
//			c.insertNewChatListItem(c.getReadableDatabase(),"1123592075","Little hao",""+MyTools.getCurrentTime());
//		}
//		ContactListSQLiteHelper c=new ContactListSQLiteHelper(MainActivity.this,"contact_list.db",1);
//		c.insertNewContact(c.getReadableDatabase(),"1123592075","Apple2","Haaaa pi","13360417480");
//		String[][] a=new String[][]{{"a","b","a","s"},{"a","b","c"},{"a","b","c"}};
//		System.out.println(a.length);
//		finish();
		//test codes end
		
		
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
		// choose whether to redirect page
		redirect();
		
		normalMode();
		
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		user_id=preferences.getString("user_id",null);
		token_key=preferences.getString("token_key",null);
		user_name=MyTools.resolveSpecialChar(preferences.getString("user_name",null));
		user_phone=preferences.getString("user_phone",null);
		 
		// listen to messages from background task service
		receiver= new MainBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("backgroundTask.action");
		registerReceiver(receiver, filter);
		
		NetworkService.cancelNotification();
	}
	
	// stop background tasks service
	@Override
	protected void onStop(){
		super.onStop();
		cancelRefreshTimers();
		try{
			unregisterReceiver(receiver);
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
			unregisterReceiver(receiver);
		}catch(Exception e){
			//
		}
	}
	
	// Communicate with BackgroundTaskService.
	public class MainBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context,Intent intent) {
			String intentAction = intent.getAction();
			if(null!=intentAction && intentAction.equals("backgroundTask.action")){
				if("reLoadChatList".equals(intent.getStringExtra("todo_action"))){
					reloadChatList();
				}else if("reLoadContactList".equals(intent.getStringExtra("todo_action"))){
					loadContactList();
				}
			}
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
	
	/**
	 * Check Application upgrade
	 */
	private void checkAppUpgrade(){
		// check app upgrade.
		new Thread(new Runnable(){
			@Override
			public void run(){
				String DataSend="{'action':'CheckUpdate','version_number':'"+version_number+"'}";
				
				SocketDataManager dataManager=new SocketDataManager();
				final String dataStr=dataManager.startRequest(DataSend);
				
//				final String dataStr=SocketWithServerService.sendData(DataSend);
				
				MainActivity.this.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							JSONObject data=new JSONObject(dataStr);
							if(data.getString("status").equals("true")&&data.getString("is_update").equals("true")){
								Uri uri=Uri.parse("https://github.com/Galaxy-cube/SpecialChat/releases"); //todo
								Intent intent=new Intent(Intent.ACTION_VIEW,uri);
								startActivity(intent);
								Looper.prepare();
								Toast.makeText(MainActivity.this,"New upgrade available! ",Toast.LENGTH_LONG).show();
								Looper.loop();
							}
						}catch(JSONException e){
							e.printStackTrace();
						}
					}
				});
				
			}
		}).start();
	}
	
	/**
	 * init views & some settings
	 */
	private void init(){
		loadChatList();
		
		// init title bar
		{
			// show or hide top_menu
			findViewById(R.id.main_menu_btn).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					LinearLayout top_menu=findViewById(R.id.main_top_menu_cover);
					if(top_menu.getVisibility()==View.GONE){
						Animation animation=AnimationUtils.loadAnimation(MainActivity.this,R.anim.anim_top_bar_show);
						top_menu.setAnimation(animation);
						top_menu.setVisibility(View.VISIBLE);
					}else{
						Animation animation=AnimationUtils.loadAnimation(MainActivity.this,R.anim.anim_top_bar_hide);
						top_menu.setAnimation(animation);
						top_menu.setVisibility(View.GONE);
					}
				}
			});
			
			
			View.OnClickListener top_menu_listener=new View.OnClickListener(){
				@Override
				public void onClick(View v){
					switch(v.getId()){
						case R.id.main_menu_add_contact:
							startActivity(new Intent(MainActivity.this,SearchNewContact.class));
							cancelRefreshTimers();
							break;
						case R.id.main_menu_stopOrStart_refresh:
							// Yeah, pretty clear here are terrible shit like codes...
							// What am I doing?
							// Perhaps I'm a bit of boring.
							// Someday may edit them...I guess so.
							TextView tv=findViewById(R.id.main_menu_stopOrStart_refresh);
							char[] a=new char[]{'S','t','a','r','t',' ','a','u','t','o',' ','r','e','f','r','e','s','h'};
							
							if(!tv.getText().toString().equals("Start auto refresh")){
								cancelRefreshTimers();
								stopService(new Intent(MainActivity.this,BackgroundTaskService.class));
								stopService(new Intent(MainActivity.this,NetworkService.class));
								if(receiver.isInitialStickyBroadcast())unregisterReceiver(receiver);
								tv.setText(a,0,a.length);
								Toast.makeText(MainActivity.this,"Auto refresh stopped.",Toast.LENGTH_LONG).show();
							}else{
								// set (or reset) timer tasks
								checkLoginTimer=new Timer();
								// Check login status per 2 minutes.
								checkLoginTimer.schedule(new TimerTask(){
									@Override public void run(){
										try{
											checkLogin();
										}catch(Exception e){
											e.printStackTrace();
										}
									}
								},17,90000);
								startService(new Intent(MainActivity.this,BackgroundTaskService.class));
								// listen to messages from background task service
								receiver= new MainBroadcastReceiver();
								IntentFilter filter = new IntentFilter();
								filter.addAction("backgroundTask.action");
								registerReceiver(receiver, filter);
								tv.setText(R.string.stop_auto_refresh);
								Toast.makeText(MainActivity.this,"Auto refresh started.",Toast.LENGTH_LONG).show();
							}
							break;
						case R.id.main_menu_logout:
							Toast.makeText(MainActivity.this,"Chat records will not be cleared.",Toast.LENGTH_SHORT).show();
							
							SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
							SharedPreferences.Editor editor=preferences.edit();
							
							editor.putString("user_id",null);
							editor.putString("user_name",null);
							editor.putString("user_phone",null);
							editor.putString("token_key",null);
							editor.putString("login_time",null);
							editor.putInt("is_login",0);
							editor.apply();
							
							startActivity(new Intent(MainActivity.this,LoginActivity.class));
							break;
						case R.id.main_menu_about:
							startActivity(new Intent(MainActivity.this,AboutActivity.class));
							break;
						default:
							Toast.makeText(MainActivity.this,"Error occur in Menu bar!",Toast.LENGTH_SHORT).show();
							break;
					}
					LinearLayout top_menu=findViewById(R.id.main_top_menu_cover);
					Animation animation=AnimationUtils.loadAnimation(MainActivity.this,R.anim.anim_top_bar_hide);
					top_menu.setAnimation(animation);
					top_menu.setVisibility(View.GONE);
				}
			};
			// bind.
			findViewById(R.id.main_menu_add_contact).setOnClickListener(top_menu_listener);
			findViewById(R.id.main_menu_stopOrStart_refresh).setOnClickListener(top_menu_listener);
			findViewById(R.id.main_menu_logout).setOnClickListener(top_menu_listener);
			findViewById(R.id.main_menu_about).setOnClickListener(top_menu_listener);
		}
		
		// click main_linear_layout to hide top_menu
		{
			findViewById(R.id.main_top_menu_cover).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					LinearLayout top_menu=findViewById(R.id.main_top_menu_cover);
					if(top_menu.getVisibility()!=View.GONE){
						Animation animation=AnimationUtils.loadAnimation(MainActivity.this,R.anim.anim_top_bar_hide);
						top_menu.setAnimation(animation);
						top_menu.setVisibility(View.GONE);
					}
				}
			});
		}
		
		// init bottom_menu buttons
		{
			findViewById(R.id.menu_btn_chats).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					setMainTitleBarVisibility(1);
					setTitle("Special Chat");
					findViewById(R.id.main_chat_recyclerView).setVisibility(View.VISIBLE);
					findViewById(R.id.main_contacts).setVisibility(View.GONE);
					findViewById(R.id.main_moments).setVisibility(View.GONE);
					findViewById(R.id.main_me).setVisibility(View.GONE);
					new Thread(){
						@Override
						public void run(){
							super.run();
							reloadChatList();
						}
					}.start();
					
				}
			});
			findViewById(R.id.menu_btn_contacts).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					setMainTitleBarVisibility(1);
					setTitle("Contacts");
					findViewById(R.id.main_chat_recyclerView).setVisibility(View.GONE);
					findViewById(R.id.main_contacts).setVisibility(View.VISIBLE);
					findViewById(R.id.main_moments).setVisibility(View.GONE);
					findViewById(R.id.main_me).setVisibility(View.GONE);
					new Thread(){
						@Override
						public void run(){
							super.run();
							loadContactList();
						}
					}.start();
				}
			});
			findViewById(R.id.menu_btn_moments).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					setMainTitleBarVisibility(1);
					setTitle("Moments");
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
					setMainTitleBarVisibility(0);
					setTitle("Me");
					findViewById(R.id.main_chat_recyclerView).setVisibility(View.GONE);
					findViewById(R.id.main_contacts).setVisibility(View.GONE);
					findViewById(R.id.main_moments).setVisibility(View.GONE);
					findViewById(R.id.main_me).setVisibility(View.VISIBLE);
					
					loadMePage();
				}
			});
		}
		
	}
	
	/**
	 * Set title bar text
	 * @param text title
	 */
	private void setTitle(String text){
		TextView main_title=findViewById(R.id.main_title);
		main_title.setText(text);
	}
	
	/**
	 * Set Main Title Bar Visibility
	 * 0 -> Gone
	 * 1 -> Visible
	 * @param visibility Integer
	 */
	private void setMainTitleBarVisibility(int visibility){
		LinearLayout MainTitleBar = findViewById(R.id.main_title_bar);
		if(visibility==0){
			if(MainTitleBar.getVisibility()==View.GONE){
				return;
			}
			AnimationSet animationSet=new AnimationSet(true);
			animationSet.addAnimation(new AlphaAnimation(1,(float)0.5));
			ScaleAnimation scaleAnimation=new ScaleAnimation(1,(float)0.96,1,0,
					Animation.RELATIVE_TO_SELF,0.5f,
					Animation.RELATIVE_TO_SELF,0f);
			scaleAnimation.setDuration(288);
			animationSet.addAnimation(scaleAnimation);
		
			MainTitleBar.setAnimation(animationSet);
			MainTitleBar.setVisibility(View.GONE);
		}else{
			if(MainTitleBar.getVisibility()==View.VISIBLE){
				return;
			}
			AnimationSet animationSet=new AnimationSet(true);
			animationSet.addAnimation(new AlphaAnimation(1,(float)0.5));
			ScaleAnimation scaleAnimation=new ScaleAnimation(1,1,0,1,
					Animation.RELATIVE_TO_SELF,0.5f,
					Animation.RELATIVE_TO_SELF,0f);
			scaleAnimation.setDuration(166);
			animationSet.addAnimation(scaleAnimation);
			
			MainTitleBar.setAnimation(animationSet);
			MainTitleBar.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * Whether redirect to front login page
 	 */
	private void redirect(){
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		if(preferences.getInt("is_login",0)!=1){
			changeViewToFontLogin();
		}else{
			findViewById(R.id.font_login_linear_layout).setVisibility(View.GONE);
			findViewById(R.id.main_linear_layout).setVisibility(View.VISIBLE);
			findViewById(R.id.main_relative_layout).setVisibility(View.VISIBLE);
		}
	}
	
	// def 1 Timer(s)
	static Timer checkLoginTimer;
	/**
	 * Normal mode perform.
	 */
	private void normalMode(){
		startService(new Intent(this,BackgroundTaskService.class));
		
		// to clear timers in front
		cancelRefreshTimers();
		
		// set (or reset) timer tasks
		checkLoginTimer=new Timer();
		
		// Check login status per 2 minutes.
		checkLoginTimer.schedule(new TimerTask(){
			@Override public void run(){
				try{
					checkLogin();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		},17,90000);
		
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
					
					final List<String[]> chatList=chatListSQLiteHelper.fetchChatList(chatListSQLiteHelper.getReadableDatabase());
					
					final RecyclerView chatList_recycleView=findViewById(R.id.main_chat_recyclerView);
					LinearLayoutManager layoutManager=new LinearLayoutManager(MainActivity.this);
					chatList_recycleView.setLayoutManager(layoutManager);
					
					adapterChatList=new ChatListItemAdapter(chatList);
					adapterChatList.count=chatList.size();
					
					chatList_recycleView.setAdapter(adapterChatList);
					chatList_recycleView.setItemAnimator(new DefaultItemAnimator());
					
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
						 * [0] -> index (index>0)
						 * [1] -> user_id
						 * [2] -> nickname
						 * [3] -> last_chat_time
						 * */
						final List<String[]> chatList=chatListSQLiteHelper.fetchChatList(chatListSQLiteHelper.getReadableDatabase());
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
	
	/*
	 * Load me page
	 */
	void loadMePage(){
//		final LinearLayout main_me=findViewById(R.id.main_me);
		
		final EditText my_name=findViewById(R.id.main_my_name);
		final TextView my_id=findViewById(R.id.main_my_id);
		final LinearLayout my_phone_container=findViewById(R.id.main_my_phone_container);
		final EditText my_phone=findViewById(R.id.main_my_phone);
		final CircleImageView my_profile=findViewById(R.id.main_my_profile);
		final Button btn_edit=findViewById(R.id.main_edit_my_info);
		
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		user_name=MyTools.resolveSpecialChar(preferences.getString("user_name","null"));
		user_phone=preferences.getString("user_phone","null");
		
		my_name.setText(user_name);
		my_id.setText(String.format("id:%s",user_id));
		my_phone.setText(user_phone);
		
		btn_edit.setOnClickListener(new View.OnClickListener(){
			int btn_status=0;
			@Override
			public void onClick(View v){
//				Toast.makeText(MainActivity.this,"Coming soon!",Toast.LENGTH_SHORT).show();
				if(btn_status==0){
					
					// move the views
					{
						ObjectAnimator valueAnimator=ObjectAnimator.ofFloat(my_profile,"translationX",-200);
						valueAnimator.setDuration(666);
						valueAnimator.start();
						
						ObjectAnimator valueAnimator2=ObjectAnimator.ofFloat(my_name,"translationY",-288);
						valueAnimator2.setDuration(666);
						valueAnimator2.start();
						
						ObjectAnimator valueAnimator3=ObjectAnimator.ofFloat(my_name,"translationX",100);
						valueAnimator3.setDuration(666);
						valueAnimator3.start();
						
						ObjectAnimator valueAnimator4=ObjectAnimator.ofFloat(my_id,"translationY",-233);
						valueAnimator4.setDuration(888);
						valueAnimator4.start();
						
						ObjectAnimator valueAnimator5=ObjectAnimator.ofFloat(my_id,"translationX",120);
						valueAnimator5.setDuration(888);
						valueAnimator5.start();
						
						ObjectAnimator valueAnimator6=ObjectAnimator.ofFloat(my_phone_container,"translationY",-188);
						valueAnimator6.setDuration(999);
						valueAnimator6.start();
						
						ObjectAnimator valueAnimator7=ObjectAnimator.ofFloat(my_phone_container,"translationX",120);
						valueAnimator7.setDuration(999);
						valueAnimator7.start();
					}
					
					my_name.setEnabled(true);
//					my_phone.setEnabled(true);
					
					// set a text "Edit" beside Image
					my_profile.draw=1;
					// reload ImageView
					my_profile.setImageResource(R.mipmap.logo2);
					
					btn_edit.setText("Save");
					btn_status=1;
				}else{
					if(user_name.equals(my_name.getText().toString()) && user_phone.equals(my_phone.getText().toString())){
						resumeViews();
						return;
					}
					
					try{
						
						Pattern pattern_user_name=Pattern.compile("^[a-zA-Z0-9\\u4e00-\\u9fa5_\\-.。?？!！() ]{2,10}$");
						Matcher m_um=pattern_user_name.matcher(my_name.getText().toString());
						
						Pattern pattern_user_phone=Pattern.compile("^[1]([3-9])[0-9]{9}$");
						Matcher m_ph=pattern_user_phone.matcher(my_phone.getText().toString());
						
						if(!m_um.matches()){
							Toast.makeText(MainActivity.this,"Illegal user name! ",Toast.LENGTH_SHORT).show();
							return;
						}else if(!m_ph.matches()){
							Toast.makeText(MainActivity.this,"Illegal phone number! ",Toast.LENGTH_SHORT).show();
							return;
						}
						
						final String new_user_name=MyTools.filterSpecialChar(my_name.getText().toString());
						final String new_user_phone=my_phone.getText().toString();
						
						new Thread(new Runnable(){
							@Override
							public void run(){
								String DataSend="{"+
									"\"client\":\"SCC-1.0\","+
									"\"action\":\"0013\","+
									"\"secret\":\"I love you.\","+
									"\"user_id\":\""+user_id+"\","+
									"\"token_key\":\""+token_key+"\","+
									"\"new_user_name\":\""+new_user_name+"\"," +
									"\"new_user_phone\":\""+new_user_phone+"\""+
									"}";
								
								SocketDataManager dataManager=new SocketDataManager();
								final String dataStr=dataManager.startRequest(DataSend);
								
//								final String dataStr=SocketWithServerService.sendData(DataSend);
								
								MainActivity.this.runOnUiThread(new Runnable(){
									@Override
									public void run(){
										try{
											JSONObject data=new JSONObject(dataStr);
											if(data.getString("status").equals("true")&&data.getString("is_updated").equals("true")){
												SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
												SharedPreferences.Editor editor=preferences.edit();
												
												editor.putString("user_name",new_user_name);
												editor.putString("user_phone",new_user_phone);
												
												editor.apply();
												
												resumeViews();
											}else{
												Toast.makeText(MainActivity.this,"Error! ",Toast.LENGTH_SHORT).show();
											}
										}catch(JSONException e){
											e.printStackTrace();
										}
									}
								});
								
							}
						}).start();
						
					}catch(Exception e){
						Toast.makeText(MainActivity.this,"Something Error! ",Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
			}
			
			// resume the views.
			private void resumeViews(){
				// move back the Views
				{
					ObjectAnimator valueAnimator=ObjectAnimator.ofFloat(my_profile,"translationX",0);
					valueAnimator.setDuration(666);
					valueAnimator.start();
					
					ObjectAnimator valueAnimator2=ObjectAnimator.ofFloat(my_name,"translationY",0);
					valueAnimator2.setDuration(666);
					valueAnimator2.start();
					
					ObjectAnimator valueAnimator3=ObjectAnimator.ofFloat(my_name,"translationX",0);
					valueAnimator3.setDuration(666);
					valueAnimator3.start();
					
					ObjectAnimator valueAnimator4=ObjectAnimator.ofFloat(my_id,"translationY",0);
					valueAnimator4.setDuration(888);
					valueAnimator4.start();
					
					ObjectAnimator valueAnimator5=ObjectAnimator.ofFloat(my_id,"translationX",0);
					valueAnimator5.setDuration(888);
					valueAnimator5.start();
					
					ObjectAnimator valueAnimator6=ObjectAnimator.ofFloat(my_phone_container,"translationY",0);
					valueAnimator6.setDuration(999);
					valueAnimator6.start();
					
					ObjectAnimator valueAnimator7=ObjectAnimator.ofFloat(my_phone_container,"translationX",0);
					valueAnimator7.setDuration(999);
					valueAnimator7.start();
				}
				
				my_name.setEnabled(false);
				//my_phone.setEnabled(false);
				
				// set a text "Edit" beside Image
				my_profile.draw=0;
				// reload ImageView
				my_profile.setImageResource(R.mipmap.logo2);
				
				btn_edit.setText(R.string.edit);
				btn_status=0;
			}
		});
		
		my_profile.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Toast.makeText(MainActivity.this,"Coming soon!",Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	/**
	 * Check login status.
	 *
	 * Server return:
	 *      {"status":"true"|"false"}
	 * **/
	private void checkLogin(){
		final SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);

		if(token_key==null || user_id==null){
			changeViewToFontLogin();
			return;
		}
		
		new Thread(new Runnable(){
			@Override
			public void run(){
				String DataSend="{" +
						"\"client\":\"SCC-1.0\"," +
						"\"action\":\"0001\"," +
						"\"user_id\":\""+user_id+"\"," +
						"\"token_key\":\""+token_key+"\"," +
						"\"timestamp\":\""+MyTools.getCurrentTime()+"\"" +
						"}";
				
				SocketDataManager dataManager=new SocketDataManager();
				final String dataStr=dataManager.startRequest(DataSend);
				
//				final String dataStr=SocketWithServerService.sendData(DataSend);
				
				MainActivity.this.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							JSONObject data=new JSONObject(dataStr);
							if(data.getString("status").equals("true")){
								if(preferences.getInt("is_login",0)!=1){
									SharedPreferences.Editor editor=preferences.edit();
									editor.putInt("is_login",1);
									editor.apply();
								}
							}else{
								SharedPreferences.Editor editor=preferences.edit();
								editor.putInt("is_login",0);
								editor.apply();
								
								changeViewToFontLogin();
							}
						}catch(JSONException|NullPointerException e){
							e.printStackTrace();
						}
					}
				});
				
			}
		}).start();
		
	}
	
	/**
	 * Cancel unused timers.
	 */
	private void cancelRefreshTimers(){
		if(checkLoginTimer!=null){checkLoginTimer.cancel();}
	}
	
	/**
	 * Change content layout
	 * **/
	private void changeViewToFontLogin(){
		MainActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				// to font login page
				findViewById(R.id.font_login_linear_layout).setVisibility(View.VISIBLE);
				findViewById(R.id.main_linear_layout).setVisibility(View.GONE);
				findViewById(R.id.main_relative_layout).setVisibility(View.GONE);
				findViewById(R.id.btn_front_login).setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v){
						goToLogin();
					}
				});
				cancelRefreshTimers();
				stopService(new Intent(MainActivity.this,BackgroundTaskService.class));
				Toast.makeText(MainActivity.this,"Maybe you haven't login yet? ",Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	/**
	 * Start LoginActivity
	 */
	private void goToLogin(){
		cancelRefreshTimers();
		stopService(new Intent(MainActivity.this,BackgroundTaskService.class));
		startActivity(new Intent(MainActivity.this,LoginActivity.class));
//		finish();
	}
	
	
}

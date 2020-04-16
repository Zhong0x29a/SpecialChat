package cc0x29a.specialchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ChatActivity extends AppCompatActivity{
	
	// this activity's lunch mode need to be update! or will cause a little bug!
	
	static String my_id;
	static String ta_id;
	static String nickname;
	
	static ChatWindowAdapter adapter;
	
	// where history loaded to
	static int history_position=0;
	
	ChatBroadcastReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		init();
		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
		//  fetch ta user_id & name
		Bundle bundle = this.getIntent().getExtras();
		ta_id= (null != bundle) ? bundle.getString("user_id") : null;
		ContactListSQLiteHelper helper=new ContactListSQLiteHelper(this,"contact_list.db",1);
		nickname=MyTools.resolveSpecialChar( helper.fetchNickname(helper.getReadableDatabase(), ta_id ) );
		
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		my_id=preferences.getString("user_id",null);
		
		// set title text
		if(null!=nickname){
			TextView title=findViewById(R.id.chat_title);
			title.setText(nickname);
		}
		
		// init & load chat history
		if(ta_id!= null && !ta_id.equals("")){
			// fetch data from database
			MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(ChatActivity.this,
					"msg_"+ta_id+".db",1);
			List<String[]> data=msgSQLiteHelper.getChatRecord(msgSQLiteHelper.getReadableDatabase(),history_position); //position start from 0
			
			// load recyclerView
			final RecyclerView recordRecyclerView=findViewById(R.id.chatWindow_recyclerView);
			LinearLayoutManager layoutManager=new LinearLayoutManager(this);
			recordRecyclerView.setLayoutManager(layoutManager);
			
			adapter=new ChatWindowAdapter(data);
			adapter.count=data.size();
			adapter.my_id=my_id;
			adapter.ta_id=ta_id;
			
			recordRecyclerView.setAdapter(adapter);
			recordRecyclerView.setItemAnimator(new DefaultItemAnimator());
			
			// reverse data set
			layoutManager.setReverseLayout(true);
			
			// scroll to top to load more history
			recordRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
				private int lastVisibleItemPosition;
				//				private int firstVisibleItemPosition;
				
				@Override
				public void onScrolled(@NotNull RecyclerView recyclerView,int dx,int dy){
					super.onScrolled(recyclerView, dx, dy);
					
					RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
					if (layoutManager instanceof LinearLayoutManager) {
						lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
						//						firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
					}
				}
				
				@Override
				public void onScrollStateChanged(@NotNull RecyclerView recyclerView,int newState) {
					super.onScrollStateChanged(recyclerView, newState);
					RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
					assert layoutManager!=null;
					int totalItemCount = layoutManager.getItemCount();
					if (newState == RecyclerView.SCROLL_STATE_IDLE) {
						if ((totalItemCount - 1) == lastVisibleItemPosition){
							history_position+=50;
							MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(ChatActivity.this,
									"msg_"+ta_id+".db",1);
							List<String[]> newData=msgSQLiteHelper.getChatRecord(msgSQLiteHelper.getReadableDatabase(),history_position);
							
							if( newData.size() == 0 ){
								Toast.makeText(ChatActivity.this,"No more! ",Toast.LENGTH_SHORT).show();
							}else{
								adapter.addMoreData(newData);
							}
						}
						// else if (firstVisibleItemPosition == 0) {
						//      // when scroll to the bottom
						// }
					}
				}
				
			});
			
		}else{
			Toast.makeText(ChatActivity.this,"ERROR! (CA125)",Toast.LENGTH_LONG).show();
			finish();
		}
		
		// listen broadcast from BackgroundTask.
		receiver=new ChatBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("backgroundTask.action.chatActivity."+ta_id);
		registerReceiver(receiver,filter);
		
	}
	
	@Override
	protected void onStop(){
		history_position=0;
		try{
			unregisterReceiver(receiver);
		}catch(Exception e){
			//
		}
		finish();
		super.onStop();
	}
	
	protected void onDestroy(){
		try{
			unregisterReceiver(receiver);
		}catch(Exception e){
			//
		}
		super.onDestroy();
	}
	
	// Communicate with BackgroundTaskService.
	public static class ChatBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context,Intent intent) {
			String intentAction = intent.getAction();
			if(null != intentAction && intentAction.equals("backgroundTask.action.chatActivity."+ta_id)){
				String[] new_data;
				if("updateChatRecord".equals( intent.getStringExtra("todo_action") ) &&
						null!=( new_data=intent.getStringArrayExtra("new_record") ) ){
					// update chat record.
					adapter.addNewData(new_data);
				}
			}
		}
	}
	
	private void init(){
		
		findViewById(R.id.chat_menu_btn).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent=new Intent(ChatActivity.this,ContactDetailActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("user_id",ta_id);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		
		// Return button
		findViewById(R.id.chat_btn_return).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				finish();
			}
		});
		
		// Send message in editText
		findViewById(R.id.chat_btn_send).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				try{
					SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
					final String my_id=preferences.getString("user_id",null);
					String token_key=preferences.getString("token_key",null);
					
					final EditText editText=findViewById(R.id.chat_EditText);
					final String msg_content=MyTools.filterSpecialChar(editText.getText().toString());
					
					// if content is empty
					if(msg_content.equals("") || msg_content.equals("&#32;")){
						Toast.makeText(ChatActivity.this,"Cannot send empty message.",Toast.LENGTH_SHORT).show();
						return;
					}
					
					if(my_id!=null && token_key!=null){
						final String dataToSend="{" +
								"'client':'SCC-1.0'," +
								"'action':'0004'," +
								"'user_id':'"+my_id+"'," +
								"'token_key':'"+token_key+"'," +
								"'to':'"+ta_id+"'," +
								"'msg_content':'"+msg_content+"'," +
								"'timestamp':'"+MyTools.getCurrentTime()+"'" +
								"}";
						
						new Thread(new Runnable(){
							@Override
							public void run(){
								final String dataStr=SocketWithServerService.sendData(dataToSend);
								ChatActivity.this.runOnUiThread(new Runnable(){
									@Override
									public void run(){
										try{
											JSONObject data=new JSONObject(dataStr);
											if(data.getString("status").equals("true")){
												// store into msg SQLite
												MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(ChatActivity.this,"msg_"+ta_id+".db",1);
												msgSQLiteHelper.insertNewMsg(msgSQLiteHelper.getReadableDatabase(),my_id,data.getString("send_time"),msg_content);
												// update recycler view
												adapter.addNewData(new String[]{"",my_id,"","",MyTools.resolveSpecialChar(msg_content)});
												// update chat list , last msg & last chat time
												ChatListSQLiteHelper chatListHelper=new ChatListSQLiteHelper(ChatActivity.this,"chat_list.db",1);
												chatListHelper.updateChatList(chatListHelper.getReadableDatabase(),ta_id,MyTools.getCurrentTime()+"",msg_content);
												// clear EditText
												editText.getText().clear();
											}else if(data.getString("status").equals("false")){
												Toast.makeText(ChatActivity.this,"Something wrong!",Toast.LENGTH_SHORT).show();
											}else{
												Toast.makeText(ChatActivity.this,"Unknown error! (CA111)",Toast.LENGTH_SHORT).show();
											}
										}catch(JSONException|NullPointerException e){
											e.printStackTrace();
										}
									}
								});
							}
						}).start();
						
					}else{
						Toast.makeText(ChatActivity.this,"Bad login info! ",Toast.LENGTH_SHORT).show();
					}
				}catch(Exception e){
					e.printStackTrace();
					Toast.makeText(ChatActivity.this,"Unknown error! (CA1004)",Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
}

package cc0x29a.specialchat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatActivity extends AppCompatActivity{
	
	// this activity lunch mode need to be update! or will cause a little bug!
	
	static String ta_id;
	static String nickname;
	
	static int history_position=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
//		this.setTitle("aa");
	
		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		init();
		
		// where history loaded to
		history_position=0;
		
		Bundle bundle = this.getIntent().getExtras();
		
		ta_id= (null != bundle) ? bundle.getString("user_id") : null;
		nickname= (null != bundle) ? bundle.getString("nickname") : null;
		
		if(null!=nickname){
			this.setTitle(nickname);
		}
		
		if(ta_id!= null && !ta_id.equals("")){
			MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(ChatActivity.this,
					"msg_"+ta_id+".db",1);
			String[][] record=msgSQLiteHelper.getChatRecord(msgSQLiteHelper.getReadableDatabase(),history_position); //position start from 0
			
			SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
			String my_id=preferences.getString("user_id",null);
			
			final RecyclerView recordRecyclerView=findViewById(R.id.chatWindow_recyclerView);
			LinearLayoutManager layoutManager=new LinearLayoutManager(this);
			recordRecyclerView.setLayoutManager(layoutManager);
			
			final ChatWindowAdapter adapter=new ChatWindowAdapter(record);
			adapter.count=Integer.parseInt(record[0][0]);
			adapter.my_id=my_id;
			adapter.ta_id=ta_id;
			recordRecyclerView.setAdapter(adapter);
			
			recordRecyclerView.setItemAnimator(new DefaultItemAnimator());
			
			layoutManager.setReverseLayout(true);
			
			
			recordRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
				private int lastVisibleItemPosition;
				private int firstVisibleItemPosition;
				
				@Override
				public void onScrolled(@NotNull RecyclerView recyclerView,int dx,int dy){
					super.onScrolled(recyclerView, dx, dy);
					
					RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
					if (layoutManager instanceof LinearLayoutManager) {
						lastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
								.findLastVisibleItemPosition();
						firstVisibleItemPosition = ((LinearLayoutManager) layoutManager)
								.findFirstVisibleItemPosition();
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
							String[][] record=msgSQLiteHelper.getChatRecord(msgSQLiteHelper.getReadableDatabase(),history_position);
							if(Integer.parseInt(record[0][0])==0){
								Toast.makeText(ChatActivity.this,"No more! ",Toast.LENGTH_SHORT).show();
							}else{
								adapter.addData(record);
							}
						}
//						else if (firstVisibleItemPosition == 0) {
//							// when scroll to the bottom
//						}
					}
				}
			});
			
			
		}else{
			Toast.makeText(ChatActivity.this,"ERROR! (CA55)",Toast.LENGTH_LONG).show();
			finish();
		}
		
		
	}
	
	@Override
	protected void onStop(){
		super.onStop();
	}
	
	private void init(){
		
		// Send message in editText
		findViewById(R.id.chat_btn_send).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				try{
					SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
					String my_id=preferences.getString("user_id",null);
					String token_key=preferences.getString("token_key",null);
					
					EditText editText=findViewById(R.id.chat_EditText);
					String msg_content=MyTools.filterSpecialChar(editText.getText().toString());
					
					if(msg_content.equals("") || msg_content.equals("&#32;")){
						Toast.makeText(ChatActivity.this,"Cannot send empty message.",Toast.LENGTH_SHORT).show();
						return;
					}
					
					if(my_id!=null && token_key!=null){
						String dataToSend="{" +
								"'client':'SCC-1.0'," +
								"'action':'0004'," +
								"'user_id':'"+my_id+"'," +
								"'token_key':'"+token_key+"'," +
								"'to':'"+ta_id+"'," +
								"'msg_content':'"+msg_content+"'," +
								"'timestamp':'"+MyTools.getCurrentTime()+"'" +
								"}";
						
						SocketWithServer socket=new SocketWithServer();
						socket.DataSend=dataToSend;
						JSONObject data=socket.startSocket();
						if( data==null ){
							Toast.makeText(ChatActivity.this,"Perhaps Network is lazy? ",Toast.LENGTH_SHORT).show();
						}else if( data.getString("status").equals("true") ){
							MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(ChatActivity.this,"msg_"+my_id+".db",1);
							msgSQLiteHelper.insertNewMsg(
									msgSQLiteHelper.getReadableDatabase(),my_id,
									data.getString("send_time"),msg_content);
							editText.getText().clear();
						}else if( data.getString("status").equals("false") ){
							Toast.makeText(ChatActivity.this,"Something wrong!",Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(ChatActivity.this,"Unknown error! (CA111)",Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(ChatActivity.this,"Bad login info! ",Toast.LENGTH_SHORT).show();
					}
				}catch(JSONException|NullPointerException e){
					e.printStackTrace();
					Toast.makeText(ChatActivity.this,"Unknown error! (CA1004)",Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}
	
}

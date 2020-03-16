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

import org.json.JSONException;
import org.json.JSONObject;

public class ChatActivity extends AppCompatActivity{
	
	// this activity lunch mode need to be update! or will cause a little bug!
	
	static String ta_id;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		Bundle bundle = this.getIntent().getExtras();
		
		ta_id= (bundle != null) ? bundle.getString("user_id") : null;
		
		if(ta_id!= null && !ta_id.equals("")){
			// todo: now can only fetch 20 pieces of messages!! need to complete.
			MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(ChatActivity.this,
					"msg_"+ta_id+".db",1);
			//			MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(ChatActivity.this,
			//					"msg_2950.db",1);
			String[][] record=msgSQLiteHelper.getChatRecord(msgSQLiteHelper.getReadableDatabase(),0); //position start from 0
			
			SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
			String my_id=preferences.getString("user_id",null);
			
			RecyclerView recyclerView=findViewById(R.id.chatWindow_recyclerView);
			LinearLayoutManager layoutManager=new LinearLayoutManager(this);
			recyclerView.setLayoutManager(layoutManager);
			
			ChatWindowAdapter adapter=new ChatWindowAdapter(record);
			adapter.count=Integer.parseInt(record[0][0]);
			adapter.my_id=my_id;
			recyclerView.setAdapter(adapter);
			recyclerView.setItemAnimator(new DefaultItemAnimator());
			
			layoutManager.setReverseLayout(true);
			
		}else{
			Toast.makeText(ChatActivity.this,"ERROR! (CA55)",Toast.LENGTH_LONG).show();
			finish();
		}
		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		init();
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

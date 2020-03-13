package cc0x29a.specialchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity{
	
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
			//			layoutManager.scrollToPosition(adapter.count-1);
			
		}else{
			Toast.makeText(ChatActivity.this,"ERROR! (...)",Toast.LENGTH_LONG).show();
			finish();
		}
		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		init();
	}
	
	private void init(){
		findViewById(R.id.chat_EditText).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				//todo send msg
				SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
				String user_id=preferences.getString("use_id",null);
				String token_key=preferences.getString("token_kay",null);
				
				EditText e=findViewById(R.id.chat_EditText);
				String msg=MyTools.filterSpecialChar(e.getText().toString());
				
				if(user_id!=null&&token_key!=null){
					String dataToSend="{" +
							"'client':'SCC-1.0'," +
							"'action':'0004'," +
							"'user_id':'"+user_id+"'," +
							"'token_key':'"+token_key+"'," +
							"'to':'"+ta_id+"'," +
							"'msg_content':'"+msg+"'," +
							"'timestamp':'"+MyTools.getCurrentTime()+"'" +
							"}";
					
					SocketWithServer socket=new SocketWithServer();
					socket.DataSend=dataToSend;
					socket.startSocket();
				}
				
			}
		});
	}
	
}

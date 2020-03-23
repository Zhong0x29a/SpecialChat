package cc0x29a.specialchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class ContactDetailActivity extends AppCompatActivity{
	static String user_id;
	static String token_key;
	
	static String ta_id;
	static String ta_phone;
	static String ta_name;
	
	// 1->chat;2->add;
	static int btn_mode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_detail);

		Bundle bundle = this.getIntent().getExtras();
		ta_id= (bundle != null) ? bundle.getString("user_id") : null;
		
		TextView tv_user_id=findViewById(R.id.detail_userID);
		tv_user_id.setText(ta_id);
		
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		user_id=preferences.getString("user_id",null);
		token_key=preferences.getString("token_key",null);
		
		findViewById(R.id.btn_add_or_chat).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				
				if(btn_mode==1){
					// Start chat activity, send user_id and ta's nickname by bundle
					
					Intent intent=new Intent(v.getContext(),ChatActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("user_id", ta_id);
					bundle.putString("nickname", ta_name); // bug will occur here, I think..
					intent.putExtras(bundle);
					startActivity(intent);
					finish();
				}else if(btn_mode==2){
				
					//add contact
					if(token_key==null || user_id==null){
						Toast.makeText(ContactDetailActivity.this,"Login info error!",Toast.LENGTH_LONG).show();
						return;
					}
					SocketWithServer socket=new SocketWithServer();
					socket.DataSend="{" +
							"'client':'SCC-1.0'," +
							"'action':'0007'," +
							"'ta_id':'"+ta_id+"'," +
							"'user_id':'"+user_id+"'," +
							"'token_key':'"+token_key+"'" +
							"}";
					JSONObject data=socket.startSocket();
					try{
						if(data!=null && data.getString("status").equals("true")){
							// insert data into contact list
							ContactListSQLiteHelper helper=new ContactListSQLiteHelper(ContactDetailActivity.this,"contact_list.db",1);
							helper.insertNewContact(helper.getReadableDatabase(),ta_id,ta_name,ta_name,ta_phone);
							
							Toast.makeText(ContactDetailActivity.this,"Succeed!\n"+ta_name+"\n"+ta_phone,Toast.LENGTH_SHORT).show();
						}
					
					}catch(JSONException e){
						e.printStackTrace();
					}
				}
			}
		});
		
		
		// fetch contact detail.
		new Thread(){
			@Override
			public void run(){
				try{
					SocketWithServer socket=new SocketWithServer();
					socket.DataSend="{" +
							"'client':'SCC-1.0'," +
							"'action':'0008'," +
							"'ta_id':'"+ta_id+"'," +
							"'secret':'I love you.'" +
							"}";
					JSONObject data=socket.startSocket();
					if(data!=null && data.getString("status").equals("true")){
						TextView tv_user_name=findViewById(R.id.detail_userName);
						TextView tv_user_phone=findViewById(R.id.detail_userPhone);
						
						ta_name=data.getString("user_name");
						ta_phone=data.getString("user_phone");
						
						tv_user_name.setText(ta_name);
						tv_user_phone.setText(ta_phone);
					}else{
						Looper.prepare();
						Toast.makeText(getApplicationContext(),"Error!\nData Null!",Toast.LENGTH_LONG).show();
						Looper.loop();
					}
				}catch(JSONException|NullPointerException e){
					e.printStackTrace();
					Looper.prepare();
					Toast.makeText(getApplicationContext(),"Error!\nData Null! (Exception)",Toast.LENGTH_LONG).show();
					Looper.loop();
				}
			}
		}.start();
		
		// check if is friend
		new Thread(){
			@Override
			public void run(){
				try{
					sleep(500);
					SocketWithServer socket=new SocketWithServer();
					socket.DataSend="{" +
							"'client':'SCC-1.0'," +
							"'action':'0011'," +
							"'my_id':'"+user_id+"'," +
							"'ta_id':'"+ta_id+"'," +
							"'secret':'I love you.'" +
							"}";
					JSONObject data=socket.startSocket();
					if(data!=null && data.getString("status").equals("true") &&
							data.getString("is_friend").equals("true")){
						
						ContactDetailActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								Button btn=findViewById(R.id.btn_add_or_chat);
								btn.setText("Chat");
							}
						});
						
						btn_mode=1;
					}else{
						btn_mode=2;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
		
	}
}

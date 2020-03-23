package cc0x29a.specialchat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class ContactDetailActivity extends AppCompatActivity{
	static String ta_id;
	static String ta_phone;
	static String ta_name;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_detail);

		Bundle bundle = this.getIntent().getExtras();
		ta_id= (bundle != null) ? bundle.getString("user_id") : null;
		
		TextView tv_user_id=findViewById(R.id.detail_userID);
		tv_user_id.setText(ta_id);
		
		findViewById(R.id.btn_add_or_chat).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				//add contact
				SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
				String user_id=preferences.getString("user_id",null);
				String token_key=preferences.getString("token_key",null);
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
						//todo insert data into contact list
						Toast.makeText(ContactDetailActivity.this,"Succeed!"+ta_name+ta_phone,Toast.LENGTH_SHORT).show();
					}
//					Toast.makeText(ContactDetailActivity.this,"Succeed!"+ta_name+ta_phone,Toast.LENGTH_SHORT).show();
				
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
		});
		
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
						ta_name="Cuberesfsf";
						ta_phone="124332525";
						Toast.makeText(getApplicationContext(),"Error!\nData Null!",Toast.LENGTH_LONG).show();
						Looper.loop();
					}
				}catch(JSONException|NullPointerException e){
					e.printStackTrace();
					Looper.prepare();
					ta_name="Cuberesfsf";
					ta_phone="124332525";
					Toast.makeText(getApplicationContext(),"Error!\nData Null! (Exception)",Toast.LENGTH_LONG).show();
					Looper.loop();
				}
			}
		}.start();
		
	}
}

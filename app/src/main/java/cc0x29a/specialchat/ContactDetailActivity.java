package cc0x29a.specialchat;

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
				//todo
				Toast.makeText(ContactDetailActivity.this,"fine",Toast.LENGTH_SHORT).show();
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
					socket.delay=3;
					JSONObject data=socket.startSocket();
					if(data!=null && data.getString("status").equals("true")){
						TextView tv_user_name=findViewById(R.id.detail_userName);
						TextView tv_user_phone=findViewById(R.id.detail_userPhone);
						tv_user_name.setText(data.getString("user_name"));
						tv_user_phone.setText(data.getString("user_phone"));
					}else{
						Looper.prepare();
						Toast.makeText(getApplicationContext(),"Error!\nData Null!",Toast.LENGTH_LONG).show();
						Looper.loop();
					}
				}catch(JSONException|NullPointerException e){
					e.printStackTrace();
					Looper.prepare();
					Toast.makeText(getApplicationContext(),"Error!\nData Null!",Toast.LENGTH_LONG).show();
					Looper.loop();
				}
			}
		}.start();
		
	}
}

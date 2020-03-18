package cc0x29a.specialchat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class SearchNewContact extends AppCompatActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_new_contact);
		
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		final String user_id=preferences.getString("user_id",null);
		final String token_key=preferences.getString("token_key",null);
		
		findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Toast.makeText(SearchNewContact.this,"start...soon...",Toast.LENGTH_LONG).show();
				
				EditText et_uid=findViewById(R.id.search_user_id);
				String uid=et_uid.getText().toString();
				
				//todo start socket
				if(null!=user_id && null!=token_key){
					SocketWithServer socket=new SocketWithServer();
					socket.DataSend="{" +
							"'client':'SCC-1.0'," +
							"'action':'0009'," +
							"'token_key':'"+token_key+"'," +
							"'user_id':'"+user_id+"'," +
							"'search_id':'"+uid+"'" +
							"}";
					JSONObject data=socket.startSocket();
					// todo: continue.
				}else{
					Toast.makeText(SearchNewContact.this,"login info error!",Toast.LENGTH_LONG).show();
				}
			}
		});
		
		//todo recycleView
	}
}

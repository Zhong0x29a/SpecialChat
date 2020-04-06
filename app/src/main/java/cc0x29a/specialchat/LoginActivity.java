package cc0x29a.specialchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * server:          scs.0x29a.cc:21027
 *
 * Send:{
 *     client:      SCC-1.0, //Special Chat Client
 *     action:      0002,
 *     user_id:     [user_id],
 *     password:    md5([password]+[user_id]),
 * }
 *
 * get:{
 *     status:      true|false,
 *     user_id:     [user_id],
 *     user_name:   [user_name],
 *     token_key:   [token_key],
 *     login_time:  [login_time],
 * }
 *
 * check v
 * **/
public class LoginActivity extends AppCompatActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		setContentView(R.layout.activity_login);
		super.onCreate(savedInstanceState);
		
		final EditText ET_user_id=findViewById(R.id.text_user_id);
		final EditText ET_password=findViewById(R.id.text_password);
		
		findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Toast.makeText(LoginActivity.this,"Plz wait a moment...",Toast.LENGTH_SHORT).show();
				
				String IdOrPhone=ET_user_id.getText().toString();
				
				String user_id="";
				
				if(IdOrPhone.length()>8){
					String data_send="{" +
							"'client':'SCC-1.0'," +
							"'action':'0012'," +
							"'secret':'I love you.'," +
							"'user_phone':'"+IdOrPhone+"'" +
							"}";
					SocketWithServer socket=new SocketWithServer();
					socket.DataSend=data_send;
					JSONObject data=socket.startSocket();
					try{
						if(data.getString("status").equals("true") && !(user_id=data.getString("user_id")).equals("")){
							Toast.makeText(LoginActivity.this,"Using mobile phone to login.",Toast.LENGTH_SHORT).show();
						}
					}catch(Exception e){
						e.printStackTrace();
						Toast.makeText(LoginActivity.this,"Something Error.",Toast.LENGTH_LONG).show();
						return;
					}
				}else{
					user_id=IdOrPhone;
				}
				
				final String password=MyTools.md5(ET_password.getText().toString()+user_id);
				
				if(user_id.equals("") || password==null
						|| ET_password.getText().toString().equals("")){
					Toast.makeText(LoginActivity.this,
							"Please input your ID and password!",Toast.LENGTH_SHORT).show();
					return;
				}
				
				// start login
				final String dataToSend="{" +
						"\"client\":\"SCC-1.0\"," +
						"\"action\":\"0002\"," +
						"\"user_id\":\""+user_id+"\"," +
						"\"password\":\""+password+"\"" +
						"}";
				
				SocketWithServer SWS=new SocketWithServer();
				SWS.DataSend=dataToSend;
				JSONObject data=SWS.startSocket();
				
				/* wait 888ms */
				try{
					Thread.sleep(888);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				
				try{
					if(data==null){
						Toast.makeText(LoginActivity.this,"Login failed! \n" +
								"Please check your Network setting. ",Toast.LENGTH_LONG).show();
					}else if(data.getString("status").equals("true")){
						SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
						SharedPreferences.Editor editor=preferences.edit();
						
						editor.putString("user_id",data.getString("user_id")+"");
						editor.putString("user_name",data.getString("user_name")+"");
						editor.putString("user_phone",data.getString("user_phone")+"");
						editor.putString("token_key",data.getString("token_key")+"");
						editor.putString("login_time",data.getString("login_time")+"");
						editor.putInt("is_login",1);
						editor.apply();
						
						Toast.makeText(LoginActivity.this,"Login succeed! \n" +
								"Enjoy your time~",Toast.LENGTH_LONG).show();
						
						startActivity(new Intent(LoginActivity.this, MainActivity.class));
						finish();
					}else if(data.getString("status").equals("false")){
						ET_password.getText().clear();
						Toast.makeText(LoginActivity.this,
								"Login failed! \n" +
								"Please check your ID number and password.",Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(LoginActivity.this,
								"Unknown ERROR! (LA0002)",
								Toast.LENGTH_LONG).show();
					}
				}catch(JSONException e){
					e.printStackTrace();
					Toast.makeText(LoginActivity.this,
							"Unknown ERROR! (LA0002+)",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		
		// check v
		findViewById(R.id.btn_forget).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Toast.makeText(LoginActivity.this,"Sorry! \nPlz contact the admin!",Toast.LENGTH_LONG).show();
			}
		});
		
		findViewById(R.id.btn_signUp).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
			}
		});
		
		findViewById(R.id.btn_signUp).setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(View v){
				Toast.makeText(LoginActivity.this,"Don't be hesitate! ",Toast.LENGTH_LONG).show();
				return false;
			}
		});
		
	}
	
	public void onStart(){
		super.onStart();
		EditText et_user_id=findViewById(R.id.text_user_id);
		SharedPreferences preferences=getSharedPreferences("sign_up_info",MODE_PRIVATE);
		String uid=preferences.getString("user_id",null);
		
		et_user_id.setText(uid);
		
	}
}

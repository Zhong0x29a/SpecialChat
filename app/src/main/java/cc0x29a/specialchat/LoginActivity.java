package cc0x29a.specialchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

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
 * **/
public class LoginActivity extends AppCompatActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				EditText ET_user_id=findViewById(R.id.text_user_id);
				EditText ET_password=findViewById(R.id.text_password);
				
				final String user_id=ET_user_id.getText().toString();
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
				SWS.startSocket();
				
				/* wait 888ms */
				try{
					Thread.sleep(888);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				
				try{
					if(SWS.DataJsonReturn==null){
						Toast.makeText(LoginActivity.this,"Login failed! \n" +
								"Please check your Network setting. ",Toast.LENGTH_LONG).show();
					}else if(SWS.DataJsonReturn.getString("status").equals("true")){
						SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
						SharedPreferences.Editor editor=preferences.edit();
						
						editor.putString("user_id",SWS.DataJsonReturn.getString("user_id")+"");
						editor.putString("user_name",SWS.DataJsonReturn.getString("user_name")+"");
						editor.putString("token_key",SWS.DataJsonReturn.getString("token_key")+"");
						editor.putString("login_time",SWS.DataJsonReturn.getString("login_time")+"");
						editor.putInt("is_login",1);
						editor.apply();
						
						Toast.makeText(LoginActivity.this,"Login succeed! \n" +
								"Enjoy your time~",Toast.LENGTH_LONG).show();
						
						Intent intent=new Intent(LoginActivity.this, MainActivity.class);
						startActivity(intent);
					}else if(SWS.DataJsonReturn.getString("status").equals("false")){
						ET_password.getText().clear();
						Toast.makeText(LoginActivity.this,"Login failed! \n" +
								"Please check your ID number and password.",Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(LoginActivity.this,"Unknown ERROR! (LA0002)",
								Toast.LENGTH_LONG).show();
					}
				}catch(JSONException e){
					e.printStackTrace();
					Toast.makeText(LoginActivity.this,"Unknown ERROR! (LA0002+)",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		
		findViewById(R.id.btn_forget).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Toast.makeText(LoginActivity.this,"Sorry! \nPlz contact the admin!",Toast.LENGTH_LONG).show();
			}
		});
		
		findViewById(R.id.btn_signUp).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
				startActivity(intent);
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
	
}

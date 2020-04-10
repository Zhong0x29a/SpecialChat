package cc0x29a.specialchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		refreshNewID();
		
		findViewById(R.id.signUp_btn_new_user_id).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				refreshNewID();
			}
		});
		
		findViewById(R.id.btn_signUp).setOnClickListener(new View.OnClickListener(){
			@SuppressLint("ApplySharedPref")
			@Override
			public void onClick(View v){
				if(checkInfo() && checkInviteCode()){
					Toast.makeText(SignUpActivity.this,"Plz wait a second...",Toast.LENGTH_SHORT).show();
					
					EditText et_user_phone=findViewById(R.id.sign_user_phone);
					EditText et_user_name=findViewById(R.id.sign_user_name);
					EditText et_password=findViewById(R.id.sign_password);
					TextView et_user_id=findViewById(R.id.sign_user_id);
					EditText et_invite_code=findViewById(R.id.sign_invite_code);

					final String user_id=et_user_id.getText().toString();
					String user_phone=et_user_phone.getText().toString();
					String user_name=MyTools.filterSpecialChar(et_user_name.getText().toString());
					String password=MyTools.md5(et_password.getText().toString()+user_id);
					String invite_code=et_invite_code.getText().toString();
					
					SocketWithServer socket=new SocketWithServer();
					
					String DataSend="{" +
							"\"client\":\"SCC-1.0\"," +
							"\"action\":\"0006\"," +
							"\"user_name\":\""+user_name+"\"," +
							"\"user_id\":\""+user_id+"\"," +
							"\"password\":\""+password+"\"," +
							"\"user_phone\":\""+user_phone+"\"," +
							"\"invite_code\":\""+invite_code+"\"," +
							"\"secret\":\"I love you.\"" +
							"}";
					
					@SuppressLint("HandlerLeak")
					Handler handler=new Handler(){
						@Override
						public void handleMessage(Message msg){
							try{
								JSONObject data=new JSONObject(msg.obj.toString());
								if(data.getString("status").equals("true")){
									Toast.makeText(SignUpActivity.this,
											"Congratulations!!! \n" +
													"You are now one of Special Chat's VIPs!! ",Toast.LENGTH_LONG).show();
									
									SharedPreferences preferences=getSharedPreferences("sign_up_info",MODE_PRIVATE);
									SharedPreferences.Editor editor=preferences.edit();
									
									editor.putString("user_id",user_id);
									editor.apply();
									
									startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
									finish();
								}else if(data.getString("status").equals("false")){
									Toast.makeText(SignUpActivity.this,"Perhaps server made a mistake...",Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(SignUpActivity.this,"Unknown error(1006+85)",Toast.LENGTH_SHORT).show();
								}
							}catch(JSONException e){
								e.printStackTrace();
							}
						}
					};
					
					try{
						socket.startSocket(DataSend,handler);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	/**
	 * Check information
	 * @return boolean , whether info is legal
	 */
	private boolean checkInfo(){
		try{
			TextView user_id=findViewById(R.id.sign_user_id);
			
			if(user_id.getText().toString().equals("")){
				Toast.makeText(SignUpActivity.this,"id error\ntry a new one",Toast.LENGTH_SHORT).show();
				return false;
			}
			
			EditText user_phone=findViewById(R.id.sign_user_phone);
			EditText user_name=findViewById(R.id.sign_user_name);
			EditText password=findViewById(R.id.sign_password);
			EditText password_confirm=findViewById(R.id.sign_confirm_password);
			
			Pattern pattern_password=Pattern.compile("^[a-zA-Z0-9._]{8,20}$");
			Matcher m_pass=pattern_password.matcher(password.getText().toString());
			
			Pattern pattern_user_name=Pattern.compile("^[a-zA-Z0-9\\u4e00-\\u9fa5_\\-.。?？!！()\\ ]{2,10}$");
			Matcher m_um=pattern_user_name.matcher(user_name.getText().toString());
			
			Pattern pattern_user_phone=Pattern.compile("^[1]([3-9])[0-9]{9}$");
			Matcher m_ph=pattern_user_phone.matcher(user_phone.getText().toString());
			
			if(!m_ph.matches()){
				Toast.makeText(SignUpActivity.this,"The phone number you input is illegal! ",Toast.LENGTH_LONG).show();
				return false;
			}else if(!m_um.matches()){
				Toast.makeText(SignUpActivity.this,"User name should only be \n"+
						"(a-zA-Z0-9\u4e00-\u9fa5_-.。?？!！() )\nand 1~10 bits!", Toast.LENGTH_LONG).show();
				return false;
			}else if(!m_pass.matches()){
				Toast.makeText(SignUpActivity.this,"Password should only be '0-9a-zA-Z._' and 8~20 bits !",Toast.LENGTH_LONG).show();
				return false;
			}else if(!password.getText().toString().equals(password_confirm.getText().toString())){
				Toast.makeText(SignUpActivity.this,"Two password do not match! ! ",Toast.LENGTH_SHORT).show();
				return false;
			}else{
				return true;
			}
		}catch(NullPointerException e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Verify invite code
	 * @return boolean
	 */
	private boolean checkInviteCode(){
		try{
			EditText ic=findViewById(R.id.sign_invite_code);
			String i_code=ic.getText().toString();
			if(!i_code.equals("0x29a.cc")){
				Toast.makeText(SignUpActivity.this,"Your Invite Code is not correct! \n"+
						"For further Info., Plz contact the admin! ",Toast.LENGTH_SHORT).show();
				return false;
			}
			return true;
		}catch(NullPointerException e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Create a new ID , 8 bits
	 * @return String , new ID
	 */
	private void refreshNewID() {
		try{
			StringBuilder sb=new StringBuilder();
			int[] pool=new int[]{0,1,2,3,4,5,6,7,8,9};
			sb.append(1);
			for(int i=1;i<=7;i++){
				Thread.sleep(128);
				sb.append(pool[MyTools.getRandomNum(10,1)-1]);
			}
			String user_id=sb.toString();
			
			SocketWithServer socket=new SocketWithServer();
			
			String DataSend="{'action':'0005','user_id':'"+user_id+"'}";
			
			@SuppressLint("HandlerLeak")
			Handler handler=new Handler(){
				@Override
				public void handleMessage(Message msg){
					try{
						JSONObject data=new JSONObject(msg.obj.toString());
						if(data.getString("status").equals("true")){
							TextView textView=findViewById(R.id.sign_user_id);
							textView.setText(data.getString("new_id"));
						}else{
							Thread.sleep(200);
							refreshNewID();
						}
					}catch(JSONException|InterruptedException e){
						Toast.makeText(SignUpActivity.this,"Network error!!",Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
			};
			
			socket.startSocket(DataSend,handler);
			
		}catch(Exception e){
			Toast.makeText(SignUpActivity.this,"Network error",Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
}

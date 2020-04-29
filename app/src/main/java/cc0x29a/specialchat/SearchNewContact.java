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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
				
				EditText et_uid=findViewById(R.id.search_user_id);
				final String uid=et_uid.getText().toString();
				
				if(uid.equals("")){
					Toast.makeText(SearchNewContact.this,"Please input user id or phone. ",Toast.LENGTH_SHORT).show();
					return;
				}
				
				Pattern p_id=Pattern.compile("^[0-9]{4,11}$");
				Matcher m_id=p_id.matcher(uid);
				if(!m_id.matches()){
					Toast.makeText(SearchNewContact.this,"Search key should be 0-9 and 4-11 bits! ",Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(null==user_id && null==token_key){
					Toast.makeText(SearchNewContact.this,"login info error!",Toast.LENGTH_LONG).show();
					return;
				}
				
				new Thread(new Runnable(){
					@Override
					public void run(){
						String DataSend="{" +
								"'client':'SCC-1.0'," +
								"'action':'0009'," +
								"'token_key':'"+token_key+"'," +
								"'user_id':'"+user_id+"'," +
								"'search_id':'"+uid+"'" +
								"}";
						
						SocketWithServerService.DataManager dataManager=new SocketWithServerService.DataManager();
						final String dataStr=dataManager.startRequest(DataSend);
//						final String dataStr=SocketWithServerService.sendData(DataSend);
						
						SearchNewContact.this.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								try{
									JSONObject data_temp=new JSONObject(dataStr);
									if(data_temp.getString("status").equals("true")){
										int number=Integer.parseInt(data_temp.getString("number"));
										String[][] data=new String[number][2];
										//to do: debug
										JSONObject temp;
										for(int i=0;i<number;i++){
											temp=new JSONObject(data_temp.getString("index_"+(i+1)));
											data[i][0]=temp.getString("user_id");
											data[i][1]=MyTools.resolveSpecialChar(temp.getString("user_name"));
										}
										
										RecyclerView recyclerView=findViewById(R.id.search_recyclerView);
										LinearLayoutManager layoutManager=new LinearLayoutManager(SearchNewContact.this);
										recyclerView.setLayoutManager(layoutManager);
										
										SearchContactAdapter adapter=new SearchContactAdapter(data);
										adapter.count=number;
										
										recyclerView.setAdapter(adapter);
										recyclerView.setItemAnimator(new DefaultItemAnimator());
									}else{
										Toast.makeText(SearchNewContact.this,"Perhaps Network made a mistake? ",Toast.LENGTH_SHORT).show();
									}
								}catch(JSONException e){
									e.printStackTrace();
								}
							}
						});
						
					}
				}).start();
			}
		});
	}
}

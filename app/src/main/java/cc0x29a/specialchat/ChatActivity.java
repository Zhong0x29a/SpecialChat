package cc0x29a.specialchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.prefs.Preferences;

public class ChatActivity extends AppCompatActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		Bundle bundle = this.getIntent().getExtras();
		String user_id=null;
		if(bundle != null){
			user_id=bundle.getString("user_id");
		}
		if( user_id != null && !user_id.equals("")){
			// todo: now can only fetch 20 pieces of messages!! need to complete.
//			MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(ChatActivity.this,
//					"msg_"+user_id+".db",1);
			MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(ChatActivity.this,
					"msg_2950.db",1);
			String[][] record=msgSQLiteHelper.getChatRecord(msgSQLiteHelper.getReadableDatabase(),0);
			
			SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
			String my_id=preferences.getString("user_id",null);
			
			ChatRecordAdapter chatRecordAdapter=new ChatRecordAdapter(ChatActivity.this);
			chatRecordAdapter.count=Integer.parseInt(record[0][0]);
			chatRecordAdapter.messages=record;
			chatRecordAdapter.my_id=my_id;
			
			ListView chat_listView=findViewById(R.id.chatWindow_listView);
			chat_listView.setAdapter(chatRecordAdapter);
			
			chat_listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent,View view,int position,long id){
					position++;
					// Noting to do here.
				}
			});
			
			chat_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id){
					position++;
					// todo add a menu to delete msg here.
					return true;
				}
			});
			
		}else{
			Toast.makeText(ChatActivity.this,"ERROR! ",Toast.LENGTH_LONG).show();
			finish();
		}
		
	}
}

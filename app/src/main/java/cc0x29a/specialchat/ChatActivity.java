package cc0x29a.specialchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		Bundle bundle = this.getIntent().getExtras();
		String user_id = bundle.getString("user_id");
		
		if(!user_id.isEmpty()){
			// todo: fetch chat record by user_id !!
			MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(ChatActivity.this,
					"msg_"+user_id,1);
			String[][] record=msgSQLiteHelper.getChatRecord(msgSQLiteHelper.getReadableDatabase(),0);
			
			// todo: show views, show record, use adapter!!
			
		}else{
			Toast.makeText(ChatActivity.this,"ERROR! ",Toast.LENGTH_LONG);
			finish();
		}
	}
}

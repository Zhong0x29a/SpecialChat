package cc0x29a.specialchat;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ContactDetailActivity extends AppCompatActivity{
	static String ta_id;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_detail);
		//todo: fetch detail
		Bundle bundle = this.getIntent().getExtras();
		ta_id= (bundle != null) ? bundle.getString("user_id") : null;
		
		Toast.makeText(ContactDetailActivity.this,ta_id+"",Toast.LENGTH_LONG).show();
	}
}

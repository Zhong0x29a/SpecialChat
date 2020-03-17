package cc0x29a.specialchat;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SearchNewContact extends AppCompatActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_new_contact);
		
		
		findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Toast.makeText(SearchNewContact.this,"start...soon...",Toast.LENGTH_LONG).show();
				//todo here
			}
		});
		
		//todo recycleView
	}
}

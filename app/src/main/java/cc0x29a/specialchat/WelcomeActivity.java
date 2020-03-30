package cc0x29a.specialchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_welcome);
		
		
		final TextView tv=findViewById(R.id.welcome_tv);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run(){
				tv.setText("Welcome\n \n \n \n");
			}
		}, 888);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run(){
				tv.setText("Welcome\nto\n \n \n");
			}
		}, 1666);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run(){
				tv.setText("Welcome\nto\nSpecial Chat\n \n");
			}
		}, 2888);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run(){
				tv.setText("Welcome\nto\nSpecial Chat\n1.0\n");
			}
		}, 3888);
		
		new Handler().postDelayed(new Runnable() {
			@Override
            public void run(){
				findViewById(R.id.btn_start_specialchat).setVisibility(View.VISIBLE);
			}
        }, 5222);
		
		findViewById(R.id.btn_start_specialchat).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				SharedPreferences preferences=getSharedPreferences("init_info",MODE_PRIVATE);
				SharedPreferences.Editor e=preferences.edit();
				e.putString("first_run","no");
				e.apply();
				startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
				finish();
			}
		});
	}
}

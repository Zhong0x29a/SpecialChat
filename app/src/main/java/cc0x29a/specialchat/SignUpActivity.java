package cc0x29a.specialchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.os.Bundle;

import java.nio.IntBuffer;

import static java.lang.System.exit;

public class SignUpActivity extends AppCompatActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		String new_user_id;
		if((new_user_id=createNewId(0))!=null){
//			findViewById(R.id.signUp_new_user_id)..setText(new_user_id);//
		}
	}
	
	public String createNewId(int t) {
		t++;
		try{
			StringBuilder sb=new StringBuilder();
			int[] pool=new int[]{0,1,2,3,4,5,6,7,8,9};
			sb.append(1);
			for(int i=1;i<=7;i++){
				Thread.sleep(16);
				sb.append(pool[MyTools.getRandomNum(10,1)-1]);
			}
			return sb.toString();
		}catch(InterruptedException e){
			e.printStackTrace();
			if(t<5){
				return createNewId(t);
			}else{
				return null;
			}
		}
	}
}

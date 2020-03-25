package cc0x29a.specialchat;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Some often used tools packed in methods below.
 * @author  Zhong Wenliang
 * mail    CuberWenliang@0x29a.cc
 * date    20.03
 */

class MyTools{
	
	/**
	 * Get view's heigth or width
	 * @param view a view to be measured
	 * @param isHeight height or width
	 * @return a integer
	 */
	public static int getViewHeight(View view,boolean isHeight){
		int result;
		if(view==null)return 0;
		if(isHeight){
			int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
			view.measure(h,0);
			result =view.getMeasuredHeight();
		}else{
			int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
			view.measure(0,w);
			result =view.getMeasuredWidth();
		}
		return result;
	}
	
	/**
	 * make a simple toast
	 * @param info information to show
	 */
	static void showToast(final Context context,final String info,final int duration) throws Exception {
		Handler toastHandler=new Handler();
		toastHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, info,
						+duration).show();
			}
		});
	}
	
	/**
	 * Filter Some not good chars
	 * @param toBeFilter String to bu filter
	 * @return a String
	 */
	static String filterSpecialChar(String toBeFilter){
		if(toBeFilter!=null && (!toBeFilter.isEmpty())){
			return toBeFilter.replaceAll("\"","&#34;")
					.replaceAll("'","&#39;")
					.replaceAll(" ","&#32;")
					.replaceAll("%","&#37;")
					.replaceAll("\\(","&#40;")
					.replaceAll("\\)","&#41;")
					.replaceAll("\\{","&#123;")
					.replaceAll("\\}","&#123;")
					.replaceAll("\\*","&#42;")
					.replaceAll("/","&#47;")
					.replaceAll("\\\\","&#92;")
					.replaceAll("\\.","&#46;")
					.replaceAll(",","&#44;")
					.replaceAll("!","&#33;");
		}else{
			return "";
		}
	}
	
	/**
	 * Resolve the char back
	 * @param toBeResolved String to be resolved
	 * @return resolved string
	 */
	static String resolveSpecialChar(String toBeResolved){
		if(toBeResolved!=null && (!toBeResolved.isEmpty())){
			return toBeResolved.replaceAll("&#34;","\"")
					.replaceAll("&#39;","'")
					.replaceAll("&#32;"," ")
					.replaceAll("&#37;","%")
					.replaceAll("&#40;","(")
					.replaceAll("&#41;",")")
					.replaceAll("&#123;","{")
					.replaceAll("&#123;","}")
					.replaceAll("&#42;","*")
					.replaceAll("&#47;","/")
					.replaceAll("&#92;","\\")
					.replaceAll("&#46;",".")
					.replaceAll("&#44;",",")
					.replaceAll("&#33;","!");
		}else{
			return "";
		}
	}
	
	/**
	 * Get a random number between max and min
	 * @param max max number
	 * @param min min number
	 * @return an integer
	 */
	static int getRandomNum(int max,int min){
		return (int)(1+Math.random()*(max-min+1));
	}
	
	/**
	 * Get current timestamp
	 * @return current timestamp (integer)
	 * **/
	static int getCurrentTime(){
		return (int)(System.currentTimeMillis()/1000);
	}
	
	/**
	 * Use md5 encrypt input String. Found By the Internet
	 * @param string to be encrypt
	 * @return String
	 * **/
	static String md5(String string){
		if(TextUtils.isEmpty(string)){
			return null;
		}
		try{
			byte[] bytes = MessageDigest.getInstance("MD5").digest(string.getBytes());
			StringBuilder result =new StringBuilder();
			for (byte b : bytes) {
				String temp = Integer.toHexString(b & 0xff);
				if (temp.length() == 1) {
					temp = "0" + temp;
				}
				result.append(temp);
			}
			return result.toString();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return null;
	}
	
}

package cc0x29a.specialchat;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Some often used tools packed in methods below.
 * @author  Zhong Wenliang
 * @mail    CuberWenliang@163.com
 * @date    20.03
 */

public class MyTools{
	
	/**
	 * Get a random number between max and min
	 * @param max
	 * @param min
	 * @return
	 */
	public static int getRandomNum(int max,int min){
		return (int)(1+Math.random()*(max-min+1));
	}
	
	/**
	 * Get current timestamp
	 * @return current timestamp (integer)
	 * **/
	public static int getCurrentTime(){
		return (int)(System.currentTimeMillis()/1000);
	}
	
	/**
	 * Use md5 encrypt input String. Found By the Internet
	 * @param string
	 * @return String
	 * **/
	public static String md5(String string){
		if(TextUtils.isEmpty(string)){
			return null;
		}
		try{
			byte[] bytes = MessageDigest.getInstance("MD5").digest(string.getBytes());
			String result = "";
			for (byte b : bytes) {
				String temp = Integer.toHexString(b & 0xff);
				if (temp.length() == 1) {
					temp = "0" + temp;
				}
				result += temp;
			}
			return result;
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return null;
	}
	
}

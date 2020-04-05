package cc0x29a.specialchat.socketserver;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Some often used tools packed in methods below.
 * @author  Zhong Wenliang
 * @mail    CuberWenliang@163.com
 * @date    20.03
 */

class MyTools{
	
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
	 * Create a new token key
	 * @return new created token key
	 */
	static String createANewTokenKey(){
		StringBuffer stringBuffer=new StringBuffer();
		String[] charSets=new String[]{"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
		stringBuffer.append(charSets[getRandomNum(15,1)]);
		for(int i=1;i<=15;i++){
			stringBuffer.append(charSets[getRandomNum(16,1)-1]);
		}
		return stringBuffer.toString();
	}
	
	/**
	 * Get a random number between max and min
	 * @param max max number
	 * @param min min number SHOULD NOT BE 0!!!
	 * @return a integer
	 */
	static int getRandomNum(int max,int min){
		return (int)(1+Math.random()*(max-min+1));
	}
	
	/**
	 * Get current timestamp
	 * @return current timestamp (integer) , 10 bits
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
		if(string==null || string.isEmpty()){
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

package cc.specialchat.specialchatserver;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Base64;

/**
 * Created by Administrator on 2018/5/3.
 */
public class ServerThread extends Thread {
	
	private Socket socket;
	private StringBuffer DataReturn=new StringBuffer();
	JSONObject DataJsonReturn=null;
	static String msg;
	
	ServerThread(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		OutputStream outputStream = null;
		
		try {
			// get data
			inputStream = socket.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(inputStreamReader);
			
			String temp;
			while((temp=bufferedReader.readLine())!=null){
				DataReturn.append("\n").append(temp);
			}
			socket.shutdownInput();
			
			DataJsonReturn=JSONObject.parseObject(DataReturn.toString());
			//todo : do action.
			System.out.println(DataJsonReturn.getString("action"));
			String user_id,password,token_key;
			
			switch(DataJsonReturn.getString("action")){
				case "0001": // check login status
					user_id=DataJsonReturn.getString("user_id");
					token_key=DataJsonReturn.getString("token_key");
					if(UserInfoSQLite.verifyUserTokenKey(user_id,token_key)){
						msg="{\"status\":\"true\"}";
					}
					break;
				case "0002": // go login
				user_id=DataJsonReturn.getString("user_id");
					password=DataJsonReturn.getString("password");
					String[] user_info=UserInfoSQLite.goLogin(user_id,password);
					if(user_info[0].equals("")){
						msg="{\"status\":\"false\"}";
					}else if(user_id!=null){
						msg="{\"status\":\"true\"," +
								"\"user_id\":\""+user_info[0]+"\"," +
								"\"user_name\":\""+user_info[1]+"\"," +
								"\"token_key\":\""+user_info[2]+"\"," +
								"\"login_time\":\""+user_info[3]+"\"" +
								"}";
					}else{
						msg="{\"msg\":\"What's Wrong?? (1002)\"}";
					}
					break;
				case "0003":
					user_id=DataJsonReturn.getString("user_id");
					token_key=DataJsonReturn.getString("token_key");
					if(UserInfoSQLite.verifyUserTokenKey(user_id,token_key)){
						//todo : ...complete
						String[][] msg_temp=MsgCacheSQLite.fetchMsg(user_id);
					}
					break;
				default:
					msg="{\"msg\":\"ERROR!! (1000)\"}";
					break;
			}
			System.out.println(DataReturn);
			
			// send
			outputStream = socket.getOutputStream();
			outputStream.write(msg.getBytes(StandardCharsets.UTF_8));
			outputStream.flush();
			
			socket.shutdownOutput();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			// release resource
			try{
				if(outputStream != null){
					outputStream.close();
				}
				if(bufferedReader != null){
					bufferedReader.close();
				}
				if(inputStreamReader != null){
					inputStreamReader.close();
				}
				if(inputStream != null){
					inputStream.close();
				}
				if(socket != null){
					socket.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
}

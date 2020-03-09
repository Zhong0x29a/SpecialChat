package cc.specialchat.specialchatserver;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Created by Administrator on 2018/5/3.
 */
public class ServerThread extends Thread {
	
	private Socket socket;
	private StringBuffer DataReturn=new StringBuffer();
	JSONObject DataJsonReturn=null;
	
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
			System.out.println(DataReturn);
			
			
			// send
			String msg="{" +
					"\"status\":\"true\"," +
					"\"user_id\":\"12414\"," +
					"\"user_name\":\"Hello world\"," +
					"\"token_key\":\"trufdse\"," +
					"\"login_time\":\"trfdue\"" +
					"}";
			
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

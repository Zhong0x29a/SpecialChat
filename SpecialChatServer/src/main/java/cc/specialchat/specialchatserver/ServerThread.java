package cc.specialchat.specialchatserver;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by Administrator on 2018/5/3.
 */
public class ServerThread extends Thread {
	
	private Socket socket;
	private StringBuffer DataReturn=new StringBuffer();
	private static String msgSend;
	
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
			
			JSONObject dataJsonReturn=JSONObject.parseObject(DataReturn.toString());
			String user_id,password,token_key;
			
			System.out.println(dataJsonReturn.getString("action"));
			switch(dataJsonReturn.getString("action")){
				case "0001": // check login status
					msgSend=ProcessAction.action_0001(dataJsonReturn);
					break;
				case "0002": // perform login
					msgSend=ProcessAction.action_0002(dataJsonReturn);
					break;
				case "0003": // client refresh message
					msgSend=ProcessAction.action_0003(dataJsonReturn);
					break;
				case "0004":
					msgSend=ProcessAction.action_0004(dataJsonReturn);
					break;
				default:
					msgSend="{\"msg\":\"ERROR!! (1000)\"}";
					break;
			}
			System.out.println(DataReturn);
			
			// send
			outputStream = socket.getOutputStream();
			outputStream.write(msgSend.getBytes(StandardCharsets.UTF_8));
			outputStream.flush();
			
			socket.shutdownOutput();
		}catch(IOException|NullPointerException e){
			e.printStackTrace();
		}finally{
			// Release resource
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

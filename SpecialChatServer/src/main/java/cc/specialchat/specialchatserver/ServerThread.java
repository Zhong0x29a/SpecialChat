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
	private StringBuffer DataGet=new StringBuffer();
	
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
			// get data from client
			inputStream = socket.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(inputStreamReader);
			
			String temp;
			while((temp=bufferedReader.readLine())!=null){
				DataGet.append("\n").append(temp);
			}
			socket.shutdownInput();
			
			JSONObject dataJsonReturn=JSONObject.parseObject(DataGet.toString());
			
			System.out.println(dataJsonReturn.getString("action"));
			
			String msgSend;
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
				case "0004": // send message
					msgSend=ProcessAction.action_0004(dataJsonReturn);
					break;
				case "0005": // check ID usability
					msgSend=ProcessAction.action_0005(dataJsonReturn);
					break;
				case "0006": // sign up new account
					msgSend=ProcessAction.action_0006(dataJsonReturn);
					break;
				case "0007": // add new contact
					msgSend=ProcessAction.action_0007(dataJsonReturn);
					break;
				case "0008": // fetch contact info.
					msgSend=ProcessAction.action_0008(dataJsonReturn);
					break;
				case "0009": // search contact.
					msgSend=ProcessAction.action_0009(dataJsonReturn);
					break;
				default: // action code error.
					msgSend="{\"msg\":\"ERROR!! (1000)\"}";
					break;
			}
			System.out.println(DataGet);
			
			//test code:
//			msgSend="{\"status\":\"true\"}";
			
			// send data to client
			if(msgSend!=null && !msgSend.equals("")){
				outputStream=socket.getOutputStream();
				//assert msgSend!=null;
				outputStream.write(msgSend.getBytes(StandardCharsets.UTF_8));
				outputStream.flush();
				
				socket.shutdownOutput();
			}
		}catch(IOException|NullPointerException e){
			e.printStackTrace();
		}finally{
			// Release unused resource
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

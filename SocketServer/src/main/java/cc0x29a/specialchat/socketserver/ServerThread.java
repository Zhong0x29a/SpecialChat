package cc0x29a.specialchat.socketserver;

//import com.alibaba.fastjson.JSONObject;

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
			
			
			String msgSend="";
			
			// send data to client
			outputStream=socket.getOutputStream();
			//assert msgSend!=null;
			outputStream.write(msgSend.getBytes(StandardCharsets.UTF_8));
			outputStream.flush();
			
			socket.shutdownOutput();
			
		}catch(Exception e){
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

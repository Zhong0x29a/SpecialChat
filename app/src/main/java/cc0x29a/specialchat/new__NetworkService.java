package cc0x29a.specialchat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class new__NetworkService extends Service{
	public new__NetworkService(){
	}
	
	@Override
	public IBinder onBind(Intent intent){
		// TO DO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	static Socket socket;
	String DataSend=null;
	private StringBuffer DataReturn=new StringBuffer();
	private JSONObject DataJsonReturn=null;
	int delay=5;
	
	@Override
	public void onCreate(){
		try{
			socket=new Socket("specialchat.0x29a.cc", 21027);
			//	Socket socket = new Socket("192.168.1.18", 21027);
			socket.setSoTimeout(5000);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void onDestroy(){
		try{
			socket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public JSONObject startSocket(){
		new Thread(){
			@Override
			public void run() {
				try {
					
					// Output, send data to server.
					OutputStream os = socket.getOutputStream();
					os.write(DataSend.getBytes(StandardCharsets.UTF_8));
					os.flush();
					os.close();
					socket.shutdownOutput();
					
					
					// Input, receive data from server.
					BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
					DataReturn.append(br.readLine());
					String temp;
					while((temp=br.readLine())!=null){
						DataReturn.append("\n").append(temp);
					}
					br.close();
					
					System.out.println(DataReturn.toString());
					
					if(DataReturn!=null){
						DataJsonReturn=new JSONObject(DataReturn.toString());
					}
				}catch(IOException|JSONException|NullPointerException e){
					DataReturn=null;
					e.printStackTrace();
				}
			}
		}.start();
		
		int startTime=MyTools.getCurrentTime();
		while(MyTools.getCurrentTime()<startTime+delay){
			if(DataJsonReturn!=null){
				return DataJsonReturn;
			}
		}
		
		return DataJsonReturn;
		
	}
	
}
/*
public class SocketThread extends Thread{
		
		Socket socket;
		String dataSend="onCreateConnection";
		JSONObject dataReturn;
		
		@Override
		public void run(){
			try{
				socket=new Socket("192.168.1.18",21027);
				socket.setSoTimeout(5000);
				while(!socket.isClosed()){
					// Output, send data to server.
					OutputStream os = socket.getOutputStream();
					os.write(dataSend.getBytes(StandardCharsets.UTF_8));
					os.flush();
					socket.shutdownOutput();
					
					sleep(16000);
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(socket!=null){
						socket.close();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				// may overflow?
				new SocketThread().start();
			}
		}
	}
* */
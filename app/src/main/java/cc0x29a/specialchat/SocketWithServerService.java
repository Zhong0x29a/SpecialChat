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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketWithServerService extends Service{
	
	@Override
	public IBinder onBind(Intent intent){
		// TO DO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	static Socket socket;
	
	public static BufferedReader br;
	public static OutputStream os;
	
	public static heart heart;
	
	public static boolean isIOBusy;
	
//	private static List<Object> StartConnectionThreads;
	
	static boolean tryingConnect=false;
	
	
	@Override
	public void onCreate(){
		startService(new Intent(SocketWithServerService.this,NetworkService.class));
		
		new Thread(new Runnable(){
			@Override
			public void run(){
				StartConnection();
			}
		},"StartConnectionThread").start();
		
	}
	
	public void onDestroy(){
		try{
			closeSocket();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*
	* todo next ver：
	*   Verify the client at the first connection.
	* */
	static void StartConnection(){ //todo this need to be perfected.
		try{
			if(tryingConnect){ return; }
			tryingConnect=true;
			if(!isSocketOn()){
				try{
					closeSocket();
					System.out.println("Retry for new connection.");
					
					socket=new Socket();
//						socket.connect(new InetSocketAddress("server.specialchat.cn",21027),1111);
					socket.connect(new InetSocketAddress("192.168.1.18",21027),1111);
					
					socket.setSoTimeout(30000);
					
					//
					System.out.println("Connected.\n");
					
					br=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
					os=socket.getOutputStream();
					
					// Send "heartbeat" to server.
//					if(heart!=null){heart.interrupt();}
					
					heart=new heart();
					heart.start();
				}catch(IOException e){
					closeSocket();
//					e.printStackTrace();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			closeSocket();
		}finally{
			tryingConnect=false;
		}
	}
	
	/**
	 * @param data , the data send to server
	 * @return data returned from server.
	 */
	public static String sendData(String data){
		try{
			int startTime=MyTools.getCurrentTime();
			while(isIOBusy){
				Thread.sleep(333);
				if(MyTools.getCurrentTime() > startTime+4) {
					System.out.println("IO busy! ");
					return "{'Error':'IO busy! '}";
				}
			}
			
			isIOBusy=true;
			os.write((data+"\n").getBytes(StandardCharsets.UTF_8));
			String str=br.readLine();
			System.out.println(data+"\n"+str);
			isIOBusy=false;
			
			return str != null ? str : "{'network':'error'}";
		}catch(IOException|InterruptedException|NullPointerException e){
			new Thread(new Runnable(){
				@Override
				public void run(){
					StartConnection();
				}
			},"StartConnectionThread").start();
		}
		isIOBusy=false;
		return "{'network':'error'}";
	}
	
	public static class heart extends Thread{
		@Override
		public void run(){
			while(isSocketOn()){
				String dataStr=sendData("{'action':'beat'}");
				try{
					JSONObject data=new JSONObject(dataStr);
					if(!data.getBoolean("alive")){
						closeSocket();
					}
				}catch(JSONException e){
					e.printStackTrace();
					closeSocket();
					return;
				}
				try{
					sleep(28888);
				}catch(InterruptedException e){
//					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * @return  Weather the socket started.
	 */
	public static boolean isSocketOn(){
		if(socket == null){
			return false;
		}
		return !socket.isClosed();
	}
	
	public static void closeSocket(){
//		try{
//			NetworkService.manuallyStop();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		try{
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		}catch(NullPointerException|IOException e){
//			e.printStackTrace();
		}finally{
			socket=null;
		}
	}
	
}

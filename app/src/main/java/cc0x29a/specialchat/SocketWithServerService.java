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
	
	@Override
	public void onCreate(){
		new Thread(new Runnable(){
			@Override
			public void run(){
				StartConnect();
			}
		}).start();
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
	void StartConnect(){ //todo this need to be perfected.
		try{
			while(true){
				if(!isSocketOn()){
					try{
						System.out.println("Retry for new connection.");
						
						socket=new Socket();
//						socket.connect(new InetSocketAddress("server.specialchat.cn",21027),1111);
						socket.connect(new InetSocketAddress("192.168.1.18",21027),1111);
						socket.setSoTimeout(30000);
						
						br=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
						os=socket.getOutputStream();
						
						// Send "heartbeat" to server.
						if(heart!=null) {heart.interrupt();}
						
						heart=new heart();
						
						stopService(new Intent(SocketWithServerService.this,NetworkService.class));
						
						startService(new Intent(SocketWithServerService.this,NetworkService.class));
						
					}catch(IOException e){
						closeSocket();
						e.printStackTrace();
					}
				}
				try{
					Thread.sleep(6000);
				}catch(InterruptedException e){
					e.printStackTrace();
					return;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			try{
				closeSocket();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			try{
				Thread.sleep(6000);
			}catch(InterruptedException ee){
				ee.printStackTrace();
			}
			StartConnect();
		}
	}
	
	public static String sendData(String data){
		try{
			int startTime=MyTools.getCurrentTime();
			while(isIOBusy || !isSocketOn()){
				Thread.sleep(500);
				if(MyTools.getCurrentTime()>=startTime+5) return "";
			}
			
			isIOBusy=true;
			os.write((data+"\n").getBytes(StandardCharsets.UTF_8));
			String str=br.readLine();
			System.out.println(data+"\n"+str);
			isIOBusy=false;
			
			return str!=null ? str : "";
		}catch(IOException|InterruptedException e){
			try{
				closeSocket();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return "";
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
					closeSocket();
					e.printStackTrace();
				}
				try{
					sleep(28888);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			this.interrupt();
		}
	}
	
	/**
	 * @return  Weather the socket started.
	 */
	public static boolean isSocketOn(){
		return !(socket==null) && (socket.isConnected() && !socket.isClosed());
	}
	
	public static void closeSocket(){
		try{
			NetworkService.manuallyStop();
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
			socket=null;
		}catch(NullPointerException e){
//			e.printStackTrace();
		}catch(IOException e){
			socket=null;
		}
	}
	
}

package cc0x29a.specialchat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class new__NetworkService extends Service{
	
	@Override
	public IBinder onBind(Intent intent){
		// TO DO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	static StartConnect startConnect;
	
	static Socket socket;
	
	public static BufferedReader br;
	public static OutputStream os;
	
	public static boolean isIOBusy;
	
	@Override
	public void onCreate(){
		startConnect = new StartConnect();
		startConnect.start();
	}
	
	public void onDestroy(){
		try{
			closeSocket();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*
	* todo：
	*   Verify the client at the first connection.
	*   0................
	* */
	
	class StartConnect extends Thread{ //todo this need to be perfected.
		@Override
		public void run(){
			try{
				while(true){
					if(!isSocketOn()){
						try{
							System.out.println("Retry for new connection.");
							
							socket=new Socket();
							socket.connect(new InetSocketAddress("192.168.1.18",21027),1111);
							socket.setSoTimeout(30000);
							
							br=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
							
							os=socket.getOutputStream();
							
						}catch(IOException e){
//							Toast.makeText(new__NetworkService.this,"Network error!",Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
//					System.out.println("Connection status:\nisClosed:"+(!(socket==null) && socket.isClosed())+"\nisConnected:"+ (!(socket==null) && socket.isConnected()) );
					try{
						sleep(6000);
					}catch(InterruptedException e){
						e.printStackTrace();
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
					sleep(6000);
				}catch(InterruptedException ee){
					ee.printStackTrace();
				}
				startConnect=new StartConnect();
				startConnect.start();
			}
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
			return str;
		}catch(IOException|InterruptedException e){
			try{
				closeSocket();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return "";
	}
	
	public static boolean isSocketOn(){
		return !(socket==null) && (socket.isConnected() && !socket.isClosed());
	}
	
	public static void closeSocket() throws Exception{
		if(socket==null){return;}
		socket.shutdownInput();
		socket.shutdownOutput();
		socket.close();
		socket=null;
	}
	
}

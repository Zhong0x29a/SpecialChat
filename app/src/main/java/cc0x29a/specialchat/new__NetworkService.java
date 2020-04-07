package cc0x29a.specialchat;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class new__NetworkService extends Service{
	public new__NetworkService() throws IOException{
	}
	
	@Override
	public IBinder onBind(Intent intent){
		// TO DO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public void onCreate(){
	
	}
	
	public void onDestroy(){
	
	}
	
	private Socket socket;
	
	private BufferedReader br;
	private OutputStream os;
	
	private class startConnect extends Thread{
		@Override
		public void run(){
			while(true){
				if(socket==null || !socket.isConnected() || socket.isClosed() ){
					try{
						socket=new Socket();
						socket.connect(new InetSocketAddress("192.168.1.18",21027),1111);
						br=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
						os=socket.getOutputStream();
					}catch(IOException e){
						e.printStackTrace();
					}
				}
				try{
					sleep(6000);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	};
	
	public Handler.Callback sendMsgHandler;
	public Handler.Callback revMsgHandler=new Handler.Callback(){
		@Override
		public boolean handleMessage(@NonNull Message msg){
			return false;
		}
	};
	
	public String sendData(final String DataSend) throws Exception{
		
		//todo
		
		return "";
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
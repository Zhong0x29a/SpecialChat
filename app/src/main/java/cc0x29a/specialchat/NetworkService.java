package cc0x29a.specialchat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.net.Socket;

public class NetworkService extends Service{
	public NetworkService(){
	}
	
	@Override
	public IBinder onBind(Intent intent){
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public void onCreate(){
		// start a socket
		new SocketThread().start();
	}
	
	public class SocketThread extends Thread{
		Socket socket;
		@Override
		public void run(){
			try{
				socket=new Socket("192.168.1.18",21027);
				while(!socket.isClosed()){
					//todo here
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
}

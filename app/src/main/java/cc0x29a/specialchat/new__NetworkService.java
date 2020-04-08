package cc0x29a.specialchat;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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
	
	public static BufferedReader br;
	public static OutputStream os;
	
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
	
//	public Handler.Callback sendMsgHandler=new Handler.Callback(){
//		@Override
//		public boolean handleMessage(@NonNull Message msg){
//			if(msg.what == 0x123){
//				System.out.println("asd");
//			}
//			return false;
//		}
//	};

//	=new Handler.Callback(){
//		@Override
//		public boolean handleMessage(@NonNull Message msg){
//			return false;
//		}
//	};
	
	
	
	public static class swapData extends Thread{
		Handler.Callback revMsgHandler;
		Handler.Callback sendMsgHandler;
		
		swapData(Handler.Callback revMsgHandler){
			this.revMsgHandler=revMsgHandler;
		}
		
		@Override
		public void run(){
			try{
				new Thread(){
					@Override
					public void run(){
						String str;
						try{
							while((str=br.readLine())!=null){
								Message msg=new Message();
								msg.what=0x29a0;
								msg.obj=str;
								revMsgHandler.handleMessage(msg);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}.start();
				
				Looper.prepare();
				sendMsgHandler=new Handler.Callback(){
					@Override
					public boolean handleMessage(@NonNull Message msg){
						if(msg.what==0x29a1){
							try{
								os.write((msg.obj.toString()+"\n").getBytes(StandardCharsets.UTF_8));
							}catch(Exception e){
								e.printStackTrace();
							}
						}
						return false;
					}
				};
				Looper.loop();
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void sendData(final String DataSend,final Handler.Callback handler) throws Exception{
		
		os.write(DataSend.getBytes(StandardCharsets.UTF_8));
		
		new Thread(){
			@Override
			public void run(){
				String str="";
				try{
					while((str=br.readLine())!=null){
						Message msg=new Message();
						msg.what=0x234;
						msg.obj=str;
						handler.handleMessage(msg);
					}
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}.start();
		
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
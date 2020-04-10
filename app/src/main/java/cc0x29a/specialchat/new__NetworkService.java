package cc0x29a.specialchat;

import android.annotation.SuppressLint;
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
import java.net.SocketTimeoutException;
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
		new startConnect().start();
	}
	
	public void onDestroy(){
	
	}
	
	private static Socket socket;
	
	public static BufferedReader br;
	public static OutputStream os;
	
	private static class startConnect extends Thread{
		@Override
		public void run(){
			try{
				while(true){
					if(socket==null || socket.isClosed() || !socket.isConnected() || socket.isInputShutdown()){
						try{
							System.out.println("Retry for new connection.");
							
							socket=new Socket();
							socket.connect(new InetSocketAddress("192.168.1.18",21027),1111);
							socket.setSoTimeout(30000);
							
							br=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
							
							os=socket.getOutputStream();
							
//							new swapData(null).start();
						}catch(IOException e){
							e.printStackTrace();
						}
					}
					System.out.println("Connection status:\nisClosed:"+socket.isClosed()+"\nisConnected:"+socket.isConnected());
					try{
						sleep(6000);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				try{
					socket.close();
				}catch(IOException ex){
					ex.printStackTrace();
				}
				new startConnect().start();
			}
		}
	};
	
	public static class swapData extends Thread{
		
		Handler revMsgHandler;
		Handler sendMsgHandler;
		
		swapData(Handler revMsgHandler){
			this.revMsgHandler=revMsgHandler;
		}
		
		@SuppressLint("HandlerLeak")
		@Override
		public void run(){
			try{
				new Thread(){
					@Override
					public void run(){
						String str;
						try{
							while((str=br.readLine())!=null){
								
								System.out.println("got data:\n");
								System.out.println(str);
								
								Message msg=new Message();
								msg.what=0x29a0;
								msg.obj=str;

								revMsgHandler.sendMessage(msg); //todo handler has bug, try fix.
							}
							System.out.println("readLine broke.\n\n\n\n\n\n\n");
							// recall a empty message
							Message emptyMsg=new Message();
							emptyMsg.what=0x29a;
							emptyMsg.obj="{}";
							revMsgHandler.sendMessage(emptyMsg);
							
							socket.close();socket=null;
						}catch(SocketTimeoutException e){
							e.printStackTrace();
							try{
								socket.close();socket=null;
							}catch(Exception ex){
								ex.printStackTrace();
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}.start();
				
				// send data to server
				Looper.prepare();
				sendMsgHandler=new Handler(){
					@Override
					public void handleMessage(@NonNull Message msg){
						if(msg.what==0x29a1){
							try{
								if(socket==null || socket.isClosed() || !socket.isConnected()){
									// recall a empty message
									Message emptyMsg=new Message();
									emptyMsg.what=0x29a;
									emptyMsg.obj="{}";
									revMsgHandler.sendMessage(emptyMsg);
									return;
								}
//								sleep(233); //todo bug may here
								os.write((msg.obj.toString()+"\n").getBytes(StandardCharsets.UTF_8));
							}catch(Exception e){
								// recall a empty message
								Message emptyMsg=new Message();
								emptyMsg.what=0x29a;
								emptyMsg.obj="{}";
								revMsgHandler.sendMessage(emptyMsg);
								
								try{
									socket.close();
								}catch(IOException ex){
									ex.printStackTrace();
								}
								e.printStackTrace();
							}
						}
					}
				};
				Looper.loop();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
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
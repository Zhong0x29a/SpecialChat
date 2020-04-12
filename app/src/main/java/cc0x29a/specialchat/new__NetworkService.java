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
	public new__NetworkService(){
	}
	
	@Override
	public IBinder onBind(Intent intent){
		// TO DO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	static StartConnect startConnect;
	
	@Override
	public void onCreate(){
		startConnect = new StartConnect();
		startConnect.start();
	}
	
	public void onDestroy(){
	
	}
	
	static Socket socket;
	
	public static BufferedReader br;
	public static OutputStream os;
	
	public static boolean isIOBusy;
	
	static class StartConnect extends Thread{
		@Override
		public void run(){
			try{
				while(true){
					if(isSocketOn()){
						try{
							System.out.println("Retry for new connection.");
							
							socket=new Socket();
							socket.connect(new InetSocketAddress("192.168.1.18",21027),1111);
							socket.setSoTimeout(30000);
							
							br=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
							
							os=socket.getOutputStream();
							
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
					closeSocket();
				}catch(IOException ex){
					ex.printStackTrace();
				}
				startConnect=new StartConnect();
				startConnect.start();
			}
		}
	};
	
	public static String sendData(String data){
		try{
			int startTime=MyTools.getCurrentTime();
			while(isIOBusy ||isSocketOn()){
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
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		return "";
	}
	
//	public static class swapData extends Thread{
//
//		Handler revMsgHandler;
//		Handler sendMsgHandler;
//
//		int msgWhat;
//
//		@SuppressLint("HandlerLeak")
//		@Override
//		public void run(){
//			try{
//				synchronized(this){
//
//					new Thread(){
//						@Override
//						public void run(){
//							synchronized(this){
//								String str;
//								try{
//									// when received message from serer.
////									while((str=br.readLine())!=null){
////										System.out.println("got data:\n"+str);
////
////										Message msg=new Message();
////
////										msg.what=msgWhat;
////
////										msg.obj=str;
////
////										revMsgHandler.sendMessage(msg); //todo handler has bug, try fix.
////									}
//
//									str=br.readLine();
//
//									//todo:
//									// rebuild it as a method ,
//									// return the str .
//
//									System.out.println("readLine broke.");
//
//									// recall a empty message
//									Message emptyMsg=new Message();
//									emptyMsg.what=0x29a;
//									emptyMsg.obj="{}";
//									revMsgHandler.sendMessage(emptyMsg);
//
//								}catch(SocketTimeoutException e){
//									e.printStackTrace();
//									try{
//										socket.close();
//										socket=null;
//									}catch(Exception ex){
//										ex.printStackTrace();
//									}
//								}catch(Exception e){
//									e.printStackTrace();
//								}
//							}
//						}
//					}.start();
//
//					// send data to server
//					Looper.prepare();
//					sendMsgHandler=new Handler(){
//						@Override
//						public void handleMessage(@NonNull Message msg){
//							if(msg.what==0x29a1){
//								try{
//									if(socket==null||socket.isClosed()||!socket.isConnected()){
//										// recall a empty message
//										Message emptyMsg=new Message();
//										emptyMsg.what=0x29a;
//										emptyMsg.obj="{}";
//										revMsgHandler.sendMessage(emptyMsg);
//										return;
//									}
//									os.write((msg.obj.toString()+"\n").getBytes(StandardCharsets.UTF_8));
//								}catch(Exception e){
//									// recall a empty message
//									Message emptyMsg=new Message();
//									emptyMsg.what=0x29a;
//									emptyMsg.obj="{}";
//									revMsgHandler.sendMessage(emptyMsg);
//
//									try{
//										socket.close();
//										socket=null;
//									}catch(IOException ex){
//										ex.printStackTrace();
//									}
//									e.printStackTrace();
//								}
//							}
//						}
//					};
//					Looper.loop();
//				}
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//		}
//	}
	
	public static boolean isSocketOn(){
		return !(socket==null || socket.isClosed() || !socket.isConnected());
	}
	
	public static void closeSocket() throws IOException{
		socket.shutdownInput();
		socket.shutdownOutput();
		socket.close();
		socket=null;
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
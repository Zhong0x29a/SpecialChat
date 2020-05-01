package cc0x29a.specialchat;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/*
 *   1.Verify the client at the first connection.---done.
 *   2.socket全双工通信。
 *
 * */

/*
* draft:
*   Socket 成功连接后 开启两条线程， 分别进行读、写操作。
*   client 先向server发送user_id和token_key 进行身份认证。
* */

public class SocketWithServerService extends Service{ //todo: not use Service??
	
	@Override
	public IBinder onBind(Intent intent){
		// TO DO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	static String user_id;
	static String token_key;
	
	static Socket socket;
	
	public static BufferedReader br;
	public static OutputStream os;
	
	public static heart heart;
	
	public static boolean isOSBusy;
	
	static boolean tryingConnect=false;
	
	// key: rid (request id) , data (data return from server)
	static HashMap<String,JSONObject> dataSet=new HashMap<>();
	static HashMap<String,SocketDataManager> dataManagerHashMap=new HashMap<>();
	
	@Override
	public void onCreate(){
		// todo: restart this service after logging in!
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		user_id=preferences.getString("user_id",null);
		token_key=preferences.getString("token_key",null);
		if(user_id==null || token_key==null){
			user_id="000"; //todo: control the permission !!!!
			token_key="000";
		}else{
			startService(new Intent(SocketWithServerService.this,NetworkService.class));
		}
		
		new Thread(new Runnable(){
			@Override
			public void run(){
				StartConnection();
			}
		},"StartConnectionThread").start();
		
	}
	
	public void onDestroy(){
		stopService(new Intent(SocketWithServerService.this,NetworkService.class));
		closeSocket();
	}
	
	static void StartConnection(){ //todo this may need to be perfected.
		try{
			if(tryingConnect){ return; }
			tryingConnect=true;
			if(!isSocketOn()){
				closeSocket();
				System.out.println("Retry for new connection.");
				
//				synchronized(this){
					
				socket=new Socket();
				// socket.connect(new InetSocketAddress("server.specialchat.cn",21027),1111);
				socket.connect(new InetSocketAddress("192.168.1.18",21027),1111);
				
				socket.setSoTimeout(26666);
				
				br=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
				os=socket.getOutputStream();
//				}
				// font-process
				// verify client
				os.write((
						"{" +
						"'client':'SCC-1.0'," +
						"'user_id':'"+user_id+"'," +
						"'token_key':'"+token_key+"'," +
						"'timestamp':'"+MyTools.getCurrentTime()+"'" +
						"}\n"
				).getBytes(StandardCharsets.UTF_8));
				
				String str=br.readLine();
				
				
				if(str!=null && str.length()>0){ // String from method: sendData.
					new ReaderThread().start();
					
					// a thread that Send "heartbeat" to server.
					heart=new heart();
					heart.start();
					
					if(str.equals("true")){
						//...
						System.out.println("True\n");
					}else{ // non-login client.
						//...
						System.out.println("Not True\n");
					}
				
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
			closeSocket();
		}finally{
			tryingConnect=false;
		}
	}
	
	static class ReaderThread extends Thread{
		@Override
		public void run(){
			try{
				while(true){
					String str=new String(Base64.decode(br.readLine(),Base64.DEFAULT));
					// font-process, get the request key.
					JSONObject object=new JSONObject(str);
					
					if(object.getJSONObject("header").getString("type").equals("return")){
						String rid=object.getJSONObject("header").getString("rid");
						SocketDataManager manager;
						synchronized(this){
							dataSet.put(rid,object.getJSONObject("body"));
							manager=dataManagerHashMap.get(rid);
							if(manager!=null){
								synchronized(manager){
									manager.notify();
								}
							}
						}
//					}else if(object.getJSONObject("header").getString("type").equals("newMsg")){
					}else if(object.getJSONObject("header").getString("type").equals("request")){
						
						//todo: balabala...
					}
				}
			}catch(IOException|JSONException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param data , the data send to server
	 * @return data returned from server.
	 */
	public static void sendData(String data){
		try{
			int startTime=MyTools.getCurrentTime();
			while(isOSBusy){
				Thread.sleep(333);
				if( MyTools.getCurrentTime() > (startTime+4) ) {
					System.out.println("IO too busy!");
				}
			}
			
			isOSBusy=true;
			//todo: may case bug when user send "<br>"!
			// solve method: may use base64 encrypt the data!
			data=Base64.encodeToString(data.getBytes(),Base64.DEFAULT);
			
			System.out.println(data);
			
			os.write((data.replaceAll("\n","")+"\n").getBytes(StandardCharsets.UTF_8));
			
		}catch(IOException|InterruptedException|NullPointerException e){
			new Thread(new Runnable(){
				@Override
				public void run(){
					StartConnection();
				}
			},"StartConnectionThread").start();
		}finally{
			isOSBusy=false;
		}
	}
	
	public static class heart extends Thread{
		@Override
		public void run(){
			while(isSocketOn()){
				try{
					SocketDataManager manager=new SocketDataManager();
					JSONObject data=manager.startRequest("{'action':'beat'}");
					
					if(!data.getBoolean("alive")){
						closeSocket();
					}
					sleep(23333);
				}catch(JSONException e){
					closeSocket();
				}catch(InterruptedException e){
//					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * @return  Weather the socket started. (Not always correct.)
	 */
	public static boolean isSocketOn(){
		if(socket == null){
			return false;
		}
		return !socket.isClosed();
	}
	
	public static void closeSocket(){
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
	
//	public static class dataProcessor{
//		private JSONObject data;
//
//		dataProcessor(String rawData) throws JSONException{
//			this.data=new JSONObject(rawData);
//		}
//
//		dataProcessor(JSONObject data){
//			this.data=data;
//		}
//
//		String a(){
//			return this.data.toString();
//		}
//	}
}

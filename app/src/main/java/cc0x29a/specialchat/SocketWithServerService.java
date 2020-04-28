package cc0x29a.specialchat;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.HashMap;
import java.util.Objects;

/*
 * todo：
 *   1.Verify the client at the first connection.
 *   2.socket全双工通信。
 *
 * */

/*
* draft:
*   Socket 成功连接后 开启两条线程， 分别进行读、写操作。
*   client 先向server发送user_id和token_key 进行身份认证。
* */

/*
* note：
*   ill today... :(
*   will get better soon.
* */

public class SocketWithServerService extends Service{
	
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
	private static HashMap<String,String> dataSet=new HashMap<>();
	private static HashMap<String,DataManager> dataManagerHashMap=new HashMap<>();
	
	public class DataManager{
		String rid;
		
		DataManager(String rid){
			this.rid=rid;
		}
		
		public String startRequest(String data){
			dataManagerHashMap.put(rid,this);
			sendData(data);
			
			try{
				wait();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			
			String temp=dataSet.get(rid);
			if(temp!=null){
				return temp;
			}else{
				return "error";
			}
		}
	}
	
	public static String getDataByKey(String key){
		return dataSet.get(key);
	}
	
	public static void addData(String key,String data){
		dataSet.put(key, data);
		Objects.requireNonNull(dataManagerHashMap.get(key)).notify();
	}
	
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
	
	static void StartConnection(){ //todo this need to be perfected.
		try{
			if(tryingConnect){ return; }
			tryingConnect=true;
			if(!isSocketOn()){
				closeSocket();
				System.out.println("Retry for new connection.");
				
				socket=new Socket();
//						socket.connect(new InetSocketAddress("server.specialchat.cn",21027),1111);
				socket.connect(new InetSocketAddress("192.168.1.18",21027),1111);
				
				socket.setSoTimeout(26666);
				
				br=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
				os=socket.getOutputStream();
				
				// font-process
				// verify client
//				String data=sendData();
				os.write(
							("{" +
							"'client':'SCC-1.0'," +
							"'user_id':'"+user_id+"'," +
							"'token_key':'"+token_key+"'," +
							"'timestamp':'"+MyTools.getCurrentTime()+"'" +
							"}").getBytes(StandardCharsets.UTF_8));
				
				String str=br.readLine();
				
				
				if(str!=null && str.length()>0){ // String from method: sendData.
					// a thread that Send "heartbeat" to server.
					heart=new heart();
					heart.start();
					
					//todo.
//					if(str.equals("true")){
//						//...
//					}else{ // non-login client.
//						//...
//					}
				
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
			closeSocket();
		}finally{
			tryingConnect=false;
		}
	}
	
	// todo: use static class?
	static class ReaderThread extends Thread{
		
		@Override
		public void run(){
			while(true){
				try{
					String str=br.readLine();
					//todo: font-process, get the request key.
					JSONObject object=new  JSONObject(str);
					if(object.getString("type").equals("return")){//todo
						String rid=object.getString("rid");
						addData(rid,object.getString("data"));
						
						DataManager manager=dataManagerHashMap.get(rid);
						if(manager!=null){
							manager.notify();
						}
					}else if(object.getString("type").equals("newMsg")){
						//todo: balabala...
					}
					
				}catch(IOException|JSONException e){
					e.printStackTrace();
				}
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
//					return "{'Error':'IO too busy!'}";
				}
			}
			
			isOSBusy=true;
			//todo: may case bug when user send "<br>"!
			// solve method: may use base64 encrypt the data!
			os.write((data.replaceAll("\n","<br>")+"\n").getBytes(StandardCharsets.UTF_8));
			
//			String str=br.readLine();
//			System.out.println(data+"\n"+str);

//			isOSBusy=false;
			
//			return str != null ? str.replaceAll("<br>","\n") : "{'network':'error'}";
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
//		return "{'network':'error'}";
	}
	
	public static class heart extends Thread{
		@Override
		public void run(){
			while(isSocketOn()){
				try{
					String dataStr=sendData("{'action':'beat'}");
					JSONObject data=new JSONObject(dataStr);
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

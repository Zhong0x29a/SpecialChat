package cc0x29a.specialchat;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Base64;

import androidx.annotation.NonNull;

import org.json.JSONArray;
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

20.05.23 Sat.

很绝望，似乎不大记得整到哪，哪有bug了。

 */

/*
* draft:
*   Socket 成功连接后 开启两条线程， 分别进行读、写操作。
*   client 先向server发送user_id和token_key 进行身份认证。
* */

/**
 * Todo:
 *  now problem: Could finish reconnect to server.
 * */

public class SocketWithServerService extends Service{ //todo: not use Service??
	
	@Override
	public IBinder onBind(Intent intent){
		// TO DO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	// local-stored userID & tokenKey
	static String user_id;
	static String token_key;

	// Socket with server
	static Socket socket;

	// br: Read data from SocketStream
	// os: Flush data to SocketStream
	public static BufferedReader br;
	public static OutputStream os;

	// HeartBeat thread, only one.
	public static heart heart;

	// OS's status.  if ture, cant use OS.
	public static boolean isOSBusy;

	// a value for whether continue start a new socket.
	static boolean tryingConnect=false;
	
	// key: rid (request id) , data (data return from server)
	static final HashMap<String,JSONObject> dataSet=new HashMap<>();
	static final HashMap<String,SocketDataManager> dataManagerHashMap=new HashMap<>();

	// a handler for
	static Handler handler;
	
	@SuppressLint("HandlerLeak")
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
		
		handler=new Handler(){
			@Override
			public void handleMessage(@NonNull final Message msg){
				if(msg.what==0x1){
					new Thread(new Runnable(){
						@Override
						public void run(){
							if(msg.obj!=null){
								sendData(msg.obj.toString());
							}
						}
					}).start();
				}
			}
		};
		
	}
	
	public void onDestroy(){
		stopService(new Intent(SocketWithServerService.this,NetworkService.class));
		closeSocket();
	}

	// start a new socket with server.
	void StartConnection(){
		try{
			// if there is another thread doing this.
			if(tryingConnect){
				return;
			}
			tryingConnect=true;
			if(!isSocketOn()){
				closeSocket();
				System.out.println("Retry for new connection.");
					
				socket=new Socket();
				// socket.connect(new InetSocketAddress("server.specialchat.cn",21027),1111);
				socket.connect(new InetSocketAddress("192.168.1.18",21027),1111);
				
				socket.setSoTimeout(26666);
				
				br=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
				os=socket.getOutputStream();
//				}
				// font-process
				// verify client
				os.write( (
						"{" +
						"'client':'SCC-1.0'," +
						"'user_id':'"+user_id+"'," +
						"'token_key':'"+token_key+"'," +
						"'timestamp':'"+MyTools.getCurrentTime()+"'" +
						"}\n"
				).getBytes(StandardCharsets.UTF_8) );
				
				String str=br.readLine();
				
				
				if(str!=null && str.length()>0){ // String from method: sendData.
					new ReaderThread().start();
					
					// start a new thread that send "heartbeat" to server.
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


	// Keep reading from the Stream,
	// process the data, put data to the hashMap.
	class ReaderThread extends Thread{
		@Override
		public void run(){
			try{
				String str;
				while( (str=br.readLine())!=null ){
					str=new String(Base64.decode(str,Base64.DEFAULT));
					// font-process, get the request key.
					JSONObject object=new JSONObject(str);
					
					if(object.getJSONObject("header").getString("type").equals("return")){
						String rid=object.getJSONObject("header").getString("rid");
						SocketDataManager manager;
						// put new data to dataSet.
						synchronized(dataSet){
							dataSet.put(rid,object.getJSONObject("body"));
						}
						synchronized(dataManagerHashMap){
							manager=dataManagerHashMap.get(rid);
							if(manager!=null){
								manager.notify();
							}
						}
						
					}else if(object.getJSONObject("header").getString("type").equals("request")){
						switch(object.getJSONObject("header").getString("action")){
							case "0001":
								//todo:
								JSONArray data=object.getJSONObject("body").getJSONArray("data");
								ChatListSQLiteHelper clh=new ChatListSQLiteHelper(SocketWithServerService.this,"chat_list.db",1);
								
								for(int i=0;i<data.length();i++){
									JSONArray oneData=data.getJSONArray(i);
									
									String friend_id=oneData.getString(1);
									String send_time=oneData.getString(2);
									String msg_content=oneData.getString(3);
									
									// insert data to database
									MsgSQLiteHelper mh=new MsgSQLiteHelper(SocketWithServerService.this,"msg_"+friend_id+".db",1);
									mh.insertNewMsg(mh.getReadableDatabase(),friend_id,send_time,msg_content);
									
									mh.close();
									
									SQLiteDatabase chat_list_db=clh.getReadableDatabase();
									// update info.
									clh.updateChatList(chat_list_db,friend_id,send_time,msg_content);
									// fetch nickname.
									String ta_nickname=clh.fetchNickname(chat_list_db,friend_id);
									// release resource
									chat_list_db.close();
								}
								break;
							default:
								break;
						}
						
						//todo: balabala...
					}
				}
			}catch(IOException|JSONException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Main function to send data to server over outputStream.
	 * @param data , the data send to server
	 */
	public void sendData(String data){
		try{
			// if os is busy for over 4 sec, output a logMsg.
			int startTime=MyTools.getCurrentTime();
			while(isOSBusy){
				Thread.sleep(333);
				if( MyTools.getCurrentTime() > (startTime+4) ) {
					System.out.println("IO too busy!");
				}
			}

			// Mark OS is in use.
			isOSBusy=true;

			data=Base64.encodeToString(data.getBytes(),Base64.DEFAULT);

			// for debug.
			System.out.println(data);
			
			synchronized(os){
				os.write((data.replaceAll("\n","")+"\n").getBytes(StandardCharsets.UTF_8));
			}
		}catch(IOException|InterruptedException|NullPointerException e){
			new Thread(new Runnable(){
				@Override
				public void run(){
					StartConnection();
						//todo: seemed it would not reconnect?
				}
			},"StartConnectionThread").start();
		}finally{
			// Cancle the mark: 'busy'.
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

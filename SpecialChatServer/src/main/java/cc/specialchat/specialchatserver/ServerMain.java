package cc.specialchat.specialchatserver;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ServerMain{
	
	private static ServerSocket serverSocket;
	static Map<String,ServerThread> serverThreadMap;
	
	public static void main(String[] args){
		// test code.

		// test code upon
		
		// init server.
		init();
		serverThreadMap=new HashMap<>();
		
		// start program.
		new Thread(new Runnable(){
			@Override
			public void run(){
				MainServer();
			}
		},"MainServerThread").start();
		
	}
	
	private static void init(){
		try{
			UserInfoSQLite.init();
			MsgCacheSQLite.init();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void MainServer(){
		try{
			serverSocket = new ServerSocket(21027);
			System.out.println("------ Special Chat Server started ------\n");
			int count=1;
			
//			Socket socket;
			while(true){
				Socket socket=serverSocket.accept();
				
				socket.setSoTimeout(30000);
				BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
				OutputStream os=socket.getOutputStream();
				
				//todo : verify client.
				String userData;
				if((userData=br.readLine())!=null){
					JSONObject userDataJson=JSONObject.parseObject(userData);
					try{
						String user_id=userDataJson.getString("user_id");
						String token_key=userDataJson.getString("token_key");
						if(UserInfoSQLite.verifyUserTokenKey(user_id,token_key)){
							//todo: add to Hash map
							ServerThread serverThread = new ServerThread(socket,br,os);
							
							serverThreadMap.put(user_id,serverThread);
						}
					}catch(JSONException e){
						System.out.println("User info header error. ");
						break;
					}
				}else{
					System.out.println("Connection broke.");
					break;
				}
				
				ServerThread serverThread = new ServerThread(socket,br,os);
				
				System.out.println("New connection: " + socket.getInetAddress().getHostAddress() +" ("+count+")\n");
				serverThread.start();
				
				count++;
			}
		}catch(Exception ex){
			System.out.println("--- Just occurred a ERROR... ---\n---- Special Chat Server restarted ----\n");
			ex.printStackTrace();
			try{
				Thread.sleep(5888);
			}catch(InterruptedException exc){
				exc.printStackTrace();
			}
			MainServer();
		}
	}
}

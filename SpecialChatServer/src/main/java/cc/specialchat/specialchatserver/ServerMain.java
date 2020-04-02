package cc.specialchat.specialchatserver;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain{
	
	public static void main(String[] args) throws Exception{
		// test code.
//		MsgCacheSQLite.init();
//		String t="{\"a\":'asd','dsa':\"dsa\"}";
//		JSONObject j=JSONObject.parseObject(t);
//		System.out.println(j.getString("a")+"\n"+j.getString("dsa"));
//		MsgCacheSQLite.insertNewMsg("123","321","Hello worrld!");
//		String[][] a= MsgCacheSQLite.fetchMsg("321");
//		JSONObject jb=JSON.parseObject("{'user_id':'14488542'," +
//				"'token_key':'96d4549f0bc16919'," +
//				"'to':'13422891'," +
//				"'msg_content':'Hello'}");
//		System.out.println(ProcessAction.action_0004(jb));
//
//		JSONObject jb2=JSON.parseObject("{'user_id':'13422891'," +
//				"'token_key':'880c0e907db4e13f'" +
//				"}");
//		System.out.println(ProcessAction.action_0003(jb2));
//
//		int phone=15360947129;
//		exit(0);
		// test code upon
		
		try{
			init();
		}catch(Exception e){
//			e.printStackTrace();
		}
		
		// start program.
		try{
			ServerSocket serverSocket = new ServerSocket(21027);
			System.out.println("------ Special Chat Server started ------\n");
			int count=1;
			
			Socket socket;
			while(true){
				socket = serverSocket.accept();
				ServerThread serverThread = new ServerThread(socket);
				System.out.println("New connection: " + socket.getInetAddress().getHostAddress() +" ("+count+")\n");
				serverThread.start();
				count++;
			}
		}catch(Exception e){
			System.out.println("--- Just occurred a ERROR... ---\n---- Special Chat Server restarted ----\n");
			e.printStackTrace();
			Thread.sleep(1888);
			main(new String[]{"e"});
		}
		
	}
	
	private static void init() throws Exception{
		UserInfoSQLite.init();
		MsgCacheSQLite.init();
	}
}

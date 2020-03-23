package cc.specialchat.specialchatserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class ServerMain{
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException{
		// test code.
//		MsgCacheSQLite.init();
//		String t="{\"a\":'asd','dsa':\"dsa\"}";
//		JSONObject j=JSONObject.parseObject(t);
//		System.out.println(j.getString("a")+"\n"+j.getString("dsa"));
		
//		exit(0);
		// start program.
		
		// first run, init.
//		try{
//			UserInfoSQLite.init();
//			MsgCacheSQLite.init();
//		exit(0);
//		}catch(SQLException|ClassNotFoundException e){
//			e.printStackTrace();
//		}
		
		try{
			ServerSocket serverSocket = new ServerSocket(21027);
			if(args.length>0 && args[0].equals("e")){
				System.out.println("--- Just happened a Error... ---\n---- Special Chat Server restarted ----\n");
			}else{
				System.out.println("---- Special Chat Server started ----\n");
			}
			int count=1;
			
			Socket socket;
			while(true){
				socket = serverSocket.accept();
				ServerThread serverThread = new ServerThread(socket);
				System.out.println("New connection: " + socket.getInetAddress().getHostAddress() +" ("+count+")");
				serverThread.start();
				count++;
			}
		}catch(Exception e){
			e.printStackTrace();
			main(new String[]{"e"});
		}
	}
}

package cc.specialchat.specialchatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain{
	
	public static void main(String[] args){
		// test code.
//		MsgCacheSQLite.init();
//		exit(0);
		// start program.
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
		}catch(IOException e){
			e.printStackTrace();
			main(new String[]{"e"});
		}
	}
}

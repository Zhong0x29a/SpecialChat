package cc.specialchat.specialchatserver;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain{
	
	public static void main(String[] args){
		// test code.

		// test code upon
		
		// init server.
		init();
		
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

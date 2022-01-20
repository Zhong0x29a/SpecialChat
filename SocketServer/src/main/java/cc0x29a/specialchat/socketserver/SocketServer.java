package cc0x29a.specialchat.socketserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class SocketServer{
	
	public static void main(String[] args) throws IOException{
		ServerSocket serverSocket = new ServerSocket(21027);
		int cid=0; // Client ID, connections' sum. (not current connection number)
		Socket socket;
		HashMap<Integer,Socket> socketPool=new HashMap<>();
		
		System.out.println("------ Server started ------\n");
		
		while(true){
			cid++;
			socket = serverSocket.accept();
			socketPool.put(cid,socket);

			// Display client ip & cid.
			System.out.println("New connection: " + socket.getInetAddress().getHostAddress() +" ("+cid+")\n");
			
			new ServerThread(socket,cid).start();
		}
		
	}
}

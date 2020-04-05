package cc0x29a.specialchat.socketserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer{
	
	public static void main(String[] args) throws IOException{
		ServerSocket serverSocket = new ServerSocket(21027);
		System.out.println("------ Server started ------\n");
		int count=1;
		Socket socket;
		while(true){
			socket = serverSocket.accept();
			ServerThread serverThread = new ServerThread(socket);
			System.out.println("New connection: " + socket.getInetAddress().getHostAddress() +" ("+count+")\n");
			serverThread.start();
			count++;
		}
	}
}

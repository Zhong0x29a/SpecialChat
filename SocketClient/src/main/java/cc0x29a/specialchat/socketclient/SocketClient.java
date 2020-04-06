package cc0x29a.specialchat.socketclient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketClient{
	
	private static String DataSend="。";
	private static String DataReturn="";
	
	public static void main(String[] args) throws IOException{
		
		String sb="";
		
//		if(sb.length()>0){
//			System.out.println("ssss");
//		}
		
		
		Socket socket = new Socket("192.168.1.18", 21027);
//
//
//		// Output, send data to server.
		OutputStream os = socket.getOutputStream();
		os.write(DataSend.getBytes(StandardCharsets.UTF_8));
		os.flush();
		socket.shutdownOutput();
//
//
//		// Input, receive data from server.
//		BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
//		DataReturn+=br.readLine();
//		String temp;
//		while((temp=br.readLine())!=null){
//			DataReturn+="\n";
//			DataReturn+=temp;
//		}
//
//		br.close();
//		os.close();
//		socket.close();
		
		
	}
}

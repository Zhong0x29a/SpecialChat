package cc.specialchat.specialchatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by Administrator on 2018/5/3.
 */
public class ServerThread extends Thread {
	
	private Socket socket;
	
	public ServerThread(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		OutputStream outputStream = null;
		PrintWriter printWriter = null;
		
		try {
			// get data
			inputStream = socket.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(inputStreamReader);
			
			String str;
			if ((str = bufferedReader.readLine()) != null) {
				System.out.println("I am Server, now get message from Client: " + str);
			}
			socket.shutdownInput();
			
			// send
			String msg="{" +
					"\"status\":\"true\"," +
					"\"user_id\":\"12414\"," +
					"\"user_name\":\"Hello world\"," +
					"\"token_key\":\"trufdse\"," +
					"\"login_time\":\"trfdue\"" +
					"}";
			System.out.println(msg);
			
			OutputStream os = socket.getOutputStream();
			os.write(msg.getBytes(StandardCharsets.UTF_8));
			os.flush();
			socket.shutdownOutput();
//			outputStream = socket.getOutputStream();
//			printWriter = new PrintWriter(outputStream);
//			printWriter.write(msg);
//			printWriter.flush();
			
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			// release resource
			try{
				if(printWriter != null){
					printWriter.close();
				}
				if(outputStream != null){
					outputStream.close();
				}
				if(bufferedReader != null){
					bufferedReader.close();
				}
				if(inputStreamReader != null){
					inputStreamReader.close();
				}
				if(inputStream != null){
					inputStream.close();
				}
				if(socket != null){
					socket.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}

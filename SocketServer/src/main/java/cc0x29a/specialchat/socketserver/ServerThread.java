package cc0x29a.specialchat.socketserver;

//import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by Administrator on 2018/5/3.
 */
public class ServerThread extends Thread {
	
	private Socket socket;
	private int cid;
	
	ServerThread(Socket socket,int cid) {
		this.socket = socket;
		this.cid=cid;
	}
	
	@Override
	public void run() {
		
		try {
			// waiting for data from client & process them.
			new ReadingData().start();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			// Release unused resource
			try{
				if(socket != null){
					socket.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
	}
	
	private class ReadingData extends Thread{
		@Override
		public void run(){
			while(!socket.isClosed()){
				StringBuilder DataGet=new StringBuilder();
				
				InputStream inputStream=null;
				InputStreamReader inputStreamReader=null;
				BufferedReader bufferedReader=null;
				
				try{
					socket.setSoTimeout(16000);
					
					inputStream=socket.getInputStream();
					inputStreamReader=new InputStreamReader(inputStream);
					bufferedReader=new BufferedReader(inputStreamReader);
					
					String temp;
					while((temp=bufferedReader.readLine())!=null){
						DataGet.append("\n").append(temp);
					}
					
					if(DataGet.length()>0){
//						ProcessData(DataGet.toString());
					}
					
				}catch(IOException e){
					e.printStackTrace();
				}finally{
					try{
						if(bufferedReader != null){
							bufferedReader.close();
						}
						if(inputStreamReader != null){
							inputStreamReader.close();
						}
						if(inputStream != null){
							inputStream.close();
						}
						if(!socket.isInputShutdown()){
							socket.shutdownInput();
						}
					}catch(IOException e){
						e.printStackTrace();
					}
				}
				
			}
		}
	}
	
	private String dataReader(){
		InputStream inputStream=null;
		InputStreamReader inputStreamReader=null;
		BufferedReader bufferedReader=null;
		try{
			StringBuilder DataGet=new StringBuilder();
			
			inputStream=socket.getInputStream();
			inputStreamReader=new InputStreamReader(inputStream);
			bufferedReader=new BufferedReader(inputStreamReader);
			
			String temp;
			while((temp=bufferedReader.readLine())!=null){
				DataGet.append("\n").append(temp);
			}
			
			return DataGet.toString();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				if(bufferedReader != null){
					bufferedReader.close();
				}
				if(inputStreamReader != null){
					inputStreamReader.close();
				}
				if(inputStream != null){
					inputStream.close();
				}
				if(!socket.isInputShutdown()){
					socket.shutdownInput();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		return "";
	}
	
	private void Writer(String msgSend) throws IOException{
		OutputStream outputStream=socket.getOutputStream();
		outputStream.write(msgSend.getBytes(StandardCharsets.UTF_8));
		outputStream.flush();
		
		socket.shutdownOutput();
	}
	
}

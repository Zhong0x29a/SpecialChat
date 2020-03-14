package cc0x29a.specialchat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Start a Socket With Server
 * Data in DataSend should be filter special chars to avoid problems !!
 *
 * @author Zhong Wenliang
 * @version 1.0
 * date: 20.03.05
 *
 * Todo: encrypt all data!!
 * **/
class SocketWithServer{
	String DataSend=null;
	private StringBuffer DataReturn=new StringBuffer();
	JSONObject DataJsonReturn=null;
	
	void startSocket(){
		new Thread(){
			@Override
			public void run() {
				try {
//todo					Socket socket = new Socket("specialchat.0x29a.cc", 21027);
					Socket socket = new Socket("192.168.1.18", 21027); // test
					
					// Output, send data to server.
					OutputStream os = socket.getOutputStream();
					os.write(DataSend.getBytes(StandardCharsets.UTF_8));
					os.flush();
					socket.shutdownOutput();
					
					// Input, receive data from server.
					BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
					DataReturn.append(br.readLine());
					String temp;
					while((temp=br.readLine())!=null){
						DataReturn.append("\n").append(temp);
					}
					br.close();
					os.close();
					socket.close();
					
					System.out.println(DataReturn.toString());
					
					if(DataReturn!=null){
						DataJsonReturn=new JSONObject(DataReturn.toString());
					}
				}catch(IOException|JSONException|NullPointerException e){
					DataReturn=null;
					e.printStackTrace();
				}
			}
		}.start();
	}
}



/* ------ Code End Here ------ */



/*
	Socket socket=new Socket("home.0x29a.cc", 21027);
	
	String Token=null;
	
	String SendData=null;
	String ReturnData=null;
	JSONObject ReturnDataJson=null;
	
	public SocketWithServer() throws IOException{}
	
	public JSONObject getData() throws IOException, JSONException{
		// Output, send data to server.
		OutputStream os = socket.getOutputStream();
		os.write(SendData.getBytes("utf-8"));
		os.flush();
		socket.shutdownOutput();
		os.close();
		
		// Input, receive data from server.
		BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.ReturnData=br.readLine();
		String temp;
		while((temp=br.readLine())!=null){
			this.ReturnData+="\n"+temp;
		}
		
		this.ReturnDataJson=new JSONObject(this.ReturnData);
		
		return this.ReturnDataJson;
	}
	
	public void closeSocket() throws IOException{
		socket.close();
	}
	
	
	
}




/***
 * ReturnData:"status"=="got" -> return true
 * else return false;
 * */
/*
	public boolean sendData() throws IOException, JSONException{
		
		// Output, send data to server.
		OutputStream os = socket.getOutputStream();
		os.write(SendData.getBytes("utf-8"));
		os.flush();
		socket.shutdownOutput();
		os.close();
		
		// Input, receive data from server.
		BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.ReturnData=br.readLine();
		String temp;
		while((temp=br.readLine())!=null){
			this.ReturnData+="\n"+temp;
		}
		
		this.ReturnDataJson=new JSONObject(this.ReturnData);
		if(this.ReturnDataJson.getString("status").equals("got")){
			return true;
		}else{
			return false;
		}
	}*/



/*
public class SocketWithServer{
	String SocketData=null;
	String ReturnData=null;
	JSONObject
	public void startSocket(){
		try {
			Socket socket = new Socket("home.0x29a.cc", 21027);
			String SocketData = this.SocketData;
			
			// Output, send data to server.
			OutputStream os = socket.getOutputStream();
			os.write(SocketData.getBytes("utf-8"));
			os.flush();
			socket.shutdownOutput();
			
			// Input, receive data from server.
			BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.ReturnData=br.readLine();
			String temp;
			while((temp=br.readLine())!=null){
				this.ReturnData+="\n"+temp;
			}
			
			// close socket.
			br.close();
			os.close();
			socket.close();
			
			this.ReturnDataJson=new JSONObject(this.ReturnData);
			
		}catch(UnknownHostException e){
			e.printStackTrace();
			this.ReturnData=null;
			this.ReturnDataJson=null;
		}catch(IOException e){
			e.printStackTrace();
			this.ReturnData=null;
			this.ReturnDataJson=null;
		}catch(JSONException e){
			e.printStackTrace();
			this.ReturnData=null;
			this.ReturnDataJson=null;
		}
	}
}
*/
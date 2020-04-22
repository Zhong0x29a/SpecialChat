package cc.specialchat.specialchatserver;

import com.alibaba.fastjson.JSONObject;
import com.sun.istack.internal.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by Administrator on 2018/5/3.
 */
public class ServerThread extends Thread {
	
	private Socket socket;
	
	private BufferedReader br;
	private OutputStream os;
	
	ServerThread(Socket socket,BufferedReader br,OutputStream os){
		this.socket = socket;
		this.br=br;
		this.os=os;
//		socket.setSoTimeout(30000);
//		br=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
//		os=socket.getOutputStream();
	}
	
	@Override
	public void run() {
		try{
			while(socket!=null && !socket.isClosed() && socket.isConnected()){
				String temp;
				while((temp=br.readLine())!=null){
					os.write( (ProcessData(temp.replaceAll("<br>","\n")).replaceAll("\n","<br>") +"\n").getBytes(StandardCharsets.UTF_8));
					sleep(88);
				}
			}
		}catch(Exception e){
			try{
				socket.close();
				System.out.println("Connection closed. ");
			}catch(IOException ex){
				ex.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	private String ProcessData(@NotNull String dataString){
		System.out.println(dataString);
		JSONObject dataJsonReturn=JSONObject.parseObject(dataString);
		String msgSend;
		
		try{
			switch(dataJsonReturn.getString("action")){
				case "beat": // heartbeat.
					msgSend="{'alive':true}";
					break;
				case "CheckUpdate": // check update
					msgSend=ProcessAction.action_checkUpdate(dataJsonReturn);
					break;
				case "0001": // check login status.
					msgSend=ProcessAction.action_0001(dataJsonReturn);
					break;
				case "0002": // perform login.
					msgSend=ProcessAction.action_0002(dataJsonReturn);
					break;
				case "0003": // client refresh message.
					msgSend=ProcessAction.action_0003(dataJsonReturn);
					break;
				case "0004": // send message.
					msgSend=ProcessAction.action_0004(dataJsonReturn);
					break;
				case "0005": // check ID usability.
					msgSend=ProcessAction.action_0005(dataJsonReturn);
					break;
				case "0006": // sign up new account.
					msgSend=ProcessAction.action_0006(dataJsonReturn);
					break;
				case "0007": // add new contact.
					msgSend=ProcessAction.action_0007(dataJsonReturn);
					break;
				case "0008": // fetch contact info.
					msgSend=ProcessAction.action_0008(dataJsonReturn);
					break;
				case "0009": // search contact.
					msgSend=ProcessAction.action_0009(dataJsonReturn);
					break;
				case "0010": // fetch contacts.
					msgSend=ProcessAction.action_0010(dataJsonReturn);
					break;
				case "0011": // check if is friend.
					msgSend=ProcessAction.action_0011(dataJsonReturn);
					break;
				case "0012": // fetch user_id by phone.
					msgSend=ProcessAction.action_0012(dataJsonReturn);
					break;
				case "0013": // edit user profile.
					msgSend=ProcessAction.action_0013(dataJsonReturn);
					break;
				default: // action code error.
					msgSend="{\"msg\":\"ERROR!! (ST1000)\"}";
					break;
				
			}
		}catch(Exception e){
			e.printStackTrace();
			msgSend="{'exception':'JSON Error.'}";
		}
		
		System.out.println(msgSend+"\n");
		
		return msgSend.replaceAll("\n","<br>");
	}
	
}


//	private class ReadingData extends Thread{
//		@Override
//		public void run(){
//			while( !socket.isClosed() ){
//				StringBuilder DataGet=new StringBuilder();
//
//				InputStream inputStream=null;
//				InputStreamReader inputStreamReader=null;
//				BufferedReader bufferedReader=null;
//
//				try{
//
//					inputStream=socket.getInputStream();
//					inputStreamReader=new InputStreamReader(inputStream);
//					bufferedReader=new BufferedReader(inputStreamReader);
//
//					String temp;
//					while((temp=bufferedReader.readLine())!=null){
//						DataGet.append("\n").append(temp);
//					}
//
//					if(DataGet.length()>0){
//						JSONObject jsonData=JSONObject.parseObject(DataGet.toString());
//
//						// Output, send data to client.
//						OutputStream outputStream = socket.getOutputStream();
//						outputStream.write(ProcessData(jsonData).getBytes(StandardCharsets.UTF_8));
//						outputStream.flush();
//
//					}else{
//						socket.close();
//					}
//
//				}catch(Exception e){
//					e.printStackTrace();
//				}finally{
//					try{
//						if(bufferedReader != null){
//							bufferedReader.close();
//						}
//						if(inputStreamReader != null){
//							inputStreamReader.close();
//						}
//						if(inputStream != null){
//							inputStream.close();
//						}
//						if(!socket.isInputShutdown()){
//							socket.shutdownInput();
//						}
//						if(!socket.isOutputShutdown()){
//							socket.shutdownOutput();
//						}
//					}catch(IOException e){
//						e.printStackTrace();
//					}
//				}
//
//			}
//		}
//	}
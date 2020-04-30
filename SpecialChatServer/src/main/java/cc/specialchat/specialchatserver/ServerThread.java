package cc.specialchat.specialchatserver;

import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.messaging.saaj.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 2020.03
 */
public class ServerThread extends Thread {
	
	private Socket socket;
	
	private BufferedReader br;
	private OutputStream os;
	
	private String user_id;
	
	ServerThread(Socket socket,BufferedReader br,OutputStream os,String user_id){
		this.socket = socket;
		this.br=br;
		this.os=os;
		this.user_id=user_id;
	}
	
	void hasNewMessage(){
		//todo: complete
		// os.write(...);
		try{
			sleep(20);
		}catch(InterruptedException e){
			e.printStackTrace();
			return;
		}
		try{
			synchronized(this){
				os.write("".getBytes(StandardCharsets.UTF_8));
			}
			// todo: wait for recall in "run()".
		}catch(IOException e){
			e.printStackTrace();
			try{
				socket.close();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Keep listening to the request from client.
	 */
	@Override
	public void run() {
		try{
			while(socket!=null && !socket.isClosed() && socket.isConnected()){
				String temp;
				while((temp=br.readLine())!=null){
					
					temp=Base64.base64Decode(temp);
					String dataSend=new String( Base64.encode(ProcessData(temp).getBytes()) );
					
					synchronized(this){
						os.write((dataSend+"\n").getBytes(StandardCharsets.UTF_8));
					}
				}
			}
		}catch(Exception e){
			try{
				socket.close();
				ServerMain.serverThreadMap.remove(this.user_id);
				System.out.println("Connection closed. ");
			}catch(IOException ex){
				ex.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	private String ProcessData(String dataString){
		System.out.println(dataString);
		JSONObject dataJsonReturn=JSONObject.parseObject(dataString);
		String msgSend;
		
		try{
			switch(dataJsonReturn.getString("action")){
				case "beat": // heartbeat.
					msgSend="{'alive':true}";
					break;
				case "recall":
					msgSend=null;
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
			msgSend="{'exception':'Error.'}";
		}
		
		System.out.println(msgSend+"\n");
		
		return msgSend;
	}
	
}

package cc.specialchat.specialchatserver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * 2020.03
 */
/*
* todo: control the permissions !!!
* */
public class ServerThread extends Thread {
	
	private Socket socket;
	
	private BufferedReader br;
	private final OutputStream os;
	
	private String user_id;
	
	boolean isLogged; // whether the client is logged in.
	
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
			sleep(66);
		}catch(InterruptedException e){
			e.printStackTrace();
			return;
		}
		try{
//			String virData="{'user_id':'"+this.user_id+"'}"; //no token_key
//			ProcessAction.action_0003(JSONObject.parseObject(virData)); todo: do not delete message immediately.
			
			List<String[]> temp=MsgCacheSQLite.fetchMsg(this.user_id);
			if(temp==null || temp.size()<=0){
				return;
			}
			
			JSONArray a=new JSONArray();
			
			a.addAll(temp);
			
			String dataStr="{'header':{'type':'request','action':'0001'},'body':{'data':"+
					JSON.toJSONString(a,SerializerFeature.DisableCircularReferenceDetect)+"}}";
			
			dataStr=new String(Base64.getEncoder().encode(dataStr.getBytes(StandardCharsets.UTF_8)) ).replaceAll("\n","");
			
			synchronized(os){
				os.write( (dataStr+"\n").getBytes(StandardCharsets.UTF_8));
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
//					System.out.println(temp);
					
					temp=new String(java.util.Base64.getDecoder().decode(temp));
					
					System.out.println(temp);
					// Phrase the header.
					JSONObject header=JSONObject.parseObject(temp).getJSONObject("header");
					//todo: process the header.
					if("return".equals(header.getString("type"))){
						
						continue;
					}
					
					JSONObject body=JSONObject.parseObject(temp).getJSONObject("body");
					
					String dataSend=ProcessData(body);
					
					if(dataSend==null){
						continue;
					}
					
					dataSend="{'header':{'type':'return','rid':'"+header.getString("rid")+"'},'body':"+dataSend+"}";
					dataSend=new String(Base64.getEncoder().encode(dataSend.getBytes(StandardCharsets.UTF_8)) ).replaceAll("\n","");
					
					System.out.println(dataSend);
					
					synchronized(os){
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
	
	private String ProcessData(JSONObject dataJsonReturn){
//		System.out.println(dataJsonReturn);
		
		String msgSend=null;
		
		try{
			switch(dataJsonReturn.getString("action")){
				case "beat": // heartbeat.
					msgSend="{'alive':true}";
					break;
				case "recall": // recall
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
					if(isLogged){
						msgSend=ProcessAction.action_0003(dataJsonReturn);
					}
					break;
				case "0004": // send message.
					if(isLogged){
						msgSend=ProcessAction.action_0004(dataJsonReturn);
					}
					break;
				case "0005": // check ID usability.
					msgSend=ProcessAction.action_0005(dataJsonReturn);
					break;
				case "0006": // sign up new account.
					msgSend=ProcessAction.action_0006(dataJsonReturn);
					break;
				case "0007": // add new contact.
					if(isLogged){
						msgSend=ProcessAction.action_0007(dataJsonReturn);
					}
					break;
				case "0008": // fetch contact info.
					if(isLogged){
						msgSend=ProcessAction.action_0008(dataJsonReturn);
					}
					break;
				case "0009": // search contact.
					if(isLogged){
						msgSend=ProcessAction.action_0009(dataJsonReturn);
					}
					break;
				case "0010": // fetch contacts.
					msgSend=ProcessAction.action_0010(dataJsonReturn);
					break;
				case "0011": // check if is friend.
					if(isLogged){
						msgSend=ProcessAction.action_0011(dataJsonReturn);
					}
					break;
				case "0012": // fetch user_id by phone.
					if(isLogged){
						msgSend=ProcessAction.action_0012(dataJsonReturn);
					}
					break;
				case "0013": // edit user profile.
					if(isLogged){
						msgSend=ProcessAction.action_0013(dataJsonReturn);
					}
					break;
				default: // action code error.
					break;
			}
		}catch(Exception e){
			e.printStackTrace();
			msgSend="{'exception':'Error.'}";
		}
		
//		System.out.println(msgSend+"\n");
		
		return msgSend;
	}
	
	private String generateRid(){
		return String.valueOf(MyTools.getRandomNum(99999999,10000000));
	}
	
}

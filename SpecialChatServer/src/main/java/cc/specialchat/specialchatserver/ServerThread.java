package cc.specialchat.specialchatserver;

import com.alibaba.fastjson.JSONObject;

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
	private StringBuffer DataReturn=new StringBuffer();
	private static String msgSend;
	
	ServerThread(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		OutputStream outputStream = null;
		
		try {
			// get data
			inputStream = socket.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(inputStreamReader);
			
			String temp;
			while((temp=bufferedReader.readLine())!=null){
				DataReturn.append("\n").append(temp);
			}
			socket.shutdownInput();
			
			JSONObject dataJsonReturn=JSONObject.parseObject(DataReturn.toString());
			String user_id,password,token_key;
			
			System.out.println(dataJsonReturn.getString("action"));
			switch(dataJsonReturn.getString("action")){
				case "0001": // check login status
					user_id=dataJsonReturn.getString("user_id");
					token_key=dataJsonReturn.getString("token_key");
					if(UserInfoSQLite.verifyUserTokenKey(user_id,token_key)){
						msgSend="{\"status\":\"true\"}";
					}
					break;
				case "0002": // perform login
					user_id=dataJsonReturn.getString("user_id");
					password=dataJsonReturn.getString("password");
					String[] user_info=UserInfoSQLite.goLogin(user_id,password);
					if(user_info[0].equals("")){
						msgSend="{\"status\":\"false\"}";
					}else if(user_id!=null){
						msgSend="{\"status\":\"true\"," +
								"\"user_id\":\""+user_info[0]+"\"," +
								"\"user_name\":\""+user_info[1]+"\"," +
								"\"token_key\":\""+user_info[2]+"\"," +
								"\"login_time\":\""+user_info[3]+"\"" +
								"}";
					}else{
						msgSend="{\"msg\":\"What's Wrong?? (1002)\"}";
					}
					break;
				case "0003": // client refresh message
					user_id=dataJsonReturn.getString("user_id");
					token_key=dataJsonReturn.getString("token_key");
					if(UserInfoSQLite.verifyUserTokenKey(user_id,token_key)){
						String[][] msg_temp;
						if((msg_temp=MsgCacheSQLite.fetchMsg(user_id)).length>0){
							StringBuffer p;
							if(msg_temp != null){
								p=new StringBuffer("{\"new_msg_num\":\""+msg_temp[0][0]+"\",");
								for(int i=0;i<Integer.parseInt(msg_temp[0][0]);i++){
									p.append("\"index_").append((i+1)).append("\":\"{'user_id':'").append(msg_temp[i][1]).append("','send_time':'").append(msg_temp[i][3]).append("','msg_content':'").append(msg_temp[i][2]).append("'}\",");
								}
								p.append("\"is_new_msg\":\"true\"}");
								msgSend=p.toString();
							}
						}else{
							msgSend="{\"is_new_msg\":\"false\"}";
						}
					}
					break;
				default:
					msgSend="{\"msg\":\"ERROR!! (1000)\"}";
					break;
			}
			System.out.println(DataReturn);
			
			// send
			outputStream = socket.getOutputStream();
			outputStream.write(msgSend.getBytes(StandardCharsets.UTF_8));
			outputStream.flush();
			
			socket.shutdownOutput();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			// release resource
			try{
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

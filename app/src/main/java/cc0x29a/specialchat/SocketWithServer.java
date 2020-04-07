package cc0x29a.specialchat;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Start a Socket With Server
 * Data in DataSend should be filter special chars to avoid problems !!
 *
 * @author Zhong Wenliang
 * @version 1.0
 * date: 20.03.05
 *
 * To do: encrypt all data!!
 * **/
class SocketWithServer{
//	String DataSend=null;
//	private StringBuffer DataReturn=new StringBuffer();
//	private JSONObject DataJsonReturn=null;
//	int delay=5;
//	Handler handler;
	
	public android.os.Handler.Callback sendMsgHandler;
	
	
	JSONObject startSocket(String DataSend) throws JSONException{
		final String[] temp=new String[1];
		
		android.os.Handler.Callback revMsgHandler=new Handler.Callback(){
			@Override
			public boolean handleMessage(@NonNull Message msg){
				temp[0]=msg.obj.toString();
				return false;
			}
		};
		
		
		
		
		return new JSONObject(temp[0]);
		
//		return new__NetworkService.sendData(DataSend);
//		new Thread(){
//			@Override
//			public void run() {
//				try {
//					Socket socket = new Socket("specialchat.0x29a.cc", 21027);
////					Socket socket = new Socket("192.168.1.18", 21027);
//					socket.setSoTimeout(delay*1000);
//
//					// Output, send data to server.
//					OutputStream os = socket.getOutputStream();
//					os.write(DataSend.getBytes(StandardCharsets.UTF_8));
//					os.flush();
//					socket.shutdownOutput();
//
//
//					// Input, receive data from server.
//					BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
//					DataReturn.append(br.readLine());
//					String temp;
//					while((temp=br.readLine())!=null){
//						DataReturn.append("\n").append(temp);
//					}
//					br.close();
//					os.close();
//					socket.close();
//
//					System.out.println(DataReturn.toString());
//
//					if(DataReturn!=null){
//						DataJsonReturn=new JSONObject(DataReturn.toString());
//					}
//				}catch(Exception e){
//					DataReturn=null;
//					e.printStackTrace();
//				}
//			}
//		}.start();
//
//		int startTime=MyTools.getCurrentTime();
//		while(MyTools.getCurrentTime()<startTime+delay){
//			if(DataJsonReturn!=null){
//				return DataJsonReturn;
//			}
//		}
//
//		return DataJsonReturn;

	}
}



/* ------ Code End Here ------ */
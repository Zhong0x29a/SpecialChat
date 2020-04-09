package cc0x29a.specialchat;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

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
	int delay=5;
	
	private String local_temp;
	
	Handler revMsgHandler;
	
	@SuppressLint("HandlerLeak")
	public void onCreate(){
	
	}
	
	new__NetworkService.swapData swapData;
	
	JSONObject startSocket(final String DataSend) throws Exception{
		
		new Thread(){
			@SuppressLint("HandlerLeak")
			@Override
			public void run(){
				Message msg=new Message();
				msg.what=0x29a1;
				msg.obj=DataSend.replaceAll("\n","<br>");
				
				swapData=new new__NetworkService.swapData(revMsgHandler); //todo bug here
				
				swapData.start();
				
				int startTime=MyTools.getCurrentTime();
				while(MyTools.getCurrentTime()<startTime+1){
				
				}
				
				swapData.sendMsgHandler.sendMessage(msg);
				
				System.out.println("SWS57, new thread started\n"+msg.obj);
				
				Looper.prepare();
				revMsgHandler=new Handler(){
					@Override
					public void handleMessage(@NonNull Message msg){
						if(msg.what==0x29a0){
							local_temp=msg.obj.toString();
							System.out.println("SWS 34:\ntemp[0]"+local_temp+"\nmsg.obj"+msg.obj);
						}else{
							System.out.println("What???");
						}
					}
				};
				Looper.loop();
				
				
				
			}
		}.start();
		
		int startTime=MyTools.getCurrentTime();
		while(MyTools.getCurrentTime()<startTime+10){
//			if(temp!=null && temp.length()>0){
//				System.out.println("SWS 65\n"+temp);
//				return new JSONObject(temp);
//			}
			if((local_temp=swapData.temp).length()>0){
				return new JSONObject(local_temp);
			}
		}
		
		System.out.println("SWS69, error!!!!!!!!!");
		return new JSONObject("{'msg':'error'}");
	}
}


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

/* ------ Code End Here ------ */
package cc0x29a.specialchat;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

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
	
//	private String local_temp;
	
	Handler revMsgHandler;
	
	@SuppressLint("HandlerLeak")
	public void onCreate(){
	
	}
	
	new__NetworkService.swapData swapData;
	
//	JSONObject startSocket(final String DataSend,Handler recallHandler) throws Exception{
	
	void startSocket(final String DataSend,final Handler recallHandler) throws Exception{
		
		new Thread(){
			@SuppressLint("HandlerLeak")
			@Override
			public void run(){
				
				revMsgHandler=new Handler(){
					@Override
					public void handleMessage(@NonNull Message msg){
						if(msg.what==0x29a0){
							recallHandler.sendMessage(msg);
						}else{
							System.out.println("What???");
						}
					}
				};
				
				swapData=new new__NetworkService.swapData(revMsgHandler);
				
				swapData.start();
				
//				int startTime=MyTools.getCurrentTime();
//				while(MyTools.getCurrentTime()<startTime+1){
//
//				}
				
				Message msg=new Message();
				msg.what=0x29a1;
				msg.obj=DataSend.replaceAll("\n","<br>");
				
				while(swapData.sendMsgHandler==null){
					try{
						Thread.sleep(5);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
				
				swapData.sendMsgHandler.sendMessage(msg);
				
				Looper.loop();
				
			}
		}.start();
		
		
		System.out.println("SWS69, error!!!!!!!!!");
		
//		return new JSONObject("{'msg':'error'}");
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
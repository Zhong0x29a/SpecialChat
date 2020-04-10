package cc0x29a.specialchat;

import android.os.Handler;
import android.os.Message;

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
	
	void startSocket(final String DataSend,final Handler recallHandler,int what){
		synchronized(this){
			if(new__NetworkService.socket==null||new__NetworkService.socket.isClosed()||!new__NetworkService.socket.isConnected()){
				new__NetworkService.StartConnect st=new new__NetworkService.StartConnect();
				st.start();
			}
			
//			new__NetworkService.swapData swapData=new new__NetworkService.swapData(recallHandler,what);
			
			new__NetworkService.swapData swapData=new new__NetworkService.swapData();
			
			swapData.msgWhat=what;
			swapData.revMsgHandler=recallHandler;
			
//			swapData.start();
			
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
		}
		
	}
}

/* ------ Code End Here ------ */
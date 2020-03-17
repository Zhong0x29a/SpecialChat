package cc0x29a.specialchat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundTask extends Service{
	
	static Timer syncLM;
	static Timer syncCL;
	
	public BackgroundTask(){
	}
	
	@Override
	public void onCreate(){
//		Toast.makeText(this,"service started",Toast.LENGTH_SHORT).show();
		
		syncLM=new Timer();
		syncCL=new Timer();
		
		syncLM.schedule(new TimerTask(){
			@Override
			public void run(){
				syncLatestMsg();
			}
		},200,60000);
		
		syncCL.schedule(new TimerTask(){
			@Override
			public void run(){
				syncContactsList();
			}
		},177,120000);
	}
	
	@Override
	public IBinder onBind(Intent intent){
		// TO DO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public void onDestroy(){
		if(syncCL!=null){syncCL.cancel();}
		if(syncLM!=null){syncLM.cancel();}
	}
	
	/**
	 * Sync the last_msg under chat_list.db
	 */
	private void syncLatestMsg(){
		ChatListSQLiteHelper cListSQLH=new ChatListSQLiteHelper(this,"chat_list.db",1);
		String[][] chatList=cListSQLH.fetchChatList(cListSQLH.getReadableDatabase(),0);
		
		// Fetch last one message then update
		String[] lastMsg;
		for(int i=1;i<= (Integer.parseInt(chatList[0][0])) && i<=50;i++){
			MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(this,
					"msg_"+chatList[i][1]+".db",1);
			lastMsg=msgSQLiteHelper.getLatestMsg(msgSQLiteHelper.getReadableDatabase());
			cListSQLH.updateChatList(cListSQLH.getReadableDatabase(),chatList[i][1],lastMsg[1],lastMsg[0]);
		}
//		System.out.println("synced");
	}
	
	// TODO: 16/03/20 finish this.
	private void syncContactsList(){
	
	}

}
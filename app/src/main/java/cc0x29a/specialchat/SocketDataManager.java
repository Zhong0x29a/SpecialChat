package cc0x29a.specialchat;

import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

public class SocketDataManager{
	private String rid;
	
	SocketDataManager(){
		this.rid=generateRid();
	}
	
	JSONObject startRequest(String data){
		synchronized(this){
			SocketWithServerService.dataManagerHashMap.put(rid,SocketDataManager.this);
		}
		data="{'header':{'type':'request','rid':'"+rid+"'},'body':"+data+" }";
//		System.out.println(data);
		
		Message msg=new Message();
		msg.what=0x1;
		msg.obj=data;
		
		if(SocketWithServerService.handler!=null){
			SocketWithServerService.handler.sendMessage(msg);
		}
		
		JSONObject temp;
		synchronized(this){ //this may not correct??
			try{
				wait();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			SocketWithServerService.dataManagerHashMap.remove(rid);
			
			temp=SocketWithServerService.dataSet.get(rid);
			SocketWithServerService.dataSet.remove(rid);
			
		}
		if(temp!=null){
			return temp;
		}else{
			try{
				return new JSONObject("{'error':'DataManager'}");
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private String generateRid(){
		return String.valueOf(MyTools.getRandomNum(99999999,10000000));
	}
}

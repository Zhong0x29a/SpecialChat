package cc0x29a.specialchat;

public class SocketDataManager{
	String rid;
	
	SocketDataManager(){
		this.rid=generateRid();
	}
	
	String startRequest(String data){
		synchronized(this){
			SocketWithServerService.dataManagerHashMap.put(rid,SocketDataManager.this);
		}
		SocketWithServerService.sendData(data);
		String temp;
		synchronized(this){
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
			return "{'error':'DataManager'}";
		}
		
	}
	
	private String generateRid(){
		return String.valueOf(MyTools.getRandomNum(99999999,10000000));
	}
}

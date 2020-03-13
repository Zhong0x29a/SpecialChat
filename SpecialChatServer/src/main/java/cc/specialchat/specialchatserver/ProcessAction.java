package cc.specialchat.specialchatserver;

import com.alibaba.fastjson.JSONObject;

public class ProcessAction{
	private static String user_id,password,token_key;
	
	static String action_0001(JSONObject JsonData){
		user_id=JsonData.getString("user_id");
		token_key=JsonData.getString("token_key");
		if(UserInfoSQLite.verifyUserTokenKey(user_id,token_key)){
			return "{\"status\":\"true\"}";
		}else{
			return "{'status':'false'}";
		}
	}
	
	static String action_0002(JSONObject JsonData){
		user_id=JsonData.getString("user_id");
		password=JsonData.getString("password");
		String[] user_info=UserInfoSQLite.goLogin(user_id,password);
		if(user_info[0].equals("")){
			return "{\"status\":\"false\"}";
		}else if(user_id!=null){
			return "{\"status\":\"true\"," +
					"\"user_id\":\""+user_info[0]+"\"," +
					"\"user_name\":\""+user_info[1]+"\"," +
					"\"token_key\":\""+user_info[2]+"\"," +
					"\"login_time\":\""+user_info[3]+"\"" +
					"}";
		}else{
			return "{\"msg\":\"What's Wrong?? (1002)\"}";
		}
	}
	
	static String action_0003(JSONObject JsonData){
		user_id=JsonData.getString("user_id");
		token_key=JsonData.getString("token_key");
		if(UserInfoSQLite.verifyUserTokenKey(user_id,token_key)){
			String[][] msg_temp;
			if((msg_temp=MsgCacheSQLite.fetchMsg(user_id)).length>0){
				StringBuffer p;
				if(msg_temp != null){
					p=new StringBuffer("{\"new_msg_num\":\""+msg_temp[0][0]+"\",");
					for(int i=0;i<Integer.parseInt(msg_temp[0][0]);i++){
						p.append("\"index_").append((i+1)).append("\":" +
								"\"{'user_id':'").append(msg_temp[i][1]).append("'," +
								"'send_time':'").append(msg_temp[i][3]).append("'," +
								"'msg_content':'").append(msg_temp[i][2]).append("'" +
								"}\",");
					}
					p.append("\"is_new_msg\":\"true\"}");
					return p.toString();
				}
			}else{
				return "{\"is_new_msg\":\"false\"}";
			}
		}
		return "{'msg':'login info error'}"; //todo complete!!
	}
	
	
	
}

/*
*

static String action_0003(JSONObject JsonData){

}

*
* */
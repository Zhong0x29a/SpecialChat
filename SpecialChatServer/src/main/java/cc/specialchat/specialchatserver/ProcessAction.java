package cc.specialchat.specialchatserver;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.sql.SQLException;

public class ProcessAction{
	private static String user_id,password,token_key;
	
	/**
	 * Check login status by user_id and token_key
	 * @param JsonData data from client
	 * @return String
	 */
	static String action_0001(JSONObject JsonData){
		try{
			user_id=JsonData.getString("user_id");
			token_key=JsonData.getString("token_key");
			if(UserInfoSQLite.verifyUserTokenKey(user_id,token_key)){
				return "{\"status\":\"true\"}";
			}else{
				return "{'status':'false'}";
			}
		}catch(JSONException e){
			e.printStackTrace();
			return "{'status':'false'}";
		}
	}
	
	/**
	 * Perform login action
	 * @param JsonData data from client
	 * @return String
	 */
	static String action_0002(JSONObject JsonData){
		try{
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
		}catch(JSONException e){
			e.printStackTrace();
			return "{\"msg\":\"What's Wrong?? (1002)\"}";
		}
	}
	
	/**
	 * Fetch new messages. (refresh messages)
	 * @param JsonData data from client
	 * @return String
	 */
	static String action_0003(JSONObject JsonData){
		try{
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
		}catch(JSONException e){
			e.printStackTrace();
			return "{'status':'false','msg':'login info error! (1003)'}";
		}
		return "{'status':'false','msg':'login info error! (1003)'}";
	}
	
	/**
	 * Send messages
	 * @param JsonData Data from client
	 * @return String
	 */
	static String action_0004(JSONObject JsonData){
		try{
			user_id=JsonData.getString("user_id");
			token_key=JsonData.getString("token_key");
			String to_id=JsonData.getString("to_id");
			String msg_content=JsonData.getString("msg_content");
			int send_time;
			if(UserInfoSQLite.verifyUserTokenKey(user_id,token_key) &&
					(send_time=MsgCacheSQLite.insertNewMsg(user_id,to_id,msg_content))!=0){
				return "{'status':'true','send_time':'"+send_time+"'}";
			}
			return "{'status':'false','msg':'login info error! (1004)'}";
		}catch(JSONException e){
			e.printStackTrace();
			return "{'status':'false','msg':'login info error! (1004)'}";
		}
	}
	
}

/*
*
/**
*
* @param JsonData data from client
* @return

static String action_0003(JSONObject JsonData){

}

*
* */
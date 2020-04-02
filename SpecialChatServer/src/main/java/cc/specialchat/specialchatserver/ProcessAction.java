package cc.specialchat.specialchatserver;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

class ProcessAction{
	
	/**
	 * Check login status by user_id and token_key
	 * @param JsonData data from client
	 * @return String
	 */
	static String action_0001(JSONObject JsonData){
		try{
			String user_id=JsonData.getString("user_id");
			String token_key=JsonData.getString("token_key");
			if(UserInfoSQLite.verifyUserTokenKey(user_id,token_key)){
				return "{\"status\":\"true\"}";
			}else{
				return "{'status':'false'}";
			}
		}catch(JSONException e){
			e.printStackTrace();
			return "{'status':'false','msg':'ERROR(PA1001)'}";
		}
	}
	
	/**
	 * Perform login action v
	 * @param JsonData data from client
	 * @return String
	 */
	static String action_0002(JSONObject JsonData){
		try{
			String user_id=JsonData.getString("user_id");
			String password=JsonData.getString("password");
			String[] user_info=UserInfoSQLite.goLogin(user_id,password);
			if(user_info[0].equals("")){
				return "{\"status\":\"false\"}";
			}else if(user_id!=null){
				return "{\"status\":\"true\"," +
						"\"user_id\":\""+user_info[0]+"\"," +
						"\"user_name\":\""+user_info[1]+"\"," +
						"\"token_key\":\""+user_info[2]+"\"," +
						"\"login_time\":\""+user_info[3]+"\"," +
						"\"user_phone\":\""+user_info[4]+"\"" +
						"}";
			}else{
				return "{\"msg\":\"What's Wrong?? (PA1002)\"}";
			}
		}catch(JSONException e){
			e.printStackTrace();
			return "{\"msg\":\"What's Wrong?? (PA1002+54)\"}";
		}
	}
	
	/**
	 * Fetch new messages. (refresh messages)
	 * @param JsonData data from client
	 * @return String
	 */
	static String action_0003(JSONObject JsonData){
		try{
			String user_id=JsonData.getString("user_id");
			String token_key=JsonData.getString("token_key");
			if(UserInfoSQLite.verifyUserTokenKey(user_id,token_key)){
				String[][] msg_temp;
				if((msg_temp=MsgCacheSQLite.fetchMsg(user_id)).length>0){
					StringBuffer p;
					if(msg_temp != null && !msg_temp[0][0].equals("0")){
						p=new StringBuffer("{\"new_msg_num\":\""+msg_temp[0][0]+"\",");
						for(int i=0;i<Integer.parseInt(msg_temp[0][0]);i++){
							p.append("\"index_").append((i+1)).append("\":" +
									"\"{'user_id':'").append(msg_temp[i+1][1]).append("'," +
									"'send_time':'").append(msg_temp[i+1][3]).append("'," +
									"'msg_content':'").append(msg_temp[i+1][2]).append("'" +
									"}\",");
						}
						p.append("\"is_new_msg\":\"true\"}"); //todo bug: only one msg would be fetched each time?
						return p.toString();
					}else{
						return "{\"is_new_msg\":\"false\"}";
					}
				}else{
					return "{\"is_new_msg\":\"false\"}";
				}
			}
		}catch(JSONException e){
			e.printStackTrace();
			return "{'status':'false','msg':'login info error! (PA1003)'}";
		}
		return "{'status':'false','msg':'login info error! (PA1003+91)'}";
	}
	
	/**
	 * Send messages
	 * @param JsonData Data from client
	 * @return String
	 */
	static String action_0004(JSONObject JsonData){
		try{
			String user_id=JsonData.getString("user_id");
			String token_key=JsonData.getString("token_key");
			String to_id=JsonData.getString("to");
			String msg_content=JsonData.getString("msg_content");
			int send_time;
			if(UserInfoSQLite.verifyUserTokenKey(user_id,token_key) &&
					(send_time=MsgCacheSQLite.insertNewMsg(user_id,to_id,msg_content))!=0){
				return "{'status':'true','send_time':'"+send_time+"'}";
			}
			return "{'status':'false','msg':'login info error! (PA1004)'}";
		}catch(JSONException e){
			e.printStackTrace();
			return "{'status':'false','msg':'login info error! (PA1004+113)'}";
		}
	}
	
	/**
	 * Check id usability
	 * @param JsonData JSONObject
	 * @return String form json
	 */
	static String action_0005(JSONObject JsonData){
		try{
			String user_id=JsonData.getString("user_id");
			if(UserInfoSQLite.checkIDUsability(user_id)){
				return "{'status':'true'}";
			}else{
				return "{'status':'false'}";
			}
		}catch(JSONException e){
			e.printStackTrace();
			return "{'status':'false','msg':'Error! (PA1005)'}";
		}
	}
	
	/**
	 * Sign up new user
	 * @param JsonData data
	 * @return String msg
	 */
	static String action_0006(JSONObject JsonData){
		try{
			String invite_code=JsonData.getString("invite_code");
			String secret=JsonData.getString("secret");
			if(invite_code.equals("0x29a.cc") && secret.equals("I love you.")){
				String user_id=JsonData.getString("user_id");
				String user_phone=JsonData.getString("user_phone");
				String user_name=JsonData.getString("user_name");
				String password=JsonData.getString("password");
				
				if(UserInfoSQLite.addNewUser(user_id,user_phone,user_name,password)){
					return "{'status':'true'}";
				}else{
					return "{'status':'false','msg':'Error! (PA1005)'}";
				}
			}else{
				return "{'status':'false',\"msg\":\"you don\'t love me...\"}";
			}
		}catch(JSONException e){
			e.printStackTrace();
			return "{'status':'false','msg':'Error! (PA1005+161)'}";
		}
	}
	
	/**
	 * Add new contact .
	 * @param JsonData JSONObject
	 * @return String
	 */
	static String action_0007(JSONObject JsonData){
		try{
			String user_id=JsonData.getString("user_id");
			String token_key=JsonData.getString("token_key");
			String ta_id=JsonData.getString("ta_id");
			if(UserInfoSQLite.verifyUserTokenKey(user_id,token_key)){
				String[] my_info=UserInfoSQLite.fetchUserInfo(user_id);
				String[] ta_info=UserInfoSQLite.fetchUserInfo(ta_id);
				if(my_info!=null && ta_info!=null &&
						!ContactListSQLite.checkIsFriend(my_info[1],ta_info[1]) &&
						ContactListSQLite.addNewContact(my_info[1],ta_info[1],my_info[3],ta_info[3])){
					return "{'status':'true'}";
				}else{
					return "{'status':'false','msg':'Error(PA1007+inn+inn)'}";
				}
			}else{
				return "{'status':'false','msg':'Error(PA1007+inn)'}";
			}
		}catch(JSONException|NullPointerException e){
			e.printStackTrace();
			return "{'status':'false','msg':'Error(PA1007)'}";
		}
	}
	
	/**
	 * Fetch contact detail
	 * @param JsonData JSONObject
	 * @return String
	 */
	static String action_0008(JSONObject JsonData){
		try{
			String[] info;
			if(null != JsonData &&
					JsonData.getString("secret").equals("I love you.") &&
					null != ( info=UserInfoSQLite.fetchUserInfo(JsonData.getString("ta_id")) )){
				return "{" +
						"'status':'true'," +
						"'user_name':'"+info[3]+"'," +
						"'user_phone':'"+info[2]+"' " +
						"}";
			}else{
				return "{'status':'false','msg':'Error(PA1008)'}";
			}
		}catch(JSONException|NullPointerException e){
			e.printStackTrace();
			return "{'status':'false','msg':'Error(PA1008+)'}";
		}
	}
	
	/**
	 * Search contact
	 * @param JsonData JSONObject
	 * @return String
	 */
	static String action_0009(JSONObject JsonData){
		try{
			String user_id=JsonData.getString("user_id");
			String token_key=JsonData.getString("token_key");
			String search_id=JsonData.getString("search_id");
			String[][] data;
			if(UserInfoSQLite.verifyUserTokenKey(user_id,token_key)
				&& (data=UserInfoSQLite.searchUsers(search_id))!=null){
				StringBuilder temp_msg=new StringBuilder("{'status':'true','number':'"+data[0][0]+"'" );
				for(int i=1;i<=Integer.parseInt(data[0][0]);i++){
//					temp_msg.append(",\"index_"+i+"\":\"{'user_id':'"+data[i][1]+"','user_name':'"+data[i][3]+"'}\"");
					temp_msg.append(",\"index_").append(i).append("\":\"{'user_id':'").append(data[i][1])
							.append("','user_name':'").append(data[i][3]).append("'}\"");
				}
				temp_msg.append("}");
				
				return temp_msg.toString();
			}else{
				return "{'status':'false','msg':'something error!'}";
			}
		}catch(JSONException|NullPointerException e){
			e.printStackTrace();
			return "{'status':'false','msg':'Error(PA1009)'}";
		}
		
	}
	
	/**
	 * Fetch user's contact list
	 * @param JsonData data
	 * @return msg
	 */
	static String action_0010(JSONObject JsonData){
		try{
			String user_id=JsonData.getString("user_id");
			String token_key=JsonData.getString("token_key");
			String[][] contacts;
			if(UserInfoSQLite.verifyUserTokenKey(user_id,token_key) &&
					(contacts=ContactListSQLite.fetchContacts(user_id))!=null
					&& !contacts[0][0].equals("0")){
				StringBuilder msg=new StringBuilder("{'status':'true',");
				for(int i=1;i<=Integer.parseInt(contacts[0][0]);i++){
					msg.append("\"index_").append(i)
							.append("\":\"{'user_id':'").append(contacts[i][0])
							.append("','nickname':'").append(contacts[i][1]).append("'}\",");
				}
				msg.append("'number':'").append(contacts[0][0]).append("'}");
				return msg.toString();
			}
			return "{'status':'false','msg':'Error(PA1010inner)'}";
		}catch(JSONException e){
			e.printStackTrace();
			return "{'status':'false','msg':'Error(PA1010)'}";
		}
	}
	
	/**
	 * Check if is friend
	 * @param JsonData JSONObject
	 * @return msg
	 */
	static String action_0011(JSONObject JsonData){
		try{
			String my_id=JsonData.getString("my_id");
			String ta_id=JsonData.getString("ta_id");
			if(my_id!=null && ta_id!=null && JsonData.getString("secret").equals("I love you.") &&
					ContactListSQLite.checkIsFriend(my_id,ta_id)){
				return "{'status':'true','is_friend':'true'}";
			}else{
				return "{'status':'false','is_friend':'false'}";
			}
		}catch(Exception e){
			e.printStackTrace();
			return "{'status':'false','is_friend':'false','msg':'Exception!"+e.toString()+"'}";
		}
	}
	
}

/*
*

*
* */
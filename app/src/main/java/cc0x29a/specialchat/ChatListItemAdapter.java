package cc0x29a.specialchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Adapter for ListView of chat list.
 */

public class ChatListItemAdapter extends BaseAdapter{
	private LayoutInflater layoutInflater;
	String[][] chatListInfo;
//	String[] lastMsg=null;
	int count=0;
	
	ChatListItemAdapter(Context context){
		layoutInflater=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return this.count;
	}
	
	@Override
	public Object getItem(int position) {
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	static class ViewHolder{
		ImageView item_profile_pic;
		TextView nickname, lastChatMsg,lastChatTime;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position,View convertView,ViewGroup parent) {
		ViewHolder holder;
		position++;
		if (convertView == null){
			convertView = layoutInflater.inflate(R.layout.chat_list_item,null);

			holder = new ViewHolder();
			holder.item_profile_pic=convertView.findViewById(R.id.chatListItem_profile_pic);
			holder.nickname=convertView.findViewById(R.id.chatListItem_nickname);
			holder.lastChatTime=convertView.findViewById(R.id.chatListItem_last_chat_time);
			holder.lastChatMsg=convertView.findViewById(R.id.chatListItem_last_msg);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		if(this.chatListInfo[position][2]==null ||this.chatListInfo[position][2].isEmpty()){
			holder.nickname.setText(this.chatListInfo[position][1]);
		}else{
			holder.nickname.setText(this.chatListInfo[position][2]);
		}
		if(this.chatListInfo[position][4]==null||this.chatListInfo[position][4].isEmpty()){
			holder.lastChatMsg.setText(" ");
		}else{
			holder.lastChatMsg.setText(this.chatListInfo[position][4]);
		}
		holder.lastChatTime.setText(this.chatListInfo[position][3]);
		
		//todo load user's profile picture
		holder.item_profile_pic.setImageResource(R.drawable.ic_launcher_background);
		return convertView;
	}
}

//		Glide.with(context).load("http://pic18.nipic.com/20120203/2457331_104836021342_2.jpg").into(holder.imageView);

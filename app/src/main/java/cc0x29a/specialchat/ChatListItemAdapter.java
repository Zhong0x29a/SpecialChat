package cc0x29a.specialchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Class for ListView of chat list.
 */

public class ChatListItemAdapter extends BaseAdapter{
	private LayoutInflater layoutInflater;
	static String[][] chatListInfo;
	static String[] lastMsg=null;
	static int count=0;
	
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
		public ImageView item_profile_pic;
		public TextView nickname, lastChatMsg,lastChatTime;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null){
			convertView = layoutInflater.inflate(R.layout.chat_list_item,null);

			holder = new ViewHolder();
			holder.item_profile_pic=convertView.findViewById(R.id.item_profile_pic);
			holder.nickname=convertView.findViewById(R.id.item_nickname);
			holder.lastChatTime=convertView.findViewById(R.id.item_last_chat_time);
			holder.lastChatMsg=convertView.findViewById(R.id.item_last_msg);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		if(this.chatListInfo[position][2].isEmpty()){
			holder.nickname.setText(this.chatListInfo[position][1]);
		}else{
			holder.nickname.setText(this.chatListInfo[position][2]);
		}
		if(this.lastMsg[position].isEmpty()){
			holder.lastChatMsg.setText(" ");
		}else{
			holder.lastChatMsg.setText(this.lastMsg[position]);
		}
		holder.lastChatTime.setText(this.chatListInfo[position][3]);
		
		//todo here
		holder.item_profile_pic.setImageResource(R.drawable.ic_launcher_background);
		return convertView;
	}
}

//		Glide.with(context).load("http://pic18.nipic.com/20120203/2457331_104836021342_2.jpg").into(holder.imageView);

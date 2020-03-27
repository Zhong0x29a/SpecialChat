package cc0x29a.specialchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/*
 * the Adapter for RecyclerView of main chat list, R.id.main_chat_recyclerView
 */

public class ChatListItemAdapter extends RecyclerView.Adapter<ChatListItemAdapter.ViewHolder>{
	
	private List<String[]> data;
	int count;
	
	static class ViewHolder extends RecyclerView.ViewHolder{
		final ImageView iv_profilePic;
		final TextView tv_nickname,tv_lastMsg,tv_lastChatTime;
		LinearLayout ll_container;
		ViewHolder(View v){
			super(v);
			iv_profilePic=v.findViewById(R.id.chatListItem_profile_pic);
			tv_nickname=v.findViewById(R.id.chatListItem_nickname);
			tv_lastMsg=v.findViewById(R.id.chatListItem_last_msg);
			tv_lastChatTime=v.findViewById(R.id.chatListItem_last_chat_time);
			ll_container=v.findViewById(R.id.chatListItem_container);
		}
	}
	
	ChatListItemAdapter(List<String[]> data){
		this.data=data;
	}
	
	/**
	 * use when delete data from chat list
	 * @param position deleted data's position
 	 */
	private void deleteData(int position) {
		this.data.remove(position);
		this.count=this.data.size();
		notifyItemRemoved(position);
	}
	
	/**
	 *  use when chat list updated
	 * @param newData new data
	 */
	void updateData(List<String[]> newData) {
		this.data=newData;
		this.count=newData.size();
		notifyDataSetChanged();
	}
	
	@Override
	public void onBindViewHolder(@NotNull ViewHolder holder,final int position){
		final String[] tempData=data.get(position);
//		holder.iv_profilePic.setImageResource();
// 		Glide.with(context).load("http://pic18.nipic.com/20120203/2457331_104836021342_2.jpg").into(holder.imageView);
		
		// set Nickname
		if(tempData[2]==null || tempData[2].isEmpty()){
			holder.tv_nickname.setText(tempData[1]);
		}else{
			holder.tv_nickname.setText(tempData[2]);
		}
		
		// set last message
		if(tempData[4]==null||tempData[4].isEmpty()){
			holder.tv_lastMsg.setText(" ");
		}else{
			holder.tv_lastMsg.setText(tempData[4]);
		}
		
//		SimpleDateFormat df = new SimpleDateFormat("yy-mm-dd HH:mm:ss",Locale.getDefault());
////		Date date=new Date();
//		String d = df.format(MyTools.getCurrentTime());
//
		// set last chat time
		holder.tv_lastChatTime.setText(MyTools.formatTime(tempData[3]));
		
		// set on clicked listener
		holder.ll_container.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				// Start chat activity, send user_id and ta's nickname by bundle
				Intent intent=new Intent(v.getContext(),ChatActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("user_id", tempData[1]);
				bundle.putString("nickname",tempData[2]); // bug in database!!
				intent.putExtras(bundle);
				
				ContextCompat.startActivity(v.getContext(),intent,bundle);
			}
		});
		
		// set on long clicked listener
		holder.ll_container.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(final View v){
				// show a alert for user to decide whether to delete the chat list item
				AlertDialog alertDialog2 = new AlertDialog.Builder(v.getContext())
						.setTitle("Notice")
						.setMessage("Sure to delete this chat? \n'"+tempData[1]+"'")
						.setPositiveButton("Yeah", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								// delete the chat item! by position
								// delete SQLite data
								ChatListSQLiteHelper chatListSQLiteHelper=
										new ChatListSQLiteHelper(v.getContext(),"chat_list.db",1);
								chatListSQLiteHelper.deleteChatListItem(chatListSQLiteHelper.getReadableDatabase(),tempData[1]);
								
								// update recyclerView
								deleteData(position);
								
								Toast.makeText(v.getContext(), "Deleted. "+position, Toast.LENGTH_SHORT).show();
							}
						})
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i){
								// noting to do
							}
						})
						.create();
				alertDialog2.show();
				
				return true;
			}
		});
		
	}
	
	// items count
	@Override
	public int getItemCount(){
		return this.count;
	}
	
	@NotNull
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
		View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item,parent,false);
		return new ViewHolder(view);
	}
	
}



/*

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
		
		
		holder.item_profile_pic.setImageResource(R.drawable.ic_launcher_background);
		return convertView;
	}
}
*/

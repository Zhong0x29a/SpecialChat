package cc0x29a.specialchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactsListItemAdapter extends BaseAdapter{
	private LayoutInflater layoutInflater;
	String[][] contactsInfo;
	int count=0;
	
	ContactsListItemAdapter(Context context){
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
		ImageView profile_pic;
		TextView nickname;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position,View convertView,ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null){
			convertView = layoutInflater.inflate(R.layout.contacts_list_item,null);
			
			holder = new ViewHolder();
			holder.profile_pic=convertView.findViewById(R.id.contactsListItem_profile_pic);
			holder.nickname=convertView.findViewById(R.id.contactsListItem_nickname);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		// todo: Seemed need to optimised
		if(!(this.contactsInfo[position][2]==null || this.contactsInfo[position][2].isEmpty())){
			holder.nickname.setText(MyTools.resolveSpecialChar(this.contactsInfo[position][2]));
		}else if(!(this.contactsInfo[position][1]==null || this.contactsInfo[position][1].isEmpty())){
			holder.nickname.setText(MyTools.resolveSpecialChar(this.contactsInfo[position][1]));
		}else{
			holder.nickname.setText(MyTools.resolveSpecialChar(this.contactsInfo[position][0]));
		}
		
		//todo: next ver load user's profile picture
		holder.profile_pic.setImageResource(R.drawable.ic_launcher_background);
		return convertView;
	}
}

//		Glide.with(context).load("http://pic18.nipic.com/20120203/2457331_104836021342_2.jpg").into(holder.imageView);

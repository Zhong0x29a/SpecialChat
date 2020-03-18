package cc0x29a.specialchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

/*
 * Adapter for R.id.chatWindow_listView.
 * To show chat records
 * No more bugs !!!
 */

public class ChatWindowAdapter extends RecyclerView.Adapter<ChatWindowAdapter.VH>{
	String my_id;
	private String[][] messages;
	int count;
	
	static class VH extends RecyclerView.ViewHolder{
		final TextView chat_msg_tv;
		final LinearLayout chat_msg_container;
		VH(View v) {
			super(v);
			chat_msg_tv= v.findViewById(R.id.chat_msg_content);
			chat_msg_container=v.findViewById(R.id.chat_msg_container);
		}
	}
	
	ChatWindowAdapter(String[][] data) {
		this.messages = data;
	}
	
	void addData(String[][] newRecord) {
		
		if(Integer.parseInt(newRecord[0][0])==0){
			return;
		}
		
		String[][] tempRecord=new String[messages.length+Integer.parseInt(newRecord[0][0])][5];
		System.arraycopy(messages,0,tempRecord,0,messages.length);
		int index=1;
		for(int i=messages.length;i<=messages.length+Integer.parseInt(newRecord[0][0])-1;i++){
			tempRecord[i]=newRecord[index];
			index++;
		}
		messages=tempRecord;
		count=count+Integer.parseInt(newRecord[0][0]);
//		notifyItemInserted(Integer.parseInt(newRecord[0][0])-1);
		notifyDataSetChanged();
	}
	
	@Override
	public void onBindViewHolder(@NotNull VH holder,final int position) {
		final int index=position+1;
		
		// avoid array index exception
		if(position>=messages.length-1){
			return;
		}
		
		holder.chat_msg_tv.setText(messages[index][4]);
		if(messages[index][1]!=null && messages[index][1].equals(my_id)){
			holder.chat_msg_container.setGravity(Gravity.END);
			holder.chat_msg_tv.setBackgroundResource(R.drawable.my_msg_style);
		}else{
			holder.chat_msg_container.setGravity(Gravity.START);
			holder.chat_msg_tv.setBackgroundResource(R.drawable.ta_msg_style);
		}
		holder.chat_msg_container.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// nothing to do here.
			}
		});
		holder.chat_msg_container.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(final View v){
				AlertDialog alertDialog2 = new AlertDialog.Builder(v.getContext())
						.setTitle("Notices")
						.setMessage("Sure to delete this message? \n'"+messages[index][4]+"'\n"+position)
						.setPositiveButton("Yeah", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								//todo delete the message! by index !
								
								Toast.makeText(v.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
							}
						})
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i){
								// nothing to do here.
							}
						})
						.create();
				alertDialog2.show();
				return true;
			}
		});
	}
	
	@Override
	public int getItemCount() {
		return this.count;
	}
	
	@NotNull
	@Override
	public VH onCreateViewHolder(ViewGroup parent,int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_msg_item, parent, false);
		return new VH(v);
	}
}

// coda. fine.


/*

public class ChatWindowAdapter extends BaseAdapter{
	private LayoutInflater layoutInflater;
	String my_id;
	String[][] messages;
	int count;
	
	ChatWindowAdapter(Context context){
		layoutInflater=LayoutInflater.from(context);
	}
	
	static class ViewHolder{
		TextView msg;
	}
	
	@Override
	public int getCount(){
		return this.count;
	}
	
	@Override
	public Object getItem(int position){
		return null;
	}
	
	@Override
	public long getItemId(int position){
		return 0;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		ViewHolder holder;
		position++;
		if(convertView==null){
			if(messages[position][1].equals(my_id)){
//			if(messages[position][1].equals("2950")){
				
				convertView=layoutInflater.inflate(R.layout.chat_my_msg,null);
				holder=new ViewHolder();
				holder.msg=convertView.findViewById(R.id.chat_my_msg);
			}else{
				convertView=layoutInflater.inflate(R.layout.chat_ta_msg,null);
				holder=new ViewHolder();
				holder.msg=convertView.findViewById(R.id.chat_ta_msg);
			}
			
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		
		holder.msg.setText(messages[position][4]);
		return convertView;
	}
}

*/
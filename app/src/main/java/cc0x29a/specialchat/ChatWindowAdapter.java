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

import java.util.List;

/*
 * Adapter for R.id.chatWindow_recyclerView.
 * To show chat history record
 * No bugs yet .
 */

public class ChatWindowAdapter extends RecyclerView.Adapter<ChatWindowAdapter.VH>{
	
	String my_id; // my id
	String ta_id; // ta id (other side)
	
	int count; // Item counts
	
	private List<String[]> data; // RecyclerView's data

	
	static class VH extends RecyclerView.ViewHolder{
		final TextView chat_msg_tv;
		final LinearLayout chat_msg_container;
		VH(View v) {
			super(v);
			chat_msg_tv= v.findViewById(R.id.chat_msg_content);
			chat_msg_container=v.findViewById(R.id.chat_msg_container);
		}
	}
	
//	ChatWindowAdapter(String[][] data) {
//		this.messages = data;
//	}
	
	ChatWindowAdapter(List<String[]> data) {
		this.data=data;
	}
	
	/**
	 * Load more history record
	 * @param newData new fetched chat history record from SQLite
	 */
	void addMoreData(List<String[]> newData) {
		
		// if no more record
		if(newData.size()==0){
			return;
		}
		
		// add data
		data.addAll(newData.size()-1,newData);
		
		// item number
		count=newData.size();
		
		// apply changes
		notifyDataSetChanged();
	}
	
	/**
	 * When send or receive new message.
	 * @param newData String[]
	 */
	void addNewData(String[] newData){
		// add new data
		data.add(0,newData);
		
		// item number
		count+=1;
		
		// apply change
		notifyDataSetChanged();
	}
	
	// abandon func.
	/**
	 *  use when chat list updated
	 * //@param new_data new data
	 */
//	void updateData(String[][] new_data) {
//		this.messages=new_data;
//		this.count=Integer.parseInt(new_data[0][0]);
//		notifyDataSetChanged();
//	}
	
	@Override
	public void onBindViewHolder(@NotNull VH holder,final int position) {
		
		final String[] itemData=data.get(position);
		
		// show message content
		holder.chat_msg_tv.setText(itemData[4]);
		
		// set message by who (view style)
		if(itemData[1]!=null && itemData[1].equals(my_id)){
			holder.chat_msg_container.setGravity(Gravity.END);
			holder.chat_msg_tv.setBackgroundResource(R.drawable.my_msg_style);
		}else{
			holder.chat_msg_container.setGravity(Gravity.START);
			holder.chat_msg_tv.setBackgroundResource(R.drawable.ta_msg_style);
		}
		
		// on item clicked
		holder.chat_msg_container.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// nothing to do here.
			}
		});
		
		// on item long clicked, delete action
		holder.chat_msg_container.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(final View v){
				AlertDialog alertDialog2 = new AlertDialog.Builder(v.getContext())
						.setTitle("Notices")
						.setMessage("Sure to delete this message? \n'"+itemData[4]+"'("+position+")\nOnce delete, data cannot be recovered.")
						.setPositiveButton("Yeah", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								// delete the message by index .
								MsgSQLiteHelper helper=new MsgSQLiteHelper(v.getContext(),"msg_"+ta_id+".db",1);
								helper.deleteMsg(helper.getReadableDatabase(),itemData[0]);
								notifyItemRemoved(position);
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
	
	// Item counts
	@Override
	public int getItemCount() {
		return this.count;
	}
	
	// def method
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
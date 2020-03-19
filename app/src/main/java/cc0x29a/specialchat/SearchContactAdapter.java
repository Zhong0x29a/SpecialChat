package cc0x29a.specialchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchContactAdapter extends RecyclerView.Adapter<SearchContactAdapter.ViewHolder>{
	
	int count;
	String[][] data;
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
		View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.search_contact_item,parent,false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder,int position){
		final int index=position+1;
		
		//todo
//		holder.tv_user_name.setText(data[index][1]);
	}
	
	@Override
	public int getItemCount(){
		return this.count;
	}
	
	static class ViewHolder extends RecyclerView.ViewHolder{
		TextView tv_user_name,tv_user_id;
		LinearLayout ll_container;
		ViewHolder(View v){
			super(v);
			tv_user_name=v.findViewById(R.id.search_contact_user_name);
			tv_user_id=v.findViewById(R.id.search_contact_user_id);
			ll_container=v.findViewById(R.id.search_contact_container);
		}
	}
	
	SearchContactAdapter(String[][] data){
		this.data=data;
	}
}

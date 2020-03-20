package cc0x29a.specialchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SearchContactAdapter extends RecyclerView.Adapter<SearchContactAdapter.ViewHolder>{
	
	int count;
	private String[][] data;
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
		View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.search_contact_item,parent,false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder,final int position){
//		final int index=position+1;
		
		//todo debug!!
		
		holder.tv_user_name.setText(data[position][1]);
		holder.tv_user_id.setText(data[position][0]);
		
		holder.ll_container.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent=new Intent(v.getContext(),ContactDetailActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("user_id",data[position][0]);
				intent.putExtras(bundle);
				ContextCompat.startActivity(v.getContext(),intent,bundle);
			}
		});
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

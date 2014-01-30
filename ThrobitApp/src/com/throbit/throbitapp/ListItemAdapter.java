package com.throbit.throbitapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItemAdapter extends ArrayAdapter<Story>{

	private Activity myContext;
	private ArrayList<Story> datas;
	
	public ListItemAdapter(Context context, int textViewResourceId, ArrayList<Story> objects){
		super(context, textViewResourceId, objects);
		
		myContext = (Activity) context;
		datas = objects;
	}
	
	static class ViewHolder{
		TextView itemTitleView;
		TextView itemDateView;
		ImageView itemURLView;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder viewHolder;
		
		if(convertView == null){
			LayoutInflater inflater = myContext.getLayoutInflater();
			convertView = inflater.inflate(R.layout.listitem, null);
			
			viewHolder = new ViewHolder();
			viewHolder.itemURLView = (ImageView) convertView.findViewById(R.id.postThumb);
			viewHolder.itemTitleView = (TextView) convertView.findViewById(R.id.postTitleLabel);
			viewHolder.itemDateView = (TextView) convertView.findViewById(R.id.postDateLabel);
			convertView.setTag(viewHolder);
		} else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if(datas.get(position).storyURL == null){
			viewHolder.itemURLView.setImageResource(R.drawable.ic_launcher);
		}
		
		viewHolder.itemTitleView.setText(datas.get(position).storyTitle);
		viewHolder.itemDateView.setText(datas.get(position).storyDate);
		
		return convertView;
	}
}

package com.example.ezmessage.menu;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * settingLayoutList안의 MainSettingLayout들을 ListView에 띄우기 위한 ArrayAdapter
 *  
 * @author SOL
 *
 */
public class MainSettingAdapter extends ArrayAdapter<MainSettingLayout>{

	private Context context;
	private ArrayList<MainSettingLayout> items;
	
	public MainSettingAdapter(Context context, int resource, ArrayList<MainSettingLayout> items) {
		super(context, resource, items);
		this.context = context;
		this.items = items;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return items.get(position);
	}

}

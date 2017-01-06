package com.example.new_message;

import com.example.contents.PhoneNumberContent;
import com.example.ezmessage.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//TODO make profilePicture
/**
 * 사람들의 전화번호부 정보를 보여주는 layout
 * 
 * @author SOL
 *
 */
public class PhoneNumberListLayout extends RelativeLayout{
	
	private ImageView profilePicture;
	private TextView name;
	private TextView information;
	private TextView informationType;
	
	public PhoneNumberListLayout(Context context, PhoneNumberContent content) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.phone_number_list_item, this, true);
		
		profilePicture = (ImageView) findViewById(R.id.profilePicture);
		name = (TextView)findViewById(R.id.name);
		information = (TextView) findViewById(R.id.information);
		informationType = (TextView) findViewById(R.id.informationType);
		
		setItem(content);
	}

	public void setItem(PhoneNumberContent content) {
		name.setText(content.getName());
		information.setText(content.getInformation());
		informationType.setText(content.getInformationType());
	}

}

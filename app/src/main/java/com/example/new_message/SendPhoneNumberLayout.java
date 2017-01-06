package com.example.new_message;

import com.example.contents.PhoneNumberContent;
import com.example.ezmessage.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * TODO 버튼을 누르면 수정, 삭제하는 기능 만들 것
 * 
 * 보낼 사람들의 간략한 정보를 넣은 layout
 * @author SOL
 *
 */
public class SendPhoneNumberLayout extends LinearLayout{

	private NewMessageActivity context;
	
	private PhoneNumberContent content;
	
	private Button sendNumberButton;
	private Button deleteSendNumberButton;

	public SendPhoneNumberLayout(NewMessageActivity context, PhoneNumberContent content) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.send_phone_number_item, this, true);
		
		this.context = context;
		this.content = content;
		
		sendNumberButton = (Button) findViewById(R.id.sendNumberButton);
		deleteSendNumberButton = (Button) findViewById(R.id.deleteButton);
		
		if(content.getName().equals("")){
			sendNumberButton.setText(content.getInformation());
		}
		else{
			sendNumberButton.setText(content.getName());
		}
		
		sendNumberButton.setOnClickListener(sendButtonListener());
		deleteSendNumberButton.setOnClickListener(deleteButtonListener());
	}

	public PhoneNumberContent getPhoneNumberContent (){
		return content;
	}
	
	//TODO make Button
	private OnClickListener sendButtonListener(){
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		};
	}
	private OnClickListener deleteButtonListener(){
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				context.deleteSendItem(SendPhoneNumberLayout.this);
			}
		};
	}
}

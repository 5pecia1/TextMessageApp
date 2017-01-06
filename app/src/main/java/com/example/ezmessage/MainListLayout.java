package com.example.ezmessage;

import com.example.contents.MessageContent;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/*
 * TODO 아직 ProfilePicture는 구현하지 않았음
 * 주소록 읽을때 추가 할 것
 */
public class MainListLayout extends RelativeLayout{

	private ImageView mainProfilePicture = null;
	private TextView mainName = null;
	private TextView mainContext = null;
	private TextView mainDate = null;

	/**
	 * 각 뷰의 객체를 저장한다
	 * setMessage 메서드를 호출한다
	 * 
	 * @param context
	 * @param message
	 * 
	 */
	public MainListLayout(Context context, MessageContent message) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.main_list_item, this, true);

		mainProfilePicture = (ImageView) findViewById(R.id.mainProfilePicture);
		mainName = (TextView) findViewById(R.id.name);
		mainContext = (TextView) findViewById(R.id.context);
		mainDate = (TextView) findViewById(R.id.itemInformation);

		setMessage(message);
	}

	/**
	 * Layout에 내용을 넣는다
	 * 
	 * @author SOL
	 *
	 * @param message
	 * 
	 */
	public void setMessage(MessageContent message) {
		//TODO make	previewProfilePicture.setImageDrawable(message.getIcon());
		mainName.setText(getMainName(message));
		mainContext.setText(message.getContext());
		//TODO make Day or Time selector
		mainDate.setText(message.getDay());
	}
	
	/**
	 * TODO TalkListLayout과 겹치는 코드 처리하기.
	 * TODO 주소록 읽어 와서 같은 번호에 대한것 처리하기.
	 * 
	 * 현재 thread_id에서 대화 하고있는 상대들의 번호를 합쳐서 리턴한다 
	 * 
	 * @author SOL
	 *
	 * @param message
	 * @return headNumber
	 * 
	 */
	private String getMainName(MessageContent message){
		Uri allMessage = Uri.parse("content://sms/");
		String[] projection = new String[]{"DISTINCT " + "address", "thread_id"};
		String selection = "thread_id==";
		String sortOrder = null; 
		String talkPeoplePhoneNumber = "";

		Cursor cur = getContext().getContentResolver().query(allMessage, projection, selection + message.getThread_id(), null, sortOrder);

		while(cur.moveToNext()){
			talkPeoplePhoneNumber += (talkPeoplePhoneNumber.equals(""))? "":", ";
			talkPeoplePhoneNumber += cur.getString(cur.getColumnIndex("address"));
		}

		cur.close();

		return talkPeoplePhoneNumber;
	}
}

package com.example.talk_list;

import com.example.contents.MessageContent;
import com.example.ezmessage.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

/**
 * TalkListView에 나오는 date line
 * 
 * @author SOL
 */
public class TalkDateLineLayout extends TalkListLayout{
	TextView talkDateTextView = null;
	
	public TalkDateLineLayout(Context context, MessageContent content) {
		super(context,content);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.talk_date_line, this, true);
		talkDateTextView = (TextView) findViewById(R.id.talkDateTextView);
		
		setTalkItem(super.content);
	}

	@Override
	public void setTalkItem(MessageContent content) {
		talkDateTextView.setText(content.getyyyyMMddE());
	}


}

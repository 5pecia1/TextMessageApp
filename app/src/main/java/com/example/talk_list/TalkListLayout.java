package com.example.talk_list;

import com.example.contents.MessageContent;

import android.content.Context;
import android.widget.RelativeLayout;

/**
 * TalkListAdapter의 Item이 되는 상위 layout
 * @author SOL
 *
 */
public abstract class TalkListLayout extends RelativeLayout{
	protected MessageContent content;
	protected TalkActivity context;
	
	public TalkListLayout(Context context, MessageContent content) {
		super(context);
		this.content = content;
		this.context = (TalkActivity)getContext();
	}
	
	abstract public void setTalkItem(MessageContent content);
}

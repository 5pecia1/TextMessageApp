package com.example.contents;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 문자 하나의 최소 단위
 * 삭제 여부에 대한 정보도 가질 수 있다
 * 
 * @author SOL
 * 
 */

public class MessageContent {
	public static final String _ID = "_id";
	public static final String THREAD_ID = "thread_id";
	public static final String ADDRESS = "address";
	public static final String BODY = "body";
	public static final String DATE = "date";
	public static final String TYPE = "type";
	
	public static final int OTHER_TYPE = 1;
	public static final int ME_TYPE = 2;
	public static final int TEMPORARY_STORAGE_SMS_TYPE = 3;
	
	private final String id; 
	private final int thread_id;
	private final long date;
	private final String context;
	private final int type;
	
	private final String number;
	
	private boolean isChecked = false;
	
	private SimpleDateFormat ahhmmFormat = new SimpleDateFormat("a hh: mm",java.util.Locale.getDefault());
	private SimpleDateFormat dayFormat = new SimpleDateFormat("MM월 dd일",java.util.Locale.getDefault());
	private SimpleDateFormat yyyyMMddEFormat = new SimpleDateFormat("yyyy/MM/dd(E)",java.util.Locale.getDefault());
	
	public MessageContent(String id, int thread_id, String address, String body, long date, int type) {
		this.id = id;
		this.thread_id = thread_id;
		this.number = address;
		this.context = body;
		this.date = date;
		this.type = type;
	}
	public MessageContent(int thread_id, long date, String body, String address) {
		this("", thread_id, address, body, date, -1);
	}


	public int getThread_id() {
		return thread_id;
	}

	public String getahhmm() {
		return ahhmmFormat.format(new Date(date));
	}
	public String getDay() {
		return dayFormat.format(new Date(date));
	}
	public String getyyyyMMddE() {
		return yyyyMMddEFormat.format(new Date(date));
	}

	public String getContext() {
		return context;
	}

	public String getNumber() {
		return number;
	}

	public String getId() {
		return id;
	}
	public int getType() {
		return type;
	}
	public long getDate() {
		return date;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
}

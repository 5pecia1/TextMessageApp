package com.example.ezmessage;

import java.util.ArrayList;

import com.example.contents.MessageContent;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
/**
 * 
 * Main의 ListView에 각 thread_id 중 
 * 가장 최근의 문자메시지를 설정하는 Adapter
 * 
 * @author SOL
 * 
 */
//TODO 리스트 최적화 하기. 효율적으로 사용하자
public class MainListAdapter extends BaseAdapter implements Filterable{
	
	private ArrayList<MessageContent> messageViewList = null;
	private ArrayList<MessageContent> searchMessageList = null;
	private Context context = null;
	
	private SearchFilter<MessageContent> filter;
	
	public MainListAdapter(MainActivity context) {
		this.context = context;
		messageViewList = saveSMS(
				new String[]{MessageContent.THREAD_ID, "Max("+ MessageContent.DATE +")" + " AS " +  MessageContent.DATE, MessageContent.BODY, MessageContent.ADDRESS},
				"0=0) GROUP BY (thread_id",
				"date DESC"
				);
		
	}
	
	/**
	 * TODO TalkAdapter와 코드가 비슷하다
	 * 
	 * 해당 thread_id에 맞는 최신 문자메시지를 .query 메서드를 이용하여 불러온다
	 * 불러온 메시지는 messageList에 MessageContent객체로 저장한다
	 * 
	 * @author SOL
	 * 
	 */
	private synchronized ArrayList<MessageContent> saveSMS(String[] projection, String selection, String sortOrder) {
		ArrayList<MessageContent> list = null;
		Uri allMessage = Uri.parse("content://sms/");
//		String[] projection = new String[]{col[0], "Max("+col[1] +")" + " AS " + col[1],col[2], col[3]};
//		String selection = "0=0) GROUP BY (thread_id";//0=0 부분은 매우 중요하다. http://www.bkjia.com/Androidjc/905358.html
//		String sortOrder = "date DESC";//내림차순
		Cursor cur = context.getContentResolver().query(allMessage, projection, selection, null, sortOrder);
		
//		if(cur.getCount() == 0){
//			cur.close();
//			if(messageList == null)
//				return new ArrayList<MessageContent>(0);
//			else
//				return null;
//		}
		list = new ArrayList<MessageContent>(cur.getCount());

		while(cur.moveToNext()){
			list.add(
					new MessageContent(
							cur.getInt(cur.getColumnIndex(MessageContent.THREAD_ID)),
							cur.getLong(cur.getColumnIndex(MessageContent.DATE)),
							cur.getString(cur.getColumnIndex(MessageContent.BODY)),
							cur.getString(cur.getColumnIndex(MessageContent.ADDRESS))
							)
					);
		}
		
		cur.close();
		return list;
	}
	
	@Override
	public int getCount() {
		return messageViewList.size();
	}

	@Override
	public Object getItem(int position) {
		return messageViewList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MainListLayout messageView;
		
		if (convertView == null) {
			messageView = new MainListLayout(context, messageViewList.get(position));
		}else{
			messageView = (MainListLayout) convertView;
			
			messageView.setMessage(messageViewList.get(position));
		}
		return messageView;
	}
	
	/**
	 * saveSMS 메서드를 호출하고
	 * notifyDataSetChanged 메서드를 이용해 리스트뷰를 업데이트 시킨다
	 * 
	 * @author SOL
	 * 
	 */
	public void setItem() {
		messageViewList = saveSMS(
				new String[]{MessageContent.THREAD_ID, "Max("+ MessageContent.DATE +")" + " AS " +  MessageContent.DATE, MessageContent.BODY, MessageContent.ADDRESS},
				"0=0) GROUP BY (thread_id",
				"date DESC"
				);
		notifyDataSetChanged();
	}

	@Override
	public Filter getFilter() {
		if(filter == null)
			filter = new SearchFilter<MessageContent>();
		
		return filter;
	}
	
	//TODO 최적화 작업 할 것
	private class SearchFilter<MessageContent> extends Filter{

		@Override
		//첫번째로 호출됨, 실제 필터작업을 행함
		protected FilterResults performFiltering(CharSequence chars) {
			String filterSeq = chars.toString().toLowerCase();
			FilterResults result = new FilterResults();
			
			if (searchMessageList == null || searchMessageList.size() == 0 || filterSeq.length() <= 1){
				searchMessageList = saveSMS(new String[]{"*"},
						"body LIKE \"%" + filterSeq + "%\" OR address LIKE \"%" + filterSeq + "%\"", 
						"date DESC"
						);
			}
			if (filterSeq != null && filterSeq.length() > 0) {
				ArrayList<com.example.contents.MessageContent> filter = null;
				filter = new ArrayList<com.example.contents.MessageContent>();

				for (com.example.contents.MessageContent message : searchMessageList) {
					// the filtering itself:
					if (message.getContext().toLowerCase().contains(filterSeq) 
							|| message.getNumber().toLowerCase().contains(filterSeq)) {
						filter.add(message);
					}
				}
				result.count = filter.size();
				result.values = filter;
			}
			else {
				result.count = messageViewList.size();
				result.values = messageViewList;
			}

			return result;
		}

		@Override
		//두번째로 호출됨, 필터가 끝난걸 messageList에 넣고 값의 변화를 adapter에 알려줌
		protected void publishResults(CharSequence constraint, FilterResults results) {
			messageViewList = (ArrayList<com.example.contents.MessageContent>) results.values;
			notifyDataSetChanged();
			
		}
		
	}
	
}

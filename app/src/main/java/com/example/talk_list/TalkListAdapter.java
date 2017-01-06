package com.example.talk_list;

import java.util.ArrayList;

import com.example.contents.MessageContent;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;


/**
 * thread_id 값의 모든 문제 메시지를 불러와서 설정하는 Adapter
 * 
 * @author SOL
 */
public class TalkListAdapter extends BaseAdapter{

	private boolean isDeleteMode = false;

	private ArrayList<MessageContent> talkList = null;
	private int thread_id = -1;
	private String headNumber = "";

	private TalkActivity context;


	/**
	 * context, thread_id 값을 저장한다
	 * saveSMS()를 호출한다
	 * cheackManyNumber를 호출한다
	 * 
	 * @param talkActivity
	 * @param thread_id
	 * 
	 */
	public TalkListAdapter(TalkActivity talkActivity, int thread_id) {
		this.context = talkActivity;
		this.thread_id = thread_id;

		saveSMS();

		checkManyNumber();
	}

	/**
	 * normalMode로 설정하고 setItem을 설정한다
	 * 
	 * @author SOL
	 * 
	 */
	public void setNormalMode(){
		isDeleteMode = false;
		setItem();
	}

	/**
	 * deleteMode로 설정한다
	 * 
	 * @author SOL
	 * 
	 */
	public void setDeleteMode(){
		isDeleteMode = true;
		//		setItem();
	}

	/**
	 * TODO MainListAdapter와 코드가 비슷하다
	 * 
	 * 해당 thread_id의 모든 문자를 불러온다
	 * 불러온 메시지는 talkList에 MessageContent객체로 저장한다
	 * TalkDAteLineLyaout을 각 날짜에 맞게 넣는다
	 * 
	 * @author SOL
	 * 
	 */
	private void saveSMS() {
		Uri allMessage = Uri.parse("content://sms/");
		String[] projection = new String[]{"*"};
		String selection = "thread_id == " + thread_id;
		String sortOrder = "date ASC";//오름차순
		Cursor cur = context.getContentResolver().query(allMessage, projection, selection, null, sortOrder);
		talkList = new ArrayList<MessageContent>(cur.getCount()*2);

		MessageContent content = null;
		String date = "";

		while(cur.moveToNext()){
			content = new MessageContent(
					cur.getString(cur.getColumnIndex(MessageContent._ID)),
					cur.getInt(cur.getColumnIndex(MessageContent.THREAD_ID)),
					cur.getString(cur.getColumnIndex(MessageContent.ADDRESS)),
					cur.getString(cur.getColumnIndex(MessageContent.BODY)),
					cur.getLong(cur.getColumnIndex(MessageContent.DATE)),
					cur.getInt(cur.getColumnIndex(MessageContent.TYPE))
					);
			//set Date Line
			if( !date.equals(content.getyyyyMMddE())){
				date = content.getyyyyMMddE();
				talkList.add(
						new MessageContent(
								cur.getInt(cur.getColumnIndex(MessageContent.THREAD_ID)),
								cur.getLong(cur.getColumnIndex(MessageContent.DATE)),
								cur.getString(cur.getColumnIndex(MessageContent.BODY)),
								cur.getString(cur.getColumnIndex(MessageContent.ADDRESS))
								));
			}
			talkList.add(content);
		}
		cur.close();

	}

	@Override
	public int getCount() {
		return talkList.size();
	}

	@Override
	public Object getItem(int position) {
		return talkList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TalkListLayout view = (TalkListLayout)convertView;

		//TODO 메모리상 비 효율적인 방법, 코드로 해결하려면 복잡하다. 여유되면 할 것
		//출력해야 할 layout이 TalkDateLineLayout 이라면
		if(talkList.size() > 0 && talkList.get(position).getId().equals("")){
			//			if(convertView == null || convertView instanceof TalkBoxListLayout){
			view = new TalkDateLineLayout(context, talkList.get(position));
			//			}
			//			else{
			//				view.setTalkItem(talkList.get(position));
			//			}
		}
		//출력해야할 layout이 TalkBoxlistLayout 이라면
		else{
			//			if(convertView == null || convertView instanceof TalkDateLineLayout){
			view = new TalkBoxListLayout(context, talkList.get(position));
			//			}
			//			else{
			//				view.setTalkItem(talkList.get(position));
			//			}
		}


		if(view instanceof TalkBoxListLayout && isDeleteMode()){
			final TalkBoxListLayout talkView = ((TalkBoxListLayout)view);

			talkView.setDeleteMode();

			talkView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					talkView.checkToggle();
				}
			});

			talkView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return false;
				}

			});

		}
		else if(view instanceof TalkBoxListLayout){
			((TalkBoxListLayout)view).setNormalMode();

			view.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}

			});
		}

		return view;
	}

	/**
	 * 
	 * @author SOL
	 *
	 * @return 
	 * 		대화 상대의 전화번호
	 */
	public String getHeader() {
		return headNumber;
	}

	/**
	 * saveSMS 메서드를 호출하고
	 * notifyDataSetChanged 메서드를 이용해 리스트뷰를 업데이트 시킨다
	 * 
	 * @author SOL
	 * 
	 */
	public void setItem() {
		saveSMS();
		notifyDataSetChanged();
	}
	public int getThread_id(){
		return thread_id;
	}
	public boolean isDeleteMode() {
		return isDeleteMode;
	}

	/**
	 * talkList의 messageContent의 isChecked 값을 다 true로 만든다
	 * 
	 * @author SOL
	 */
	public void setAllChecked() {

		for (MessageContent messageContent : talkList) {
			if( !messageContent.getId().equals("") &&  !messageContent.isChecked()){
				messageContent.setChecked(true);

				context.addtCheckItemCountTextView();
			}
		}
		notifyDataSetChanged();
	}

	/**
	 * talkList의 messageContent의 isChecked 값을 다 false로 만든다
	 * 
	 * @author SOL
	 */
	public void setAllNotChecked() {

		Log.i("delete/TalkListADpater.setAllNotChecked", "start / talkList Size : " + talkList.size());
		for (MessageContent messageContent : talkList) {
			if( !messageContent.getId().equals("") && messageContent.isChecked()){
				messageContent.setChecked(false);
				context.subtractCheckItemCountTextView();
			}
		}		
		notifyDataSetChanged();
	}

	/**
	 * TODO 어쩌다 모든 문자 메시지가 삭제 될 수 있으나 현재는 막아 놨다
	 * 원인은 모르겠다
	 * 
	 * check되어 있는 talkList의 MesasgeContent를 삭제 시킨다
	 * 앱이 현재 기본 문자앱으로 설정 되어있어야 사용이 가능하다
	 * 사용이 불가능 할때 작동을 시키면 delete 실패가 Toast로 나온다
	 * 
	 * @author SOL
	 * 
	 */
	public void deleteSMS() {
		String where = null;

		for (MessageContent message : talkList) {

			//TODO 원인을 찾아서 깔끔하게 해결할 것
			if(message.isChecked()){
				if(message.getId().equals("")){
					Toast.makeText(context, "심각한 문제!!\n모든 문자 다 삭제 될뻔.\n checked : " + 
							message.isChecked() + " context : " + message.getContext() 
							+ "개발자에게 문의 하세요"
							, Toast.LENGTH_LONG);
				}else{
					int i = context.getContentResolver().delete(Uri.parse("content://sms/" + message.getId()), where, null);
					if(i == 0 ){
						Toast.makeText(context, "delete 실패",Toast.LENGTH_SHORT).show();
					}else{
						if(message.getType()  != 3)
							Toast.makeText(context, "delete 성공",Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}

	/**
	 * TODO MainListLayout과 겹치는 코드 처리하기.
	 * TODO 주소록 읽어 와서 같은 번호에 대한것 처리하기
	 * 
	 * 현재 thread_id에서 대화 하고있는 상대들의 번호를 합쳐서 리턴한다
	 * 
	 * @author SOL
	 * 
	 */
	private void checkManyNumber(){
		Uri allMessage = Uri.parse("content://sms/");
		String[] projection = new String[]{"DISTINCT " + "address", "thread_id"};
		String selection = "thread_id==";
		String sortOrder = null; 

		Cursor cur = context.getContentResolver().query(allMessage, projection, selection + thread_id, null, sortOrder);
		Log.i("send/TalkListAdapter.checkManyNumber", "cur count : " + cur.getCount());
		while(cur.moveToNext()){
			headNumber += (headNumber.equals(""))? "":", ";
			headNumber += cur.getString(cur.getColumnIndex("address"));
		}

		cur.close();	
	}

}


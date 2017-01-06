package com.example.ezmessage.menu.setting;

import java.util.LinkedList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * DB안의 QuickReplyPhrasesLayout들을 ListView에 띄우기 위한 CursorAdapter
 * 
 * @author SOL
 *
 */
public class QuickReplyPhrasesAdapter extends CursorAdapter{

	private boolean isDeleteMode = false;
	private LinkedList<Integer> checkedList = new LinkedList<Integer>();
	private TextView textView = null;
	private Button deleteButton = null;
	
	private QuickReplyPhrasesActivity context;

	public QuickReplyPhrasesAdapter(Context context, Cursor cur) {
		super(context, cur);
		this.context = (QuickReplyPhrasesActivity)context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cur) {

		for (Integer integer : checkedList) {

			if (cur.getInt(cur.getColumnIndex(QuickReplyPhrasesActivity.COL_ID)) == integer.intValue()) {
				((QuickReplyPhrasesLayout)view).setLayout(
						isDeleteMode, 
						true, 
						cur.getString(cur.getColumnIndex(QuickReplyPhrasesActivity.COL_PHRASE)),
						cur.getInt(cur.getColumnIndex(QuickReplyPhrasesActivity.COL_ID))
						);
				return;
			}
		}
		
		((QuickReplyPhrasesLayout)view).setLayout(isDeleteMode,
				false, 
				cur.getString(cur.getColumnIndex(QuickReplyPhrasesActivity.COL_PHRASE)),
				cur.getInt(cur.getColumnIndex(QuickReplyPhrasesActivity.COL_ID))
				);
	}

	@Override
	public View newView(Context context, Cursor cur, ViewGroup parent) {

		for (Integer integer : checkedList) {
			if (cur.getInt(cur.getColumnIndex(QuickReplyPhrasesActivity.COL_ID)) == integer.intValue()) {
				return new QuickReplyPhrasesLayout(context, isDeleteMode, 
						true, 
						cur.getString(cur.getColumnIndex(QuickReplyPhrasesActivity.COL_PHRASE)),
						cur.getInt(cur.getColumnIndex(QuickReplyPhrasesActivity.COL_ID)));
			}
		}
		return new QuickReplyPhrasesLayout(context, 
				isDeleteMode, 
				false, 
				cur.getString(cur.getColumnIndex(QuickReplyPhrasesActivity.COL_PHRASE)),
				cur.getInt(cur.getColumnIndex(QuickReplyPhrasesActivity.COL_ID)));
	}

	/**
	 * super.notifyDataSetChanged()를 호출 한다
	 * DeleteMode이고 item이 하나 이상 check 되었을 시 delete 버튼을 보여준다
	 * 아니면 숨긴다
	 */
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if(isDeleteMode && textView  != null){
			if(checkedList.size() <= 0 ){
				deleteButton.setVisibility(View.GONE);
			}else{
				deleteButton.setVisibility(View.VISIBLE);
			}
			textView.setText(String.valueOf(checkedList.size()));
		}
	}
	
	/**
	 * Delete에 맞는 Layout 설정후 notifyDataSetChanged 호출한다
	 * @author SOL
	 *
	 * @param textView
	 * @param deleteButton
	 */
	public void setDeleteMode(TextView textView, Button deleteButton) {
		isDeleteMode = true;
		this.textView = textView;
		this.deleteButton = deleteButton;
		notifyDataSetChanged();
	}

	/**
	 * NormalMode에 맞는 Layout 설정 후 notifyDataSetChanged 호출한다
	 * @author SOL
	 *
	 */
	public void setNormalMode() {
		isDeleteMode = false;
		checkedList.clear();
		textView =null;
		notifyDataSetChanged();
	}

	public boolean isDeleteMode() {
		return isDeleteMode;
	}

	/**
	 * delete 해야할 것들의 id를 이용하여 직접 SQLiteDatabase로 지운다
	 * 이 객체에 cursor를 업데이트하고 normalMode로 바꾼후 notifyDataSetChanged 호출한다
	 * @author SOL
	 *
	 * @param db
	 */
	public void deletePhrases(SQLiteDatabase db) {

		for (Integer integer : checkedList) {
			db.delete(QuickReplyPhrasesActivity.class.getSimpleName(), QuickReplyPhrasesActivity.COL_ID + " = " + integer, null);
		}

		changeCursor(context.getCursor());
		setNormalMode();
		notifyDataSetChanged();
	}

	public void setToggleCheckBox(long id) {
		Integer integer = new Integer((int)id);

		if( !checkedList.remove(integer)){
			checkedList.add(integer);
		}
		notifyDataSetChanged();
	}

	/**
	 * 모든 item을 check한다 
	 * check된 아이템은 무시한다
	 * 
	 * @author SOL
	 *
	 * @param cur
	 */
	public void setAllCheck(Cursor cur){
		if(cur.getCount() == 0) return;
		cur.moveToFirst();

		do{
			Integer integer = new Integer(cur.getInt(cur.getColumnIndex(QuickReplyPhrasesActivity.COL_ID)));

			if( !checkedList.contains(integer)){
				checkedList.add(integer);
			}
		}while(cur.moveToNext());
		notifyDataSetChanged();
	}
	
	public void setAllUnCheck(){
		checkedList.clear();
		notifyDataSetChanged();
	}
	
}

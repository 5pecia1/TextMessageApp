package com.example.ezmessage.menu.setting;

import com.example.ezmessage.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * QuickReplyPhrasesActivity의 ListView에 들어가는 Layout
 * 
 * @author SOL
 *
 */
public class QuickReplyPhrasesLayout extends RelativeLayout{

	private CheckBox leftCheckBox;
	private CheckBox rightCheckBox;
	private TextView titleText;
	private TextView subtitleText;

	private boolean isDeleteMode = false;
	private boolean isChecked = false;
	private Integer id = null;
	
	public QuickReplyPhrasesLayout(Context context, boolean isDeleteMode, boolean isChecked, String title, Integer id) {
		super(context);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.check_text_sub_text_check_item,this, true);

		leftCheckBox = (CheckBox) findViewById(R.id.leftCheckBox);
		rightCheckBox = (CheckBox) findViewById(R.id.rightCheckBox);
		titleText = (TextView) findViewById(R.id.titleText);
		subtitleText = (TextView) findViewById(R.id.subtitleText);

		setLayout(isDeleteMode,isChecked, title, id);
		
		setClickAction();
	}

	/**
	 * 모드에 따른 설정을 해준다
	 * 
	 * @author SOL
	 *
	 * @param isDeleteMode
	 * @param isChecked
	 * @param title
	 * @param id
	 */
	public void setLayout(boolean isDeleteMode, boolean isChecked, String title, int id) {
		
		this.isDeleteMode = isDeleteMode;
		this.isChecked = isChecked;
		this.id = id;
		
		rightCheckBox.setVisibility(View.GONE);
		
		subtitleText.setVisibility(View.GONE);
		
		if(isDeleteMode){
			leftCheckBox.setVisibility(View.VISIBLE);
			leftCheckBox.setChecked(isChecked );
		}else{
			leftCheckBox.setVisibility(View.GONE);
		}
		
		titleText.setText(title);
	}
	
	public int getId(){
		return id;
	}
	
	/**
	 * layout내의 모든 클릭은 listview의 item 클릭으로만 되게 설정한다
	 * 
	 * @author SOL
	 *
	 */
	private void setClickAction() {
		leftCheckBox.setFocusable(false);
		leftCheckBox.setClickable(false);
		
	}
	
	
}

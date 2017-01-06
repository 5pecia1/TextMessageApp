package com.example.ezmessage.menu;

import com.example.ezmessage.R;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * MainSettingACtivity의 ListView 아이템 들의 값을 가진다.
 * 
 * @author SOL
 *
 */
public class MainSettingLayout extends RelativeLayout{

	private CheckBox leftCheckBox;
	private CheckBox rightCheckBox;
	private TextView titleText;
	private TextView subtitleText;
	
	private Boolean isLeftCheck;
	private String title;
	private String subtitle;
	private Boolean isRigthCheck;
	
	private Class<? extends AppCompatActivity> nextActivityClass;
	
	/**
	 * 
	 * @param context
	 * @param isLeftCheck
	 * @param title
	 * @param subtitle
	 * @param isRigthCheck
	 * @param nextActivityClass 선택하면 나올 Item들의 Class를 넘겨준다
	 */
	public MainSettingLayout(
			MainSettingActivity context, Boolean isLeftCheck, 
			String title, String subtitle, Boolean isRigthCheck, 
			Class<? extends AppCompatActivity> nextActivityClass) {
		
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.check_text_sub_text_check_item, this, true);
		
		leftCheckBox = (CheckBox) findViewById(R.id.leftCheckBox);
		rightCheckBox = (CheckBox) findViewById(R.id.rightCheckBox);
		titleText = (TextView) findViewById(R.id.titleText);
		subtitleText = (TextView) findViewById(R.id.subtitleText);
		
		this.isLeftCheck = isLeftCheck;
		this.title = title;
		this.subtitle = subtitle;
		this.isRigthCheck = isRigthCheck;
		this.nextActivityClass = nextActivityClass;
		
		titleText.setText(title);
		
		setLayout(isLeftCheck, subtitle, isRigthCheck);
	}


	private void setLayout(Boolean isLeftCheck, String subtitle, Boolean isRigthCheck) {
		
		if(isLeftCheck == null){
			leftCheckBox.setVisibility(View.GONE);
		}else{
			leftCheckBox.setVisibility(View.VISIBLE);
			leftCheckBox.setChecked(isLeftCheck.booleanValue());
		}
		
		if(subtitle == null){
			subtitleText.setVisibility(View.GONE);
		}else{
			subtitleText.setVisibility(View.VISIBLE);
			subtitleText.setText(subtitle);
		}
		
		if(isRigthCheck == null){
			rightCheckBox.setVisibility(View.GONE);
		}else{
			rightCheckBox.setVisibility(View.VISIBLE);
			rightCheckBox.setChecked(isRigthCheck.booleanValue());
		}
		
	}

	public Boolean getIsLeftCheck() {
		return isLeftCheck;
	}

	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public Boolean getIsRigthCheck() {
		return isRigthCheck;
	}
	
	public Class<? extends AppCompatActivity> getNextActivityClass(){
		return nextActivityClass;
	}
}

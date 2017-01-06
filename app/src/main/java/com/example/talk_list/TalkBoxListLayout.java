package com.example.talk_list;

import com.example.contents.MessageContent;
import com.example.ezmessage.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * TODO add profile picture
 * 주소록 읽을때 추가 할 것
 * 
 * check를 감지하면 그 값을 MessageContent content에 저장한다
 * 
 * @author SNPLUS
 */
public class TalkBoxListLayout extends TalkListLayout{

	private LinearLayout talkTextBox;
	private LinearLayout dateLinearLayout;
	private CheckBox checkBox;
	private ImageView talkBoxProfilePictureRight;
	private ImageView talkBoxProfilePictureLeft;
	private LinearLayout talkContentLayout;
	private TextView talkDate;
	private TextView talkMessage;
	private TextView temporaryStorageMessage;

	/**
	 * 각 view의 객체를 저장한다
	 * setTalkItem 메서드를 호출한다
	 * setClickedAction 메서드를 호출한다
	 * 
	 * @param context
	 * @param content
	 * 
	 */
	public TalkBoxListLayout(TalkActivity context, MessageContent content) {
		super(context,content);

		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.talk_box, this, true);

		talkTextBox = (LinearLayout) findViewById(R.id.talkTextBox); 
		dateLinearLayout = (LinearLayout) findViewById(R.id.dateLinearLayout);
		talkContentLayout = (LinearLayout) findViewById(R.id.talkContentLayout);
		talkBoxProfilePictureLeft = (ImageView) findViewById(R.id.talkBoxProfilePictureLeft);
		talkBoxProfilePictureRight = (ImageView) findViewById(R.id.talkBoxProfilePictureRight);
		talkDate = (TextView) findViewById(R.id.talkDate);
		talkMessage = (TextView) findViewById(R.id.talkMessage);
		checkBox = (CheckBox) findViewById(R.id.talkBoxDeleteCheckBox);
		temporaryStorageMessage = (TextView) findViewById(R.id.temporaryStorageMessage);

		setTalkItem(super.content);

		setClickedAction();
	}

	/**
	 * talkMessage에 대화 내용을 넣는다
	 * talkDate에 시간을 넣는다
	 * checkBox를 content의 체크 여부에 따라 체크한다
	 * content의 type에 따라 채팅 layout을 좌우 또는 임시저장메시지로 설정한다  
	 */
	@Override
	public void setTalkItem(MessageContent content) {
		LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) dateLinearLayout.getLayoutParams();
		int type = content.getType();
		checkBox.setChecked(content.isChecked());

		if(type == MessageContent.ME_TYPE ){//Right
			params.gravity = Gravity.RIGHT;
			talkTextBox.setGravity(Gravity.RIGHT);
			temporaryStorageMessage.setVisibility(View.GONE);
		}
		else if(type == MessageContent.OTHER_TYPE){//Left
			params.gravity = Gravity.LEFT;
			talkTextBox.setGravity(Gravity.LEFT);
			temporaryStorageMessage.setVisibility(View.GONE);
		}
		else if(type == MessageContent.TEMPORARY_STORAGE_SMS_TYPE){//임시 저장 메시지
			params.gravity = Gravity.RIGHT;
			talkTextBox.setGravity(Gravity.RIGHT);
			talkContentLayout.setBackgroundColor(0xAAAAAAAA);
			temporaryStorageMessage.setVisibility(View.VISIBLE);

		}


		talkDate.setText(content.getahhmm());
		talkDate.setLayoutParams(params);


		talkMessage.setText(content.getContext());
	}

	/**
	 * deleteMode일때 checkBox를 보이게 한다
	 * 
	 * @author SOL
	 * 
	 */
	public void setDeleteMode() {
		checkBox.setVisibility(View.VISIBLE);
	}

	/**
	 * 
	 * normalMode일 때 checkBox를 숨긴다
	 * @author SOL
	 * 
	 */
	public void setNormalMode(){
		checkBox.setVisibility(View.GONE);
	}

	/**
	 * checkBox를 토글한다
	 * 
	 * @author SOL
	 * 
	 */
	public void checkToggle() {
		checkBox.toggle();
	}

	/**
	 * TODO DeleteMode일 때 talkBox 클릭시 check 되게 만들 것
	 * 
	 * TalkBoxListLayout에서 클릭과 관련된 것들을 모아놓은 매서드.
	 * checkBox에 리스너를 달아 체크여부에 따라 content의 check값을 바꾸고 
	 * TalkActivity의 add/sub 메서드를 호출해서 현재 체크된 checkBox 개수를 바꾼다.
	 * talkContentLayout을 길게 눌렀을 경우 Menu가 나오게 한다.
	 * 
	 * @author SOL
	 *
	 */
	private void setClickedAction(){

		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				TalkBoxListLayout.this.content.setChecked(isChecked);

				if(isChecked){
					((TalkActivity)TalkBoxListLayout.this.getContext()).addtCheckItemCountTextView();
				}
				else{
					((TalkActivity)TalkBoxListLayout.this.getContext()).subtractCheckItemCountTextView();
				}
			}
		});

		talkContentLayout.setOnClickListener(context.onTalkContentLayoutClicked(content));

		//TODO 물결 액션 구현해 보기
		talkContentLayout.setOnCreateContextMenuListener(context.onTalkContentLayoutMunuClickedListener(content));
	}

}

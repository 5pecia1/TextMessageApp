package com.example.ezmessage.menu;

import java.util.ArrayList;

import com.example.ezmessage.R;
import com.example.ezmessage.menu.setting.QuickReplyPhrasesActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 메인에서 설정을 누르면 나타나는 Activity
 * 
 * @author SOL
 *
 */
@SuppressLint("NewApi")
public class MainSettingActivity extends AppCompatActivity{

	private ListView mainSettingListView = null;

	private MainSettingAdapter adapter = null;

	private ArrayList<MainSettingLayout> settingLayoutList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view_layout);

		mainSettingListView = (ListView) findViewById(R.id.mainSettingListView);
		settingLayoutList = new ArrayList<MainSettingLayout>();

		setSettingLayoutList(settingLayoutList);

		adapter = new MainSettingAdapter(this, R.layout.list_view_layout, settingLayoutList);
		mainSettingListView.setAdapter(adapter);

		mainSettingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(MainSettingActivity.this, ((MainSettingLayout)view).getNextActivityClass());
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}
		});

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("기본 설정 복원");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			resetSetting();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 설정의 모든 기능을 리셋시킨다.
	 * 해당 내용이 들어있는 DB를 삭제한다
	 * 
	 * @author SOL
	 *
	 */
	private void resetSetting() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("기본 설정 복원")
		.setMessage("메시지 설정을 복원합니다.\n계속할까요?")
		.setPositiveButton("확인", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				boolean b= deleteDatabase(MainSettingActivity.class.getSimpleName());
				if(b == false){
					Toast.makeText(MainSettingActivity.this, "복원 실패", Toast.LENGTH_LONG).show();
				}
			}
		})
		.setNegativeButton("취소", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//아무 일 없이 끝남
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();

	}

	/**
	 * MainSettingLayout들을 설정한다. 초기값을 준다.
	 * 
	 * @author SOL
	 *
	 * @param items
	 */
	private void setSettingLayoutList(ArrayList<MainSettingLayout> items) {

		items.add(new MainSettingLayout(this,null, "빠른 답장 문구", null, null, QuickReplyPhrasesActivity.class));

	}

}

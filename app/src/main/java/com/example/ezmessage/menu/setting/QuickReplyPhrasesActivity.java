package com.example.ezmessage.menu.setting;

import com.example.ezmessage.R;
import com.example.ezmessage.menu.MainSettingActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;


/**
 * 빠른 답장 문구 Activity
 * @author SOL
 *
 */
@SuppressLint("NewApi")
public class QuickReplyPhrasesActivity extends AppCompatActivity{

	
	private static final int REQUEST_CODE_INSERT_PHRASE = 1001;
	private static final int REQUEST_CODE_MODITY_PHRASE = 1002;
	
	public static final String COL_ID = "_id";
	public static final String COL_PHRASE = "phrase";
	
	private static String[] PHRASES;
	private static int DATABASE_VERSION = 1;

	private ActionMode actionMode = null;
	
	private TextView countTextView = null;
	private Button deleteButton = null;
	
	private ListView mainListView;
	private QuickReplyPhrasesAdapter adapter = null;

	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	
	
	/**
	 * 초기 설정 메시지를 DB가 없을경우 넣어준다
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view_layout);

		PHRASES = new String[]{
				"죄송합니다. 지금 전화를 받을 수 없습니다.",
				"늦어서 미안해요, 곧 도착합니다.",
				"요즘 잘 지내요?",
				"무슨 일이에요?",
				"어디에 있나요?",
				"이 메시지를 받으면 전화해 주세요.",
				"언제 만날까요?",
				"나중에 이야기해요.",
				"회의 장소가 어디인가요?",
				"번호가 몇 번인가요?"
		};

		mainListView = (ListView) findViewById(R.id.mainSettingListView);

		/**
		 * NormalMode일 때는 수정으로 넘어가고
		 * DeleteMode일 때는 해당 item의 checkBox를 toggle한다
		 */
		mainListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(adapter.isDeleteMode()){
					adapter.setToggleCheckBox(id);
				}else{
					Cursor cur = getcursor("_id == " + id);
					cur.moveToFirst();
					startQuickReplyPhraseInsertActivity(REQUEST_CODE_MODITY_PHRASE, id, cur.getString(cur.getColumnIndex(COL_PHRASE)));
				}
			}

		});
		
		mainListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				if( !adapter.isDeleteMode()){
					setDeleteMode();
				}
			}
		});

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("빠른 답장 문구");

		boolean isOpen = openDatabase();

		if(isOpen){
			Cursor cur = getCursor();
			adapter = new QuickReplyPhrasesAdapter(this, cur);
			mainListView.setAdapter(adapter);
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		db.close();
	}
	
	/**
	 * 수정, 추가 Activity가 보내온 Intent를 처리한다
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		if(requestCode == REQUEST_CODE_INSERT_PHRASE){
			if(resultCode == RESULT_OK){
				String phrase = intent.getExtras().getString(QuickReplyPhrasesInsertActivity.RESULT_KEY);
				try {
					dbHelper.insertPhrase(db, phrase);
				} catch (Exception e) {
					Log.e("QuickReplyPhrasesActivity.onActivityResult", e.getMessage());
				}
				adapter.changeCursor(getCursor());
				adapter.notifyDataSetChanged();
			}
		}
		if(requestCode == REQUEST_CODE_MODITY_PHRASE){
			if(resultCode == RESULT_OK){
				String phrase = intent.getExtras().getString(QuickReplyPhrasesInsertActivity.RESULT_KEY);
				int id = intent.getExtras().getInt(QuickReplyPhrasesInsertActivity.RESULT_ID);
				try {
					dbHelper.updatePhrase(db, id, phrase);
				} catch (Exception e) {
					Log.e("QuickReplyPhrasesActivity.onActivityResult", e.getMessage());
				}
				adapter.changeCursor(getCursor());
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.quick_reply_phrases_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home :
			onBackPressed();
			break;

		case R.id.selectButton :
			setDeleteMode();
			break;

		case R.id.insertButton :
			startQuickReplyPhraseInsertActivity(REQUEST_CODE_INSERT_PHRASE, -1, null);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 문구를 삭제하기 위한 버튼
	 * AlertDialog를 띄워 최종적으로 확인한다
	 * 
	 * @author SOL
	 *
	 * @param v
	 */
	public void onDeleteButtonClicked(View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle("삭제")
		.setMessage("빠른 답장 문구를 삭제합니다.")
		.setPositiveButton("삭제", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				adapter.deletePhrases(db);
				actionMode.finish();
			}
		})
		.setNegativeButton("취소", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				actionMode.finish();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	/**
	 * 모든 박스를 check, uncheck 한다
	 * 
	 * @author SOL
	 *
	 * @param v
	 */
	public void onAllCheckBoxButtonClicked(View v){
		CheckBox box = (CheckBox) v;
		if(box.isChecked()){
			adapter.setAllCheck(getCursor());
		}
		else{
			adapter.setAllUnCheck();
		}
			
	}
	
	/**
	 * 삭제모드로 전환한다
	 * Contextual Action Bar를 띄운다
	 * 
	 * @author SOL
	 *
	 */
	private void setDeleteMode(){
		actionMode = this.startSupportActionMode(new DeleteActionMode());
		adapter.setDeleteMode(countTextView, deleteButton);
	}

	/**
	 * QuickReplyPhrasesInsertActivity를 추가, 수정에 맞게 띄운다
	 * @author SOL
	 *
	 * @param request_code
	 * @param id
	 * @param phrase
	 */
	private void startQuickReplyPhraseInsertActivity(int request_code, long id, String phrase) {
		Intent intent = new Intent(this, QuickReplyPhrasesInsertActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		if(phrase  != null){
			intent.putExtra(COL_PHRASE, phrase);
			intent.putExtra(COL_ID, id);
		}
		
		startActivityForResult(intent, request_code);
	}
	
	public Cursor getCursor() {
		return db.rawQuery("select * from " + QuickReplyPhrasesActivity.class.getSimpleName(), null);
	}
	
	public Cursor getcursor(String where){
		return db.rawQuery("select * from " + QuickReplyPhrasesActivity.class.getSimpleName() + " where " + where, null);
	}

	/**
	 * SQLiteOpenHelper를 상속받은 DB객체를 만든다.
	 * 해당 객체를 이용해서 SQLiteDatabase 객체를 만든다.
	 * 
	 * @author SOL
	 *
	 * @return true
	 */
	private boolean openDatabase(){
		dbHelper = new DatabaseHelper(this);
		db = dbHelper.getWritableDatabase();
		return true;
	}

	/**
	 * DB를 여는것을 도와주는 클래스
	 * 
	 * @author SOL
	 *
	 */
	private class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			super(context, MainSettingActivity.class.getSimpleName(), null, DATABASE_VERSION);
		}

		@Override 
		public void onCreate(SQLiteDatabase db) {
			try{
				String DROP_SQL = "drop table if exists " + QuickReplyPhrasesActivity.class.getSimpleName();
				db.execSQL(DROP_SQL);
			}catch (Exception e){
				Log.e("table/QuickReplyPhrasesActivity.onCreate", "drop error");
			}

			String CREATE_SQL = "create table " + QuickReplyPhrasesActivity.class.getSimpleName() + "("
					+ "_id integer PRIMARY KEY autoincrement, "
					+ " " + COL_PHRASE + " text)";

			try{
				db.execSQL(CREATE_SQL);
			}catch(Exception e){
				Log.e("table/QuickReplyPhrasesActivity.onCreate", "create error");
			}

			for (String s : PHRASES) {
				try {
					insertPhrase(db, s);
				} catch (Exception e) {
					Log.e("table/QuickReplyPhrasesActivity.onCreate", "insert error");
				}
			}

		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
		
		public void insertPhrase(SQLiteDatabase db, String phrase) throws Exception{
			db.execSQL("insert into " + QuickReplyPhrasesActivity.class.getSimpleName() + "("+QuickReplyPhrasesActivity.COL_PHRASE+") values ('" + phrase + "');");
		}
		
		public void updatePhrase(SQLiteDatabase db, int id, String phrase) {
			ContentValues values = new ContentValues();
			values.put(COL_PHRASE, phrase);
			db.update(QuickReplyPhrasesActivity.class.getSimpleName(), values, COL_ID + " == " + id, null);
		}

	}
	
	/**
	 * Contextual Action Bar를 만드는 객체
	 * 
	 * @author SOL
	 *
	 */
	private class DeleteActionMode implements ActionMode.Callback{

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//			switch (item.getItemId()) {
//			case 0:
//				adapter.deletePhrases(db);
//				actionMode.finish();
//				break;
//
//			default:
//				break;
//			}
//			return true;
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//			menu.add("삭제").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);;
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.check_text_delete, null);
			countTextView = (TextView) view.findViewById(R.id.countTextView);
			deleteButton = (Button) view.findViewById(R.id.deleteButton);
			mode.setCustomView(view);
			return true;
		}

		/**
		 * normalMode로 바꾼다
		 */
		@Override
		public void onDestroyActionMode(ActionMode arg0) {
			adapter.setNormalMode();
		}

		@Override
		public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
			return false;
		}
		
	}
}

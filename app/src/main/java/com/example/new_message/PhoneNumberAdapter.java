package com.example.new_message;

import java.util.ArrayList;

import com.example.contents.PhoneNumberContent;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

/**
 * 이름 - 전화번호
 * TODO 전화번호 없으면 이메일
 * 
 * @author SOL
 *
 */
public class PhoneNumberAdapter extends BaseAdapter implements Filterable{

	private Context context = null;
	private ArrayList<PhoneNumberContent> items = null;
	private ArrayList<PhoneNumberContent> searchItems = null;

	private Filter filter;

	public PhoneNumberAdapter(Context context) {
		this.context = context;
		items = new ArrayList<PhoneNumberContent>(0);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PhoneNumberListLayout view;

		if(convertView == null){
			view = new PhoneNumberListLayout(context, items.get(position));
		}
		else{
			view = (PhoneNumberListLayout) convertView;
			view.setItem(items.get(position));
		}
		return view;
	}



	public PhoneNumberContent getFirstItem() {
		if(items.size() > 0)
			return items.get(0);
		else
			return null;
	}
	
	//TODO 일단은 number와 name만으로 완성한다.
	private synchronized ArrayList<PhoneNumberContent> readPhoneNumber(String[] projection, String selection, String sortOrder) {
		ArrayList<PhoneNumberContent> list = null;
		Uri allPhoneNumber = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

		//			String[] projection = new String[]{
		//					ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
		//					ContactsContract.CommonDataKinds.Phone.NUMBER
		//			};
		//			String selection = null;
		//			String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

		Cursor cur = context.getContentResolver().query(allPhoneNumber, projection, selection, null, sortOrder);
		list = new ArrayList<PhoneNumberContent>(cur.getCount());
		

		while(cur.moveToNext()){
			list.add(new PhoneNumberContent(
					cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
					cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
					null
					));
		}

		cur.close();
		return list;
	}


	@Override
	public Filter getFilter() {
		if(filter == null)
			filter = new SearchFilter();
		return filter;
	}

	//TODO 코드 최적화 하기
	private class SearchFilter extends Filter{

		@Override
		protected  FilterResults performFiltering(CharSequence chars) {
			String filterSeq = chars.toString().toLowerCase();
			FilterResults result = new FilterResults();

			if(filterSeq.length() == 1){
				searchItems = readPhoneNumber(
						new String[]{
								ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
								ContactsContract.CommonDataKinds.Phone.NUMBER
						},
//						null,
						ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE \"%"
								+ filterSeq + "%\" OR "
								+ ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE \"%"
								+ filterSeq + "%\"",
								ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
//								ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
						);
			}
			if (filterSeq != null && filterSeq.length() > 0) {
				ArrayList<PhoneNumberContent> filter = null;
				filter = new ArrayList<PhoneNumberContent>();

				for (PhoneNumberContent phoneNumber : searchItems) {
					// the filtering itself:
					if (phoneNumber.getName().toLowerCase().contains(filterSeq) 
							|| phoneNumber.getInformation().toLowerCase().contains(filterSeq)) {
						filter.add((PhoneNumberContent) phoneNumber);
					}
				}
				result.count = filter.size();
				result.values = filter;
			}
			else {
				result.count = 0;
				result.values = new ArrayList<PhoneNumberContent>(0);
			}

			return result;
		}


		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			items = (ArrayList<PhoneNumberContent>) results.values;
			
			notifyDataSetChanged();
		}

	}
}

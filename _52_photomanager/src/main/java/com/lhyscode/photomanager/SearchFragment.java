package com.lhyscode.photomanager;

import java.io.File;
import java.util.ArrayList;

import com.lhyscode.photomanager.CatchFragment.ViewHolder;
import com.lhyscode.photomanager.utils.DataBaseUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

public class SearchFragment extends Fragment {

	private Activity activity;
	private View view;
	private SearchView searchView;
	private DataBaseUtils dataBaseUtils;
	private ArrayList<String> imageNameList;
	private ArrayList<String> imageUrlList;
	private ListView listView;
	private MyListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = getActivity();
		return initView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
	}

	private View initView() {
		view = View.inflate(activity, R.layout.fragment_search, null);
		searchView = (SearchView) view.findViewById(R.id.searchView);
		listView = (ListView) view.findViewById(R.id.lv_search);
		return view;
	}

	private void initData() {
		adapter = new MyListAdapter();
		dataBaseUtils = new DataBaseUtils(activity);
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				queryImage(query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		searchView.setSubmitButtonEnabled(true);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				ArrayList<CharSequence> arrayList = new ArrayList<CharSequence>(
						imageUrlList);
				intent.putCharSequenceArrayListExtra("imageList", arrayList);

				ArrayList<CharSequence> arrayList2 = new ArrayList<CharSequence>(
						imageNameList);
				intent.putCharSequenceArrayListExtra("nameList", arrayList2);

				intent.putExtra("begin_index", position);
				intent.setClass(getActivity(), DisplayActivity.class);
				startActivityForResult(intent, 1);
			}
		});
	}

	protected void queryImage(String queryName) {
		dataBaseUtils.queryImage(queryName);
		imageNameList = dataBaseUtils.imageNameList;
		imageUrlList = dataBaseUtils.imageUrlList;
		adapter.notifyDataSetChanged();
		listView.setAdapter(adapter);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			String imageUrl = data.getStringExtra("imageUrl");
			dataBaseUtils.deletePic(imageUrl);
			File file = new File(imageUrl);
			if (file.delete()) {
				Toast.makeText(activity, "删除成功", 1).show();
			} else {
				Toast.makeText(activity, "删除失败", 1).show();
			}

		}
		imageNameList.clear();
		imageUrlList.clear();
		adapter.notifyDataSetChanged();
		// listView.setAdapter(new MyListAdapter());
	}

	public void displayImage(ImageView imageView, String url) {
		Options options = new Options();
		// 设置只读边界属性
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, options);
		int imageWidth = options.outWidth;
		int imageheight = options.outHeight;

		// 获取屏幕宽高

		Display display = activity.getWindowManager().getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenheight = display.getHeight();
		// 计算缩放比例
		int scale = 1;
		int widthScale = imageWidth / screenWidth;
		int heightScale = imageheight / screenheight;
		// 选择缩放比例
		if (widthScale >= heightScale && widthScale >= 1) {
			scale = widthScale;
		} else if (widthScale < heightScale && heightScale >= 1) {
			scale = heightScale;
		}
		// 设置缩放比例
		options.inSampleSize = scale;
		// 读取图片
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(url, options);
		imageView.setImageBitmap(bitmap);
	}

	class MyListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return imageUrlList.size();
		}

		@Override
		public Object getItem(int position) {
			return imageUrlList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(activity, R.layout.list_item_search,
						null);
				holder = new ViewHolder();
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.iv_list_item_search);
				holder.textView = (TextView) convertView
						.findViewById(R.id.tv_title_search);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			displayImage(holder.imageView, imageUrlList.get(position));
			holder.textView.setText(imageNameList.get(position));
			return convertView;
		}

	}

	static class ViewHolder {
		private ImageView imageView;
		private TextView textView;
	}
}

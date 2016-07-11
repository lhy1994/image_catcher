package com.lhyscode.photomanager;

import java.io.File;
import java.util.ArrayList;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ManageFragment extends Fragment {

	private Activity activity;
	private View view;
	private DataBaseUtils dataBaseUtils;
	private ArrayList<String> imageNameList;
	private ArrayList<String> imageUrlList;
	private GridView gridView;
	private MyListAdapter adapter;
	private ImageButton change;
	private ListView listView;
	private MyListAdapter2 adapter2;
	private boolean isGrid = true;

	public ManageFragment(ImageButton change) {
		this.change = change;
		change.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeDisplay();
			}
		});
	}

	protected void changeDisplay() {
		if (isGrid) {
			isGrid = false;
			gridView.setVisibility(View.INVISIBLE);
			listView.setVisibility(View.VISIBLE);
		} else {
			isGrid = true;
			listView.setVisibility(View.INVISIBLE);
			gridView.setVisibility(View.VISIBLE);
		}
	}

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
		view = View.inflate(activity, R.layout.fragment_manage, null);
		return view;
	}

	private void initData() {
		change.setVisibility(View.VISIBLE);

		dataBaseUtils = new DataBaseUtils(activity);
		dataBaseUtils.query();
		imageNameList = dataBaseUtils.imageNameList;
		imageUrlList = dataBaseUtils.imageUrlList;

		gridView = (GridView) view.findViewById(R.id.gv_manage);
		adapter = new MyListAdapter();
		gridView.setAdapter(adapter);

		listView = (ListView) view.findViewById(R.id.lv_manage);
		adapter2 = new MyListAdapter2();
		listView.setAdapter(adapter2);

		gridView.setOnItemClickListener(new OnItemClickListener() {

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
		dataBaseUtils.query();
		imageNameList = dataBaseUtils.imageNameList;
		imageUrlList = dataBaseUtils.imageUrlList;

		adapter.notifyDataSetChanged();
		gridView.setAdapter(adapter);
		listView.setAdapter(adapter2);
	}

	class MyListAdapter extends BaseAdapter {

//		private BitmapUtils bitmapUtils=new BitmapUtils(activity);
		@Override
		public int getCount() {
			return imageNameList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(activity,
						R.layout.list_item_fragment_manage, null);
				holder = new ViewHolder();
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.iv_list_item_manage);
				holder.textView = (TextView) convertView
						.findViewById(R.id.tv_list_item_manage);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textView.setText(imageNameList.get(position));
			displayImage(holder.imageView, imageUrlList.get(position));
//			bitmapUtils.display(holder.imageView, (String) imageUrlList.get(position));
			return convertView;
		}

	}

	class MyListAdapter2 extends BaseAdapter {

//		private BitmapUtils bitmapUtils=new BitmapUtils(activity);

		@Override
		public int getCount() {
			return imageNameList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(activity,
						R.layout.list_item_fragment_manage2, null);
				holder = new ViewHolder();
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.iv_list_item_manage2);
				holder.textView = (TextView) convertView
						.findViewById(R.id.tv_list_item_manage2);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textView.setText(imageNameList.get(position));
			
//			bitmapUtils.display(holder.imageView, (String) imageUrlList.get(position));
			displayImage(holder.imageView, imageUrlList.get(position));
			return convertView;
		}

	}

	static class ViewHolder {
		private TextView textView;
		private ImageView imageView;
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
}

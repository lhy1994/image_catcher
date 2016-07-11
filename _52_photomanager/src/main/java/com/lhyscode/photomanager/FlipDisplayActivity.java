package com.lhyscode.photomanager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.aphidmobile.flip.FlipViewController;
public class FlipDisplayActivity extends Activity {

	private FlipViewController flipView;
	private ArrayList<CharSequence> imageUrlList;
	private int beginIndex;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Intent intent = getIntent();
		imageUrlList = intent.getCharSequenceArrayListExtra("imageUrlList");
		
		flipView = new FlipViewController(this,FlipViewController.HORIZONTAL);
		flipView.setAnimationBitmapFormat(Bitmap.Config.RGB_565);
		flipView.setDrawingCacheBackgroundColor(Color.WHITE);
		flipView.setAdapter(new MyListAdapter());
		setContentView(flipView);
		
	}

	class MyListAdapter extends BaseAdapter {

		private int repeatCount = 1;
		@Override
		public int getCount() {
			return imageUrlList.size();
		}

		public int getRepeatCount() {
			return repeatCount;
		}

		public void setRepeatCount(int repeatCount) {
			this.repeatCount = repeatCount;
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
				convertView = View.inflate(FlipDisplayActivity.this,
						R.layout.list_item_flip, null);
				holder = new ViewHolder();
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.iv_flip);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			displayImage(holder.imageView, (String) imageUrlList.get(position));
			return convertView;
		}

	}

	static class ViewHolder {
		private ImageView imageView;
	}
	public void displayImage(ImageView imageView, String url) {
		Options options = new Options();
		// ����ֻ���߽�����
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, options);
		int imageWidth = options.outWidth;
		int imageheight = options.outHeight;

		// ��ȡ��Ļ���

		Display display = this.getWindowManager().getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenheight = display.getHeight();
		// �������ű���
		int scale = 1;
		int widthScale = imageWidth / screenWidth;
		int heightScale = imageheight / screenheight;
		// ѡ�����ű���
		if (widthScale >= heightScale && widthScale >= 1) {
			scale = widthScale;
		} else if (widthScale < heightScale && heightScale >= 1) {
			scale = heightScale;
		}
		// �������ű���
		options.inSampleSize = scale;
		// ��ȡͼƬ
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(url, options);
		imageView.setImageBitmap(bitmap);
	}
}

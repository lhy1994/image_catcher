package com.lhyscode.photomanager;

import java.io.IOException;
import java.util.ArrayList;

import com.lhyscode.photomanager.utils.DataBaseUtils;
import com.lhyscode.photomanager.view.ZoomImageView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class DisplayActivity extends Activity {

	private ArrayList<CharSequence> imageUrlList;
	private ViewPager viewPager;
	private int beginIndex;
	private MyPagerAdapter adapter;
	private int currentItem;
	private ArrayList<CharSequence> nameList;
	private DataBaseUtils dataBaseUtils;
	private int height;
	private int width;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_display);
		Intent intent = getIntent();

		dataBaseUtils = new DataBaseUtils(this);
		beginIndex = intent.getIntExtra("begin_index", 0);
		imageUrlList = intent.getCharSequenceArrayListExtra("imageList");
		nameList = intent.getCharSequenceArrayListExtra("nameList");

		viewPager = (ViewPager) findViewById(R.id.vp_display);
		adapter = new MyPagerAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(beginIndex);

		viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				width = viewPager.getWidth();
				height = viewPager.getHeight();
			}
		});
	}

	public void delete(View view) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("ȷ��Ҫɾ����");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int currentItem = viewPager.getCurrentItem();
				String imageUrl = (String) imageUrlList.get(currentItem);
				Intent intent = new Intent();
				intent.putExtra("imageUrl", imageUrl);
				setResult(1, intent);
				finish();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	public void edit(View view) {
		currentItem = viewPager.getCurrentItem();
		String imageUrl = (String) imageUrlList.get(currentItem);
		Intent intent = new Intent();
		intent.putExtra("imageUrl", imageUrl);
		intent.putExtra("width", width);
		intent.putExtra("height", height);
		intent.setClass(this, EditActivity.class);
		startActivityForResult(intent, 1);
	}

	public void rename(View view) {
		AlertDialog.Builder builder = new Builder(this);
		View dialogView = View.inflate(this, R.layout.dialog_rename, null);
		final EditText editText = (EditText) dialogView
				.findViewById(R.id.ed_rename);

		builder.setTitle("������");
		builder.setView(dialogView);
		builder.setPositiveButton("ȷ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int currentItem = viewPager.getCurrentItem();
				String currentName = (String) nameList.get(currentItem);
				String newName = editText.getText().toString();
				dataBaseUtils.update(currentName, newName);
				System.out.println("������" + newName + "������" + currentName);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("ȡ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	//���ñ�ֽ
	public void setw(View view) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("���ñ�ֽ");
		builder.setMessage("Ҫ��������ͼƬΪ��ֽ��");
		builder.setPositiveButton("ȷ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				WallpaperManager wallpaperManager = WallpaperManager
						.getInstance(DisplayActivity.this);
				int current=viewPager.getCurrentItem();
				Bitmap bitmap = BitmapFactory.decodeFile((String) imageUrlList.get(current));
				try {
					wallpaperManager.setBitmap(bitmap);
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(DisplayActivity.this, "����ʧ�ܣ���������", 1).show();
				}
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("ȡ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			viewPager.removeAllViews();
			viewPager.setAdapter(adapter);
			viewPager.setCurrentItem(currentItem);
		}
	}

	@Override
	public void onBackPressed() {
		setResult(0);
		finish();
	}

	class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageUrlList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// ImageView imageView=new ImageView(DisplayActivity.this);
			ZoomImageView imageView=new ZoomImageView(DisplayActivity.this);
			// Bitmap bitmap = BitmapFactory.decodeFile((String) imageUrlList
			// .get(position));
			// imageView.setImageBitmap(bitmap);
			
			displayImage(imageView, (String) imageUrlList.get(position));
			container.addView(imageView);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	public void displayImage(ZoomImageView imageView, String url) {
		Options options = new Options();
		// ����ֻ���߽�����
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, options);
		int imageWidth = options.outWidth;
		int imageheight = options.outHeight;

		// ��ȡ��Ļ���

		Display display = DisplayActivity.this.getWindowManager()
				.getDefaultDisplay();
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

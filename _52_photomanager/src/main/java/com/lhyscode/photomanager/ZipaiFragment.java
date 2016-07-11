package com.lhyscode.photomanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import com.lhyscode.photomanager.utils.DataBaseUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ZipaiFragment extends Fragment {

	private Activity activity;
	private View view;
	private ImageView imageView;
	private Button zipai;
	private Button save;
	private String name;
	private ArrayList<String> imageNameList;
	private ArrayList<String> imageUrlList;
	private DataBaseUtils dataBaseUtils;
	private boolean preSaved = false;

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
		view = View.inflate(activity, R.layout.fragment_zipai, null);
		imageView = (ImageView) view.findViewById(R.id.iv_zipai);
		zipai = (Button) view.findViewById(R.id.btn_zipai);
		save = (Button) view.findViewById(R.id.btn_zipai_save);

		return view;
	}

	private void initData() {
		dataBaseUtils = new DataBaseUtils(activity);
		zipai.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!preSaved) {
					File file = new File(Environment
							.getExternalStorageDirectory(), name + ".jpg");
					file.delete();
				}
				popDialog();
			}
		});
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				File file = new File(Environment.getExternalStorageDirectory(),
						name + ".jpg");
				if (file.exists()) {
					//拷贝缓存图片
					File copyFile = new File(Environment
							.getExternalStorageDirectory().getAbsoluteFile()
							+ "/pic", name + ".jpg");
					try {
						FileInputStream inputStream=new FileInputStream(file);
						FileOutputStream outputStream=new FileOutputStream(copyFile);
						int len=0;
						byte[] bs=new byte[1024];
						while((len=inputStream.read(bs))!=-1){
							outputStream.write(bs, 0, len);
						}
						outputStream.close();
						inputStream.close();
						file.delete();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					// 查看数据库中是否以前存储过同名字的照片
					dataBaseUtils.queryImage(name);
					imageNameList = dataBaseUtils.imageNameList;
					imageUrlList = dataBaseUtils.imageUrlList;
					if (imageNameList.size() <= 0 && imageUrlList.size() <= 0) {
						dataBaseUtils.addPic(name, copyFile.getAbsolutePath());
					}
					Toast.makeText(activity, "保存成功", 1).show();
					preSaved = true;
				} else {
					Toast.makeText(activity, "请先拍照片", 1).show();
				}

			}
		});
	}

	protected void popDialog() {
		AlertDialog.Builder builder = new Builder(activity);
		builder.setTitle("取个名字吧");
		View inflate = View.inflate(activity, R.layout.dialog_rename, null);
		builder.setView(inflate);
		final EditText editText = (EditText) inflate
				.findViewById(R.id.ed_rename);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				name = editText.getText().toString();
				Intent intent = new Intent();
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
						Environment.getExternalStorageDirectory(), name
								+ ".jpg")));
				startActivityForResult(intent, 1);
				preSaved = false;
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		File file = new File(Environment.getExternalStorageDirectory(), name
				+ ".jpg");
		if (file.exists()) {
			displayImage(imageView, file.getAbsolutePath());
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (!preSaved) {
			File file = new File(Environment.getExternalStorageDirectory(),
					name + ".jpg");
			file.delete();
		}
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

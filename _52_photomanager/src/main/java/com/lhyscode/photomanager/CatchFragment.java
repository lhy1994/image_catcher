package com.lhyscode.photomanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.lhyscode.photomanager.utils.CatchImage;
import com.lhyscode.photomanager.utils.DataBaseUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class CatchFragment extends Fragment {

	private View view;
	private Button catcherButton;
	private String URL;
	private EditText editText;
	private ArrayList<String> imageUrlList = new ArrayList<String>();
	private Activity activity;
	private GridView gridView;
	private Button diaplayButton;
	private DataBaseUtils dataBaseUtils;
	private Button bookButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = getActivity();
		return initView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		iniData();
	}

	private View initView() {
		view = View.inflate(activity, R.layout.fragment_catch, null);
		return view;
	}

	private void iniData() {
		dataBaseUtils = new DataBaseUtils(activity);
		catcherButton = (Button) view.findViewById(R.id.bt_catcher);
		diaplayButton = (Button) view.findViewById(R.id.bt_display);
		bookButton = (Button) view.findViewById(R.id.bt_book);
		editText = (EditText) view.findViewById(R.id.ed_url);
		editText.setText("http://pic.yesky.com/");
		gridView = (GridView) view.findViewById(R.id.gv_catcher);
		
		catcherButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!checkNetState()) {
					Toast.makeText(activity, "��ǰû�����磬������������", 0).show();
				} else {
					imageUrlList.clear();
					URL = editText.getText().toString();
					Thread thread = new Thread() {
						public void run() {
							CatchImage cm = new CatchImage();
							// ���html�ı�����
							String HTML = null;
							try {
								HTML = cm.getHTML(URL);
							} catch (Exception e) {
								e.printStackTrace();
							}
							// System.out.println(HTML);
							// ��ȡͼƬ��ǩ
							List<String> imgUrl = cm.getImageUrl(HTML);
							// ��ȡͼƬsrc��ַ
							List<String> imgSrc = cm.getImageSrc(imgUrl);
							// ����ͼƬ
							// download(imgSrc);
							multiDownload(imgSrc, 3);
						};
					};
					thread.start();
				}
			}

		});
		diaplayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gridView.setAdapter(new MyListAdapter());
			}
		});
		
		bookButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				ArrayList<CharSequence> arrayList = new ArrayList<CharSequence>(
						imageUrlList);
				intent.putCharSequenceArrayListExtra("imageUrlList", arrayList);
				intent.setClass(getActivity(), FlipDisplayActivity.class);
				startActivity(intent);
			}
		});
	}

	 public boolean checkNetState() {
		ConnectivityManager connectivity = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			 NetworkInfo[] info = connectivity.getAllNetworkInfo();
             if (info != null)
                 for (int i = 0; i < info.length; i++)
                     if (info[i].getState() == NetworkInfo.State.CONNECTED)
                     {
                         return true;
                     }
			}
		
		return false;
	}

	// ���߳�����
	public void download(List<String> listImgSrc) {
		try {
			for (String url : listImgSrc) {
				String imageName = url.substring(url.lastIndexOf("/") + 1,
						url.length());
				URL uri = new URL(url);
				InputStream in = uri.openStream();

				File parentFile = new File(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/pic");
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
				File file = new File(parentFile, imageName);

				imageUrlList.add(file.getAbsolutePath());
				dataBaseUtils.addPic(imageName, file.getAbsolutePath());

				FileOutputStream fo = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int length = 0;
				System.out.println("��ʼ����:" + url);
				while ((length = in.read(buf, 0, buf.length)) != -1) {
					fo.write(buf, 0, length);
				}
				in.close();
				fo.close();
				System.out.println(imageName + "�������");
			}
		} catch (Exception e) {
			System.out.println("����ʧ��");
		}
	}

	// ���߳�����
	public void multiDownload(List<String> listImgSrc, int threadNum) {
		int length = listImgSrc.size() / threadNum;
		int lastLength = listImgSrc.size() - threadNum * length;
		for (int i = 0; i < threadNum; i++) {
			if (i != threadNum - 1) {
				new MyThread(length, listImgSrc, i * length).start();
			} else {
				new MyThread(lastLength + length, listImgSrc, i * length)
						.start();
			}
		}
	}

	class MyThread extends Thread {

		private int length;
		private List<String> listImgSrc;
		private int beginIndex;

		public MyThread(int length, List<String> listImgSrc, int beginIndex) {
			this.length = length;
			this.listImgSrc = listImgSrc;
			this.beginIndex = beginIndex;
		}

		@Override
		public void run() {
			for (int i = 0; i < length; i++) {
				try {
					String url = listImgSrc.get(beginIndex + i);

					String imageName = url.substring(url.lastIndexOf("/") + 1,
							url.length());
					URL uri = new URL(url);
					InputStream in = uri.openStream();

					File parentFile = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/pic");
					if (!parentFile.exists()) {
						parentFile.mkdirs();
					}
					File file = new File(parentFile, imageName);

					imageUrlList.add(file.getAbsolutePath());
					if (!file.exists()) {
						dataBaseUtils.addPic(imageName, file.getAbsolutePath());
					}
					FileOutputStream fo = new FileOutputStream(file);
					byte[] buf = new byte[1024];
					int length = 0;
					System.out.println(Thread.currentThread().getId() + "��ʼ����:"
							+ url);
					while ((length = in.read(buf, 0, buf.length)) != -1) {
						fo.write(buf, 0, length);
					}
					in.close();
					fo.close();
					System.out.println(imageName + "�������");

				} catch (Exception e) {
					System.out.println("����ʧ��");
				}
			}
		}
	}

	public void displayImage(ImageView imageView, String url) {
		Options options = new Options();
		// ����ֻ���߽�����
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, options);
		int imageWidth = options.outWidth;
		int imageheight = options.outHeight;

		// ��ȡ��Ļ���

		Display display = activity.getWindowManager().getDefaultDisplay();
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
				convertView = View.inflate(activity,
						R.layout.list_item_fragment_catch, null);
				holder = new ViewHolder();
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.image_list);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			displayImage(holder.imageView, imageUrlList.get(position));
			return convertView;
		}

	}

	static class ViewHolder {
		private ImageView imageView;
	}
}

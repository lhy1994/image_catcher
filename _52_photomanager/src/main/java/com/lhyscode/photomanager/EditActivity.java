package com.lhyscode.photomanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class EditActivity extends Activity {

	private Bitmap copyBitmap;
	private Bitmap srcBitmap;
	private ImageView imageView;
	private int preX;
	private int preY;
	private String imageUrl;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		imageView = (ImageView) findViewById(R.id.iv_edit_image);
		Intent intent = getIntent();
		int width=intent.getIntExtra("width", 1);
		int height=intent.getIntExtra("height",1);
		imageUrl = intent.getStringExtra("imageUrl");

		Options options = new Options();
//		 设置只读边界属性
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imageUrl, options);
		int imageWidth = options.outWidth;
		int imageheight = options.outHeight;
		// 获取屏幕宽高
		Display display = this.getWindowManager().getDefaultDisplay();
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
		options.inJustDecodeBounds = false;
		
		srcBitmap = BitmapFactory.decodeFile(imageUrl,options);
		copyBitmap = Bitmap.createBitmap(800,
				800, srcBitmap.getConfig());
		
		final Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStrokeWidth(10);
		final Canvas canvas = new Canvas(copyBitmap);

		canvas.drawBitmap(srcBitmap, null, new Rect(0,0,800,800), paint);
//		Matrix matrix=new Matrix();
//		canvas.drawBitmap(srcBitmap, new Matrix(), paint);

		
		imageView = (ImageView) findViewById(R.id.iv_edit_image);
		imageView.setImageBitmap(copyBitmap);
		imageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					preX = (int) event.getX();
					preY = (int) event.getY();
					break;

				case MotionEvent.ACTION_MOVE:
					int x = (int) event.getX();
					int y = (int) event.getY();
					canvas.drawLine(preX, preY, x, y, paint);
					System.out.println(".........." + x + ";" + y);
					imageView.setImageBitmap(copyBitmap);
					preX = x;
					preY = y;
					break;
				case MotionEvent.ACTION_UP:
					System.out.println("离开屏幕");
					break;
				default:
					break;
				}
				return true;
			}
		});
	}

	public void save(View view) {
		File file = new File(imageUrl);
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file, false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		copyBitmap.compress(CompressFormat.JPEG, 50, fileOutputStream);
		setResult(1);
		finish();
	}

	public void cancel(View view) {
		setResult(0);
		finish();
	}
}

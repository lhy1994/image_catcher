package com.lhyscode.photomanager.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DataBaseUtils {
	public DataBaseUtils(Context context) {
		this.context = context;
		helper = new MyOpenHelper(context, "photo.db", null, 1);
		database = helper.getWritableDatabase();
	}

	private Context context;
	public ArrayList<String> imageNameList = new ArrayList<String>();
	public ArrayList<String> imageUrlList = new ArrayList<String>();
	public MyOpenHelper helper;
	public SQLiteDatabase database;

	public void addPic(String name, String imageUrl) {
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("image", imageUrl);
		database.insert("photo", null, values);
	}

	@SuppressLint("NewApi")
	public void query() {
		imageNameList.clear();
		imageUrlList.clear();
		Cursor cursor = database.query(true, "photo", null, null, null, null,
				null, null, null, null);
		while (cursor.moveToNext()) {
			String imageName = cursor.getString(cursor.getColumnIndex("name"));
			String imageUrl = cursor.getString(cursor.getColumnIndex("image"));
			imageNameList.add(imageName);
			imageUrlList.add(imageUrl);
		}
		return;
	}

	public void queryImage(String imageName) {
		imageNameList.clear();
		imageUrlList.clear();
		Cursor cursor = database.query("photo", null, "name like ?",
				new String[] { "%"+imageName+"%" }, null, null, null);
		while(cursor.moveToNext()){
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String image = cursor.getString(cursor.getColumnIndex("image"));
			imageNameList.add(name);
			imageUrlList.add(image);
		}
	}

	public void deletePic(String imageUrl) {
		String[] strings = new String[] { imageUrl };
		database.delete("photo", "image=?", strings);
	}

	public void update(String oldValue, String newValue) {
		String[] strings = new String[] { oldValue };
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", newValue);
		database.update("photo", contentValues, "name=?", strings);
	}
	public void updateImageUrl(String imageName,String imageUrl) {
		String [] strings=new String[]{imageName};
		ContentValues contentValues=new ContentValues();
		contentValues.put("image", imageUrl);
		database.update("photo", contentValues, "name=?", strings);
	}
}

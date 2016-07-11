package com.lhyscode.photomanager;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends SlidingFragmentActivity {

	private ListView listView;
	private ImageButton leftMenuButton;
	private TextView title;
	private FragmentManager fragmentManager;
	private FragmentTransaction transaction;
	private ImageButton change;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		setBehindContentView(R.layout.left_menu);

		final SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		int width = getWindowManager().getDefaultDisplay().getWidth();
		slidingMenu.setBehindOffset(width * 5 / 9);

		title = (TextView) findViewById(R.id.tv_title);
		change = (ImageButton) findViewById(R.id.bt_grid);

		final String[] items = new String[] { "图片抓取", "图片管理", "图片搜索", "自拍一张"
				 };
		listView = (ListView) findViewById(R.id.lv_left_menu);
		listView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.left_menu_list_item, R.id.tv_left_menu_item, items));

		leftMenuButton = (ImageButton) findViewById(R.id.bt_left_menu);
		leftMenuButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				slidingMenu.toggle();
			}
		});
		
		fragmentManager = getFragmentManager();
		transaction = fragmentManager.beginTransaction();
		CatchFragment catchFragment = new CatchFragment();
		transaction.replace(R.id.fl_content, catchFragment);
		transaction.commit();
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				title.setText(items[position]);
				switch (position) {
				case 0:
					change.setVisibility(View.INVISIBLE);
					transaction = fragmentManager.beginTransaction();
					CatchFragment catchFragment = new CatchFragment();
					transaction.replace(R.id.fl_content, catchFragment);
					transaction.commit();
					break;
				case 1:
					transaction = fragmentManager.beginTransaction();
					ManageFragment manageFragment = new ManageFragment(change);
					transaction.replace(R.id.fl_content, manageFragment);
					transaction.commit();
					break;
				case 2:
					change.setVisibility(View.INVISIBLE);
					transaction = fragmentManager.beginTransaction();
					SearchFragment searchFragment=new SearchFragment();
					transaction.replace(R.id.fl_content, searchFragment);
					transaction.commit();
					break;
				case 3:
					change.setVisibility(View.INVISIBLE);
					transaction = fragmentManager.beginTransaction();
					ZipaiFragment zipaiFragment=new ZipaiFragment();
					transaction.replace(R.id.fl_content, zipaiFragment);
					transaction.commit();
					break;
				default:
					break;
				}
				slidingMenu.toggle();
			}
		});
	};

}

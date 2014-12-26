package com.demo.drawlayout;


import java.io.File;
import java.io.IOException;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainFrameLayout extends FragmentActivity{
	private static final String TAG = "MainFrameLayout";
	// 抽屉菜单对象
	private ActionBarDrawerToggle drawerbar;
	public DrawerLayout drawerLayout;
	private TestFragment testFragment;
	private RelativeLayout left_menu_layout, right_xiaoxi_layout;
	private TextView text;
	
	private EditText file;
	private MediaPlayer mediaPlayer;
    private String filename;
    private int position;
	private Button play;
	private Button pause;
	private Button reset;
	private Button stop;
	@Override
	protected void onCreate(Bundle arg0){
		super.onCreate(arg0);
		setContentView(R.layout.main_frame_activity);
		initView();
		initEvent();
	}
	public void initView(){
			testFragment = new TestFragment();
			mediaPlayer = new MediaPlayer();
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction f_transaction = fragmentManager.beginTransaction();
			f_transaction.replace(R.id.main_content_frame_parent, testFragment);
			f_transaction.commitAllowingStateLoss();
			initLeftLayout();
			initRightLayout();
	}
	public void initLeftLayout(){
		drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
		//设置透明
		drawerLayout.setScrimColor(0x00000000);
		//左边菜单
		left_menu_layout = (RelativeLayout) findViewById(R.id.main_left_drawer_layout);
		View view2 = getLayoutInflater().inflate(R.layout.menu_layout, null);
		text=(TextView)view2.findViewById(R.id.text);
		text.setText("左边测试菜单");
		left_menu_layout.addView(view2);
	}
	public void initRightLayout(){
		//左边菜单
		right_xiaoxi_layout = (RelativeLayout) findViewById(R.id.main_right_drawer_layout);
		View view = getLayoutInflater().inflate(R.layout.xiaoxi_layout, null);
//		text=(TextView)view.findViewById(R.id.text);
		file = (EditText)view.findViewById(R.id.file);
		
		play = (Button)view.findViewById(R.id.play);
		pause = (Button)view.findViewById(R.id.pause);
		reset = (Button)view.findViewById(R.id.reset);
		stop = (Button)view.findViewById(R.id.stop);
		
		OnClickListener myClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				filename = file.getText().toString();//先得到文本框中的内容
				Button button = (Button) v;//得到button 
				try {
					switch (v.getId()) {//通过传过来的Buttonid可以判断Button的类型
					case R.id.play://播放
						play();
						break;

					case R.id.pause:
						if(mediaPlayer.isPlaying()){
							mediaPlayer.pause();
							button.setText(R.string.continues);//让这个按钮上的文字显示为继续
						}else{
							mediaPlayer.start();//继续播放
							button.setText(R.string.pause);
						}
						break;
						
					case R.id.reset:
						if(mediaPlayer.isPlaying()){
							mediaPlayer.seekTo(0);//让它从0开始播放
						}else{
							play();//如果它没有播放，就让它开始播放
						}
						break;
						
					case R.id.stop:
						if(mediaPlayer.isPlaying()) mediaPlayer.stop();//如果它正在播放的话，就让他停止
						break;
					}
				} catch (Exception e) {//抛出异常
					Log.e(TAG, e.toString());
				}
			}
		};
		
		play.setOnClickListener(myClickListener);
		pause.setOnClickListener(myClickListener);
		reset.setOnClickListener(myClickListener);
		stop.setOnClickListener(myClickListener);
		
		 
		
//		text.setText("右边测试菜单");
		right_xiaoxi_layout.addView(view);
	}
	private void initEvent() {
		drawerbar = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_launcher, R.string.open, R.string.close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				
				super.onDrawerOpened(drawerView);
			}

			@Override
			public void onDrawerClosed(View drawerView) {

				super.onDrawerClosed(drawerView);
			}
		};
		drawerLayout.setDrawerListener(drawerbar);
	}

	public void openLeftLayout() {
		if (drawerLayout.isDrawerOpen(left_menu_layout)) {
			drawerLayout.closeDrawer(left_menu_layout);
		} else {
			drawerLayout.openDrawer(left_menu_layout);

		}
	}

	// 消息开关事件
	public void openRightLayout() {
		if (drawerLayout.isDrawerOpen(right_xiaoxi_layout)) {
			drawerLayout.closeDrawer(right_xiaoxi_layout);
		} else {
			drawerLayout.openDrawer(right_xiaoxi_layout);
		}
	}
	
	@Override
	protected void onStart() {
		Log.i(TAG, "MainActivity------------onStart()");
		super.onStart();
	}
	@Override
	protected void onPause() {//如果突然电话到来，停止播放音乐
		Log.i(TAG, "MainActivity------------onPause()");
		if(mediaPlayer.isPlaying()){
			position = mediaPlayer.getCurrentPosition();//保存当前播放点
			mediaPlayer.stop();
		}
		super.onPause();
	}
	@Override
	protected void onResume() {
		Log.i(TAG, "MainActivity------------onResume()");
		if(position>0 && filename!=null){//如果电话结束，继续播放音乐
			try {
				play();
				mediaPlayer.seekTo(position);
				position = 0;
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
		}
		super.onResume();
	}
	@Override
	protected void onStop() {
		Log.i(TAG, "MainActivity------------onStop()");
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		Log.i(TAG, "MainActivity------------onDestroy()");
		mediaPlayer.release();
		super.onDestroy();
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		this.filename = savedInstanceState.getString("filename");
		this.position = savedInstanceState.getInt("position");
		super.onRestoreInstanceState(savedInstanceState);
		Log.i(TAG, "onRestoreInstanceState()");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("filename", filename);
		outState.putInt("position", position);
		super.onSaveInstanceState(outState);
		Log.i(TAG, "onSaveInstanceState()");
	}
	private void play() throws IOException {
		File audioFile = new File(Environment.getExternalStorageDirectory(),filename);
		mediaPlayer.reset();
		mediaPlayer.setDataSource(audioFile.getAbsolutePath());
		mediaPlayer.prepare();
		mediaPlayer.start();//播放
	}
}

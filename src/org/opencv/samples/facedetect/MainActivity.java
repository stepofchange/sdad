package org.opencv.samples.facedetect;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

public class MainActivity extends AppCompatActivity implements OnClickListener {
	
	Button btn1;
	Button btn2;
	CheckBox ChkBx;
	boolean recognize;
    public SharedPreferences mSetRecognizeValue;
    public static final String RECOGNIZE = "recognizeValue";
    public SharedPreferences mSetCheckBoxValue;
    public static final String CHECKBOXV = "recognizeValue";
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainview);
		mSetRecognizeValue = getSharedPreferences(RECOGNIZE, Context.MODE_PRIVATE);
		mSetCheckBoxValue = getSharedPreferences(CHECKBOXV, Context.MODE_PRIVATE);
		btn1 = (Button)findViewById(R.id.button1);
		btn1.setOnClickListener(this);
		
		btn2 = (Button)findViewById(R.id.button2);
		btn2.setOnClickListener(this);
		
		Intent intent = getIntent();
		
		
		
		if(intent.getBooleanExtra("recognize", false)==false){
			recognize = mSetRecognizeValue.getBoolean(RECOGNIZE, false);
		}else{recognize = true;}
		
		
		ChkBx = (CheckBox)findViewById(R.id.CheckBox2);
		ChkBx.setClickable(recognize);
		
		
		ChkBx.setChecked(mSetCheckBoxValue.getBoolean(CHECKBOXV, false));
		if(ChkBx.isChecked()){
			Intent lockScreenStart = new Intent(MainActivity.this, LockScreen.class);
			startActivity(lockScreenStart);
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button2:
			Intent testIntent = new Intent(MainActivity.this, TestActivity.class);
			startActivity(testIntent);
			Log.i("hhhh", "�������� ����������");
			break;
		case R.id.button1:	
			Intent addPersonIntent = new Intent(MainActivity.this, AddPerson.class);
			startActivity(addPersonIntent);
		default:
			break;
		
		}
		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
		
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		mSetCheckBoxValue = getSharedPreferences(CHECKBOXV, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = mSetCheckBoxValue.edit();
		editor.putBoolean(CHECKBOXV, ChkBx.isChecked());
		editor.commit();
		
        mSetRecognizeValue = getSharedPreferences(RECOGNIZE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = mSetRecognizeValue.edit();
        editor1.putBoolean(RECOGNIZE, recognize);
        editor1.commit();
		 
	}


}

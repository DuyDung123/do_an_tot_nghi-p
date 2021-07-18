package com.dinhvandung.smarthome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dinhvandung.smarthome.DAO.SettingsDAO;
import com.dinhvandung.smarthome.Database.CreateDatabase;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    TextView MainDangNhap;
    ImageView DeviceMain,ChartMain,UserMain,SettingsMain;

    SettingsDAO settingsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainDangNhap = (TextView)findViewById(R.id.main_dangnhap);
        DeviceMain = (ImageView)findViewById(R.id.device_main);
        ChartMain = (ImageView)findViewById(R.id.chart_main);
        UserMain = (ImageView)findViewById(R.id.user_main);
        SettingsMain = (ImageView)findViewById(R.id.settings_main);


        settingsDao = new SettingsDAO(this);

        hienThiNut();

        MainDangNhap.setOnClickListener(this);
        DeviceMain.setOnClickListener(this);
        ChartMain.setOnClickListener(this);
        UserMain.setOnClickListener(this);
        SettingsMain.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.main_dangnhap:
                dangNhap();
                break;
            case R.id.device_main:
                dangNhap();
                break;
            case R.id.chart_main:
                dangNhap();
                break;
            case R.id.user_main:
                dangNhap();
                break;
            case R.id.settings_main:
                dangNhap();
                break;
        }
    }

    private void dangNhap(){
        boolean check = settingsDao.checkIpServerSettings();
        Intent redirect;
        if (check){
            redirect = new Intent(MainActivity.this,LoginActivity.class);

        }else {
            redirect = new Intent(MainActivity.this,SettingsActivity.class);
        }
        startActivity(redirect);
    }


    public void hienThiNut(){
        boolean check = settingsDao.checkIpServerSettings();
        if (check){
            SettingsMain.setVisibility(View.GONE);
        }
    }

}

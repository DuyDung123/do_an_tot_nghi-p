package com.dinhvandung.smarthome;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dinhvandung.smarthome.DTO.SettingsDTO;
import com.dinhvandung.smarthome.SEVICE.SettingsService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    TextView ipServer;
    Button subMit, Update;
    ProgressBar bar;

    SettingsService settingsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ipServer = (TextView)findViewById(R.id.edIpserver);
        subMit = (Button)findViewById(R.id.btnsubmitSettings);
        Update = (Button)findViewById(R.id.btnUpdateSettings);
        bar = (ProgressBar)findViewById(R.id.progressBar2);

        subMit.setOnClickListener(this);
        Update.setOnClickListener(this);

        settingsService = new SettingsService(this);

        hienThiNut();
        bar.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnsubmitSettings:
                String sIpServer = ipServer.getText().toString();
                if (sIpServer== null || sIpServer.equals("")){
                    Toast.makeText(SettingsActivity.this,"hãy nhập ip server để kết nối với server",Toast.LENGTH_SHORT).show();
                }else{
                    new CheckIpServer(this,LoginActivity.class,"0",sIpServer).execute(sIpServer);
                }
                break;
            case R.id.btnUpdateSettings:
                String sIpServerUpdate = ipServer.getText().toString();
                if (sIpServerUpdate== null || sIpServerUpdate.equals("")){
                    Toast.makeText(SettingsActivity.this,"hãy nhập ip server để kết nối với server",Toast.LENGTH_SHORT).show();
                }else {
                    new CheckIpServer(this,LoginActivity.class,"1",sIpServerUpdate).execute(sIpServerUpdate);
                }
                break;
        }
    }

    public void hienThiNut(){
        boolean check = settingsService.checkIpServerSettings();
        if (check){
            subMit.setVisibility(View.GONE);
        }else {
            Update.setVisibility(View.GONE);
        }
    }

    private class CheckIpServer extends AsyncTask<String,Boolean, Boolean> {
        private Context context;
        private String checkNut;
        private String ip;
        private int reqCode;
        private String uri;
        private Class aClass;
        ProgressDialog dialog;


        public CheckIpServer(Context context,Class aClass,String checkNut,String ip) {
            this.context = context;
            this.checkNut = checkNut;
            this.ip = ip;
            this.aClass = aClass;
        }

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
            subMit.setEnabled(false);
            ipServer.setEnabled(false);
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            dialog.setMessage("check...");
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Log.e("Buffer Error", "Error converting result " + strings[0].toString());
            uri = "http://"+strings[0]+"/webcontroldevice/dang-nhap?action=login";
            try {
                URL url = new URL(uri);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
                reqCode = httpURLConnection.getResponseCode();
                if (reqCode ==200){
                    return true;
                }else {
                    return  false;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            bar.setVisibility(View.GONE);
            if (checkNut =="0"){
                if (aBoolean) {
                    SettingsDTO settingsDTO = new SettingsDTO();
                    settingsDTO.setIpserver(ip);
                    Long kiemtra = settingsService.save(settingsDTO);
                    if (kiemtra != 0){
                        Toast.makeText(context,"thêm ip server thành công",Toast.LENGTH_SHORT).show();
                        Intent iLogin = new Intent(context, aClass);
                        startActivity(iLogin);
                    }else {
                        Toast.makeText(SettingsActivity.this,"thêm ip server thất bại",Toast.LENGTH_SHORT).show();
                        subMit.setEnabled(true);
                        ipServer.setEnabled(true);
                    }
                }else {
                    Toast.makeText(SettingsActivity.this,"không thể connect tới server",Toast.LENGTH_SHORT).show();
                    subMit.setEnabled(true);
                    ipServer.setEnabled(true);
                }
            }else if (checkNut =="1"){
                if (aBoolean) {
                    SettingsDTO settingsDTO = new SettingsDTO();
                    settingsDTO.setIpserver(ip);
                    Long kiemtra = settingsService.save(settingsDTO);
                    if (kiemtra != 0){
                        Toast.makeText(SettingsActivity.this,"thêm ip server thành công",Toast.LENGTH_SHORT).show();
                        Intent iLogin = new Intent(context, aClass);
                        startActivity(iLogin);
                    }else {
                        Toast.makeText(SettingsActivity.this,"thêm ip server thất bại",Toast.LENGTH_SHORT).show();
                        subMit.setEnabled(true);
                        ipServer.setEnabled(true);
                    }
                }else {
                    Toast.makeText(SettingsActivity.this,"không thể connect tới server",Toast.LENGTH_SHORT).show();
                    subMit.setEnabled(true);
                    ipServer.setEnabled(true);
                }
            }
        }
    }
}
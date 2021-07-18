package com.dinhvandung.smarthome.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dinhvandung.smarthome.DTO.DeviceDTO;
import com.dinhvandung.smarthome.DTO.SettingsDTO;
import com.dinhvandung.smarthome.LoginActivity;
import com.dinhvandung.smarthome.R;
import com.dinhvandung.smarthome.SEVICE.SettingsService;
import com.dinhvandung.smarthome.SettingsActivity;
import com.dinhvandung.smarthome.TrangChuActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    TextView  txtSetingIp;
    EditText ipServer;
    Button subMit, Update;
    ProgressBar bar;

    SettingsService settingsService;
    SettingsDTO settingsDTO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_activity,container,false);

        ipServer = (EditText) view.findViewById(R.id.edIpserver);
        txtSetingIp = (TextView) view.findViewById(R.id.txtSetingIp);
        subMit = (Button)view.findViewById(R.id.btnsubmitSettings);
        Update = (Button)view.findViewById(R.id.btnUpdateSettings);
        bar = (ProgressBar)view.findViewById(R.id.progressBar2);

        ((TrangChuActivity)getActivity()).getSupportActionBar().setTitle(R.string.setting);



        bar.setVisibility(View.GONE);
        subMit.setVisibility(View.GONE);
        Update.setOnClickListener(this);

        settingsService = new SettingsService(getActivity());
        settingsDTO = settingsService.findOne();

        ipServer.setText(settingsDTO.getIpserver());
        txtSetingIp.setText("Up date ip server");

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnUpdateSettings:
                String ip = ipServer.getText().toString();
                new CheckIpServerUpdate(getActivity(),LoginActivity.class,"1",ip).execute(ip);
                break;
        }
    }

    private class CheckIpServerUpdate extends AsyncTask<String,Boolean, Boolean> {
        private Context context;
        private String checkNut;
        private String ip;
        private int reqCode;
        private String uri;
        private Class aClass;
        ProgressDialog dialog;


        public CheckIpServerUpdate(Context context,Class aClass,String checkNut,String ip) {
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
                    settingsDTO.setIpserver(ip);
                    Long kiemtra = settingsService.save(settingsDTO);
                    if (kiemtra != 0){
                        Toast.makeText(context,"thêm ip server thành công",Toast.LENGTH_SHORT).show();
                        Intent iLogin = new Intent(context, aClass);
                        startActivity(iLogin);
                    }else {
                        Toast.makeText(context,"thêm ip server thất bại",Toast.LENGTH_SHORT).show();
                        subMit.setEnabled(true);
                        ipServer.setEnabled(true);
                    }
                }else {
                    Toast.makeText(context,"không thể connect tới server",Toast.LENGTH_SHORT).show();
                    subMit.setEnabled(true);
                    ipServer.setEnabled(true);
                }
            }else if (checkNut =="1"){
                if (aBoolean) {
                    settingsDTO.setIpserver(ip);
                    Boolean kiemtra = settingsService.update(settingsDTO);
                    if (kiemtra){
                        Toast.makeText(context,"thêm ip server thành công",Toast.LENGTH_SHORT).show();
                        Intent iLogin = new Intent(context, aClass);
                        startActivity(iLogin);
                    }else {
                        Toast.makeText(context,"thêm ip server thất bại",Toast.LENGTH_SHORT).show();
                        subMit.setEnabled(true);
                        ipServer.setEnabled(true);
                    }
                }else {
                    Toast.makeText(context,"không thể connect tới server",Toast.LENGTH_SHORT).show();
                    subMit.setEnabled(true);
                    ipServer.setEnabled(true);
                }
            }
        }
    }
}

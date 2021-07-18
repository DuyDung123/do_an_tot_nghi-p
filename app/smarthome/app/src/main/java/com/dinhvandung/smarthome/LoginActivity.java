package com.dinhvandung.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dinhvandung.smarthome.DTO.SettingsDTO;
import com.dinhvandung.smarthome.SEVICE.SettingsService;

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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText edTenDangNhapDN, edMatKhauDN;
    Button btnDangNhapDN;
    ProgressBar barLogin;
    SettingsDTO settingsDTO = new SettingsDTO();

    SettingsService settingsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        edTenDangNhapDN = (EditText)findViewById(R.id.edTenDangNhapDN);
        edMatKhauDN = (EditText)findViewById(R.id.edMatKhauDN);
        btnDangNhapDN = (Button)findViewById(R.id.btnDangNhapDN);
        barLogin = (ProgressBar)findViewById(R.id.progressBarLogin);

        btnDangNhapDN.setOnClickListener(this);

        settingsService = new SettingsService(this);
        settingsDTO = settingsService.findOne();


        Log.e("Buffer Error", "ipgetsetttingdao " + settingsDTO.getIpserver());


        barLogin.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnDangNhapDN:
                dangNhap();
                break;
        }
    }

    private void dangNhap(){
        String tenDangNhap = edTenDangNhapDN.getText().toString();
        String matKhauDangNhap = edMatKhauDN.getText().toString();
        //SettingsDTO settingsDTO = settingsService.findOne();
        Log.e("Buffer Error", "result " + "34.134.46.182");
        if (tenDangNhap.equals("") || tenDangNhap == null){
            Toast.makeText(LoginActivity.this,"Nhập tên đăng nhập!",Toast.LENGTH_SHORT).show();
        }else if (matKhauDangNhap.equals("") || matKhauDangNhap == null){
            Toast.makeText(LoginActivity.this,"Nhập mật khẩu!",Toast.LENGTH_SHORT).show();
        }else {
            new CheckLogin(this,TrangChuActivity.class).execute(settingsDTO.getIpserver(),tenDangNhap,matKhauDangNhap);
        }
    }

    private class CheckLogin extends AsyncTask<String,Boolean, JSONObject>{
        private Context context;
        private Class aClass;
        ProgressDialog dialog;

        public CheckLogin(Context context,Class aClass) {
            this.context = context;
            this.aClass = aClass;
        }
        @Override
        protected void onPreExecute() {
            barLogin.setVisibility(View.VISIBLE);
            edTenDangNhapDN.setEnabled(false);
            edMatKhauDN.setEnabled(false);
            btnDangNhapDN.setEnabled(false);
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            dialog.setMessage("check...");
        }

        @Override

        protected JSONObject doInBackground(String... strings) {
            Log.e("Buffer Error", "resultjsonArray " + strings[0]);
            JSONObject userObject = null;
            String uri ="http://"+strings[0]+"/webcontroldevice/api-admin-user";
            String username = strings[1];
            String password = strings[2];
            String jsonInputString = "{\"userName\":\""+username+"\",\"password\":\""+password+"\"}";
            OutputStream out = null;
            StringBuffer response = new StringBuffer();
            try {
                URL url = new URL(uri);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setDoOutput(true);
                out = new BufferedOutputStream(httpURLConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(jsonInputString);
                writer.flush();
                writer.close();
                out.close();
                httpURLConnection.connect();
                int reqCode = httpURLConnection.getResponseCode();
                if (reqCode ==200){
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null){
                        response.append(inputLine);
                    }
                    Log.e("Buffer Error", "resultjsonArray2 " + response.toString());
                    userObject =  new JSONObject(response.toString());
                    Log.e("Buffer Error", "resultjsonArray22 " + userObject);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("Buffer Error", "vào dây"+ 1);
            } catch (IOException e) {
                Log.e("Buffer Error", "vào dây" +2);
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e("Buffer Error", "vào dây: " +3);
                e.printStackTrace();
            }
            return userObject;
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);
            Log.e("Buffer Error", "jsonArray " + object);
            barLogin.setVisibility(View.GONE);
            edTenDangNhapDN.setEnabled(true);
            edMatKhauDN.setEnabled(true);
            btnDangNhapDN.setEnabled(true);
            if (object != null) {
                Intent iTrangChu = new Intent(context, aClass);
                iTrangChu.putExtra("tendangnhap", edTenDangNhapDN.getText().toString());
                startActivity(iTrangChu);
            } else{
                Toast.makeText(LoginActivity.this,"Sai tên đăng nhập hoặc mật khẩu",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

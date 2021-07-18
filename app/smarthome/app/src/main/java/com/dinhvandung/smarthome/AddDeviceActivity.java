package com.dinhvandung.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.dinhvandung.smarthome.DTO.CategoryDTO;
import com.dinhvandung.smarthome.DTO.SettingsDTO;
import com.dinhvandung.smarthome.SEVICE.CategoryService;
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
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddDeviceActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    EditText txtNamDevice;
    Button btnAddDevice;
    Spinner spinner;
    List<CategoryDTO> categoryDTOS;
    ProgressBar progressAddDevice;
    SettingsService settingsService;
    SettingsDTO settingsDTO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_device);
        txtNamDevice = (EditText)findViewById(R.id.txtAddDevice);
        spinner = (Spinner)findViewById(R.id.spinnerAddDevice);
        btnAddDevice  = (Button)findViewById(R.id.btnAddDevice);
        progressAddDevice = (ProgressBar)findViewById(R.id.progressAddDevice);

        progressAddDevice.setVisibility(View.GONE);

        this.getSupportActionBar().setTitle(R.string.addDevice1);

        CategoryService categoryService = new CategoryService();

        List<String> categoriesDevice = new ArrayList<String>();
        categoriesDevice.add("Chọn loại thiết bị");

        categoryDTOS = categoryService.findAll();
        for (CategoryDTO dto:categoryDTOS) {
            categoriesDevice.add(dto.getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddDeviceActivity.this, android.R.layout.simple_spinner_item, categoriesDevice);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        btnAddDevice.setOnClickListener(this);

        settingsService = new SettingsService(this);
        settingsDTO = settingsService.findOne();
    }


    @Override
    public void onClick(View v) {
        String nameDevice = txtNamDevice.getText().toString();
        String item = String.valueOf(spinner.getSelectedItem());
        if (item.equals("Chọn loại thiết bị")){
            Toast.makeText(this, "Bạn chưa chọn loại thiết bị", Toast.LENGTH_LONG).show();
        }else if (nameDevice.equals("") || nameDevice == null){
            Toast.makeText(this, "Bạn chưa điền tên thiết bị", Toast.LENGTH_LONG).show();
        }else{
            CategoryDTO categoryDTO = new CategoryDTO();
            for (CategoryDTO dto:categoryDTOS) {
                if (dto.getName().equals(item)) {
                    categoryDTO = dto;
                }
            }
            String ip = settingsDTO.getIpserver();
            new AddDevice().execute(ip,String.valueOf(categoryDTO.getValue()),nameDevice);
        }
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class AddDevice extends AsyncTask<String,Boolean, JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressAddDevice.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            Log.e("Buffer Error", "ip " + strings[0]);
            JSONObject objectDevice = null;
            String uri ="http://"+strings[0]+"/webcontroldevice/api-admin-device";
            String categoryDevice = strings[1];
            String nameDevice = strings[2];
            String jsonInputString = "{\"category\":\""+categoryDevice+"\",\"name\":\""+nameDevice+"\"}";
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
                Log.e("Buffer Error", "reqCode " + reqCode);
                if (reqCode ==200){
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null){
                        response.append(inputLine);
                    }
                    Log.e("Buffer Error", "resultjsonArray2 " + response.toString());
                    objectDevice =  new JSONObject(response.toString());
                    Log.e("Buffer Error", "resultjsonArray22 " + objectDevice);
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
            return objectDevice;
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            Log.e("Buffer Error", "vào  object: " +object);
            super.onPostExecute(object);
            progressAddDevice.setVisibility(View.GONE);
            boolean result;
            if (object != null) {
                result = true;
            }else {
                result = false;
            }
            Intent intent = new Intent();
            intent.putExtra("resultAdd",result);
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
    }
}

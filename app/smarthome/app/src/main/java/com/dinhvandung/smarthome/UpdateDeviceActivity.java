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
import com.dinhvandung.smarthome.DTO.DeviceDTO;
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

public class UpdateDeviceActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Spinner spinner;
    EditText editTextDevice;
    Button btnSua;
    List<CategoryDTO> categoryDTOS;
    ProgressBar progressAddDevice;
    DeviceDTO deviceDTO = new DeviceDTO();
    SettingsService settingsService;
    SettingsDTO settingsDTO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_device);

        spinner = (Spinner)findViewById(R.id.spinnerAddDevice);
        editTextDevice = (EditText)findViewById(R.id.txtAddDevice);
        btnSua = (Button)findViewById(R.id.btnAddDevice);
        progressAddDevice = (ProgressBar)findViewById(R.id.progressAddDevice);

        progressAddDevice.setVisibility(View.GONE);

        this.getSupportActionBar().setTitle(R.string.addDevice2);

        ArrayList<String> dto = getIntent().getStringArrayListExtra("device");
        deviceDTO.setId(Long.valueOf(dto.get(0)));
        deviceDTO.setCategory(Long.valueOf(dto.get(1)));
        deviceDTO.setName(dto.get(2));

        CategoryService categoryService = new CategoryService();
        categoryDTOS = categoryService.findAll();

        List<String> categoriesDevice = new ArrayList<String>();
        String valueSelect = "";
        categoriesDevice.add("Chọn loại thiết bị");
        for (int i = 0; i < categoryDTOS.size(); i++) {
            categoriesDevice.add(categoryDTOS.get(i).getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(UpdateDeviceActivity.this, android.R.layout.simple_spinner_item, categoriesDevice);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        String valueSpinner = String.valueOf(deviceDTO.getCategory());

        spinner.setSelection(Integer.valueOf(valueSpinner));
        editTextDevice.setText(deviceDTO.getName());
        btnSua.setText(R.string.addDevice2);


        btnSua.setOnClickListener(this);

        settingsService = new SettingsService(this);
        settingsDTO = settingsService.findOne();
    }



    @Override
    public void onClick(View v) {
        String nameDevice = editTextDevice.getText().toString();
        String item = String.valueOf(spinner.getSelectedItem());
        Log.e("TAG", "onClick: "+ item);

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
             new UpdateDevice().execute(ip,String.valueOf(deviceDTO.getId()),String.valueOf(categoryDTO.getValue()),nameDevice);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private  class  UpdateDevice extends AsyncTask<String,Boolean, JSONObject>{
        @Override
        protected JSONObject doInBackground(String... strings) {
            Log.e("Buffer Error", "ip " + strings[0]);
            JSONObject objectDevice = null;
            String uri ="http://"+strings[0]+"/webcontroldevice/api-admin-device";
            String id = strings[1];
            String category = strings[2];
            String name = strings[3];
            String  jsonInputString = "{\"id\":\""+id+"\",\"category\":\""+category+"\",\"name\":\""+name+"\"}";

            OutputStream out = null;
            StringBuffer response = new StringBuffer();
            try {
                URL url = new URL(uri);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("PUT");
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
                Log.e("Buffer Error", "vào dây "+ 1);
            } catch (IOException e) {
                Log.e("Buffer Error", "vào dây " +2);
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e("Buffer Error", "vào dây " +3);
                e.printStackTrace();
            }
            return objectDevice;
        }

        @Override
        protected void onPostExecute(JSONObject object) {
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

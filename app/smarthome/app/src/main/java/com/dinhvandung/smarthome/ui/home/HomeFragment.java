package com.dinhvandung.smarthome.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.dinhvandung.smarthome.AddDeviceActivity;
import com.dinhvandung.smarthome.CustomAdapter.AdapterShowDevice;
import com.dinhvandung.smarthome.DTO.DeviceDTO;
import com.dinhvandung.smarthome.DTO.SettingsDTO;
import com.dinhvandung.smarthome.LoginActivity;
import com.dinhvandung.smarthome.R;
import com.dinhvandung.smarthome.SEVICE.DeviceService;
import com.dinhvandung.smarthome.SEVICE.SettingsService;
import com.dinhvandung.smarthome.TrangChuActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    public static int REQUEST_CODE_ADD = 111;

    GridView gridViewDevice;

    SettingsService settingsService;
    SettingsDTO settingsDTO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_trangchu_home,container,false);
        setHasOptionsMenu(true);
        ((TrangChuActivity)getActivity()).getSupportActionBar().setTitle(R.string.Device);
        gridViewDevice = (GridView)view.findViewById(R.id.SHOW_ALL_DEVICE);

        settingsService = new SettingsService(getActivity());
        settingsDTO = settingsService.findOne();

        String ip = settingsDTO.getIpserver();
        new GetDeviceOnServer().execute(ip);
        return view;
    }

    //tạo một menu trên toolbar
//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        MenuItem add_AddDevice = menu.add(1,R.id.add_device,1,R.string.addDevice);
//        add_AddDevice.setIcon(R.drawable.adddevice16);
//        add_AddDevice.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//    }

    // nếu cái menu tạo ở trên được chọn thì hiện thị layout này AddDeviceActivity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.add_device:
                Intent itAdđevice = new Intent(getActivity(), AddDeviceActivity.class);
                startActivityForResult(itAdđevice,REQUEST_CODE_ADD);
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== REQUEST_CODE_ADD){
            if (requestCode == Activity.RESULT_OK){
                Intent intent = data;
                boolean check = intent.getBooleanExtra("resultAdd",false);
                if (check){
                    Toast.makeText(getActivity(),"Thêm thành công!",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(),"Thêm thất bại!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class GetDeviceOnServer extends AsyncTask<String,Boolean, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            Log.e("Buffer Error", "ip " + strings[0].toString());

            JSONArray object = null;
            StringBuffer response = new StringBuffer();

            String uri = "http://"+strings[0]+"/webcontroldevice/api-admin-device";
            try {
                URL url = new URL(uri);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
                int reqCode = httpURLConnection.getResponseCode();
                if (reqCode ==200){
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            httpURLConnection.getInputStream(), "utf-8"), 8);
                    String inputLine;
                    while ((inputLine = in.readLine()) != null){
                        response.append(inputLine);
                    }
                    Log.e("Buffer Error", "resultjsonString " + response.toString());
                    object =  new JSONArray(response.toString());
                    Log.e("Buffer Error", "resultjsonArray22 " + object);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return object;
        }

        @Override
        protected void onPostExecute(JSONArray object) {
            List<DeviceDTO> deviceDTOS = new ArrayList<>();
            try{
                for(int i = 0; i<object.length();i++) {
                    JSONObject obj = obj = (JSONObject) object.get(i);
                    DeviceDTO deviceModel = new DeviceDTO();
                    if (obj.getLong("category")!= 3){
                        deviceModel.setId(obj.getLong("id"));
                        deviceModel.setName(obj.getString("name"));
                        deviceModel.setCategory(obj.getLong("category"));
                        deviceModel.setDevicecode(obj.getString("devicecode"));
                        deviceModel.setStatus(obj.getString("status"));
                        deviceDTOS.add(deviceModel);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AdapterShowDevice adapterShowDevice = new AdapterShowDevice(getActivity(),R.layout.layout_custom_one_device,deviceDTOS);
            gridViewDevice.setAdapter(adapterShowDevice);
        }
    }
}
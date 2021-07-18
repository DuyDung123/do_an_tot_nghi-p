package com.dinhvandung.smarthome.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dinhvandung.smarthome.AddDeviceActivity;
import com.dinhvandung.smarthome.CustomAdapter.AdapterShowOneDevice;
import com.dinhvandung.smarthome.DTO.DeviceDTO;
import com.dinhvandung.smarthome.DTO.SettingsDTO;
import com.dinhvandung.smarthome.R;
import com.dinhvandung.smarthome.SEVICE.SettingsService;
import com.dinhvandung.smarthome.TrangChuActivity;
import com.dinhvandung.smarthome.UpdateDeviceActivity;

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

public class DsDeviceFragment extends Fragment {

    ListView listViewDevice;
    List<DeviceDTO> deviceDTOS;

    public static int REQUEST_CODE_ADD = 111;
    public static int REQUEST_CODE_SUA = 16;

    SettingsService settingsService;
    SettingsDTO settingsDTO;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_trangchu_list_device,container,false);
        setHasOptionsMenu(true);

        ((TrangChuActivity)getActivity()).getSupportActionBar().setTitle(R.string.DsDevice);

        listViewDevice = (ListView)view.findViewById(R.id.SHOW_ALL_LIST_DEVICE);

        registerForContextMenu(listViewDevice);

        settingsService = new SettingsService(getActivity());
        settingsDTO = settingsService.findOne();

        String ip = settingsDTO.getIpserver();
        new GetAllDevice().execute(ip);
        return view;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull  View v, @Nullable  ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.edit_context_menu,menu);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long idDevide = deviceDTOS.get(menuInfo.position).getId();
        switch (id){
            case R.id.itSua:
                DeviceDTO deviceDTO = deviceDTOS.get(menuInfo.position);
                Intent intent = new Intent(getActivity(), UpdateDeviceActivity.class);
                ArrayList<String> dto = new ArrayList<String>();
                dto.add(String.valueOf(deviceDTO.getId()));
                dto.add(String.valueOf(deviceDTO.getCategory()));
                dto.add(deviceDTO.getName());
                intent.putStringArrayListExtra("device",dto);
                startActivityForResult(intent,REQUEST_CODE_SUA);
            break;
            case R.id.itXoa:
                String ip = settingsDTO.getIpserver();
                new DeleteDevice().execute(ip,String.valueOf(idDevide));
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem add_AddDevice = menu.add(1,R.id.add_device,1,R.string.addDevice);
        add_AddDevice.setIcon(R.drawable.adddevice16);
        add_AddDevice.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }


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
            if (resultCode == Activity.RESULT_OK){
                Intent intent = data;
                boolean check = intent.getBooleanExtra("resultAdd",false);
                if (check){
                    Toast.makeText(getActivity(),"Thêm thành công!",Toast.LENGTH_SHORT).show();
                    String ip = settingsDTO.getIpserver();
                    new GetAllDevice().execute(ip);
                }else {
                    Toast.makeText(getActivity(),"Thêm thất bại!",Toast.LENGTH_SHORT).show();
                }
            }
        }else if(requestCode== REQUEST_CODE_SUA){
            if (resultCode == Activity.RESULT_OK){
                Intent intent = data;
                boolean check = intent.getBooleanExtra("resultAdd",false);
                if (check){
                    String ip = settingsDTO.getIpserver();
                    new GetAllDevice().execute(ip);
                    Toast.makeText(getActivity(),"Sửa thành công!",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(),"Sửa thất bại!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private class GetAllDevice extends AsyncTask<String,Boolean, JSONArray> {

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
            deviceDTOS = new ArrayList<>();
            try{
                for(int i = 0; i<object.length();i++) {
                    JSONObject obj = obj = (JSONObject) object.get(i);
                    DeviceDTO deviceModel = new DeviceDTO();
                    deviceModel.setId(obj.getLong("id"));
                    deviceModel.setName(obj.getString("name"));
                    deviceModel.setCategory(obj.getLong("category"));
                    deviceModel.setDevicecode(obj.getString("devicecode"));
                    deviceModel.setStatus(obj.getString("status"));
                    deviceDTOS.add(deviceModel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AdapterShowOneDevice adapterShowOneDevice = new AdapterShowOneDevice(getActivity(),R.layout.layout_custom_one_device_of_list_device,deviceDTOS);
            listViewDevice.setAdapter(adapterShowOneDevice);
        }
    }

    private class DeleteDevice extends AsyncTask<String,Boolean,JSONObject>{

        @Override
        protected JSONObject doInBackground(String... strings) {
            Log.e("Buffer Error", "ip " + strings[0]);
            JSONObject objectDevice = null;
            String uri ="http://"+strings[0]+"/webcontroldevice/api-admin-device";
            String id = strings[1];
            String  jsonInputString = "{\"ids\":[\""+id+"\"]}";

            OutputStream out = null;
            StringBuffer response = new StringBuffer();
            try {
                URL url = new URL(uri);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("DELETE");
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
                    objectDevice =  new JSONObject();
                    Log.e("Buffer Error", "resultjsonArray22 " + objectDevice);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("Buffer Error", "vào dây"+ 1);
            } catch (IOException e) {
                Log.e("Buffer Error", "vào dây" +2);
                e.printStackTrace();
            }
            return objectDevice;
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            if (object.length() == 0){
                String ip = settingsDTO.getIpserver();
                new GetAllDevice().execute(ip);
                Toast.makeText(getActivity(),"Xóa thành công !",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getActivity(),"Xóa thất bại !",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

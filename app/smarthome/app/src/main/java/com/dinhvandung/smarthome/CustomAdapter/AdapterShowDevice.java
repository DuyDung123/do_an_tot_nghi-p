package com.dinhvandung.smarthome.CustomAdapter;

import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dinhvandung.smarthome.DTO.DeviceDTO;
import com.dinhvandung.smarthome.DTO.SettingsDTO;
import com.dinhvandung.smarthome.LoginActivity;
import com.dinhvandung.smarthome.R;
import com.dinhvandung.smarthome.SEVICE.SettingsService;

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
import java.util.List;

public class AdapterShowDevice extends BaseAdapter implements View.OnClickListener{

    Context context;
    int layout;
    List<DeviceDTO> deviceDTOS;
    ViewHolderDevice viewHolderDevice;

    SettingsDTO settingsDTO;
    SettingsService settingsService;

    public AdapterShowDevice(Context context, int layout, List<DeviceDTO> deviceDTOS){
        this.context = context;
        this.layout = layout;
        this.deviceDTOS = deviceDTOS;
    }
    @Override
    public int getCount() {
        return deviceDTOS.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceDTOS.get(position);
    }

    @Override
    public long getItemId(int position) {
        return deviceDTOS.get(position).getId();
    }


    public class ViewHolderDevice{
        TextView txtNameDevice,txtIdDevice;
        ImageView imgDevice;
        Switch status;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewHolderDevice = new ViewHolderDevice();
            view = inflater.inflate(R.layout.layout_custom_one_device,parent,false);
            viewHolderDevice.txtNameDevice = (TextView)view.findViewById(R.id.txtNameDevice);
            viewHolderDevice.imgDevice = (ImageView)view.findViewById(R.id.imgDevice);
            viewHolderDevice.status = (Switch)view.findViewById(R.id.btnOnOffDevice);
            viewHolderDevice.txtIdDevice = (TextView)view.findViewById(R.id.txtidDevice);
            view.setTag(viewHolderDevice);
        }else {
            viewHolderDevice = (ViewHolderDevice)view.getTag();
        }

        settingsService = new SettingsService(context);
        settingsDTO = settingsService.findOne();

        DeviceDTO deviceDTO = deviceDTOS.get(position);
        viewHolderDevice.txtNameDevice.setText(deviceDTO.getName());
        viewHolderDevice.txtIdDevice.setHint(deviceDTO.getId().toString());
        viewHolderDevice.status.setTag(position);
        if (deviceDTO.getStatus().equals("1")) {
            viewHolderDevice.status.setChecked(true);
            if (deviceDTO.getCategory() == 1){
                viewHolderDevice.imgDevice.setImageResource(R.drawable.lighton);
            }else{
                viewHolderDevice.imgDevice.setImageResource(R.drawable.fanon);
            }
        } else if (deviceDTO.getStatus().equals("0")) {
            viewHolderDevice.status.setChecked(false);
            if (deviceDTO.getCategory() == 1){
                viewHolderDevice.imgDevice.setImageResource(R.drawable.lightoff);
            }else{
                viewHolderDevice.imgDevice.setImageResource(R.drawable.fanoff);
            }
        }
        viewHolderDevice.status.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String ip = settingsDTO.getIpserver();
        viewHolderDevice = (ViewHolderDevice) ((View)v.getParent()).getTag();
        switch (id){
            case R.id.btnOnOffDevice:
                int index = (int)v.getTag();
                String idDevicde = deviceDTOS.get(index).getId().toString();
                if (viewHolderDevice.status.isChecked()){
                    String status = "1";
                    new SwitchOnOffDevice(v.getContext(),index,deviceDTOS.get(index).getStatus()).execute(ip,idDevicde,status);
                }else {
                    String status = "0";
                    new SwitchOnOffDevice(v.getContext(),index,deviceDTOS.get(index).getStatus()).execute(ip,idDevicde,status);
                }
                break;
        }
    }

    private class SwitchOnOffDevice extends AsyncTask<String,Boolean, JSONObject>{
        private Context context;
        int index;
        String currentstatus;
        ProgressDialog dialog;

        public SwitchOnOffDevice(Context context,int index,String currentstatus) {
            this.context = context;
            this.index = index;
            this.currentstatus = currentstatus;
        }
        @Override
        protected void onPreExecute() {
            //barLogin.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
        }

        @Override

        protected JSONObject doInBackground(String... strings) {
            Log.e("Buffer Error", "resultjsonArray " + strings[0]);
            JSONObject deviceObject = null;
            String uri ="http://"+strings[0]+"/webcontroldevice/api-admin-device";
            String id = strings[1];
            String status = strings[2];
            String jsonInputString = "{\"type\":\"updatestatus\",\"id\":\""+id+"\",\"status\":\""+status+"\"}";
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
                if (reqCode ==200){
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            httpURLConnection.getInputStream(), "utf-8"), 8);
                    String inputLine;
                    while ((inputLine = in.readLine()) != null){
                        response.append(inputLine);
                    }
                    Log.e("Buffer Error", "resultjsonArray2 " + response.toString());
                    deviceObject =  new JSONObject(response.toString());
                    Log.e("Buffer Error", "resultjsonArray22 " + deviceObject);
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
            return deviceObject;
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);
            Log.e("Buffer Error", "jsonArray " + object);
            if (object != null) {
                DeviceDTO deviceModel = new DeviceDTO();
                try {
                    deviceModel.setId(object.getLong("id"));
                    deviceModel.setName(object.getString("name"));
                    deviceModel.setCategory(object.getLong("category"));
                    deviceModel.setDevicecode(object.getString("devicecode"));
                    deviceModel.setStatus(object.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                deviceDTOS.get(index).setStatus(deviceModel.getStatus());
                if (deviceModel.getStatus().equals("1")){
                    if (deviceModel.getCategory() ==1){
                        viewHolderDevice.imgDevice.setImageResource(R.drawable.lighton);
                    }else{
                        viewHolderDevice.imgDevice.setImageResource(R.drawable.fanon);
                    }
                    Toast.makeText(context,deviceModel.getName()+ " bật" ,Toast.LENGTH_SHORT).show();
                }else {
                    if (deviceModel.getCategory() ==1){
                        viewHolderDevice.imgDevice.setImageResource(R.drawable.lightoff);
                    }else{
                        viewHolderDevice.imgDevice.setImageResource(R.drawable.fanoff);
                    }
                    Toast.makeText(context,deviceModel.getName()+ " tắt" ,Toast.LENGTH_SHORT).show();
                }
            } else{
                deviceDTOS.get(index).setStatus(currentstatus);
                Toast.makeText(context, "lỗi thiết bị",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

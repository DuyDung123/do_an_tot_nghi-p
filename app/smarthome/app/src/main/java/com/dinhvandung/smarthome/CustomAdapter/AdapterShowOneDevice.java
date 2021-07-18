package com.dinhvandung.smarthome.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dinhvandung.smarthome.DTO.DeviceDTO;
import com.dinhvandung.smarthome.DTO.StatusDTO;
import com.dinhvandung.smarthome.R;

import java.util.List;

public class AdapterShowOneDevice extends BaseAdapter implements View.OnClickListener{

    Context context;
    int layout;
    List<DeviceDTO> deviceDTOS;
    ViewHolderOneDevice viewHolderOneDevice;


    public AdapterShowOneDevice(Context context, int layout, List<DeviceDTO> deviceDTOS){
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



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewHolderOneDevice = new ViewHolderOneDevice();
            view = inflater.inflate(R.layout.layout_custom_one_device_of_list_device,parent,false);

            viewHolderOneDevice.edNameDevice = (TextView)view.findViewById(R.id.edNameDevice);
            viewHolderOneDevice.edCodeDevice = (TextView)view.findViewById(R.id.edCodeDevice);
            viewHolderOneDevice.lnK_One_Of_List_Device = (LinearLayout)view.findViewById(R.id.lnK_One_Of_List_Device);
            view.setTag(viewHolderOneDevice);
        }else {
            viewHolderOneDevice = (ViewHolderOneDevice) view.getTag();
        }

        DeviceDTO deviceDTO = deviceDTOS.get(position);
        viewHolderOneDevice.edNameDevice.setText(deviceDTO.getName());
        viewHolderOneDevice.edCodeDevice.setText(deviceDTO.getDevicecode());
        return view;
    }

    @Override
    public void onClick(View v) {

    }


    public class ViewHolderOneDevice{
        TextView edNameDevice,edCodeDevice;
        LinearLayout lnK_One_Of_List_Device;
    }
}

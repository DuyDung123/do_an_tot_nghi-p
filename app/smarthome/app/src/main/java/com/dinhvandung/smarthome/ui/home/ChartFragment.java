package com.dinhvandung.smarthome.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.dinhvandung.smarthome.DTO.DeviceDTO;
import com.dinhvandung.smarthome.DTO.SettingsDTO;
import com.dinhvandung.smarthome.DTO.StatusDTO;
import com.dinhvandung.smarthome.R;
import com.dinhvandung.smarthome.SEVICE.SettingsService;
import com.dinhvandung.smarthome.TrangChuActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChartFragment extends Fragment implements AdapterView.OnItemSelectedListener  {
    ProgressBar bar;
    Spinner spinner;
    List<DeviceDTO> deviceDTOList;

    LineChart lineChart;
    SettingsService settingsService;
    SettingsDTO settingsDTO;



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_trangchu_chart,container,false);
        bar = (ProgressBar)view.findViewById(R.id.progressBarChart);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        deviceDTOList = new ArrayList<>();
        spinner.setOnItemSelectedListener(this);

        ((TrangChuActivity)getActivity()).getSupportActionBar().setTitle(R.string.Chart);

        lineChart = (LineChart)view.findViewById(R.id.lineChart);

        settingsService = new SettingsService(getActivity());
        settingsDTO = settingsService.findOne();
        String ip = settingsDTO.getIpserver();
        new GetDataChart().execute(ip);
        return view;
    }

    public void setValueChart(DeviceDTO valueChart) {
        ArrayList<Entry> temp = new ArrayList<Entry>();
        ArrayList<Entry> hum = new ArrayList<Entry>();
        for (StatusDTO status : valueChart.getListStatus()) {
            long unixSeconds = Long.valueOf(status.getCreatedDate());
            Date dates= new Timestamp(unixSeconds);
            temp.add(new Entry(Float.parseFloat(status.getCreatedDate()), status.getTemperature()));
            hum.add(new Entry(Float.parseFloat(status.getCreatedDate()), status.getHumidity()));

            //temp.add(new Entry(Float.parseFloat(dates.getHours()+"."+dates.getMinutes()), status.getTemperature()));
            //hum.add(new Entry(Float.parseFloat(dates.getHours()+"."+dates.getMinutes()), status.getHumidity()));
        }


        LineDataSet lineDataSet = new LineDataSet(temp, "nhiệt độ");
        lineDataSet.setHighLightColor(R.color.colorRed);
        lineDataSet.setCircleColor(R.color.colorGray);
        lineDataSet.setColor(R.color.colorRed);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setCircleColors(R.color.colorRed);
        lineDataSet.setLineWidth(3f);


        LineDataSet lineDataSet2 = new LineDataSet(hum, "độ ẩm");
        lineDataSet2.setHighLightColor(R.color.design_default_color_primary);
        lineDataSet2.setCircleColor(R.color.colorGreenButton);
        lineDataSet2.setColor(R.color.design_default_color_primary);
        lineDataSet2.setDrawFilled(true);
        lineDataSet2.setLineWidth(1f);


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new CustomAxisValueDateTimLineChart(valueChart.getListStatus().size()));
        YAxis yAxisTemp = lineChart.getAxisLeft();
        YAxis yAxisHum = lineChart.getAxisRight();
        yAxisTemp.setValueFormatter(new CustomAyisValueTempLineChart(valueChart.getListStatus().size()));
        yAxisHum.setValueFormatter(new CustomAyisValueHumLineChart(valueChart.getListStatus().size()));



        int colorTempAndHum[] = {R.color.colorRed, R.color.design_default_color_primary};
        String[] labelTempAndHum = {"Temp","Hum"};

        LegendEntry[] legendEntries = new LegendEntry[2];
        for (int i = 0; i < legendEntries.length; i++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = colorTempAndHum[i];
            entry.label = String.valueOf(labelTempAndHum[i]);
            legendEntries[i] = entry;
        }




        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        dataSets.add(lineDataSet);
        dataSets.add(lineDataSet2);

        Log.e("Buffer Error", "vo day temp " + temp);
        Log.e("Buffer Error", "vo day hum " + hum);

        LineData lineData = new LineData(dataSets);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        lineChart.setData(lineData);

        Description lineChartDescription = lineChart.getDescription();
        lineChartDescription.setEnabled(false);


        Legend lineChartLegend = lineChart.getLegend();
        lineChartLegend.setEnabled(true);
        lineChartLegend.setTextSize(15);
        lineChartLegend.setForm(Legend.LegendForm.LINE);
        lineChartLegend.setFormSize(20);
        lineChartLegend.setXEntrySpace(20);
        lineChartLegend.setFormToTextSpace(15);
        lineChartLegend.setCustom(legendEntries);
    }

    private class CustomAxisValueDateTimLineChart implements IAxisValueFormatter{

        private  int LabelCount;
        public CustomAxisValueDateTimLineChart(int LabelCount) {
            this.LabelCount = LabelCount;
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            //axis.setLabelCount(LabelCount,true);
            float e = (float) value;
            String a = String.valueOf(e);
            long w = (long)Float.parseFloat(a)+45304;
            Date date = new Timestamp(w);
            Log.e("Tag","xxx "+date.getHours()+"h"+date.getMinutes());
            return String.valueOf(date.getDay()+" :"+date.getHours()+"h "+date.getMinutes());
        }
    }

    private class CustomAyisValueTempLineChart implements IAxisValueFormatter{
        private  int LabelCount;
        public CustomAyisValueTempLineChart(int LabelCount) {
            this.LabelCount = LabelCount + 1;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            //axis.setLabelCount(LabelCount,true);
            return value+ " °C";
        }
    }

    private class CustomAyisValueHumLineChart implements IAxisValueFormatter{

        private  int LabelCount;
        public CustomAyisValueHumLineChart(int LabelCount) {
            this.LabelCount = LabelCount + 1;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            //axis.setLabelCount(LabelCount,true);
            //return value+ " %";
            return value+ " %";
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        DeviceDTO dto = new DeviceDTO();
        for (DeviceDTO deviceDTO:deviceDTOList) {
            if(deviceDTO.getName().equals(item)){
                dto = deviceDTO;
            }
        }
        setValueChart(dto);
        Toast.makeText(parent.getContext(), "hiển thị: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class GetDataChart extends AsyncTask<String,Boolean,JSONArray>{

        @Override
        protected JSONArray doInBackground(String... strings) {
            String uri = "http://"+strings[0]+"/webcontroldevice/api-admin-device?type=3";
            JSONArray object = null;
            StringBuffer response = new StringBuffer();
            String data ="";
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
                    object =  new JSONArray(response.toString());
                    Log.e("Buffer Error", "resultjsonString " + response.toString());
                    Log.e("Buffer Error", "resultjsonArray22 " + object);
                }
            } catch (MalformedURLException e) {
                Log.e("Buffer Error", "vo day " + 1);
                e.printStackTrace();
            } catch (IOException e){
                Log.e("Buffer Error", "vo day " + 2);
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return object;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            Log.e("Buffer Error", "results " + jsonArray);
            List<DeviceDTO> deviceDTOS = new ArrayList<>();
            List<String> categories = new ArrayList<String>();
            try{
                for(int i = 0; i<jsonArray.length();i++) {
                    JSONObject obj = obj = (JSONObject) jsonArray.get(i);
                    DeviceDTO deviceModel = new DeviceDTO();
                    deviceModel.setId(obj.getLong("id"));
                    deviceModel.setName(obj.getString("name"));
                    deviceModel.setCategory(obj.getLong("category"));
                    deviceModel.setDevicecode(obj.getString("devicecode"));
                    deviceModel.setStatus(obj.getString("status"));
                    Log.e("Buffer Error", "name " + deviceModel.getName());

                    JSONArray status = new JSONArray(obj.getString("listStatus"));
                    Log.e("Buffer Error", "listStatus " + status);
                    List<StatusDTO> statusDTO = new ArrayList<>();
                    for(int j = status.length()-1; j>=0;j--){
                        JSONObject objj = (JSONObject) status.get(j);
                        StatusDTO dto = new StatusDTO();
                        dto.setId(objj.getLong("id"));
                        dto.setCreatedDate(objj.getString("createdDate"));
                        dto.setTemperature((float) objj.getDouble("temperature"));
                        dto.setHumidity((float) objj.getDouble("humidity"));
                        statusDTO.add(dto);
                        Log.e("Buffer Error", "temperature " + dto.getTemperature());
                    }
                    deviceModel.setListStatus(statusDTO);
                    deviceDTOS.add(deviceModel);
                    deviceDTOList.addAll(deviceDTOS);

                    categories.add(deviceModel.getName());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Buffer Error", "vaoday " + 1);
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);

            DeviceDTO deviceDTOValueChart = deviceDTOS.get(0);
            Log.e("Buffer Error", "deviceDTOValueChart " + deviceDTOValueChart.getName());
            setValueChart(deviceDTOValueChart);

            bar.setVisibility(View.GONE);
        }
    }
}

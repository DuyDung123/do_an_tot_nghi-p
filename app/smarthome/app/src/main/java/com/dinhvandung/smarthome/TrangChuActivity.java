package com.dinhvandung.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dinhvandung.smarthome.ui.home.ChartFragment;
import com.dinhvandung.smarthome.ui.home.DsDeviceFragment;
import com.dinhvandung.smarthome.ui.home.HomeFragment;
import com.dinhvandung.smarthome.ui.home.SettingsFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class TrangChuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView txtTDangNhap_Navigation;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_trangchu);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigationview_trangchu);
        toolbar = findViewById(R.id.toolBar);

        // nhớ là dòng này để truyền set text co view nào bên khác
        View view = navigationView.inflateHeaderView(R.layout.nav_header_trangchu);
        txtTDangNhap_Navigation = (TextView) view.findViewById(R.id.txtTenDangNhap_Navigation);

        setSupportActionBar(toolbar);  //đoạn đày khai báo để nó biết đây là toorbar
        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //tạo 3 gạch
        getSupportActionBar().setDisplayShowHomeEnabled(true);  // hiện 3 gạch

        //toolbar nhớ cho cái này vào để khi click nó mới gọi cái drawerLayout ra
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.mo,R.string.dong){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        // nó chưa biết setDrawerToggle là của thằng nào nên phải gán nó vào drawerLayout
        drawerLayout.addDrawerListener(actionBarDrawerToggle); //setDrawerToggle cho thằng drawerLayout
        actionBarDrawerToggle.syncState(); //đồng bộ nó

        navigationView.setItemIconTintList(null); // set màu sắc cho các icon item trong navvigation
        navigationView.setNavigationItemSelectedListener(this); // sét sự kiên click cho nava

        Intent intent = getIntent();
        String tenDN = intent.getStringExtra("tendangnhap");
        Log.d("dulieu", tenDN);
        txtTDangNhap_Navigation.setText(tenDN);


        fragmentManager = getSupportFragmentManager();

        // khi vào trang chủ thì hiện page home vào luôn
        FragmentTransaction transactionHome = fragmentManager.beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        transactionHome.replace(R.id.flcontent,homeFragment);
        transactionHome.commit();
    }


    // add sự kiện click cho các item trong navvigation
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();
        switch (id){
            case R.id.itTrangChuMenu:
                FragmentTransaction transactionHome = fragmentManager.beginTransaction();
                HomeFragment homeFragment = new HomeFragment();
                transactionHome.replace(R.id.flcontent,homeFragment);
                transactionHome.commit();

                menuItem.setChecked(true);
                drawerLayout.closeDrawers(); //đóng cái navigation lại
                break;
            case R.id.itDeviceMenu:
                FragmentTransaction transactionChart= fragmentManager.beginTransaction();
                ChartFragment chartFragment = new ChartFragment();
                transactionChart.replace(R.id.flcontent,chartFragment);
                transactionChart.commit();
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                break;

            case R.id.itListDeviceMenu:
                FragmentTransaction transactionDsDevice= fragmentManager.beginTransaction();
                DsDeviceFragment dsDeviceFragment = new DsDeviceFragment();
                transactionDsDevice.replace(R.id.flcontent,dsDeviceFragment);
                transactionDsDevice.commit();
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                break;

            case R.id.SettingMenu:
                FragmentTransaction transactionSetting = fragmentManager.beginTransaction();
                SettingsFragment settingsFragment = new SettingsFragment();
                transactionSetting.replace(R.id.flcontent,settingsFragment);
                transactionSetting.commit();
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                break;
        }
        return false;
    }
}

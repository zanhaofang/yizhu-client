package com.sysu.yizhu.Activity.Business.AskHelp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.sysu.yizhu.MapHolder.BMap;
import com.sysu.yizhu.R;
import com.sysu.yizhu.Util.SerializableJson;

/**
 * Created by QianZixuan on 2017/4/30.
 * Description: 求助fragment
 */
public class AskHelpFragment extends Fragment {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient locClient;
    private LocationClientOption locClientOpt;
    private AskHelpFragment.MyLocationListener mListener = new AskHelpFragment.MyLocationListener();

    boolean isRequest; //手动请求
    boolean isFirstLoc; //初次定位

    private Button hotkey_help_locate;
    private Button refreshHelpBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplicationContext());

        View view = inflater.inflate(R.layout.ask_help_layout, container, false);
        //获取地图控件引用
        mMapView = (MapView) view.findViewById(R.id.ask_help_bmapView);
        hotkey_help_locate = (Button) view.findViewById(R.id.ask_help_locate);
        refreshHelpBtn = (Button) view.findViewById(R.id.ask_help_refresh_btn);



        isRequest = false;
        isFirstLoc = true;

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        BitmapDescriptor bitmapDesc = BitmapDescriptorFactory.fromResource(R.drawable.location);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, bitmapDesc));

        BMap.initBMapMarkerClickListener(mBaiduMap, getActivity());
        findMyLocation();

        hotkey_help_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRequest = true;
                findMyLocation();
            }
        });

        refreshHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshAskHelps();
            }
        });

        return  view;
    }

    private void refreshAskHelps() {
        getAndShowAskHelps();
    }

    public void findMyLocation() {
        locClient = new LocationClient(this.getActivity());
        locClient.registerLocationListener(mListener);
        locClientOpt = new LocationClientOption();
        locClientOpt.setOpenGps(true);// 打开gps
        locClientOpt.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locClientOpt.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//      locClientOpt.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//      locClientOpt.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
//      locClientOpt.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//      locClientOpt.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//      locClientOpt.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        locClient.setLocOption(locClientOpt);
        locClient.start();

    }

    private void getAndShowAskHelps() {
        SerializableJson json = new SerializableJson();
        json.put("helpId", "1");
        json.put("latitude", "21");
        json.put("longitude", "120");
        json.put("finished", "false");
        json.put("title", "抬米");
        json.put("detail", "帮忙抬米上五楼");
        json.put("needs", "3");
        json.put("responseNum", "2");
        json.put("pushUserId", "12345678911");

        BMap.showAskHelp(mBaiduMap, json);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null){
                return;
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(0)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(locData);
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 18.0f);
            if (isFirstLoc || isRequest) {
                mBaiduMap.animateMapStatus(u);
                isRequest = false;
            }
            isFirstLoc = false;
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
}

package com.sysu.yizhu.Activity.Business.Sos;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.sysu.yizhu.R;
import com.sysu.yizhu.Util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by QianZixuan on 2017/4/30.
 * Description: 一键求救fragment
 */
public class SosFragment extends Fragment {
    private static final String updateLocationUrl = "http://112.74.165.37:8080/user/updateLocation";
    private static final String sosPushUrl = "http://112.74.165.37:8080/sos/push";

    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient locClient;
    private LocationClientOption locClientOpt;
    private MyLocationListener mListener = new MyLocationListener();

    boolean isRequest; //手动请求
    boolean isFirstLoc; //初次定位

    private double latitude;
    private double longitude;

    private Button sos_locate;
    private FloatingActionButton sos_push;
    private FloatingActionButton sos_response;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() != null)
            SDKInitializer.initialize(getActivity().getApplicationContext());

        View view = inflater.inflate(R.layout.sos_layout, container, false);
        //获取控件引用
        mMapView = (TextureMapView) view.findViewById(R.id.bmapView);
        sos_locate = (Button) view.findViewById(R.id.sos_locate);
        sos_push = (FloatingActionButton) view.findViewById(R.id.sos_push);
        sos_response = (FloatingActionButton) view.findViewById(R.id.sos_response);

        isRequest = false;
        isFirstLoc = true;

        mMapView.showScaleControl(false);
        mMapView.showZoomControls(false);

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        BitmapDescriptor bitmapDesc = BitmapDescriptorFactory.fromResource(R.drawable.location);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, bitmapDesc));

        findMyLocation();

        sos_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRequest = true;
                findMyLocation();
            }
        });

        sos_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sosPush();
            }
        });

        sos_response.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sosResponse();
            }
        });

        return  view;
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

    private class MyLocationListener implements BDLocationListener{
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
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                mBaiduMap.animateMapStatus(u);
                isRequest = false;
            }
            isFirstLoc = false;
            if (getActivity() != null) {
                updateLocation();
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    private void updateLocation() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("latitude", Double.toString(latitude));
        params.put("longitude", Double.toString(longitude));
        HttpUtil.post(updateLocationUrl, params, new HttpUtil.HttpResponseCallBack() {
            @Override
            public void onSuccess(int code, String result) {
                switch (code) {
                    case 200:
                        /*if (getActivity() != null)
                            Toast.makeText(getActivity(), "更新定位成功！", Toast.LENGTH_SHORT).show();*/
                        break;
                    case 401:
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "未登录", Toast.LENGTH_SHORT).show();
                        break;
                    case 403:
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "经纬度错误", Toast.LENGTH_SHORT).show();
                        break;
                    case 450:
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "用户未记录安装Id", Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(String result, Exception e) {

            }
        });
    }

    private void sosPush() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("latitude", Double.toString(latitude));
        params.put("longitude", Double.toString(longitude));
        HttpUtil.post(sosPushUrl, params, new HttpUtil.HttpResponseCallBack() {
            @Override
            public void onSuccess(int code, String result) {
                switch (code) {
                    case 200:
                        if (getActivity() != null) {
                            String sosId = "";
                            JSONObject object = null;
                            try {
                                object = new JSONObject(result);

                                sosId = object.optString("sosId");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), SosPushActivity.class);
                            intent.putExtra("sosId",sosId); //传送Id
                            getActivity().startActivity(intent);
                        }
                        break;
                    case 401:
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "未登录", Toast.LENGTH_SHORT).show();
                        break;
                    case 403:
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "经纬度错误", Toast.LENGTH_SHORT).show();
                        break;
                    case 450:
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "用户未记录安装Id", Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(String result, Exception e) {

            }
        });
    }

    private void sosResponse() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), SosResponseActivity.class);
        getActivity().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onResume() {

        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        super.onResume();
    }
    @Override
    public void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        super.onPause();
    }
}

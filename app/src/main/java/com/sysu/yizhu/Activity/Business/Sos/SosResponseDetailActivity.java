package com.sysu.yizhu.Activity.Business.Sos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.sysu.yizhu.R;
import com.sysu.yizhu.Util.AppManager;
import com.sysu.yizhu.Util.HttpUtil;

import java.util.HashMap;

/**
 * Created by QianZixuan on 2017/7/1.
 */
public class SosResponseDetailActivity extends AppCompatActivity {
    private static final String sosResponseUrl = "http://112.74.165.37:8080/sos/response";
    private static final String sosFinishUrl = "http://112.74.165.37:8080/sos/finish";

    private String sosId;

    private Button sos_response_detail_response;
    private Button sos_response_detail_finish;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient locClient;
    private LocationClientOption locClientOpt;
    //private MyLocationListener mListener = new MyLocationListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sos_response_detail_layout);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        sosId = bundle.getString("sosId");

        sos_response_detail_response = (Button) findViewById(R.id.sos_response_detail_response);
        sos_response_detail_finish = (Button) findViewById(R.id.sos_response_detail_finish);

        mMapView = (MapView) findViewById(R.id.sos_response_detail_bmapView);
        mMapView.showScaleControl(false);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        BitmapDescriptor bitmapDesc = BitmapDescriptorFactory.fromResource(R.drawable.location);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, bitmapDesc));

        sos_response_detail_response.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sosResponse();
            }
        });

        sos_response_detail_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sosFinish();
            }
        });

        AppManager.getAppManager().addActivity(SosResponseDetailActivity.this);
    }

    private void sosResponse() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("sosId", sosId);
        HttpUtil.post(sosResponseUrl, params, new HttpUtil.HttpResponseCallBack() {
            @Override
            public void onSuccess(int code, String result) {
                switch (code) {
                    case 200:
                        Toast.makeText(SosResponseDetailActivity.this, "响应求救成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 401:
                        Toast.makeText(SosResponseDetailActivity.this, "未登录", Toast.LENGTH_SHORT).show();
                        break;
                    case 403:
                        Toast.makeText(SosResponseDetailActivity.this, "已响应，不能重复响应", Toast.LENGTH_SHORT).show();
                        break;
                    case 404:
                        Toast.makeText(SosResponseDetailActivity.this, "Sos Id不存在或已完成", Toast.LENGTH_SHORT).show();
                        break;
                    case 450:
                        Toast.makeText(SosResponseDetailActivity.this, "用户未记录安装Id", Toast.LENGTH_SHORT).show();
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

    private void sosFinish() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("sosId", sosId);
        HttpUtil.post(sosFinishUrl, params, new HttpUtil.HttpResponseCallBack() {
            @Override
            public void onSuccess(int code, String result) {
                switch (code) {
                    case 200:
                        Toast.makeText(SosResponseDetailActivity.this, "结束求救成功", Toast.LENGTH_SHORT).show();
                        AppManager.getAppManager().finishActivity();
                        break;
                    case 401:
                        Toast.makeText(SosResponseDetailActivity.this, "未登录", Toast.LENGTH_SHORT).show();
                        break;
                    case 404:
                        Toast.makeText(SosResponseDetailActivity.this, "Sos Id不存在或已完成", Toast.LENGTH_SHORT).show();
                        break;
                    case 450:
                        Toast.makeText(SosResponseDetailActivity.this, "非发起用户无法结束该求救", Toast.LENGTH_SHORT).show();
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
}

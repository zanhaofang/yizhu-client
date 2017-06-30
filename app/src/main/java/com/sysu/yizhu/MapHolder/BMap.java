package com.sysu.yizhu.MapHolder;


import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.AlphabeticIndex;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.sysu.yizhu.Activity.MainActivity;
import com.sysu.yizhu.R;
import com.sysu.yizhu.Util.SerializableJson;

public class BMap {

    public static void showAskHelp(BaiduMap mBaiduMap, SerializableJson helpData) {
        LatLng latLng = new LatLng(Double.valueOf(helpData.get("latitude")), Double.valueOf(helpData.get("longitude")));
        // 图标
        OverlayOptions overlayOptions = new MarkerOptions().position(latLng)
                .icon(MarkerIcon.getAskHelpIcon()).zIndex(5);
        Marker marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));

        Bundle bundle = new Bundle();
        bundle.putSerializable("helpData", helpData);
        marker.setExtraInfo(bundle);
    }

    // 在Activity中初始化地图后即调用该方法
    public static void initBMapMarkerClickListener(BaiduMap mBaiduMap, final Context context) {
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                //获得marker中的数据
                SerializableJson helpData = (SerializableJson) marker.getExtraInfo().get("helpData");
                if (helpData != null) {
                    LayoutInflater factory = LayoutInflater.from(context);
                    View dialogView = factory.inflate(R.layout.ask_help_detail, null);

                    ((TextView) dialogView.findViewById(R.id.ask_help_title_text_view)).setText(helpData.get("title"));
                    ((TextView) dialogView.findViewById(R.id.ask_help_detail_text_view)).setText(helpData.get("detail"));
                    ((TextView) dialogView.findViewById(R.id.ask_help_needs_text_view)).setText(helpData.get("needs"));
                    ((TextView) dialogView.findViewById(R.id.ask_help_response_num_text_view)).setText(helpData.get("responseNum"));

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(dialogView);

                    builder.setPositiveButton("前往", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Nothing to do
                        }
                    });

                    builder.create();
                    builder.show();
                }


                return true;
            }
        });
    }

}

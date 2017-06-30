package com.sysu.yizhu.MapHolder;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.sysu.yizhu.R;
import com.sysu.yizhu.Util.SerializableJson;

public class BMap {

    public static void showAskHelp(BaiduMap mBaiduMap, SerializableJson helpData) {
        LatLng latLng = new LatLng(Double.valueOf(helpData.get("latitude")), Double.valueOf(helpData.get("longitude")));
        // 图标
        OverlayOptions overlayOptions = new MarkerOptions().position(latLng)
                .icon(MarkerIcon.getAskHelpIcon()).zIndex(10);
        Marker marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));

        Bundle bundle = new Bundle();
        bundle.putSerializable("helpData", helpData);
        marker.setExtraInfo(bundle);
    }

}

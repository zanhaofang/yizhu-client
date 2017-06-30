package com.sysu.yizhu.MapHolder;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.sysu.yizhu.R;

public class MarkerIcon {
    public static BitmapDescriptor getAskHelpIcon() {
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.locate_help_icon);
        return bitmap;
    }
}

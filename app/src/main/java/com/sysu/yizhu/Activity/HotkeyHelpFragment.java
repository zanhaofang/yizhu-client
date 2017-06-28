package com.sysu.yizhu.Activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sysu.yizhu.R;

/**
 * Created by QianZixuan on 2017/4/30.
 * Description: 一键求救fragment
 */
public class HotkeyHelpFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hotkey_help_layout, container, false);
    }
}

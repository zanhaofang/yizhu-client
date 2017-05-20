package com.sysu.yizhu;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by QianZixuan on 2017/4/30.
 * Description: 提问fragment
 */
public class AskQuestionFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ask_question_layout, container, false);
    }
}

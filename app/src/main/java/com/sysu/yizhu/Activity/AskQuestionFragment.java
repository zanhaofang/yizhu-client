package com.sysu.yizhu.Activity;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.sysu.yizhu.R;
import com.sysu.yizhu.Util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by QianZixuan on 2017/4/30.
 * Description: 提问fragment
 */
public class AskQuestionFragment extends ListFragment {
    private static final String getAllQuestionIdUrl = "http://112.74.165.37:8080/question/getAllId";
    private static final String getQusetionByIdUrl = "http://112.74.165.37:8080/question/digest/";

    private FloatingActionButton addQuestion;

    private SimpleAdapter adapter;

    private List<Map<String, String>> list = new ArrayList<Map<String, String>>();

    private int count;
    private String [] data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ask_question_layout, container, false);

        addQuestion = (FloatingActionButton) view.findViewById(R.id.addQuestion);

        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), AddQuestionActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new SimpleAdapter(getActivity(), getData(), R.layout.question_item_layout,
                new String[] {"userName", "title", "createDate"},
                new int[] {R.id.questionItemUserName, R.id.questionItemTitle, R.id.questionItemCreateDate});
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //listview Item点击事件
        if (getActivity() != null) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), QuestionDetailActivity.class);
            intent.putExtra("questionId", list.get(position).get("questionId")); //传送questionId
            getActivity().startActivity(intent);
        }
    }

    private List<Map<String, String>> getData() {
        list.clear(); //清空Adapter中的数据
        count = 0;

        HttpUtil.get(getAllQuestionIdUrl, new HttpUtil.HttpResponseCallBack() { //获得所有问题id
            @Override
            public void onSuccess(int code, String result) {
                switch (code) {
                    case 200:
                        JSONObject object = null;
                        try {
                            object = new JSONObject(result);
                            count = Integer.parseInt(object.optString("count"));
                            String strTemp = object.optString("data");
                            if (strTemp.length() > 2) {
                                strTemp = strTemp.substring(1, strTemp.length()-1);
                                data = strTemp.split(",");
                            } else {
                                data = new String[]{};
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            for (int i = 0; i < count; i++) {
                                HttpUtil.get(getQusetionByIdUrl + data[i], new HttpUtil.HttpResponseCallBack() { //根据id获取问题
                                    @Override
                                    public void onSuccess(int code, String result) {
                                        switch (code) {
                                            case 200:
                                                JSONObject object = null;
                                                try {
                                                    object = new JSONObject(result);

                                                    Map<String, String> map = new HashMap<String, String>();
                                                    map.put("questionId", object.optString("questionId"));
                                                    map.put("userId", object.optString("userId"));
                                                    map.put("userName", object.optString("userName"));
                                                    map.put("title", object.optString("title"));
                                                    map.put("createDate", object.optString("createDate"));
                                                    list.add(map);
                                                    adapter.notifyDataSetChanged(); //更新adapter数据
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                            case 404:
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
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(String result, Exception e) {

            }
        });

        return list;
    }
}

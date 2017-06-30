package com.sysu.yizhu.Activity.Business.AskQuestion;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sysu.yizhu.R;
import com.sysu.yizhu.Util.AppManager;
import com.sysu.yizhu.Util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by QianZixuan on 2017/6/27.
 */
public class QuestionDetailActivity extends AppCompatActivity {
    private static final String getQuestionDetailUrl = "http://112.74.165.37:8080/question/detail/"; //{questionId}
    private static final String getAllAnswerIdUrl = "http://112.74.165.37:8080/question/getAnswerIds/"; //{questionId}
    private static final String getAnswerByIdUrl = "http://112.74.165.37:8080/question/getAnswer/"; //{answerId}

    private ListView answer_list;
    private FloatingActionButton addAnswer;
    private TextView question_username;
    private TextView question_title;
    private TextView question_content;
    private TextView question_createDate;

    private SimpleAdapter adapter;

    private List<Map<String, String>> list = new ArrayList<Map<String, String>>();

    private String questionId;
    private int count;
    private String [] data;//存回答Id

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_detail_layout);

        answer_list = (ListView) findViewById(R.id.answer_list);
        addAnswer = (FloatingActionButton) findViewById(R.id.addAnswer);
        question_username = (TextView) findViewById(R.id.question_username);
        question_title = (TextView) findViewById(R.id.question_title);
        question_content = (TextView) findViewById(R.id.question_content);
        question_createDate = (TextView) findViewById(R.id.question_createDate);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        questionId = bundle.getString("questionId");

        getQuestionDetail();

        adapter = new SimpleAdapter(QuestionDetailActivity.this, getAnswerList(), R.layout.answer_item_layout,
                new String[] {"userName", "createDate", "content"},
                new int[] {R.id.answer_username, R.id.answer_createDate, R.id.answer_content});
        answer_list.setAdapter(adapter);

        addAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(QuestionDetailActivity.this, AddAnswerActivity.class);
                intent.putExtra("questionId", questionId); //传送questionId
                QuestionDetailActivity.this.startActivity(intent);
            }
        });

        AppManager.getAppManager().addActivity(QuestionDetailActivity.this);
    }

    private void getQuestionDetail() {
        HttpUtil.get(getQuestionDetailUrl + questionId, new HttpUtil.HttpResponseCallBack() { //获得所有问题id
            @Override
            public void onSuccess(int code, String result) {
                switch (code) {
                    case 200:
                        JSONObject object = null;
                        try {
                            object = new JSONObject(result);

                            question_username.setText(object.optString("userName"));
                            question_title.setText(object.optString("title"));
                            question_content.setText(object.optString("content"));
                            question_createDate.setText(object.optString("createDate"));
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

    private List<Map<String, String>> getAnswerList() {
        list.clear(); //清空Adapter中的数据
        count = 0;

        HttpUtil.get(getAllAnswerIdUrl + questionId, new HttpUtil.HttpResponseCallBack() { //获得所有问题id
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
                                HttpUtil.get(getAnswerByIdUrl + data[i], new HttpUtil.HttpResponseCallBack() { //根据id获取问题
                                    @Override
                                    public void onSuccess(int code, String result) {
                                        switch (code) {
                                            case 200:
                                                JSONObject object = null;
                                                try {
                                                    object = new JSONObject(result);

                                                    Map<String, String> map = new HashMap<String, String>();
                                                    map.put("answerId", object.optString("answerId"));
                                                    map.put("userId", object.optString("userId"));
                                                    map.put("userName", object.optString("userName"));
                                                    map.put("content", object.optString("content"));
                                                    map.put("createDate", object.optString("createDate"));
                                                    map.put("good", object.optString("good"));
                                                    map.put("bad", object.optString("bad"));
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

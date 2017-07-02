package com.sysu.yizhu.Activity.Business.Sos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.sysu.yizhu.R;
import com.sysu.yizhu.Util.AppManager;
import com.sysu.yizhu.Util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

/**
 * Created by QianZixuan on 2017/7/1.
 */
public class SosResponseActivity extends AppCompatActivity {
    private static final String getAllValidSosIdUrl = "http://112.74.165.37:8080/sos/allValidId";
    private static final String getSosByIdUrl = "http://112.74.165.37:8080/sos/get/"; // +sosId

    private SimpleAdapter adapter;

    private List<Map<String, String>> list = new ArrayList<Map<String, String>>();

    private PtrFrameLayout mPtrFrame;
    private ListView sos_response_push_list;

    private int count;
    private String[] data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sos_response_layout);

        mPtrFrame = (PtrFrameLayout) findViewById(R.id.sos_response_ptr_frame);
        sos_response_push_list = (ListView) findViewById(R.id.sos_response_push_list);

        final PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(getApplicationContext());
        header.setPadding(0, PtrLocalDisplay.dp2px(15), 0, 0);

        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);

        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getData();
                Toast.makeText(SosResponseActivity.this, "下拉刷新", Toast.LENGTH_SHORT).show();
                mPtrFrame.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });

        adapter = new SimpleAdapter(SosResponseActivity.this, list, R.layout.sos_response_push_list_item_layout,
                new String[] {"createTime", "finished"},
                new int[] {R.id.sos_response_push_list_createTime, R.id.sos_response_push_list_finished});
        sos_response_push_list.setAdapter(adapter);

        sos_response_push_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(SosResponseActivity.this, SosResponseDetailActivity.class);
                intent.putExtra("sosId", list.get(position).get("sosId")); //传送questionId
                SosResponseActivity.this.startActivity(intent);
            }
        });

        getData();

        AppManager.getAppManager().addActivity(SosResponseActivity.this);
    }

    private List<Map<String, String>> getData() {
        list.clear(); //清空Adapter中的数据
        count = 0;

        HttpUtil.get(getAllValidSosIdUrl, new HttpUtil.HttpResponseCallBack() { //获得所有id
            @Override
            public void onSuccess(int code, String result) {
                switch (code) {
                    case 200:
                        JSONObject object = null;
                        try {
                            object = new JSONObject(result);
                            count = object.optInt("count");
                            JSONArray jsonArray = object.optJSONArray("data");
                            List<String> arrayList = new ArrayList<String>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                arrayList.add( jsonArray.getString(i) );
                            }
                            data = arrayList.toArray(new String[arrayList.size()]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            for (int i = 0; i < count; i++) {
                                HttpUtil.get(getSosByIdUrl + data[i], new HttpUtil.HttpResponseCallBack() { //根据id获取
                                    @Override
                                    public void onSuccess(int code, String result) {
                                        switch (code) {
                                            case 200:
                                                JSONObject object = null;
                                                try {
                                                    object = new JSONObject(result);

                                                    Map<String, String> map = new HashMap<String, String>();
                                                    map.put("sosId", object.optString("sosId"));
                                                    map.put("latitude", object.optString("latitude"));
                                                    map.put("longitude", object.optString("longitude"));
                                                    map.put("createTime", object.optString("createTime"));
                                                    map.put("finished", object.optString("finished"));
                                                    map.put("pushUserId", object.optString("pushUserId"));
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
        adapter.notifyDataSetChanged();
        return list;
    }
}

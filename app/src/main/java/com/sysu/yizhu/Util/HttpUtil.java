package com.sysu.yizhu.Util;

import com.sysu.yizhu.UserData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

/**
 * Created by QianZixuan on 2017/5/27.
 * Http请求的工具类
 */
public class HttpUtil {
    private HttpUtil() {
    }

    /**
     * get方法请求数据
     *
     * @param url      请求地址
     * @param callBack 回调接口
     */
    public static void get(final String url, final HttpResponseCallBack callBack) {
        if (callBack != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final StringBuilder sb = new StringBuilder(64);
                        //构建新连接
                        URL httpUrl = new URL(url);
                        HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                        conn.setConnectTimeout(3000);
                        conn.setReadTimeout(3000);
                        conn.setRequestMethod("GET");

                        final int code = conn.getResponseCode();

                        if (code == 200) {//成功接受返回数据
                            BufferedReader br = new BufferedReader(
                                    new InputStreamReader(conn.getInputStream(), "UTF-8"));

                            sb.setLength(0);
                            String strTemp;
                            while ((strTemp = br.readLine()) != null) {
                                sb.append(strTemp).append('\n');
                            }
                        }

                        conn.disconnect();

                        AsyncRun.run(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onSuccess(code, sb.toString());
                            }
                        });

                    } catch (final IOException e) {
                        AsyncRun.run(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onFailure("失败信息：", e);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    /**
     * post方法请求数据
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param callBack 回调地址
     */
    public static void post(final String url, final Map<String, String> params, final HttpResponseCallBack callBack) {
        if (callBack != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final StringBuilder sb = new StringBuilder(64);

                        URL httpUrl = new URL(url);
                        HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                        conn.setConnectTimeout(3000);
                        conn.setReadTimeout(3000);
                        conn.setRequestMethod("POST");
                        // 设置请求的头
                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                        conn.addRequestProperty("Cookie", UserData.getInstance().getJsessionId());//加入session id

                        conn.setDoOutput(true);
                        conn.setDoInput(true);

                        //拼装请求参数列表
                        if (params != null) {
                            for (Map.Entry<String, String> entry : params.entrySet()) {
                                sb.append(entry.getKey());
                                sb.append("=");
                                sb.append(entry.getValue());
                                sb.append("&");
                            }
                            if (sb.length() != 0)
                                sb.deleteCharAt(sb.length() - 1);

                            conn.setRequestProperty("Content_length", String.valueOf(sb.toString().getBytes().length));

                            conn.getOutputStream().write(sb.toString().getBytes());
                        }

                        final int code = conn.getResponseCode(); // 获取服务器响应
                        if (code == 200) { //成功则接受返回数据
                            BufferedReader br = new BufferedReader(
                                    new InputStreamReader(conn.getInputStream(), "UTF-8"));

                            sb.setLength(0);
                            String strTemp;
                            while ((strTemp = br.readLine()) != null) {
                                sb.append(strTemp);
                            }

                            final String cookieval = conn.getHeaderField("Set-Cookie"); //保存sessionid
                            if (cookieval != null) {
                                UserData.getInstance().setJsessionId(cookieval);
                            }
                        }

                        conn.disconnect();

                        AsyncRun.run(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onSuccess(code, sb.toString());
                            }
                        });

                    } catch (final IOException e) {
                        AsyncRun.run(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onFailure("失败信息：", e);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    /**
     * 获取回调
     */
    public interface HttpResponseCallBack {
        void onSuccess(int code, String result);

        void onFailure(String result, Exception e);
    }
}
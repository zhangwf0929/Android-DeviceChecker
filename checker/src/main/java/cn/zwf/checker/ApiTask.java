package cn.zwf.checker;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Api请求
 * Created by ZhangWF(zhangwf0929@gmail.com) on 16/9/6.
 */
public class ApiTask extends AsyncTask<String, Integer, Map<String, ApiTask.Response>> {

    public interface Callback {
        void onFinish(Map<String, ApiTask.Response> result);
    }

    private Callback mCallback;

    public ApiTask(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    protected Map<String, ApiTask.Response> doInBackground(String... params) {
        if (params != null) {
            Map<String, ApiTask.Response> result = new HashMap<>();
            for (String url : params) {
                // 逐个解析域名
                ApiTask.Response response = doGet(url);
                result.put(url, response);
            }
            return result;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Map<String, ApiTask.Response> addresses) {
        super.onPostExecute(addresses);
        if (mCallback != null) {
            mCallback.onFinish(addresses);
        }
    }

    private ApiTask.Response doGet(String url) {
        ApiTask.Response result = null;
        try {
            // 根据地址创建URL对象(网络访问的url)，打开网络链接
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");// 设置请求的方式
            urlConnection.setReadTimeout(15000);// 设置超时的时间
            urlConnection.setConnectTimeout(15000);// 设置链接超时的时间

            result = new ApiTask.Response();
            result.code = urlConnection.getResponseCode();
            // 获取响应的状态码 404 200 505 302
            if (result.code >= HttpURLConnection.HTTP_OK && result.code < HttpURLConnection.HTTP_MULT_CHOICE) {
                // 返回字符串
                result.raw = getStringByInputStream(urlConnection.getInputStream());
            } else {
                result.raw = getStringByInputStream(urlConnection.getErrorStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getStringByInputStream(InputStream inputStream) {
        String result = null;
        // 创建字节输出流对象
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 定义读取的长度
        int len = 0;
        // 定义缓冲区
        byte buffer[] = new byte[1024];
        // 按照缓冲区的大小，循环读取
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                // 根据读取的长度写入到os对象中
                os.write(buffer, 0, len);
            }
            result = new String(os.toByteArray());
            // 释放资源
            inputStream.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 返回字符串
        return result;
    }

    public static class Response {
        public int code;
        public String raw;
    }
}

package cn.zwf.checker;

import android.os.AsyncTask;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * 本地DNS查询
 * Created by ZhangWF(zhangwf0929@gmail.com) on 16/9/6.
 */
public class LocalDNSTask extends AsyncTask<String, Integer, Map<String, String>> {

    public interface Callback {
        void onFinish(Map<String, String> addresses);
    }

    private Callback mCallback;

    public LocalDNSTask(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    protected Map<String, String> doInBackground(String... params) {
        if (params != null) {
            Map<String, String> result = new HashMap<>();
            for (String domain : params) {
                // 逐个解析域名
                try {
                    InetAddress address = InetAddress.getByName(domain);
                    result.put(domain, address.getHostAddress());
                } catch (UnknownHostException e) {
                    result.put(domain, null);
                    e.printStackTrace();
                }
            }
            return result;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Map<String, String> addresses) {
        super.onPostExecute(addresses);
        if (mCallback != null) {
            mCallback.onFinish(addresses);
        }
    }
}

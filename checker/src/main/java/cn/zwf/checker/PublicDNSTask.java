package cn.zwf.checker;

import android.os.AsyncTask;

import org.xbill.DNS.Cache;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公共DNS查询
 * 参数为dns和域名列表，结果为dns与解析结果(域名与ip的map)的map
 * Created by ZhangWF(zhangwf0929@gmail.com) on 16/9/6.
 */
public class PublicDNSTask extends AsyncTask<PublicDNSTask.PublicDNS, Integer, Map<String, Map<String, String>>> {

    public interface Callback {
        void onFinish(Map<String, Map<String, String>> addresses);
    }

    private Callback mCallback;

    public PublicDNSTask(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    protected Map<String, Map<String, String>> doInBackground(PublicDNS... params) {
        if (params != null) {
            Map<String, Map<String, String>> result = new HashMap<>();
            for (PublicDNS publicDNS : params) {
                if (publicDNS != null) {
                    // 遍历每台dns服务器
                    Map<String, String> addresses = new HashMap<>();
                    if (!Utils.isEmpty(publicDNS.domains)) {
                        for (String domain : publicDNS.domains) {
                            // 指定的dns服务器，逐个解析域名
                            String ip = lookup(publicDNS.dns, domain);
                            addresses.put(domain, ip);
                        }
                    }
                    result.put(publicDNS.dns, addresses);
                }
            }
            return result;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Map<String, Map<String, String>> addresses) {
        super.onPostExecute(addresses);
        if (mCallback != null) {
            mCallback.onFinish(addresses);
        }
    }

    protected String lookup(String dns, String domain) {
        String address = null;
        Cache cache = new Cache();

        try {
            SimpleResolver res = new SimpleResolver(dns);
            res.setTCP(false);

            Lookup lu = new Lookup(domain);
            lu.setCache(cache);
            lu.setResolver(res);
            Record[] records = lu.run();
            if (!Utils.isEmpty(records)) {
                for (Record record : records) {
                    if (record.getType() == 1) {
                        address = record.rdataToString();
                        break;
                    }
                }
            }
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (TextParseException e2) {
            e2.printStackTrace();
        } finally {
            cache.clearCache();
        }
        return address;
    }

    public static class PublicDNS {
        public String dns;
        public List<String> domains;
    }
}

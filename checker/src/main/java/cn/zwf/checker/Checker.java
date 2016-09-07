package cn.zwf.checker;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 要检测的数据内容
 * Created by ZhangWF(zhangwf0929@gmail.com) on 16/9/6.
 */
public class Checker {

    List<String> mDNSes;
    List<String> mDomains;
    List<String> mApiUrls;
    List<String> mImageUrls;
    Map<String, String> mFileUrls;
    private Context mContext;

    private static Checker ourInstance = new Checker();

    static Checker getInstance() {
        return ourInstance;
    }

    private Checker() {
    }

    public static Checker with(Context context) {
        Checker checker = Checker.getInstance();
        checker.mContext = context;
        return checker;
    }

    public Checker setDNS(String... dnses) {
        if (mDNSes == null) {
            mDNSes = new ArrayList<>();
        } else {
            mDNSes.clear();
        }
        if (dnses != null) {
            Collections.addAll(mDNSes, dnses);
        }
        return this;
    }

    public Checker setDomain(String... domains) {
        if (mDomains == null) {
            mDomains = new ArrayList<>();
        } else {
            mDomains.clear();
        }
        if (domains != null) {
            Collections.addAll(mDomains, domains);
        }
        return this;
    }

    public Checker setApiUrl(String... urls) {
        if (mApiUrls == null) {
            mApiUrls = new ArrayList<>();
        } else {
            mApiUrls.clear();
        }
        if (urls != null) {
            Collections.addAll(mApiUrls, urls);
        }
        return this;
    }

    public Checker setImageUrl(String... urls) {
        if (mImageUrls == null) {
            mImageUrls = new ArrayList<>();
        } else {
            mImageUrls.clear();
        }
        if (urls != null) {
            Collections.addAll(mImageUrls, urls);
        }
        return this;
    }

    public final Checker setFileUrl(Map<String, String> urlMd5Map) {
        mFileUrls = urlMd5Map;
        return this;
    }

    public void startCheck() {
        Intent intent = new Intent(mContext, CheckerActivity.class);
        mContext.startActivity(intent);
    }
}

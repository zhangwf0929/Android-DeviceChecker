package cn.zwf.checker;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

/**
 * 检测页面
 * Created by ZhangWF(zhangwf0929@gmail.com) on 16/9/6.
 */
public class CheckerActivity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checker);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvResult = (TextView) findViewById(R.id.tv_result);

        findViewById(R.id.btn_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvResult.setText(null);

                addLog(Utils.getModel(CheckerActivity.this));
                addLog(Utils.getAndroidInfo(CheckerActivity.this));
                addLog(Utils.getRomInfo(CheckerActivity.this));
                addLog(Utils.getNetworkType(CheckerActivity.this));
                addLog(Utils.getAppInfo(CheckerActivity.this));

                localDNS();
                publicDNS();
                apiCheck();
                downloadCheck();
            }
        });

        findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyText(tvResult.getText());
                showToast(R.string.copy_successful);
            }
        });
    }

    private void localDNS() {
        final List<String> domains = Checker.getInstance().mDomains;
        if (!Utils.isEmpty(domains)) {
            LocalDNSTask task = new LocalDNSTask(new LocalDNSTask.Callback() {
                @Override
                public void onFinish(Map<String, String> addresses) {
                    if (!Utils.isEmpty(addresses)) {
                        for (String domain : domains) {
                            String address = addresses.get(domain);
                            if (TextUtils.isEmpty(address)) {
                                address = getString(R.string.dns_fail);
                            }
                            addLog(getString(R.string.local_dns) + "\n" + domain + "\n-->\n" + address);
                        }
                    }
                }
            });
            task.execute(domains.toArray(new String[domains.size()]));
        }
    }

    private void publicDNS() {
        final List<String> dnses = Checker.getInstance().mDNSes;
        final List<String> domains = Checker.getInstance().mDomains;
        if (!Utils.isEmpty(dnses) && !Utils.isEmpty(domains)) {
            PublicDNSTask task = new PublicDNSTask(new PublicDNSTask.Callback() {
                @Override
                public void onFinish(Map<String, Map<String, String>> addresses) {
                    if (!Utils.isEmpty(addresses)) {
                        for (String dns : dnses) {
                            // 遍历dns服务器
                            Map<String, String> map = addresses.get(dns);
                            if (map != null) {
                                for (String domain : domains) {
                                    // 指定dns服务器逐个解析域名
                                    String address = map.get(domain);
                                    if (TextUtils.isEmpty(address)) {
                                        address = getString(R.string.dns_fail);
                                    }
                                    addLog(getString(R.string.format_public_dns, dns) + "\n" + domain + "\n-->\n" + address);
                                }
                            }
                        }
                    }
                }
            });
            PublicDNSTask.PublicDNS[] publicDNSes = new PublicDNSTask.PublicDNS[dnses.size()];
            for (int i = 0; i < dnses.size(); i++) {
                PublicDNSTask.PublicDNS publicDNS = new PublicDNSTask.PublicDNS();
                publicDNS.dns = dnses.get(i);
                publicDNS.domains = domains;
                publicDNSes[i] = publicDNS;
            }
            task.execute(publicDNSes);
        }
    }

    private void apiCheck() {
        final List<String> apiUrls = Checker.getInstance().mApiUrls;
        if (!Utils.isEmpty(apiUrls)) {
            ApiTask task = new ApiTask(new ApiTask.Callback() {
                @Override
                public void onFinish(Map<String, ApiTask.Response> addresses) {
                    if (!Utils.isEmpty(addresses)) {
                        for (String url : apiUrls) {
                            ApiTask.Response response = addresses.get(url);
                            String result;
                            if (response == null) {
                                result = getString(R.string.api_request_fail);
                            } else {
                                result = "http code=" + response.code;
                                if (!TextUtils.isEmpty(response.raw)) {
                                    result = result + "\n" + response.raw;
                                }
                            }
                            addLog(getString(R.string.api_request) + "\n" + url + "\n-->\n" + result);
                        }
                    }
                }
            });
            task.execute(apiUrls.toArray(new String[apiUrls.size()]));
        }
    }

    private void downloadCheck() {
        final Map<String, String> fileUrls = Checker.getInstance().mFileUrls;
        if (!Utils.isEmpty(fileUrls)) {
            DownloadTask task = new DownloadTask(this, new DownloadTask.Callback() {
                @Override
                public void onFinish(Map<String, DownloadTask.DownloadFile> downloadFiles) {
                    if (!Utils.isEmpty(downloadFiles)) {
                        for (String url : fileUrls.keySet()) {
                            String md5 = fileUrls.get(url);
                            DownloadTask.DownloadFile downloadFile = downloadFiles.get(url);
                            String result;
                            if (downloadFile == null) {
                                result = getString(R.string.file_download_fail);
                            } else {
                                result = getString(R.string.file_download_success) + "\n" +
                                        getString(R.string.file_path) + downloadFile.path + "\n" +
                                        getString(R.string.file_md5) + downloadFile.md5;

                                // 如果有提供md5
                                if (!TextUtils.isEmpty(md5)) {
                                    // 更新url展示
                                    url = url + "\n" + getString(R.string.file_md5) + md5;
                                    // 检查是否一致
                                    result = result + "\n" + (md5.equals(downloadFile.md5) ?
                                            getString(R.string.md5_match) :
                                            getString(R.string.md5_not_match));
                                }
                            }
                            addLog(getString(R.string.file_download) + "\n" + url + "\n-->\n" + result);
                        }
                    }
                }
            });
            task.execute(fileUrls.keySet().toArray(new String[fileUrls.keySet().size()]));
        }
    }

    private void addLog(CharSequence s) {
        SpannableString sp = new SpannableString(s);
        int prefix = s.toString().indexOf(":") + 1;
        sp.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue)),
                0, prefix, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        tvResult.append(sp);
        tvResult.append("\n");
    }

    private void copyText(CharSequence s) {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", s);
        cm.setPrimaryClip(clipData);
    }

    private void showToast(@StringRes int resId) {
        showToast(getString(resId));
    }

    private void showToast(CharSequence s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}

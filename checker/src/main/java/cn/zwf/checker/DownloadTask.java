package cn.zwf.checker;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 下载任务
 * 参数是url和md5的map，结果为url和下载结果的map
 * Created by ZhangWF(zhangwf0929@gmail.com) on 16/9/7.
 */
public class DownloadTask extends AsyncTask<String, Integer, Map<String, DownloadTask.DownloadFile>> {

    private static final int BUFFER_SIZE = 10 * 1024; // 8k ~ 32K

    public interface Callback {
        void onFinish(Map<String, DownloadTask.DownloadFile> result);
    }

    private Context mContext;
    private Callback mCallback;

    public DownloadTask(Context context, Callback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    @Override
    protected Map<String, DownloadTask.DownloadFile> doInBackground(String... params) {
        if (params != null) {
            Map<String, DownloadTask.DownloadFile> result = new HashMap<>();
            for (String url : params) {
                // 逐个下载
                DownloadTask.DownloadFile downloadFile = download(url);
                result.put(url, downloadFile);
            }
            return result;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Map<String, DownloadTask.DownloadFile> addresses) {
        super.onPostExecute(addresses);
        if (mCallback != null) {
            mCallback.onFinish(addresses);
        }
    }

    private DownloadTask.DownloadFile download(String url) {
        DownloadTask.DownloadFile result = new DownloadTask.DownloadFile();
        InputStream in = null;
        FileOutputStream out = null;
        try {
            File file = initFile(url);
            out = new FileOutputStream(file);
            Log.d(Utils.TAG, "download:" + url + ";filepath:" + file.getAbsolutePath());

            HttpURLConnection urlConnection = getHttpURLConnection(url);
            urlConnection.connect();
            in = urlConnection.getInputStream();

            long byteTotal = urlConnection.getContentLength();
            long byteSum = 0;
            int byteRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            int oldProgress = 0;

            while ((byteRead = in.read(buffer)) != -1) {
                byteSum += byteRead;
                out.write(buffer, 0, byteRead);

//                int progress = (int) (byteSum * 100L / byteTotal);
//                // 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
//                if (progress != oldProgress) {
//                    updateProgress(progress);
//                }
//                oldProgress = progress;
            }

            // 下载完成
            result.url = url;
            result.path = file.getAbsolutePath();
            result.md5 = Utils.getMd5(result.path);
        } catch (IOException e) {
            e.printStackTrace();
            result.error = e.toString();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private File initFile(String url) {
        File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), String.valueOf(url.hashCode()));

        // 如果文件已存在，先删除再下载，保证多次下载都只有一个文件
        if (file.exists()) {
            file.delete();
        }
        return file;
    }

    private HttpURLConnection getHttpURLConnection(String url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setDoOutput(false);
        urlConnection.setConnectTimeout(10 * 1000);
        urlConnection.setReadTimeout(10 * 1000);
        urlConnection.setRequestProperty("Connection", "Keep-Alive");
        urlConnection.setRequestProperty("Charset", "UTF-8");
        urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

        return urlConnection;
    }

    public static class DownloadFile {
        public String url;
        public String md5;
        public String path;
        public String error;
    }
}

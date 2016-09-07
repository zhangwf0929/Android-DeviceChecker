package cn.zwf.checker.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import cn.zwf.checker.Checker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Map<String, String> fileMd5Map = new HashMap<>();
        fileMd5Map.put("http://api.newgamepad.com/v2/clients/8547.47/Gamekeyboard_ha.apk",
                "c208a1d7e791132a26f612e205a55da9");

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Checker.with(MainActivity.this)
                        .setDNS("119.29.29.29", "223.5.5.5", "114.114.114.114")
                        .setDomain("api.newgamepad.com", "oss.newgamepad.com")
                        .setApiUrl("https://api.newgamepad.com/v2/games/exterior_types")
                        .setFileUrl(fileMd5Map)
                        .setImageUrl("http://oss.newgamepad.com/ng-images/c0/92/c092d127f4ef93771bae35893f7b7522.png")
                        .startCheck();
            }
        });
    }
}

package cn.zwf.checker.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cn.zwf.checker.Checker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Checker.with(MainActivity.this)
                        .setDNS("119.29.29.29", "223.5.5.5", "114.114.114.114")
                        .setDomain("api.newgamepad.com", "oss.newgamepad.com")
                        .setApiUrl("https://api.newgamepad.com/v2/games/exterior_types")
                        .setImageUrl()
                        .setFileUrl()
                        .startCheck();
            }
        });
    }
}

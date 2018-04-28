package com.example.lvweihao.cityserve;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by lv.weihao on 2018/4/24.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText mNumber = (EditText) findViewById(R.id.edit);
        final Button mButton = (Button) findViewById(R.id.btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = 0;
                String str = mNumber.getText().toString();
                if (!"".equals(str)) {
                    number = Integer.parseInt(str);
                } else {
                    number = 0;
                }
                Intent intent = new Intent(MainActivity.this, OpenActivity.class);
                intent.putExtra("number", number);
                startActivity(intent);
            }
        });
    }
}

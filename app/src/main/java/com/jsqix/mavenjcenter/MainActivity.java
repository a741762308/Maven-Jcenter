package com.jsqix.mavenjcenter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jsqix.utils.StringUtils;
import com.jsqix.utils.Utils;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button_submit);
        editText = (EditText) findViewById(R.id.edit_txt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.notPhone(editText.getText().toString().trim())) {
                    Utils.makeToast(MainActivity.this, "手机号不正确");
                } else {
                    Utils.makeToast(MainActivity.this, "手机号正确");
                }
            }
        });
    }
}

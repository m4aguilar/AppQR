package com.example.mal90.appqr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by mal90 on 23/06/2017.
 */

public class QRActivity extends Activity {

    Button button;
    EditText editText;
    String key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                key = editText.getText().toString();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        });

    }
}

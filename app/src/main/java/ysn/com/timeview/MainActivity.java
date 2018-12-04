package ysn.com.timeview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import ysn.com.timeview.bean.Data;
import ysn.com.timeview.bean.Time;
import ysn.com.timeview.util.ValidatorUtil;
import ysn.com.timeview.view.TimeView;

public class MainActivity extends AppCompatActivity {

    private EditText contentEditText, lastCloseEditText;
    private TimeView timeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentEditText = findViewById(R.id.main_activity_content);
        lastCloseEditText = findViewById(R.id.main_activity_last_close);
        timeView = findViewById(R.id.main_activity_time_view);
    }

    public void confirm(View view) {
        if (ValidatorUtil.isBlank(contentEditText.getText().toString().trim()) ||
                ValidatorUtil.isBlank(lastCloseEditText.getText().toString().trim())) {
            Toast.makeText(this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Data data = new Gson().fromJson(contentEditText.getText().toString().trim(), Data.class);
            Time time = new Time();
            time.setCode(data.getCode())
                    .setDate(data.getDate())
                    .setSettlement(Float.valueOf(lastCloseEditText.getText().toString().trim()))
                    .setDataBeanStringList(data.getData());
            timeView.setDate(time);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "请检查数据格式是否正确", Toast.LENGTH_SHORT).show();
        }
    }
}

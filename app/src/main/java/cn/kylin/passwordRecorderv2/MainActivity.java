package cn.kylin.passwordRecorderv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import cn.kylin.passwordRecorderv2.db.DBMaster;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBMaster master=new DBMaster(getApplicationContext());
        master.openDataBase();
        Button addInfo=findViewById(R.id.BT_AddInfo_Main);
        addInfo.setOnClickListener(click->{
            Intent intent=new Intent(this,AddInActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
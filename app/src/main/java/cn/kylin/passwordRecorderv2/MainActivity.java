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
        Button addInfo=findViewById(R.id.BT_AddInfo_Main),viewEditInfo=findViewById(R.id.BT_View_Mod_Info),manageInfo=findViewById(R.id.BT_Manage_Main),searchInfo=findViewById(R.id.BT_Search_Main);
        addInfo.setOnClickListener(click->{
            Intent intent=new Intent(this,AddInActivity.class);
            startActivity(intent);
            finish();
        });
        viewEditInfo.setOnClickListener(click->{
            Intent intent=new Intent(this, ViewCopyActivity.class);
            startActivity(intent);
            finish();
        });
        manageInfo.setOnClickListener(click->{
            Intent intent=new Intent(this,ManageInfoActivity.class);
            startActivity(intent);
            finish();
        });
        searchInfo.setOnClickListener(click->{
            Intent intent=new Intent(this,SearchInfoActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
package cn.kylin.passwordRecorderv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Toast;

import cn.kylin.passwordRecorderv2.db.DBMaster;

public class MainActivity extends AppCompatActivity {

    private long lastBackTime =0;
    private long currentBackTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBMaster master=new DBMaster(getApplicationContext());
        master.openDataBase();
        Button addInfo=findViewById(R.id.BT_AddInfo_Main),viewEditInfo=findViewById(R.id.BT_View_Mod_Info),manageInfo=findViewById(R.id.BT_Manage_Main),searchInfo=findViewById(R.id.BT_Search_Main);
        Button backupRestore=findViewById(R.id.BT_BR_Main);
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
        backupRestore.setOnClickListener(click->{
            Intent intent=new Intent(this,BackupRestoreActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onKeyDown(int KC, KeyEvent KE){
        if(KC==KeyEvent.KEYCODE_BACK){
            currentBackTime=System.currentTimeMillis();
            if(currentBackTime-lastBackTime>2*1000){
                Toast.makeText(this,getResources().getString(R.string.exit_hint),Toast.LENGTH_SHORT).show();
                lastBackTime=currentBackTime;
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(KC,KE);
    }
}
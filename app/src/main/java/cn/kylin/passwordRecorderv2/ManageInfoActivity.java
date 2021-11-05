package cn.kylin.passwordRecorderv2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.kylin.passwordRecorderv2.db.DBMaster;
import cn.kylin.passwordRecorderv2.model.AccountInfoBean;

public class ManageInfoActivity extends AppCompatActivity {

    private ArrayList<AccountInfoBean> infoArrayList=new ArrayList<AccountInfoBean>();
    private infoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_info);
        ListView infoList=findViewById(R.id.LV_InfoList_ManagePage);
        DBMaster dbMaster=new DBMaster(getApplicationContext());
        dbMaster.openDataBase();
        infoArrayList=dbMaster.accountInfoDBDao.getAllData();
        if(infoArrayList.size()!=0){
            adapter=new infoAdapter();
            infoList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        infoList.setOnItemLongClickListener((p,b,pos,id)->{
            AccountInfoBean tempInfo=infoArrayList.get(pos);
            View tempView=View.inflate(ManageInfoActivity.this,R.layout.copy_list_resource,null);
            TextView usernameInfo=tempView.findViewById(R.id.TV_username_Info_CP),passwordInfo=tempView.findViewById(R.id.TV_password_Info_CP),registerInfo=tempView.findViewById(R.id.TV_register_Info_CP),dateInfo=tempView.findViewById(R.id.TV_date_info_CP);
            AlertDialog.Builder builder=new AlertDialog.Builder(ManageInfoActivity.this);
            usernameInfo.setText(getResources().getString(R.string.ViewEdit_Username)+tempInfo.getUsername());
            passwordInfo.setText(getResources().getString(R.string.ViewEdit_Password)+tempInfo.getPassword());
            registerInfo.setText(getResources().getString(R.string.ViewEdit_Register)+tempInfo.getRegister());
            dateInfo.setText(getResources().getString(R.string.ViewEdit_Date)+tempInfo.getDate());
            builder.setTitle(getResources().getString(R.string.CopyData_Title));
            builder.setView(tempView);
            builder.setPositiveButton(getResources().getString(R.string.Delete_Btn),(click,arg)->{
                AlertDialog.Builder deleteBuilder=new AlertDialog.Builder(ManageInfoActivity.this);
                deleteBuilder.setTitle(getResources().getString(R.string.Delete_Hint));
                deleteBuilder.setPositiveButton(getResources().getString(R.string.Delete_Btn),(click1,arg1)->{
                    dbMaster.accountInfoDBDao.deleteDataByID(tempInfo.getId());
                    infoArrayList=dbMaster.accountInfoDBDao.getAllData();
                    adapter=new infoAdapter();
                    infoList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }).setNegativeButton(getResources().getString(R.string.Back_Btn),(click1,arg1)->{

                }).create().show();
            }).setNegativeButton(getResources().getString(R.string.Modify_Btn),(click,arg)->{
                AlertDialog.Builder modifyBuilder=new AlertDialog.Builder(ManageInfoActivity.this);
                View tempModView=View.inflate(ManageInfoActivity.this,R.layout.modify_list_resource,null);
                EditText ET_username=tempModView.findViewById(R.id.ET_Username_Mod),ET_Password=tempModView.findViewById(R.id.ET_Password_Mod),ET_Security=tempModView.findViewById(R.id.ET_PhoneNumber_Mod);
                if(tempInfo.getSecurity().equals("empty3")){
                    ET_Security.setVisibility(View.GONE);
                }
                ET_username.setText(tempInfo.getUsername());
                ET_Password.setText(tempInfo.getPassword());
                ET_Security.setText(tempInfo.getSecurity());
                modifyBuilder.setTitle(getResources().getString(R.string.Modify_Title));
                modifyBuilder.setView(tempModView);
                modifyBuilder.setPositiveButton(getResources().getString(R.string.Modify_Btn),(click1,arg1)->{
                    if(!ET_username.getText().toString().equals("")&&!ET_Password.getText().toString().equals("")&&!ET_Security.getText().toString().equals("")){
                        tempInfo.setUsername(ET_username.getText().toString());
                        tempInfo.setPassword(ET_Password.getText().toString());
                        tempInfo.setSecurity(ET_Security.getText().toString());
                        dbMaster.accountInfoDBDao.updateDataByID(tempInfo.getId(),tempInfo);
                        infoArrayList=dbMaster.accountInfoDBDao.getAllData();
                        adapter=new infoAdapter();
                        infoList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(this,"Can not be empty",Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton(getResources().getString(R.string.Back_Btn),(click1,arg1)->{

                }).create().show();
            }).create().show();
            return true;
        });
    }

    @Override
    public boolean onKeyDown(int KC, KeyEvent KE){
        if(KC==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    protected class infoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return infoArrayList.size();
        }

        @Override
        public AccountInfoBean getItem(int position) {
            return infoArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
            View rowView;
            TextView rowMessage;
            AccountInfoBean thisRow=getItem(position);
            rowView=inflater.inflate(R.layout.show_list_resource,parent,false);
            rowMessage=rowView.findViewById(R.id.info_text_view);
            rowMessage.setText(getResources().getString(R.string.ViewEdit_Username)+thisRow.getUsername()+"\n"+getResources().getString(R.string.ViewEdit_Register)+thisRow.getRegister()+"\n"+getResources().getString(R.string.ViewEdit_Date)+thisRow.getDate());
            rowMessage.setTextColor(Color.BLACK);
            rowMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            return rowView;
        }
    }
}
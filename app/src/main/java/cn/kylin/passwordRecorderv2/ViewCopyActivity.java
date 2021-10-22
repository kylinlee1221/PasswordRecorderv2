package cn.kylin.passwordRecorderv2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.kylin.passwordRecorderv2.db.DBMaster;
import cn.kylin.passwordRecorderv2.model.AccountInfoBean;

public class ViewCopyActivity extends AppCompatActivity {

    private ArrayList<AccountInfoBean> infoArrayList=new ArrayList<AccountInfoBean>();
    private infoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_copy);
        ListView infoList=findViewById(R.id.LV_InfoList_ViewPage);
        DBMaster master=new DBMaster(getApplicationContext());
        master.openDataBase();
        infoArrayList=master.accountInfoDBDao.getAllData();
        if(infoArrayList.size()!=0){
            adapter=new infoAdapter();
            infoList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        infoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AccountInfoBean tempInfo= infoArrayList.get(position);
                View tempView=View.inflate(ViewCopyActivity.this,R.layout.copy_list_resource,null);
                TextView usernameInfo=tempView.findViewById(R.id.TV_username_Info_CP),passwordInfo=tempView.findViewById(R.id.TV_password_Info_CP),registerInfo=tempView.findViewById(R.id.TV_register_Info_CP),dateInfo=tempView.findViewById(R.id.TV_date_info_CP);
                AlertDialog.Builder builder=new AlertDialog.Builder(ViewCopyActivity.this);
                usernameInfo.setText(getResources().getString(R.string.ViewEdit_Username)+tempInfo.getUsername());
                passwordInfo.setText(getResources().getString(R.string.ViewEdit_Password)+tempInfo.getPassword());
                registerInfo.setText(getResources().getString(R.string.ViewEdit_Register)+tempInfo.getRegister());
                dateInfo.setText(getResources().getString(R.string.ViewEdit_Date)+tempInfo.getDate());
                builder.setTitle(getResources().getString(R.string.CopyData_Title));
                builder.setView(tempView);
                builder.setPositiveButton(getResources().getText(R.string.Copy_Btn),(click,arg)->{
                    ClipboardManager clipboardManager=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData=ClipData.newPlainText("infoData",usernameInfo.getText().toString()+"\n"+passwordInfo.getText().toString()+"\n"+registerInfo.getText().toString()+"\n"+dateInfo.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);
                }).setNegativeButton(getResources().getText(R.string.Back_Btn),(click,arg)->{

                }).create().show();
                return true;
            }
        });
    }

    protected class infoAdapter extends BaseAdapter{

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
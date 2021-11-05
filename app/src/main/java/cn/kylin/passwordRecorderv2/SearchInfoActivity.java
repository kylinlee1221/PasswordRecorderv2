package cn.kylin.passwordRecorderv2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

import cn.kylin.passwordRecorderv2.db.DBMaster;
import cn.kylin.passwordRecorderv2.model.AccountInfoBean;

public class SearchInfoActivity extends AppCompatActivity {

    private ArrayList<AccountInfoBean> infoArrayList=new ArrayList<AccountInfoBean>();
    private infoAdapter adapter;
    private Calendar cal;
    private int year,day,month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_info);
        getDate();
        Spinner search_category=findViewById(R.id.SP_SearchCategory),search_website=findViewById(R.id.SP_WebPlace_Search);
        String[] searchBy=getResources().getStringArray(R.array.search_category).clone();
        final String[] search_selected=new String[1];
        String[] website=getResources().getStringArray(R.array.register_Place).clone();
        final String[] website_selected=new String[1];
        Button DatePickerBtn=findViewById(R.id.BT_DatePicker);
        EditText Search_Detail=findViewById(R.id.ET_SearchInfo_Detail);
        search_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==3){
                    search_selected[0]=searchBy[position];
                    Log.e("Search Cate",search_selected[0]);
                    DatePickerBtn.setVisibility(View.VISIBLE);
                    Search_Detail.setVisibility(View.GONE);
                    search_website.setVisibility(View.GONE);
                    DatePickerBtn.setOnClickListener(click->{
                        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                DatePickerBtn.setText(year+"-"+(++month)+"-"+dayOfMonth);
                            }

                        };
                        DatePickerDialog dialog=new DatePickerDialog(SearchInfoActivity.this,0,listener,year,month,day);
                        dialog.show();
                    });
                }else if(position==1){
                    Search_Detail.setVisibility(View.GONE);
                    DatePickerBtn.setVisibility(View.GONE);
                    search_website.setVisibility(View.VISIBLE);
                    search_selected[0]=searchBy[position];
                    search_website.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            website_selected[0]=website[position];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }else{
                    Search_Detail.setVisibility(View.VISIBLE);
                    DatePickerBtn.setVisibility(View.GONE);
                    search_website.setVisibility(View.GONE);
                    search_selected[0]=searchBy[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //EditText search_Details=findViewById(R.id.ET_SearchInfo_Detail)
        Button search_Btn=findViewById(R.id.BT_SearchInfo);
        DBMaster master=new DBMaster(getApplicationContext());
        master.openDataBase();
        AtomicReference<ArrayList<AccountInfoBean>> resultList= new AtomicReference<>(new ArrayList<AccountInfoBean>());
        ListView resultView=findViewById(R.id.LV_SearchResult);
        search_Btn.setOnClickListener(click->{
            if(search_selected[0].equals("add in date")){
                String searchEntered=DatePickerBtn.getText().toString();
                resultList.set(master.accountInfoDBDao.getByDate(searchEntered));
                infoArrayList=resultList.get();
                if(infoArrayList.size()!=0){
                    adapter=new infoAdapter();
                    resultView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(this,"No data found",Toast.LENGTH_LONG).show();
                }
            }else if(search_selected[0].equals("username")){
                //EditText ET_details=findViewById(R.id.ET)
                if(Search_Detail.getText().toString().equals("")){
                    Toast.makeText(this,"Please enter details",Toast.LENGTH_LONG).show();
                }else{
                    resultList.set(master.accountInfoDBDao.getByUsername(Search_Detail.getText().toString()));
                    infoArrayList=resultList.get();
                    if(infoArrayList.size()!=0){
                        adapter=new infoAdapter();
                        resultView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(this,"No data found",Toast.LENGTH_LONG).show();
                    }
                }
            }else if(search_selected[0].equals("phone number")){
                if(Search_Detail.getText().toString().equals("")){
                    Toast.makeText(this,"Please enter details",Toast.LENGTH_LONG).show();
                }else{
                    resultList.set(master.accountInfoDBDao.getByPhoneNumber(Search_Detail.getText().toString()));
                    infoArrayList=resultList.get();
                    if(infoArrayList.size()!=0){
                        adapter=new infoAdapter();
                        resultView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(this,"No data found",Toast.LENGTH_LONG).show();
                    }
                }
            }else if(search_selected[0].equals("register place")) {
                if (website_selected[0].equals("")) {
                    Toast.makeText(this, "Please enter details", Toast.LENGTH_LONG).show();
                } else {
                    resultList.set(master.accountInfoDBDao.getByWebsite(website_selected[0]));
                    infoArrayList = resultList.get();
                    if (infoArrayList.size() != 0) {
                        adapter = new infoAdapter();
                        resultView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "No data found", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        resultView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AccountInfoBean tempInfo= infoArrayList.get(position);
                View tempView=View.inflate(SearchInfoActivity.this,R.layout.copy_list_resource,null);
                TextView usernameInfo=tempView.findViewById(R.id.TV_username_Info_CP),passwordInfo=tempView.findViewById(R.id.TV_password_Info_CP),registerInfo=tempView.findViewById(R.id.TV_register_Info_CP),dateInfo=tempView.findViewById(R.id.TV_date_info_CP);
                AlertDialog.Builder builder=new AlertDialog.Builder(SearchInfoActivity.this);
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

    private void getDate(){
        cal=Calendar.getInstance();
        year=cal.get(Calendar.YEAR);
        month=cal.get(Calendar.MONTH);
        day=cal.get(Calendar.DAY_OF_MONTH);
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
    @Override
    public boolean onKeyDown(int KC, KeyEvent KE){
        if(KC==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
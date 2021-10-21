package cn.kylin.passwordRecorderv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

import cn.kylin.passwordRecorderv2.db.DBMaster;

public class AddInActivity extends AppCompatActivity {
    private Calendar cal;
    private int year,day,month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_in);
        getDate();
        EditText ET_username,ET_Password,ET_RegisterPlace,ET_phone,ET_other;
        Spinner SP_website;
        Button BT_Addin;
        ET_username=findViewById(R.id.ET_Username_AddInPage);
        ET_Password=findViewById(R.id.ET_Password_AddInPage);
        ET_RegisterPlace=findViewById(R.id.ET_RegisterPlace_AddInPage);
        ET_phone=findViewById(R.id.ET_PhoneNumber_AddInPage);
        ET_other=findViewById(R.id.ET_OtherInfo_AddInPage);
        SP_website=findViewById(R.id.SP_RegisterPlace_Detail);
        BT_Addin=findViewById(R.id.BT_AddInfo_AddInPage);
        String[] websiteList=getResources().getStringArray(R.array.register_Place).clone();
        final String[] websiteSelected=new String[1];
        SP_website.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                websiteSelected[0]=websiteList[position];
                if(websiteList[position].equals("other")){
                    ET_RegisterPlace.setVisibility(View.VISIBLE);
                    ET_RegisterPlace.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            websiteSelected[0]=websiteList[position]+" : "+ET_RegisterPlace.getText().toString();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Drawable[] drawables=ET_Password.getCompoundDrawables();
        final int eyeWidth=drawables[2].getBounds().width();
        final Drawable EyeOpen=getResources().getDrawable(R.drawable.eye_open);
        final boolean[] isHide={true};
        EyeOpen.setBounds(drawables[2].getBounds());
        ET_Password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP){
                    float et_pwdMinX = v.getWidth() - eyeWidth - ET_Password.getPaddingRight();
                    float et_pwdMaxX = v.getWidth();
                    float et_pwdMinY = 0;
                    float et_pwdMaxY = v.getHeight();
                    float x = event.getX();
                    float y = event.getY();

                    if (x < et_pwdMaxX && x > et_pwdMinX && y > et_pwdMinY && y < et_pwdMaxY) {
                        // 点击了眼睛图标的位置
                        isHide[0] = !isHide[0];
                        if (isHide[0]) {
                            ET_Password.setCompoundDrawables(null,
                                    null,
                                    drawables[2], null);

                            ET_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        } else {
                            ET_Password.setCompoundDrawables(null, null,
                                    EyeOpen,
                                    null);
                            ET_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        }
                    }
                }
                return false;

            }
        });
        DBMaster master=new DBMaster(getApplicationContext());
        master.openDataBase();
        BT_Addin.setOnClickListener(click->{
            String PhoneEntered,commentsEntered;
            if(ET_username.getText().toString().equals("")||ET_Password.getText().toString().equals("")){
                Toast.makeText(this,getResources().getString(R.string.addInPage_Hint),Toast.LENGTH_LONG).show();
            }else{
                if(ET_phone.getText().toString().equals("")){
                    PhoneEntered="empty3";
                }else{
                    PhoneEntered=ET_phone.getText().toString();
                }
                if(ET_other.getText().toString().equals("")){
                    commentsEntered="empty3";
                }else{
                    commentsEntered=ET_other.getText().toString();
                }
                String dateNow=String.valueOf(year+"-"+(++month)+"-"+day);
                long resultId=master.accountInfoDBDao.insertData(ET_username.getText().toString(),ET_Password.getText().toString(),PhoneEntered,websiteSelected[0],commentsEntered,dateNow);
                if(resultId==-77777){
                    Toast.makeText(this,getResources().getString(R.string.Exists_Hint),Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this,"added",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getDate(){
        cal=Calendar.getInstance();
        year=cal.get(Calendar.YEAR);
        month=cal.get(Calendar.MONTH);
        day=cal.get(Calendar.DAY_OF_MONTH);
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
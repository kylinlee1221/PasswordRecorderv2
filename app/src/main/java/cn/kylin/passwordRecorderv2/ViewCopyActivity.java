package cn.kylin.passwordRecorderv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Random;
import java.util.UUID;

import cn.kylin.passwordRecorderv2.db.DBMaster;
import cn.kylin.passwordRecorderv2.model.AccountInfoBean;

public class ViewCopyActivity extends AppCompatActivity {

    private ArrayList<AccountInfoBean> infoArrayList=new ArrayList<AccountInfoBean>();
    private infoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_copy);
        requestPermissions();
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

                }).setNeutralButton(getResources().getText(R.string.Share_Btn),(click,arg)->{
                    //QRCode qrCode=new QRCode();
                    String info=usernameInfo.getText().toString()+"\n"+passwordInfo.getText().toString()+"\n"+registerInfo.getText().toString()+"\n"+dateInfo.getText().toString();
                    Bitmap qrCode=createQRCode(info);
                    if(qrCode!=null){
                        View qrCodeView=View.inflate(ViewCopyActivity.this,R.layout.show_qrcode_view,null);
                        ImageView qrCodeImg=qrCodeView.findViewById(R.id.IV_QRCode_Show);
                        TextView qrCodeHint=qrCodeView.findViewById(R.id.TV_QRCode_Hint);
                        qrCodeImg.setImageBitmap(qrCode);
                        qrCodeHint.setText(getResources().getText(R.string.QRCode_hint));
                        AlertDialog.Builder builder1=new AlertDialog.Builder(ViewCopyActivity.this);
                        builder1.setView(qrCodeView);
                        /*builder1.setPositiveButton(getResources().getText(R.string.Save_Btn),(click1,arg1)->{
                            String dir=getApplicationContext().getFilesDir().getAbsolutePath()+"/PwdQRCode/";
                            //String dir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/PwdQRCode/";
                            //Log.e("file dir",dir);
                            File dir1=new File(dir);
                            if(!dir1.exists()){
                                dir1.mkdirs();
                            }
                            Log.e("file dir",dir1.getAbsolutePath());
                            String fileName1 = UUID.randomUUID().toString()+".jpg";
                            try {
                                File file = new File(dir1,fileName1);
                                FileOutputStream out = new FileOutputStream(file);
                                qrCode.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                Toast.makeText(ViewCopyActivity.this,"QROUT TO :"+file.getAbsolutePath(),Toast.LENGTH_LONG).show();
                                out.flush();
                                out.close();
                                //保存图片后发送广播通知更新数据库
                                Uri uri = Uri.fromFile(file);
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });*/
                        builder1.setNegativeButton(getResources().getString(R.string.Share_Btn),(click1,arg1)->{
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), qrCode, "InfoImg" + Calendar.getInstance().getTime(), null));
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(intent, "title"));
                        });
                        builder1.create().show();
                    }
                }).create().show();
                return true;
            }
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

    public Bitmap createQRCode(String info){
        int width=400,height=400;
        try{
            Hashtable<EncodeHintType,String> hints=new Hashtable<EncodeHintType,String>();
            hints.put(EncodeHintType.CHARACTER_SET,"utf-8");
            BitMatrix bitMatrix=new QRCodeWriter().encode(info, BarcodeFormat.QR_CODE,width,height,hints);
            int[] pixels=new int[width*height];
            for(int y=0;y<height;y++){
                for(int x=0;x<width;x++){
                    if(bitMatrix.get(x,y)){
                        pixels[y*width+x]=0xff000000;
                    }else{
                        pixels[y*width+x]=0xffffffff;
                    }
                }
            }
            Bitmap bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels,0,width,0,0,width,height);
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void requestPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                //writeFile();
            } else {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION), 1024);
                //startActivityForResult(new Intent(Settings.ACTION_), 1024);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 先判断有没有权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //writeFile();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1024);
            }
        } else {
            //writeFile();
        }
    }

    private void saveBitmap(Bitmap bitmap,String src, String bitName) throws IOException
    {
        File pictureFileDir = new File(src);
        if (!pictureFileDir.exists())
        {
            pictureFileDir.mkdir();
        }
        File pictureFileDirImage =  new File(pictureFileDir.getAbsolutePath()+"/images/");
        if (!pictureFileDirImage.exists())
        {
            pictureFileDirImage.mkdir();
        }
        File picFile = new File(pictureFileDirImage.getAbsolutePath()+"/"+ bitName);
        if(!picFile.exists()){
            picFile.createNewFile();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(picFile);
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out))
            {
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // writeFile();
            } else {
                //ToastUtils.show("存储权限获取失败");
            }
        }
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
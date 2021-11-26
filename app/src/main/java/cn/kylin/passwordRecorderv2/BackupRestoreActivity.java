package cn.kylin.passwordRecorderv2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import android.text.format.Time;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import cn.kylin.passwordRecorderv2.db.DBMaster;
import cn.kylin.passwordRecorderv2.model.AccountInfoBean;

public class BackupRestoreActivity extends AppCompatActivity {

    private ArrayList<AccountInfoBean> infoArrayList=new ArrayList<AccountInfoBean>();
    private String restoreFileName;
    private DBMaster dbMaster;
    private File restoreFile;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);
        dbMaster=new DBMaster(getApplicationContext());
        dbMaster.openDataBase();
        infoArrayList=dbMaster.accountInfoDBDao.getAllData();
        requestPermissions();
        TextView InfoNumHint=findViewById(R.id.TV_ExistInfo_Hint);
        Button backupBtn=findViewById(R.id.BT_Backup_BR),restoreBtn=findViewById(R.id.BT_Restore_BR);
        InfoNumHint.setText(Html.fromHtml(getString(R.string.ExistInfo_Hint,String.valueOf(infoArrayList.size()))));
        backupBtn.setOnClickListener(click->{
            requestPermissions();
            Time time  =  new Time();
            time.setToNow();
            String str_time2 = time.format("%Y%m%d%M_%S");
            File backupXlsFile=new File(Environment.getExternalStorageDirectory(),str_time2+"_recorderBackupFile.xls");
            Log.e("file path:",backupXlsFile.getAbsolutePath());
            if(infoArrayList.size()!=0) {
                backupToXlsFile(backupXlsFile);
            }
        });
        restoreBtn.setOnClickListener(click->{
            showFileChooser();
            //Log.e("restore file name:",restoreFile.getAbsolutePath());
            infoArrayList=dbMaster.accountInfoDBDao.getAllData();
            InfoNumHint.setText(Html.fromHtml(getString(R.string.ExistInfo_Hint,String.valueOf(infoArrayList.size()))));
        });
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
    private void backupToXlsFile(File xlsFile){
        HSSFWorkbook workbook=new HSSFWorkbook();
        HSSFSheet sheet=workbook.createSheet(dbMaster.accountInfoDBDao.Table_Name);
        createExcelHead(sheet);
        for(AccountInfoBean bean:infoArrayList){
            createCell1(bean.getId(),bean.getUsername(),bean.getPassword(),bean.getSecurity(),bean.getRegister(),bean.getOtherInfo(),bean.getDate(),sheet);
        }
        try{
            if(!xlsFile.exists()){
                if(xlsFile.createNewFile()){
                    workbook.write(xlsFile);
                    workbook.close();
                    Toast.makeText(this,"backup to :"+xlsFile.getAbsolutePath(),Toast.LENGTH_LONG).show();
                }
            }else{
                boolean flag = xlsFile.delete();
                if(flag){
                    if(xlsFile.createNewFile()){
                        workbook.write(xlsFile);
                        workbook.close();
                        Toast.makeText(this,"backup to :"+xlsFile.getAbsolutePath(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    private void createExcelHead(HSSFSheet sheet){
        HSSFRow headRow=sheet.createRow(0);
        headRow.createCell(0).setCellValue(dbMaster.accountInfoDBDao.COL_Acc_ID);
        headRow.createCell(1).setCellValue(dbMaster.accountInfoDBDao.COL_Acc_UserName);
        headRow.createCell(2).setCellValue(dbMaster.accountInfoDBDao.COL_Acc_Password);
        headRow.createCell(3).setCellValue(dbMaster.accountInfoDBDao.COL_Acc_Security);
        headRow.createCell(4).setCellValue(dbMaster.accountInfoDBDao.COL_Acc_Register_place);
        headRow.createCell(5).setCellValue(dbMaster.accountInfoDBDao.COL_Acc_OtherInfo);
        headRow.createCell(6).setCellValue(dbMaster.accountInfoDBDao.COL_Acc_AddIn);
    }
    private void createCell1(long id,String user,String password,String secPhone,String website,String otherInfo,String addInDate,HSSFSheet sheet){
        HSSFRow dataRow=sheet.createRow(sheet.getLastRowNum()+1);
        dataRow.createCell(0).setCellValue(id);
        dataRow.createCell(1).setCellValue(user);
        dataRow.createCell(2).setCellValue(password);
        dataRow.createCell(3).setCellValue(secPhone);
        dataRow.createCell(4).setCellValue(website);
        dataRow.createCell(5).setCellValue(otherInfo);
        dataRow.createCell(6).setCellValue(addInDate);
    }
    private void showFileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.ms-excel");
        this.startActivityForResult(intent, 400);
    }
    private void restoreXlsFile(File file){
        dbMaster.DropTable();
        try{
            InputStream input=new FileInputStream(file);
            POIFSFileSystem fs=new POIFSFileSystem(input);
            HSSFWorkbook wb=new HSSFWorkbook(fs);
            HSSFSheet sheet=wb.getSheet(dbMaster.accountInfoDBDao.Table_Name);
            Iterator<Row> rows=sheet.rowIterator();
            while(rows.hasNext()){
                HSSFRow row=(HSSFRow) rows.next();
                if(row.getRowNum()==0&&rows.hasNext()) row=(HSSFRow)rows.next();
                AccountInfoBean tempBean=new AccountInfoBean();
                Iterator<Cell> cells=row.cellIterator();
                while(cells.hasNext()){
                    HSSFCell cell=(HSSFCell) cells.next();
                    switch (cell.getColumnIndex()){
                        case 0:
                            tempBean.setId((long) cell.getNumericCellValue());
                            break;
                        case 1:
                            tempBean.setUsername(cell.getStringCellValue());
                            break;
                        case 2:
                            tempBean.setPassword(cell.getStringCellValue());
                            break;
                        case 3:
                            tempBean.setSecurity(cell.getStringCellValue());
                            break;
                        case 4:
                            tempBean.setRegister(cell.getStringCellValue());
                            break;
                        case 5:
                            tempBean.setOtherInfo(cell.getStringCellValue());
                            break;
                        case 6:
                            tempBean.setDate(cell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                }
                dbMaster.accountInfoDBDao.insertDataByRestore(tempBean);
            }
        }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            // 用户未选择任何文件，直接返回
            return;
        }
        Uri uri = data.getData(); // 获取用户选择文件的URI
        Log.e("uri",uri.getEncodedPath());
        String fileName=requestDocumentName(data);
        Log.e("fileName",fileName);
        if(fileName.endsWith("_recorderBackupFile.xls")){
            File restoreFile=new File(fileName);
            if(restoreFile.exists()){
                try{
                    restoreXlsFile(restoreFile);
                }catch (Exception e){
                    Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this,"File not exist",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this,"Please chose correct file",Toast.LENGTH_LONG).show();
        }
    }
    private String requestDocumentName(Intent data){
        Uri uri=data.getData();
        String filePath= FileUtils.getRealPath(this,uri);
        return filePath;
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
    @Override
    public boolean onKeyDown(int KC, KeyEvent KE) {
        if (KC == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
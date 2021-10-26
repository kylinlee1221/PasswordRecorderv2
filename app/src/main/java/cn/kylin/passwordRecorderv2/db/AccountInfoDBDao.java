package cn.kylin.passwordRecorderv2.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import cn.kylin.passwordRecorderv2.model.AccountInfoBean;

public class AccountInfoDBDao {
    public final static String Table_Name="Account_Table";
    public final static String COL_Acc_ID="_id";
    public final static String COL_Acc_UserName="username";
    public final static String COL_Acc_Password="password";
    public final static String COL_Acc_Security="security";
    public final static String COL_Acc_Register_place="register";
    public final static String COL_Acc_OtherInfo="otherInfo";
    public final static String COL_Acc_AddIn="date";
    private SQLiteDatabase mDB;
    private Context context;
    private DBMaster.DBOpenHelper dbOpenHelper;
    private ArrayList<AccountInfoBean> infoList=new ArrayList<AccountInfoBean>();
    public AccountInfoDBDao(Context context){
        this.context=context;
    }
    public void setDataBase(SQLiteDatabase db){
        this.mDB=db;
    }
    public long insertData(AccountInfoBean bean){
        long resultId;
        if(!isExists(bean)){
            ContentValues values=new ContentValues();
            values.put(COL_Acc_UserName,bean.getUsername());
            values.put(COL_Acc_Password,bean.getPassword());
            values.put(COL_Acc_Security,bean.getSecurity());
            values.put(COL_Acc_Register_place,bean.getRegister());
            values.put(COL_Acc_OtherInfo,bean.getOtherInfo());
            values.put(COL_Acc_AddIn,bean.getDate());
            resultId=mDB.insert(Table_Name,null,values);
        }else{
            resultId=-77777;
        }
        return resultId;
    }
    public long insertData(String username,String password,String security,String register,String otherInfo,String date){
        AccountInfoBean tempBean=new AccountInfoBean(username,password,security,register,otherInfo,date);
        long resultId;
        if(!isExists(tempBean)){
            resultId=insertData(tempBean);
        }else{
            resultId=-77777;
        }
        return resultId;
    }
    public boolean isExists(AccountInfoBean bean){
        infoList=getAllData();
        if(infoList.size()==0){
            return false;
        }else{
            for(AccountInfoBean infoBean:infoList){
                if(infoBean.equals(bean)){
                    return true;
                }
            }
        }
        return false;
    }
    public ArrayList<AccountInfoBean> getAllData(){
        ArrayList<AccountInfoBean> dataList=new ArrayList<AccountInfoBean>();
        if(mDB!=null){
            Cursor cursor=mDB.rawQuery("select * from "+Table_Name,new String[]{});
            if(cursor.moveToFirst()){
                do{
                    if(cursor.getColumnIndex(COL_Acc_UserName)!=-1&&cursor.getColumnIndex(COL_Acc_Password)!=-1&&cursor.getColumnIndex(COL_Acc_Security)!=-1&&cursor.getColumnIndex(COL_Acc_Register_place)!=-1&&cursor.getColumnIndex(COL_Acc_OtherInfo)!=-1&&cursor.getColumnIndex(COL_Acc_AddIn)!=-1&&cursor.getColumnIndex(COL_Acc_ID)!=-1){
                        @SuppressLint("Range") long id=cursor.getLong(cursor.getColumnIndex(COL_Acc_ID));
                        @SuppressLint("Range") String username=cursor.getString(cursor.getColumnIndex(COL_Acc_UserName));
                        @SuppressLint("Range") String password=cursor.getString(cursor.getColumnIndex(COL_Acc_Password));
                        @SuppressLint("Range") String security=cursor.getString(cursor.getColumnIndex(COL_Acc_Security));
                        @SuppressLint("Range") String register=cursor.getString(cursor.getColumnIndex(COL_Acc_Register_place));
                        @SuppressLint("Range") String otherInfo=cursor.getString(cursor.getColumnIndex(COL_Acc_OtherInfo));
                        @SuppressLint("Range") String date=cursor.getString(cursor.getColumnIndex(COL_Acc_AddIn));
                        AccountInfoBean tempBean=new AccountInfoBean(id,username,password,security,register,otherInfo,date);
                        dataList.add(tempBean);
                    }
                }while(cursor.moveToNext());
                cursor.close();
            }
        }
        return dataList;
    }
    public long deleteAllData(){
        return mDB.delete(Table_Name,null,null);
    }
    public long deleteDataByID(long id){
        return mDB.delete(Table_Name,COL_Acc_ID+" = "+id,null);
    }
    public long updateDataByID(long id,AccountInfoBean newData){
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_Acc_UserName,newData.getUsername());
        contentValues.put(COL_Acc_Password,newData.getPassword());
        contentValues.put(COL_Acc_Security,newData.getSecurity());
        //contentValues.put(COL_Acc_Register_place,newData.getSecurity());
        return mDB.update(Table_Name,contentValues,COL_Acc_ID+" = "+id,null);
    }
}

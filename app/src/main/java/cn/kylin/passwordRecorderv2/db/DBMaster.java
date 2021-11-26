package cn.kylin.passwordRecorderv2.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBMaster {
    private Context context;
    private SQLiteDatabase sqLiteDatabase;
    private DBOpenHelper dbOpenHelper;
    public AccountInfoDBDao accountInfoDBDao;
    public DBMaster(Context context){
        this.context=context;
        accountInfoDBDao=new AccountInfoDBDao(context);
    }
    public void openDataBase(){
        dbOpenHelper=new DBOpenHelper(context,DBConfig.DB_Name,null,DBConfig.Version);
        try{
            sqLiteDatabase=dbOpenHelper.getWritableDatabase();
        }catch (SQLException e){
            sqLiteDatabase=dbOpenHelper.getReadableDatabase();
        }
        accountInfoDBDao.setDataBase(sqLiteDatabase);
    }
    public void closeDataBase(){
        if(sqLiteDatabase!=null){
            sqLiteDatabase.close();
        }
    }
    private static final String AccountSqlStr="CREATE TABLE IF NOT EXISTS "+AccountInfoDBDao.Table_Name+"("+
            AccountInfoDBDao.COL_Acc_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            AccountInfoDBDao.COL_Acc_UserName+" TEXT,"+
            AccountInfoDBDao.COL_Acc_Password+" TEXT,"+
            AccountInfoDBDao.COL_Acc_Security+" TEXT,"+
            AccountInfoDBDao.COL_Acc_Register_place+" TEXT,"+
            AccountInfoDBDao.COL_Acc_OtherInfo+" TEXT,"+
            AccountInfoDBDao.COL_Acc_AddIn+" TEXT)";
    private static final String AccountSqlDropStr="DROP TABLE IF EXISTS "+AccountInfoDBDao.Table_Name;
    public void DropTable(){
        sqLiteDatabase.execSQL(AccountSqlDropStr);
        sqLiteDatabase.execSQL(AccountSqlStr);
    }
    public static class DBOpenHelper extends SQLiteOpenHelper{

        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
            super(context,name,factory,version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(AccountSqlStr);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(AccountSqlDropStr);
            onCreate(db);
        }

    }
}

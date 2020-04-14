package adr.precios.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.example.adrprecios.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import adr.precios.entities.SequenceVo;
import adr.precios.entities.StockBranchVo;
import adr.precios.entities.GroupVo;
import adr.precios.entities.PriceVo;
import adr.precios.entities.StockInventoryVo;

public class DBHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "";
    private static String DB_NAME = "adrimar.db";
    private SQLiteDatabase db;
    private Context mContext = null;

    public static final String TABLE_DB1 = "grupos";
    public static final String COLUMN_DB1_= "gru_otro";

    private static final String DATABASE_ALTER_TEAM_TO_V2 = "ALTER TABLE "
            + TABLE_DB1 + " ADD COLUMN " + COLUMN_DB1_ + " VARCHAR ( 45 ) DEFAULT 'xxx';";

    public ArrayList <GroupVo> grupos;
    public ArrayList <PriceVo> Item;
    public ArrayList <StockBranchVo> branch;
    ArrayList <PriceVo> ItemFull;

    Cursor c = null;

    int[] drawables = {R.drawable.virojo,
            R.drawable.vinaranja,
            R.drawable.viamarillo,
            R.drawable.viverde,
            R.drawable.viazul};

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);

        if(Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir+"/databases/";
        else
            DB_PATH = "/data/data/"+context.getPackageName()+"/databases/";

        mContext = context;
    }

    public void createDatabase(){
        boolean isDBExist = checkDataBase();

        if(!isDBExist){
            this.getReadableDatabase();
            this.close();

            try{
                copyDataBase();
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }

            System.out.println("CREA BASE DE DATOS XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        } else{
            System.out.println("NO CREA YA EXISTE UNA BASE DE DATOS   XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        }

    }


/*
    public void createDatabase() {
        boolean mDataBaseExist = checkDataBase();
        if (!mDataBaseExist) {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } finally {
                this.close();
            }
        }

        System.out.println("entra a create  xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    }

*/
/*
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {
            // Base de datos no creada todavia
        }

        if (checkDB != null) {

            checkDB.close();
        }

        return checkDB != null ? true : false;
    }
*/

    private boolean checkDataBase() {
        try {
            final String mPath = DB_PATH + DB_NAME;
            final File file = new File(mPath);
            if (file.exists())
                return true;
            else
                return false;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void copyDataBase(){
        try{
            InputStream myInput = mContext.getAssets().open(DB_NAME);
            String outputFileName = DB_PATH+DB_NAME;
            OutputStream myOutput = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = myInput.read(buffer)) >0 ){
                myOutput.write(buffer,0,length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (IOException e){

        }
    }

    public void openDataBase(){
        String path = DB_PATH+DB_NAME;
        db = SQLiteDatabase.openDatabase(path,null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if(db != null)
            db.close();

        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
        switch (oldVersion) {
            case 1: db.execSQL(DATABASE_ALTER_TEAM_TO_V2);
        }

        System.out.println("REVISA LA VERSION XXXXXXXXXXXXXXXXXXXXXXXXXXXXX  *************************************************");
        */

    }

    public String getLoginBranch(String usu, String pass) {
        //db = this.getWritableDatabase();
        c = db.rawQuery("SELECT usu_suc_clave FROM usuarios " +
                "WHERE usu_login = '" + usu + "'" +
                "  and usu_password = '" + pass + "'", new String[]{});

        StringBuffer buffer = new StringBuffer();

        while (c.moveToNext()) {
            String user = c.getString(0);
            buffer.append("" + user);
        }

        return buffer.toString();
    }

    // *****************************************************************************************************************
    public ArrayList<GroupVo> getGrupos(String idBranch){
        c = db.rawQuery("SELECT gru_clave, gru_nombre, emp_clave FROM grupos, empresas, sucursales " +
                "             WHERE gru_emp_clave = emp_clave " +
                "               and suc_emp_clave = emp_clave" +
                "               and gru_status = 1 and suc_clave = "+idBranch +
                "             ORDER BY gru_nombre", new String[]{});

        grupos = new ArrayList<GroupVo>();

        int i = 0;
        while (c.moveToNext()){
            if (i >= drawables.length) {
                i = 0;
            }
            grupos.add(new GroupVo(drawables[i], c.getInt(0), c.getString(1), c.getInt(2)));
           // System.out.println(c.getString(1));
            i++;
        }

        return grupos;
    }

    // ******************************************************************************************************************
    // *****************************************************************************************************************

    public ArrayList<PriceVo> getPriceItem(String idGrupo, String idBranch){
        int clave_subg = 0;
        c = db.rawQuery("SELECT art_clave, art_clavearticulo, art_nombrelargo, lisd_fecha,  " +
                "      lisd_precio, subg_clave, subg_nombre, subg_descripcion "+
                " FROM articulos, listapreciosdetalle, subgrupos, " +
                "      sucursales, listapreciosEncabezado "+
                " WHERE lisd_art_clave = art_clave "+
                "   and lisd_lise_clave = lise_clave"+
                "   and art_subg_clave = subg_clave "+
                "   and suc_emp_clave = art_emp_clave"+
                "   and suc_lise_clave = lise_clave" +
                "   and suc_clave = "+idBranch+
                "   and subg_gru_clave = "+idGrupo+" "+
                " ORDER BY subg_nombre, art_clavearticulo", new String[]{});

        Item = new ArrayList<PriceVo>();

        while (c.moveToNext()){
            if(clave_subg != c.getInt(5)){
                Item.add(new PriceVo(0, "", "", c.getString(1), c.getString(6)+" "+c.getString(7), "",0));
                clave_subg = c.getInt(5);
            }

            Item.add(new PriceVo(c.getInt(0), "", "", c.getString(1), c.getString(2), c.getString(3),  c.getFloat(4)));
        }

        return Item;
    }

    // **************************************************************************************************************
    public ArrayList<PriceVo> getPriceItemFull(String idBranch){
        c = db.rawQuery("SELECT art_clave, gru_nombre, subg_nombre, art_clavearticulo,   " +
                "       art_nombrelargo, lisd_fecha, lisd_precio "+
                "  FROM articulos, listapreciosdetalle, subgrupos, grupos, "+
                "       listapreciosEncabezado, sucursales"+
                " WHERE lisd_art_clave = art_clave "+
                "   and art_subg_clave = subg_clave "+
                "   and subg_gru_clave = gru_clave "+
                "   and suc_lise_clave = lise_clave"+
                "   and art_emp_clave = suc_emp_clave "+
                "   and lisd_lise_clave = lise_clave"+
                "   and suc_clave = "+idBranch+
                " ORDER BY subg_nombre, art_clavearticulo", new String[]{});

        ItemFull = new ArrayList<PriceVo>();

        while (c.moveToNext()){
            ItemFull.add(new PriceVo(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5),   c.getFloat(6)));
        }

        return ItemFull;
    }

    // *****************************************************************************************************************
    public ArrayList<StockBranchVo> getInventory(String noItem){
        c = db.rawQuery("SELECT suc_clave, suc_nombreCorto, inv_existencia, lisd_precio " +
                "  FROM sucursales, inventario, articulos,  " +
                "       listapreciosEncabezado, listapreciosdetalle "+
                " WHERE inv_suc_clave = suc_clave "+
                "   and suc_lise_clave = lise_clave "+
                "   and lisd_lise_clave = lise_clave "+
                "   and lisd_art_clave = art_clave "+
                "   and inv_art_clave = art_clave "+
                "   and art_clavearticulo = '"+noItem+"'"+
                "   and suc_status = 1 and art_listavisible = 1"+
                " ORDER BY suc_nombreCorto", new String[]{});

        branch = new ArrayList<StockBranchVo>();

        while (c.moveToNext()){
            branch.add(new StockBranchVo(c.getInt(0), c.getString(1), c.getString(2), c.getFloat(3)));
        }

        return branch;
    }

    public void sqlUpdate (String noParte){

        try {
            String qry = " UPDATE inventario SET inv_existencia = 33"+
                         "  WHERE inv_art_clave IN ( " +
                                " SELECT art_clave "+
                                "   FROM articulos WHERE art_clavearticulo = '"+noParte+"')";

            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(qry);

        } catch (Exception e){
            System.out.println("no se puede ejecutar sql"+e);
            Log.e("MYDB", "UPDATE fallo",e);
        }
    }

    public int getLastIdInv() {
        c = db.rawQuery("SELECT sec_final FROM secuencia " +
                            " WHERE sec_clave = 1", new String[]{});

        int idSecInv = 0;

        while (c.moveToNext()) {
            idSecInv = c.getInt(0);
        }

        return idSecInv;
    }

    public void updateSecInv (int lastInv){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues registro = new ContentValues();
        registro.put("sec_final", lastInv);

        db.update("secuencia", registro, " sec_clave = "+1, null);
        db.close();
    }

    public void updateSecGroup (int lastGru){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues registro = new ContentValues();
        registro.put("sec_update", lastGru);

        db.update("secuencia", registro, " sec_clave = 6", null);
        db.close();
    }

    public void deleteGroups(String nameTable) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(nameTable, null, null);
        db.close();
    }

    public void addGroups(List<GroupVo> listgroup) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues registro = new ContentValues();

            for (GroupVo ob : listgroup) {
                registro.put("gru_clave", ob.getGruId());
                registro.put("gru_nombre", ob.getGruNombre());
                registro.put("gru_status", 1);
                registro.put("gru_emp_clave", ob.getGruEmpresa());

                db.insert("grupos", null, registro);
            }
            System.out.println(" herper TERMINA DE HACER INSERTS  ...................................");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<SequenceVo> getTableSequence() {
       // c = db.rawQuery("SELECT MAX(gru_clave) as gru_clave FROM grupos ", new String[]{});
        db = this.getWritableDatabase();
        c = db.rawQuery(" SELECT sec_clave, sec_codigo, sec_tabla,   " +
                            "        sec_fecha, sec_final, sec_update "+
                            "   FROM secuencia ", new String[]{});

        ArrayList <SequenceVo> listSeq = new ArrayList<SequenceVo>();

        while (c.moveToNext()) {
            listSeq.add(new SequenceVo(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getInt(4), c.getInt(5)));
        }

        return listSeq;
    }

    public void updateInventory (List <StockInventoryVo> listinv) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues registro = new ContentValues();

            for (StockInventoryVo ob : listinv) {
                registro.put("inv_existencia", ob.getInvExist());
                db.update("inventario", registro, " inv_clave = "+ob.getInvId(), null);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


/*
    public void bulkInsertEmp(ArrayList<StockBranchVo> list) {

        SQLiteDatabase database = this.getWritableDatabase();
        String sql = "INSERT INTO temp_inventario VALUES(?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        database.beginTransaction(); //starting the Transaction

        try {
            for (StockBranchVo c : list) {
                statement.bindString(1, c.getEmpId());
                statement.bindString(2, c.getEmpName());
                statement.bindString(3, c.getEmpMobile());
                statement.executeInsert();
            }
            database.setTransactionSuccessful();
        }catch (Exception e) {
            Log.e("error","error-->"+e)
        } finally {
            database.endTransaction();
        }
    }
*/
}

package adr.precios.adrprecios;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.example.adrprecios.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "";
    private static String DB_NAME = "adrimar.db";
    private SQLiteDatabase db;
    private Context mContext = null;

    ArrayList <GroupVo> grupos;
    ArrayList <PriceVo> Item;
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
        //boolean isDBExist = checkDataBase();        // BD_Existente

        this.getReadableDatabase();
        this.close();

        try{
            copyDataBase();
        }catch (Exception ex){
            throw new RuntimeException(ex);
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

    }

    public String getLoginBranch(String usu, String pass) {
        db = this.getWritableDatabase();
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
        c = db.rawQuery("SELECT gru_clave, gru_nombre FROM grupos, empresas, sucursales " +
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
            grupos.add(new GroupVo(drawables[i], c.getInt(0), c.getString(1)));
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
}

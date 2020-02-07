package adr.precios.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.adrprecios.R;

import java.util.ArrayList;

import adr.precios.entities.GroupVo;
import adr.precios.entities.PriceVo;


public class DataBaseAcces {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DataBaseAcces instance;

    ArrayList <GroupVo> grupos;
    ArrayList <PriceVo> Item;
    ArrayList <PriceVo> ItemFull;

    Cursor c = null;

    int[] drawables = {R.drawable.virojo,
            R.drawable.vinaranja,
            R.drawable.viamarillo,
            R.drawable.viverde,
            R.drawable.viazul};

    // private constructor so that object creation from outside the class is avoided
    private DataBaseAcces(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    // to return the single instance of database
    public static DataBaseAcces getInstance(Context context) {
        if (instance == null) {
            instance = new DataBaseAcces(context);
        }
        return instance;
    }

    // to open the database
    public void open() {
        this.db = openHelper.getWritableDatabase();
    }

    // closing the database connection
    public void close() {
        if (db != null) {
            this.db.close();
        }
    }

    // ******************************************************************************************************************
    public String getLoginBranch(String usu, String pass) {
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
                             "   and art_status = 1 "+
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
                            "   and art_status = 1 "+
                            "   and suc_clave = "+idBranch+
                            " ORDER BY subg_nombre, art_clavearticulo", new String[]{});

        ItemFull = new ArrayList<PriceVo>();

        while (c.moveToNext()){
            ItemFull.add(new PriceVo(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5),   c.getFloat(6)));
        }

        return ItemFull;
    }

}

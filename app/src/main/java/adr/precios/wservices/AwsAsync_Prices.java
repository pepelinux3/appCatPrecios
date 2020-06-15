package adr.precios.wservices;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import adr.precios.entities.GroupVo;
import adr.precios.entities.ItemVo;
import adr.precios.entities.PriceVo;
import adr.precios.entities.StockInventoryVo;
import adr.precios.entities.SubgroupVo;
import adr.precios.view.GroupActivity;
import retrofit2.Call;

public class AwsAsync_Prices extends AsyncTask <Integer, Integer, String> {
    private WeakReference<GroupActivity> activityWeakReference;
    private int awsGruFinal, awsSubFinal, awsItemFinal, awsPriFinal, awsInvFinal;
    private int sqlGruFinal, sqlSubFinal, sqlItemFinal, sqlPriFinal, sqlInvFinal;
    private int awsGruRestore, awsSubRestore, awsItemRestore, awsPriRestore, awsInvRestore;
    private int sqlGruRestore, sqlSubRestore, sqlItemRestore, sqlPriRestore, sqlInvRestore;
    private int option;
    String titulo;

    public AwsAsync_Prices(GroupActivity activity) {
        activityWeakReference = new WeakReference<GroupActivity>(activity);
    }

    @Override
    protected void onPreExecute() {
        GroupActivity activity = activityWeakReference.get();

        awsGruFinal = activity.awsListSeq.get(5).getTsec_final();
        sqlGruFinal = activity.sqlListSeq.get(5).getTsec_final();

        awsSubFinal = activity.awsListSeq.get(4).getTsec_final();
        sqlSubFinal = activity.sqlListSeq.get(4).getTsec_final();

        awsItemFinal = activity.awsListSeq.get(3).getTsec_final();
        sqlItemFinal = activity.sqlListSeq.get(3).getTsec_final();

        awsPriFinal = activity.awsListSeq.get(1).getTsec_final();
        sqlPriFinal = activity.sqlListSeq.get(1).getTsec_final();

        awsInvFinal = activity.awsListSeq.get(0).getTsec_final();
        sqlInvFinal = activity.sqlListSeq.get(0).getTsec_final();
        //  *************************************************************************************
        awsGruRestore = activity.awsListSeq.get(5).getTsec_restore();
        sqlGruRestore = activity.sqlListSeq.get(5).getTsec_restore();

        awsSubRestore = activity.awsListSeq.get(4).getTsec_restore();
        sqlSubRestore = activity.sqlListSeq.get(4).getTsec_restore();

        awsItemRestore = activity.awsListSeq.get(3).getTsec_restore();
        sqlItemRestore = activity.sqlListSeq.get(3).getTsec_restore();

        awsPriRestore = activity.awsListSeq.get(1).getTsec_restore();
        sqlPriRestore = activity.sqlListSeq.get(1).getTsec_restore();

        awsInvRestore = activity.awsListSeq.get(0).getTsec_restore();
        sqlInvRestore = activity.sqlListSeq.get(0).getTsec_restore();

        activity.progressBar.setVisibility(View.VISIBLE);
        activity.txtProgress.setVisibility(View.VISIBLE);
        activity.txtTittle.setVisibility(View.VISIBLE);

        activity.awsRunning = true;
        titulo = "";
    }

    @Override
    protected String doInBackground(Integer... integers) {
        option = integers[0];
        boolean actualiza = false;
        String mensaje = "";

        GroupActivity activity = activityWeakReference.get();

        System.out.println("COMPARA RESTORE GRUPO aws = "+awsGruRestore+"  y  RESTORE sql =  "+sqlGruRestore+" **********************************************");
        if(awsGruRestore > sqlGruRestore) {     //    BLANCO
            titulo = "Act. Grupos";
            System.out.println("Entra a update");
            restGroups();
            actualiza = true;
        } else
            if (awsGruFinal > sqlGruFinal) {
                titulo = "Act. Grupos";
                System.out.println("Entra a grupos   ***************************************************************************");
                newGroups();
                actualiza = true;
            }

        if(awsSubRestore > sqlSubRestore) {    //    TURQUESA
            titulo = "Act. Subgrupos";
            restSubgroups();
            actualiza = true;
        } else
            if (awsSubFinal > sqlSubFinal) {
                titulo = "Act. Subgrupos";
                System.out.println("Entra a subgrupos  ***************************************************************************");
                newSubgroups();
                actualiza = true;
            }

        if(awsItemRestore > sqlItemRestore){
            titulo = "Act. Articulos";
            restItems();
            actualiza = true;
        } else
            if (awsItemFinal > sqlItemFinal) {
                titulo = "Act. Articulos";
                System.out.println("Entra a articulos   ***************************************************************************");
                addUpdateItems();
                actualiza = true;
            }

        if(option == 1){
            if(awsPriRestore > sqlPriRestore){
                titulo = "Act. Precios";
                restPrices();
                actualiza = true;
            } else
            if(awsPriFinal > sqlPriFinal){
                titulo = "Act. Precios";
                System.out.println("Entra a Precios final = "+sqlPriFinal+" ***************************************************************************");
                addUpdatePrices();
                actualiza = true;
            }
        }

        if(option == 2){
            if(awsInvRestore > sqlInvRestore){
                titulo = "Act. Inventarios";
                restInventory();
                actualiza = true;
            } else
            if(awsInvFinal > sqlInvFinal){
                titulo = "Act. Inventarios";
                System.out.println("Entra a inventario  final = "+sqlInvFinal+" ***************************************************************************");
                updateInventory();
                actualiza = true;
            }
        }

        if(actualiza)
            mensaje = "Actualizacion terminada con exito";
        else
            mensaje = "No hay actualizaciones";

        return mensaje;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        GroupActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        activity.txtProgress.setText(values[0]+" %");
        activity.txtTittle.setText(titulo);
        activity.progressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String msj) {

        GroupActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        activity.progressBar.setVisibility(View.GONE);
        activity.txtProgress.setVisibility(View.GONE);
        activity.txtTittle.setVisibility(View.GONE);

        activity.awsAsyncTaskFinish(msj);

        activity.awsRunning = false;
        activity.fillRecyclerView();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }


    private void sqlInsertGroups(List<GroupVo> listgroup, GroupActivity activity){
        int cont = 1;

        SQLiteDatabase db = activity.dbHelper.getWritableDatabase();
        ContentValues registro = new ContentValues();

        try{
            for (GroupVo ob : listgroup) {
                registro.put("gru_clave", ob.getGruId());
                registro.put("gru_nombre", ob.getGruNombre());
                registro.put("gru_status", 1);
                registro.put("gru_emp_clave", ob.getGruEmpresa());

                db.insert("grupos", null, registro);

                publishProgress((cont * 100) / listgroup.size());
                Thread.sleep(1);

                cont ++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void restGroups()  {
        GroupActivity activity = activityWeakReference.get();
        ArrayList<GroupVo> listgroup = new ArrayList<>();

        servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<GroupVo>> respon_updateGroup = service.getAllGroups();

        try {
            for (GroupVo gru : respon_updateGroup.execute().body()) {
                listgroup.add(gru);
            }

            activity.dbHelper.deleteTableData("grupos");
            sqlInsertGroups(listgroup, activity);

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqRestore(awsGruRestore, 6);
            activity.dbHelper.close();

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqFinal(awsGruFinal, 6);
            activity.dbHelper.close();

        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void newGroups()  {
        GroupActivity activity = activityWeakReference.get();
        ArrayList<GroupVo> listgroup = new ArrayList<>();

        servicesGroup serv_addGroup = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<GroupVo>> respon_addGroup = serv_addGroup.getNewGroups(sqlGruFinal);

        try {
            for (GroupVo gru : respon_addGroup.execute().body()) {
                listgroup.add(gru);
            }

            sqlInsertGroups(listgroup, activity);

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqFinal(awsGruFinal, 6);
            activity.dbHelper.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sqlInsertSubgroup(List<SubgroupVo> listSubg, GroupActivity activity) {
        int cont = 1;

        SQLiteDatabase db = activity.dbHelper.getWritableDatabase();
        ContentValues registro = new ContentValues();

        try{
            for (SubgroupVo ob : listSubg) {
                registro.put("subg_clave", ob.getSubClave());
                registro.put("subg_nombre", ob.getSubNombre());
                registro.put("subg_status", 1);
                registro.put("subg_descripcion", ob.getSubDescripcion());
                registro.put("subg_gru_clave", ob.getSubGroup());

                db.insert("subgrupos", null, registro);

                publishProgress((cont * 100) / listSubg.size());
                Thread.sleep(1);

                cont ++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void restSubgroups(){
        GroupActivity activity = activityWeakReference.get();
        ArrayList<SubgroupVo> listSubg = new ArrayList<>();

        servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<SubgroupVo>> respon_addGroup = service.getAllSubgroups();

        try {
            for(SubgroupVo ob : respon_addGroup.execute().body()){
                listSubg.add(ob);
            }

            activity.dbHelper.deleteTableData("subgrupos");
            sqlInsertSubgroup(listSubg, activity);

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqRestore(awsSubRestore, 5);
            activity.dbHelper.close();

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqFinal(awsSubFinal, 5);
            activity.dbHelper.close();

        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void newSubgroups (){
        GroupActivity activity = activityWeakReference.get();
        ArrayList<SubgroupVo> listsubg = new ArrayList<>();

        servicesGroup serv_addSubg = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<SubgroupVo>> respon_addSubg = serv_addSubg.getNewSubgroups(sqlSubFinal);

        try {
            for (SubgroupVo sub : respon_addSubg.execute().body()) {
                listsubg.add(sub);
            }

            sqlInsertSubgroup(listsubg, activity);

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqFinal(awsSubFinal, 5);
            activity.dbHelper.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sqlInsertItems(List<ItemVo> listItem, GroupActivity activity){
        int cont = 1;

        SQLiteDatabase db = activity.dbHelper.getWritableDatabase();
        ContentValues registro = new ContentValues();

        try {

            for (ItemVo ob : listItem) {
                registro.put("art_clave", ob.getIdClave());
                registro.put("art_clavearticulo", ob.getNoParte());
                registro.put("art_nombreCorto", ob.getNomCorto());
                registro.put("art_nombreLargo", ob.getNomLargo());
                registro.put("art_inventariable", 1);

                registro.put("art_status", ob.getStatus());
                registro.put("art_emp_clave", ob.getIdEmpresa());
                registro.put("art_subg_clave", ob.getIdSubgrupo());
                registro.put("art_impuesto", ob.getImpuesto());
                registro.put("art_listavisible", ob.getVisible());

                db.insert("articulos", null, registro);

                publishProgress((cont * 100) / listItem.size());
                Thread.sleep(1);

                cont ++;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void restItems(){
        GroupActivity activity = activityWeakReference.get();
        ArrayList<ItemVo> listItem = new ArrayList<>();

        servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<ItemVo>> respon_Item = service.getAllItems();

        try {
            for (ItemVo item : respon_Item.execute().body()) {
                listItem.add(item);
            }

            activity.dbHelper.deleteTableData("articulos");
            sqlInsertItems(listItem, activity);

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqRestore(awsItemRestore, 4);
            activity.dbHelper.close();

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqFinal(awsItemFinal, 4);
            activity.dbHelper.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addUpdateItems() {
        int cont = 1;
        GroupActivity activity = activityWeakReference.get();
        ArrayList<ItemVo> listItem = new ArrayList<>();

        SQLiteDatabase db = activity.dbHelper.getWritableDatabase();
        ContentValues regisAdd = new ContentValues();
        ContentValues regisUpdate = new ContentValues();

        servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<ItemVo>> respon_addItem = service.getNewItems(sqlItemFinal);

        try{
            for (ItemVo ob : respon_addItem.execute().body()) {
                listItem.add(ob);
            }

            for (ItemVo ob : listItem) {
                regisUpdate.put("art_clavearticulo", ob.getNoParte());
                regisUpdate.put("art_nombreCorto", ob.getNomCorto());
                regisUpdate.put("art_nombreLargo", ob.getNomLargo());

                regisUpdate.put("art_status", ob.getStatus());
                regisUpdate.put("art_subg_clave", ob.getIdSubgrupo());
                regisUpdate.put("art_impuesto", ob.getImpuesto());
                regisUpdate.put("art_listavisible", ob.getVisible());

                int update =  db.update("articulos", regisUpdate, " art_clave="+ob.getIdClave(), null);

                if(update != 1) {
                    regisAdd.put("art_clave", ob.getIdClave());
                    regisAdd.put("art_clavearticulo", ob.getNoParte());
                    regisAdd.put("art_nombreCorto", ob.getNomCorto());
                    regisAdd.put("art_nombreLargo", ob.getNomLargo());
                    regisAdd.put("art_inventariable", 1);

                    regisAdd.put("art_status", ob.getStatus());
                    regisAdd.put("art_emp_clave", ob.getIdEmpresa());
                    regisAdd.put("art_subg_clave", ob.getIdSubgrupo());
                    regisAdd.put("art_impuesto", ob.getImpuesto());
                    regisAdd.put("art_listavisible", ob.getVisible());

                    db.insert("articulos", null, regisAdd);
                    activity.dbHelper.addInventory(ob.getIdClave());
                }

                publishProgress((cont * 100) / listItem.size());
                Thread.sleep(1);

                cont ++;
            }

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqFinal(awsItemFinal, 4);
            activity.dbHelper.close();

        } catch (InterruptedException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sqlInsertPrices(List<PriceVo> listPrice, GroupActivity activity){
        int cont = 1;

        SQLiteDatabase db = activity.dbHelper.getWritableDatabase();
        ContentValues registro = new ContentValues();

        try {

            for (PriceVo ob : listPrice) {
                registro.put("lisd_precio", ob.getTpri_precio());
                registro.put("lisd_fecha", ob.getTpri_fecha());
                registro.put("lisd_lise_clave", ob.getTpri_lista());
                registro.put("lisd_art_clave", ob.getTpri_articulo());
                registro.put("lisd_ultimo", ob.getTpri_ultimo());

                db.insert("listapreciosdetalle", null, registro);

                publishProgress((cont * 100) / listPrice.size());
                Thread.sleep(1);

                cont ++;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void restPrices(){
        GroupActivity activity = activityWeakReference.get();
        ArrayList<PriceVo> listPrice = new ArrayList<>();

        servicesGroup serv_price = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<PriceVo>> respon_price = serv_price.getAllPrice();

        try {
            for (PriceVo pri : respon_price.execute().body()) {
                listPrice.add(pri);
            }

            activity.dbHelper.deleteTableData("listapreciosdetalle");
            sqlInsertPrices(listPrice, activity);

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqRestore(awsPriRestore, 2);
            activity.dbHelper.close();

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqFinal(awsPriFinal, 2);
            activity.dbHelper.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addUpdatePrices() {
        int cont = 1;
        GroupActivity activity = activityWeakReference.get();
        ArrayList<PriceVo> listPrice = new ArrayList<>();

        SQLiteDatabase db = activity.dbHelper.getWritableDatabase();
        ContentValues regisAdd = new ContentValues();
        ContentValues regisUpdate = new ContentValues();

        servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<PriceVo>> respon_addPrice = service.getNewPrice(sqlPriFinal);

        try{
            for (PriceVo ob : respon_addPrice.execute().body()) {
                listPrice.add(ob);
            }

            for (PriceVo ob : listPrice) {
                regisUpdate.put("lisd_precio", ob.getTpri_precio());
                regisUpdate.put("lisd_fecha", ob.getTpri_fecha());
                regisUpdate.put("lisd_ultimo", ob.getTpri_lista());

                int update =  db.update("listapreciosdetalle", regisUpdate, " lisd_lise_clave = "+ob.getTpri_lista()+" and lisd_art_clave ="+ob.getTpri_articulo(), null);

                if(update != 1) {
                    regisAdd.put("lisd_precio", ob.getTpri_precio());
                    regisAdd.put("lisd_fecha", ob.getTpri_fecha());
                    regisAdd.put("lisd_lise_clave", ob.getTpri_lista());
                    regisAdd.put("lisd_art_clave", ob.getTpri_articulo());
                    regisAdd.put("lisd_ultimo", ob.getTpri_ultimo());

                    db.insert("listapreciosdetalle", null, regisAdd);
                }

                publishProgress((cont * 100) / listPrice.size());
                Thread.sleep(1);

                cont ++;
            }

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqFinal(awsPriFinal, 2);
            activity.dbHelper.close();

        } catch (InterruptedException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sqlInsertInvent(List<StockInventoryVo> listInv, GroupActivity activity){
        int cont = 1;

        SQLiteDatabase db = activity.dbHelper.getWritableDatabase();
        ContentValues registro = new ContentValues();

        try {

            for (StockInventoryVo ob : listInv) {
                registro.put("inv_clave", ob.getInvId());
                registro.put("inv_art_clave", ob.getInvIdItem());
                registro.put("inv_suc_clave", ob.getInvIdBranch());
                registro.put("inv_existencia", ob.getInvExist());

                db.insert("inventario", null, registro);

                publishProgress((cont * 100) / listInv.size());
                Thread.sleep(1);

                cont ++;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void restInventory(){
        GroupActivity activity = activityWeakReference.get();
        ArrayList<StockInventoryVo> listInv = new ArrayList<>();

        servicesGroup serv_invent = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<StockInventoryVo>> respon_invent = serv_invent.getAllInvent();

        try {
            for (StockInventoryVo inv : respon_invent.execute().body()) {
                listInv.add(inv);
            }

            activity.dbHelper.deleteTableData("inventario");
            sqlInsertInvent(listInv, activity);

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqRestore(awsInvRestore, 1);
            activity.dbHelper.close();

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqFinal(awsInvFinal, 1);
            activity.dbHelper.close();

            String currTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            String currDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqDateTime(currDate, currTime);
            activity.dbHelper.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateInventory() {
        int cont = 1;
        GroupActivity activity = activityWeakReference.get();
        ArrayList<StockInventoryVo> listInv = new ArrayList<>();

        servicesGroup serv_invent = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<StockInventoryVo>> respon_invent = serv_invent.getForDayInvent(sqlInvFinal);

        try {
            for(StockInventoryVo inv: respon_invent.execute().body()){
                listInv.add(inv);
            }

            SQLiteDatabase db = activity.dbHelper.getWritableDatabase();
            ContentValues registro = new ContentValues();

            for (StockInventoryVo ob : listInv) {
                registro.put("inv_existencia", ob.getInvExist());
                registro.put("inv_ultimo", ob.getInvUltimo());

                System.out.println("No.:"+cont+"  "+listInv.size()+"     Ultimo: "+ob.getInvUltimo()+"   numero = "+(cont * 100) / listInv.size());
                db.update("inventario", registro, " inv_art_clave = "+ob.getInvIdItem()+" and inv_suc_clave = "+ob.getInvIdBranch(), null);

                publishProgress((cont * 100) / listInv.size());
                Thread.sleep(1);

                cont ++;
            }

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqFinal(awsInvFinal, 1);
            activity.dbHelper.close();

            String currTime = new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(new Date());
            String currDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            activity.dbHelper.openDataBase();
            activity.dbHelper.updateSeqDateTime(currDate, currTime);
            activity.dbHelper.close();

        } catch (InterruptedException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

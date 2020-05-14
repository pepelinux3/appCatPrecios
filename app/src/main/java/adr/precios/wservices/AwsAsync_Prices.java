package adr.precios.wservices;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import adr.precios.entities.GroupVo;
import adr.precios.entities.ItemVo;
import adr.precios.entities.StockInventoryVo;
import adr.precios.entities.SubgroupVo;
import adr.precios.view.GroupActivity;
import retrofit2.Call;

public class AwsAsync_Prices extends AsyncTask <Void, Integer, Void> {
    private WeakReference<GroupActivity> activityWeakReference;
    private int awsGruFinal, awsSubFinal, awsItemFinal, awsInvFinal;
    private int sqlGruFinal, sqlSubFinal, sqlItemFinal, sqlInvFinal;
    private int awsGruRestore, awsSubRestore, awsItemRestore, awsInvRestore;
    private int sqlGruRestore, sqlSubRestore, sqlItemRestore, sqlInvRestore;
    private int option;

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

        awsInvFinal = activity.awsListSeq.get(0).getTsec_final();
        sqlInvFinal = activity.sqlListSeq.get(0).getTsec_final();
        //  *************************************************************************************
        awsGruRestore = activity.awsListSeq.get(5).getTsec_restore();
        sqlGruRestore = activity.sqlListSeq.get(5).getTsec_restore();

        awsSubRestore = activity.awsListSeq.get(4).getTsec_restore();
        sqlSubRestore = activity.sqlListSeq.get(4).getTsec_restore();

        awsItemRestore = activity.awsListSeq.get(3).getTsec_restore();
        sqlItemRestore = activity.sqlListSeq.get(3).getTsec_restore();

        awsInvRestore = activity.awsListSeq.get(0).getTsec_restore();
        sqlInvRestore = activity.sqlListSeq.get(0).getTsec_restore();

        activity.progressBar.setVisibility(View.VISIBLE);
        activity.txtProgress.setVisibility(View.VISIBLE);

        activity.awsRunning = true;
    }

    @Override
    protected Void doInBackground(Void... Void) {

        System.out.println("COMPARA RESTORE GRUPO aws = "+awsGruRestore+"  y  RESTORE sql =  "+sqlGruRestore+" **********************************************");
        if(awsGruRestore > sqlGruRestore) {
            System.out.println("Entra a update");
            restGroups();

        } else
            if (awsGruFinal > sqlGruFinal) {
                System.out.println("Entra a grupos   ***************************************************************************");
                newGroups();
            }

        if(awsSubRestore > sqlSubRestore) {
            restSubgroups();
        } else
            if (awsSubFinal > sqlSubFinal) {
                System.out.println("Entra a subgrupos  ***************************************************************************");
                newSubgroups();
            }

        if(awsItemRestore > sqlItemRestore){
            restItems();
        } else
            if (awsItemFinal > sqlItemFinal) {
                System.out.println("Entra a articulos   ***************************************************************************");
                addUpdateItems();
            }

        if(awsInvRestore > sqlInvRestore){
            restInventory();
        } else
            if(awsInvFinal > sqlInvFinal){
                System.out.println("Entra a inventario  final = "+sqlInvFinal+" ***************************************************************************");
                updateInventory();
            }

        return null;
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
                    regisAdd.put("art_listavisible", ob.getVisible());

                    db.insert("articulos", null, regisAdd);
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

        } catch (InterruptedException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        GroupActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        activity.txtProgress.setText(values[0]+" %");

        activity.progressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        GroupActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        activity.progressBar.setVisibility(View.GONE);
        activity.txtProgress.setVisibility(View.GONE);

        activity.awsRunning = false;
        activity.fillRecyclerView();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}

package adr.precios.wservices;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import adr.precios.database.DBHelper;
import adr.precios.entities.StockInventoryVo;
import adr.precios.entities.SubgroupVo;
import adr.precios.view.GroupActivity;
import adr.precios.view.MainActivity;
import adr.precios.view.PriceActivity;
import retrofit2.Call;

public class AwsAsync_Prices extends AsyncTask <Integer, StockInventoryVo, Void> {
    private WeakReference<GroupActivity> activityWeakReference;
    private int option;

    public AwsAsync_Prices(GroupActivity activity) {
        activityWeakReference = new WeakReference<GroupActivity>(activity);
    }

    @Override
    protected void onPreExecute() {
        GroupActivity activity = activityWeakReference.get();
        activity.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Integer... integers) {

        option = integers[0];
        GroupActivity activity = activityWeakReference.get();

        int awsUpdate = activity.awsListSeq.get(0).getTsec_update();
        int awsFinal = activity.awsListSeq.get(0).getTsec_final();

        ArrayList<StockInventoryVo> listInv = new ArrayList<>();

        try {
            switch(option) {
                case 1:
                    servicesGroup serv_invent = ServGenerator_AWS.createService(servicesGroup.class);
                    Call<List<StockInventoryVo>> respon_invent = serv_invent.getForDayInvent(activity.sqlListSeq.get(0).getTsec_final());

                    for(StockInventoryVo inv: respon_invent.execute().body()){
                        listInv.add(inv);
                    }

                    SQLiteDatabase db = activity.dbHelper.getWritableDatabase();
                    ContentValues registro = new ContentValues();

                    for (StockInventoryVo ob : listInv) {
                        registro.put("inv_existencia", ob.getInvExist());
                        registro.put("inv_ultimo", ob.getInvUltimo());

                        System.out.println("Ultimo: "+ob.getInvUltimo());
                        db.update("inventario", registro, " inv_art_clave = "+ob.getInvIdItem()+" and inv_suc_clave = "+ob.getInvIdBranch(), null);

                        publishProgress(new StockInventoryVo(ob.getInvId(), ob.getInvIdItem(), ob.getInvIdBranch(), ob.getInvExist(), ob.getInvUltimo()));
                        Thread.sleep(1);
                    }

                    activity.dbHelper.openDataBase();
                    activity.dbHelper.updateSeqFinal(awsFinal, 1);
                    activity.dbHelper.close();
                    break;

                case 2:
                    servicesGroup serv_AllInv = ServGenerator_AWS.createService(servicesGroup.class);
                    Call<List<StockInventoryVo>> respon_ALlInv = serv_AllInv.getAllInvent();

                    for(StockInventoryVo inv: respon_ALlInv.execute().body()){
                        listInv.add(inv);
                    }

                    activity.dbHelper.deleteTableData("inventario");

                    SQLiteDatabase db2 = activity.dbHelper.getWritableDatabase();
                    ContentValues registro2 = new ContentValues();

                    for (StockInventoryVo ob : listInv) {
                        registro2.put("inv_clave", ob.getInvId());
                        registro2.put("inv_art_clave", ob.getInvIdItem());
                        registro2.put("inv_suc_clave", ob.getInvIdBranch());
                        registro2.put("inv_existencia", ob.getInvExist());

                        System.out.println("Ultimo NEW: "+ob.getInvId()+"  --  "+ob.getInvUltimo());
                        db2.insert("inventario", null, registro2);

                        publishProgress(new StockInventoryVo(ob.getInvId(), ob.getInvIdItem(), ob.getInvIdBranch(), ob.getInvExist(), ob.getInvUltimo()));
                        Thread.sleep(1);
                    }

                    activity.dbHelper.openDataBase();
                    activity.dbHelper.updateSeqUpdate(awsUpdate, 1);
                    activity.dbHelper.close();

                    activity.dbHelper.openDataBase();
                    activity.dbHelper.updateSeqFinal(awsFinal, 1);
                    activity.dbHelper.close();
                    break;

                default:
                    System.out.println("error de opcion wsLogin");
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        GroupActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        activity.progressBar.setVisibility(View.GONE);
    }
}

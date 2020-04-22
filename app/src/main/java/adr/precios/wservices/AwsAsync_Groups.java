package adr.precios.wservices;

import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import adr.precios.entities.SubgroupVo;
import adr.precios.view.GroupActivity;
import retrofit2.Call;

public class AwsAsync_Groups extends AsyncTask <Integer, String, String> {
    private WeakReference<GroupActivity> activityWeakReference;
    private int option;

    public AwsAsync_Groups(GroupActivity activity) {
        activityWeakReference = new WeakReference<GroupActivity>(activity);
    }

    @Override
    protected String doInBackground(Integer... integers) {
        String strResponse = "";
        option = integers[0];

        GroupActivity activity = activityWeakReference.get();

        try {
            switch(option) {
                case 1:
                    servicesGroup serv_UpdateSubg = ServGenerator_AWS.createService(servicesGroup.class);
                    Call<List<SubgroupVo>> respon_group = serv_UpdateSubg.getAllSubgroups();

                    ArrayList<SubgroupVo> listSubg = new ArrayList<>();

                    for(SubgroupVo subg: respon_group.execute().body()){
                        listSubg.add(subg);
                    }

                    activity.dbHelper.deleteTableData("subgrupos");
                    activity.dbHelper.addSubgroups(listSubg);
                    break;

                case 2:

                    break;

                default:
                    System.out.println("error de opcion wsGroup");
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return strResponse;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        GroupActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        switch(option) {
            case 1:
                // activity.awsResp_UpdateSubgroups();
                break;

            default:
                System.out.println("Error de opcion WS onPostExecute");
                break;
        }
    }
}

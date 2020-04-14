package adr.precios.wservices;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import adr.precios.entities.GroupVo;
import adr.precios.view.MainActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class AwsGetData extends AsyncTask <Integer, String, String>{
    private WeakReference<MainActivity> activityWeakReference;
    private int option;

    public AwsGetData(MainActivity activity) {
        activityWeakReference = new WeakReference<MainActivity>(activity);
    }

    @Override
    protected String doInBackground(Integer... integers) {
        String strResponse = "";
        option = integers[0];

        MainActivity activity = activityWeakReference.get();

        try {
            switch(option) {
                case 1:
                    servicesGroup apiInterface = ServGenerator_Date.createService(servicesGroup.class);
                    Call<ResponseBody> result_date = apiInterface.getCurrDate();

                    String ws_date = result_date.execute().body().string();
                    JSONObject json = new JSONObject(ws_date);
                    strResponse = json.getString("datetime");
                    break;

                case 2:
                    servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
                    Call<ResponseBody> resultSequence = service.getSequences();
                    strResponse = resultSequence.execute().body().string();
                    break;

                case 3:
                    servicesGroup serv_group = ServGenerator_AWS.createService(servicesGroup.class);
                    Call<List<GroupVo>> respon_group = serv_group.getGroupGet();

                    ArrayList<GroupVo> listGroup = new ArrayList<>();

                    for(GroupVo gru: respon_group.execute().body()){
                            listGroup.add(gru);
                    }

                    activity.dbHelper.deleteGroups("grupos");
                    activity.dbHelper.addGroups(listGroup);
                    break;

                default:
                    System.out.println("error de opcion wsLogin");
                    break;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return strResponse;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        MainActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        switch(option) {
            case 1:
                activity.awsResp_GetDate(s);
                break;

            case 2:
                activity.awsResp_GetSequence(s);
                break;

            case 3:
                activity.awsResp_UpdateGroups();
                break;

            default:
                System.out.println("Error de opcion WS onPostExecute");
                break;
        }
    }
}

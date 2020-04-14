package adr.precios.wservices;

import java.util.List;

import adr.precios.entities.GroupVo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface servicesGroup {

    @GET("api/timezone/America/Mexico_City")
    Call<ResponseBody> getCurrDate();

    @GET("RESTadrimar/webresources/inventory/idgru_final")
    Call<ResponseBody> getIdSec_LastGroup();

    @GET("RESTadrimar/webresources/inventory/allgroups")
    Call<List<GroupVo>> getGroupGet();

    @GET("RESTadrimar/webresources/inventory/allsequences")
    Call<ResponseBody> getSequences();

}

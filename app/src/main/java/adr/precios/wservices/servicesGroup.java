package adr.precios.wservices;

import java.util.List;

import adr.precios.entities.GroupVo;
import adr.precios.entities.ItemVo;
import adr.precios.entities.SubgroupVo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface servicesGroup {

    @GET("api/timezone/America/Mexico_City")
    Call<ResponseBody> getCurrDate();

    @GET("RESTadrimar/webresources/inventory/allsequences")
    Call<ResponseBody> getSequences();


    @GET("RESTadrimar/webresources/inventory/allgroups")
    Call<List<GroupVo>> getAllGroups();

    @GET("RESTadrimar/webresources/inventory/group/{id}")
    Call<List<GroupVo>> getNewGroups(@Path("id") int idLastGroup);


    @GET("RESTadrimar/webresources/inventory/allsubgroups")
    Call<List<SubgroupVo>> getAllSubgroups();

    @GET("RESTadrimar/webresources/inventory/subg/{id}")
    Call<List<SubgroupVo>> getNewSubgroups(@Path("id") int idLastSubg);


    @GET("RESTadrimar/webresources/inventory/allitems")
    Call<List<ItemVo>> getAllItems();

    @GET("RESTadrimar/webresources/inventory/editem/{id}")
    Call<List<ItemVo>> getNewItems(@Path("id") int idLastItem);
}

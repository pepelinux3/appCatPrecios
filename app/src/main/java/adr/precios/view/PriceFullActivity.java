package adr.precios.view;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.adrprecios.R;

import java.util.ArrayList;
import java.util.List;

import adr.precios.database.DBHelper;
import adr.precios.adapter.PriceFullAdapter;
import adr.precios.entities.StockInventoryVo;
import adr.precios.tools.MiDialogFragment;
import adr.precios.wservices.ServGenerator_AWS;
import adr.precios.wservices.servicesGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PriceFullActivity extends AppCompatActivity {

    private RecyclerView recyclerPricesFull;
    private Toolbar toolbarPriceFull;
    private PriceFullAdapter priceFullAdapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precios_full);

        recyclerPricesFull = (RecyclerView)findViewById(R.id.recycler_pricefull_id);
        recyclerPricesFull.setLayoutManager(new LinearLayoutManager(this));

       //  DataBaseAcces databaseAcces = DataBaseAcces.getInstance(getApplicationContext());
       //  databaseAcces.open();

        dbHelper = new DBHelper(getApplicationContext());
        dbHelper.openDataBase();

        String idBranch = getIntent().getStringExtra("branchId");

        priceFullAdapter = new PriceFullAdapter(dbHelper.getPriceItemFull(idBranch));
        recyclerPricesFull.setAdapter(priceFullAdapter);

        dbHelper.close();
        setUpToolBar();
    }

    private void accesActivityImagen (){
        Intent activityImagen = new Intent(this, ImagenActivity.class);
        startActivity(activityImagen);
    }

    private void accesActivityExistencia (String recyNoItem, boolean aws){
        MiDialogFragment myDialogFragment = new MiDialogFragment();

        myDialogFragment.setValue(recyNoItem, aws);
        myDialogFragment.show(getSupportFragmentManager(), "MyFragment");
    }

    private void setUpToolBar() {
        toolbarPriceFull = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarPriceFull);
        getSupportActionBar().setTitle("LISTA COMPLETA");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_pricefull, menu);
        menuIconColor(menu, R.color.colorWhiteApp);

        MenuItem searchItem = menu.findItem(R.id.search_full);
        SearchView searchView = (SearchView)searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                priceFullAdapter.getFilter().filter(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void menuIconColor(Menu menu, int color){
        for(int i=0; i<menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null){
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                Toast.makeText(this, "Ver Existencia", Toast.LENGTH_SHORT).show();
                awsUpdateExist();
                // accesActivityExistencia ();
               // accesActivityExistencia(priceFullAdapter.getNoParteAdapter(), aws);
                return true;

            case 2:
                Toast.makeText(this, "No disponible", Toast.LENGTH_SHORT).show();
                //accesActivityImagen ();
                return true;

            default: return super.onContextItemSelected(item);
        }
    }

    private void awsUpdateExist(){
       // System.out.println("articulo existencia = "+priceFullAdapter.getIdItem()+"   xxxxxxxxxxxxxxx");

        servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<StockInventoryVo>> respon_updateExist = service.getNoPartInvent(priceFullAdapter.getIdItem());

        respon_updateExist.enqueue(new Callback<List<StockInventoryVo>>() {
            @Override
            public void onResponse(Call<List<StockInventoryVo>> call, Response<List<StockInventoryVo>> response) {
                System.out.println("ENTRA A AWS DE EXISTENCIAS ...................................");
                boolean aws;
                if(response.isSuccessful()){
                    ArrayList<StockInventoryVo> listInv = new ArrayList<StockInventoryVo>();

                    for(StockInventoryVo ob: response.body()){
                        listInv.add(ob);
                    }

                    dbHelper.updateDayInventory(listInv);
                    aws = true;
                } else{
                    Toast.makeText(PriceFullActivity.this, "error al actualizar Inventario existencia", Toast.LENGTH_SHORT).show();
                    aws = false;
                }

                accesActivityExistencia(priceFullAdapter.getNoParteAdapter(), aws);
            }

            @Override
            public void onFailure(Call<List<StockInventoryVo>> call, Throwable t) {
                accesActivityExistencia(priceFullAdapter.getNoParteAdapter(), false);
                Toast.makeText(PriceFullActivity.this, "Falla al actualizar Inventario existencia", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

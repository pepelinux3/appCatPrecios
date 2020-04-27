package adr.precios.view;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adrprecios.R;

import java.util.ArrayList;
import java.util.List;

import adr.precios.database.DBHelper;
import adr.precios.adapter.PriceAdapter;
import adr.precios.entities.StockInventoryVo;
import adr.precios.tools.MiDialogFragment;
import adr.precios.wservices.ServGenerator_AWS;
import adr.precios.wservices.servicesGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceActivity extends AppCompatActivity {

    private Toolbar toolbar2;
    private RecyclerView recyclerPrice;
    private PriceAdapter priceAdapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precios);

        setUpToolBar();
        setUpHomeUpIconAndColor(R.drawable.ic_search, R.color.colorWhiteApp);

        fillRecyclerView();
    }

    private void fillRecyclerView(){
        recyclerPrice = (RecyclerView)findViewById(R.id.recycler_price_id);
        recyclerPrice.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

       // DataBaseAcces databaseAcces = DataBaseAcces.getInstance(getApplicationContext());
       // databaseAcces.open();

        dbHelper = new DBHelper(getApplicationContext());
        dbHelper.openDataBase();

        String idGroup = getIntent().getStringExtra("groupId");
        final String idBranch = getIntent().getStringExtra("branchId");

        priceAdapter  = new PriceAdapter(dbHelper.getPriceItem(idGroup, idBranch));
        recyclerPrice.setAdapter(priceAdapter);

        dbHelper.close();
    }

    private void accesActivityImagen (){
        Intent activityImagen = new Intent(this, ImagenActivity.class);
        startActivity(activityImagen);
    }

    private void accesActivityExistencia (String recyNoItem){
        MiDialogFragment myDialogFragment = new MiDialogFragment();

        myDialogFragment.setValue(recyNoItem);
        myDialogFragment.show(getSupportFragmentManager(), "MyFragment");
    }

    private void setUpHomeUpIconAndColor(int drawable, int color) {
        if(getSupportActionBar() != null){
            final Drawable icon = getResources().getDrawable(drawable);
            icon.setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(icon);
        }
    }


    private void setUpToolBar() {
        toolbar2 = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar2);

        String tittle = getIntent().getStringExtra("groupTittle");

        getSupportActionBar().setTitle(tittle);
        //showHomeUpIcon();
    }

    private void showHomeUpIcon() {
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_price, menu);
        menuIconColor(menu, R.color.colorWhiteApp);

        MenuItem searchItem = menu.findItem(R.id.search_price);
        SearchView searchView = (SearchView)searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                priceAdapter.getFilter().filter(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    private void menuIconColor(Menu menu, int color) {
        for(int i=0; i<menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();

            if(drawable != null){
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    public void modificar (){

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase baseDataBase = dbHelper.getWritableDatabase();

        ContentValues registro = new ContentValues();
            registro.put("lisd_precio", 333);

            int cantidad = baseDataBase.update("listapreciosdetalle", registro, " lisd_clave="+1139, null);
            baseDataBase.close();

            if(cantidad == 1){
                Toast.makeText(this, "Articulo modificado correctamente", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Articulo no existe", Toast.LENGTH_SHORT).show();
            }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case 1:
                Toast.makeText(this, "Existencias", Toast.LENGTH_SHORT).show();
               // accesActivityExistencia(priceAdapter.getNoParteAdapter());
                awsUpdateExist();
                return true;

            case 2:
                Toast.makeText(this, "No disponible", Toast.LENGTH_SHORT).show();
               // DBHelper dbHelper = new DBHelper(getApplicationContext());
               // dbHelper.sqlUpdate(priceAdapter.getNoParteAdapter());

               // accesActivityImagen ();
                //modificar ();

                return true;

            default: return super.onContextItemSelected(item);
        }
    }

    private void awsUpdateExist(){
        System.out.println("articulo existencia = "+priceAdapter.getIdItem()+"   xxxxxxxxxxxxxxx");

        servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<StockInventoryVo>> respon_updateExist = service.getNoPartInvent(priceAdapter.getIdItem());

        respon_updateExist.enqueue(new Callback<List<StockInventoryVo>>() {
            @Override
            public void onResponse(Call<List<StockInventoryVo>> call, Response<List<StockInventoryVo>> response) {
                System.out.println("ENTRA A AWS DE EXISTENCIAS ...................................");
                if(response.isSuccessful()){
                    ArrayList<StockInventoryVo> listInv = new ArrayList<StockInventoryVo>();

                    for(StockInventoryVo ob: response.body()){
                        listInv.add(ob);
                    }

                    dbHelper.updateDayInventory(listInv);
                } else{
                    Toast.makeText(PriceActivity.this, "error al actualizar Inventario existencia", Toast.LENGTH_SHORT).show();
                }

                accesActivityExistencia(priceAdapter.getNoParteAdapter());
            }

            @Override
            public void onFailure(Call<List<StockInventoryVo>> call, Throwable t) {
                accesActivityExistencia(priceAdapter.getNoParteAdapter());
                Toast.makeText(PriceActivity.this, "Falla al actualizar Inventario existencia", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

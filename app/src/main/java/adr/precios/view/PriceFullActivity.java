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

import adr.precios.database.DBHelper;
import adr.precios.adapter.PriceFullAdapter;
import adr.precios.tools.MiDialogFragment;


public class PriceFullActivity extends AppCompatActivity {

    private RecyclerView recyclerPricesFull;
    private Toolbar toolbarPriceFull;
    private PriceFullAdapter priceFullAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precios_full);

        recyclerPricesFull = (RecyclerView)findViewById(R.id.recycler_pricefull_id);
        recyclerPricesFull.setLayoutManager(new LinearLayoutManager(this));

       //  DataBaseAcces databaseAcces = DataBaseAcces.getInstance(getApplicationContext());
       //  databaseAcces.open();

        final DBHelper dbHelper = new DBHelper(getApplicationContext());
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

    private void accesActivityExistencia (String recyNoItem){
        MiDialogFragment myDialogFragment = new MiDialogFragment();

        myDialogFragment.setValue(recyNoItem);
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
                // accesActivityExistencia ();
                accesActivityExistencia(priceFullAdapter.getNoParteAdapter());
                return true;

            case 2:
                Toast.makeText(this, "No disponible", Toast.LENGTH_SHORT).show();
                //accesActivityImagen ();
                return true;

            default: return super.onContextItemSelected(item);
        }
    }
}
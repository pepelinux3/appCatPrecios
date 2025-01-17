package adr.precios.adrprecios;

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

public class PreciosActivity extends AppCompatActivity {

    private Toolbar toolbar2;
    private RecyclerView recycler2;
    private PriceAdapter priceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precios);

        setUpToolBar();
        setUpHomeUpIconAndColor(R.drawable.ic_search, R.color.colorWhiteApp);

        fillRecyclerView();
    }

    private void fillRecyclerView(){
        recycler2 = (RecyclerView)findViewById(R.id.recycler_price_id);
        recycler2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

       // DataBaseAcces databaseAcces = DataBaseAcces.getInstance(getApplicationContext());
       // databaseAcces.open();

        final DBHelper dbHelper = new DBHelper(getApplicationContext());
        dbHelper.openDataBase();

        String idGroup = getIntent().getStringExtra("groupId");
        String idBranch = getIntent().getStringExtra("branchId");

        priceAdapter  = new PriceAdapter(dbHelper.getPriceItem(idGroup, idBranch));
        recycler2.setAdapter(priceAdapter);

        dbHelper.close();
    }

    private void accesActivityImagen (){
        Intent activityImagen = new Intent(this, ImagenActivity.class);
        startActivity(activityImagen);
    }

    private void accesActivityExistencia (){
        Intent activityExistencia = new Intent(this, ExistenciaActivity.class);
        startActivity(activityExistencia);
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case 1:
                Toast.makeText(this, "Ver Existencia", Toast.LENGTH_SHORT).show();
                accesActivityExistencia();
                return true;

            case 2:
                Toast.makeText(this, "Ver Imagen", Toast.LENGTH_SHORT).show();
                accesActivityImagen ();
                return true;

            default: return super.onContextItemSelected(item);
        }
    }
}

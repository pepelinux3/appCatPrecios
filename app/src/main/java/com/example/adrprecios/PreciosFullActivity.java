package com.example.adrprecios;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class PreciosFullActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    private RecyclerView recyclerPricesFull;
    private Toolbar toolbarPriceFull;
    private PriceAdapterFull priceAdapterFull;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precios_full);

        recyclerPricesFull = (RecyclerView)findViewById(R.id.recycler_pricefull_id);
        recyclerPricesFull.setLayoutManager(new LinearLayoutManager(this));

        DataBaseAcces databaseAcces = DataBaseAcces.getInstance(getApplicationContext());
        databaseAcces.open();

        priceAdapterFull = new PriceAdapterFull(databaseAcces.getPriceItemFull());
        recyclerPricesFull.setAdapter(priceAdapterFull);

        databaseAcces.close();
        setUpToolBar();
    }


    private void setUpToolBar() {
        toolbarPriceFull = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarPriceFull);
        getSupportActionBar().setTitle("LISTA COMPLETA");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
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
                priceAdapterFull.getFilter().filter(s);
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

    public void showPopup(View view){
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_prices);
        popup.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.it_imagen:
                Toast.makeText(this, "ver imagen", Toast.LENGTH_SHORT).show();
                return  true;

            case R.id.it_existencia:
                Toast.makeText(this, "ver existencia", Toast.LENGTH_SHORT).show();
                 return true;
            default:
                return false;

        }
    }
}

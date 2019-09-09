package com.example.adrprecios;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

public class PreciosFullActivity extends AppCompatActivity {

    RecyclerView recyclerPricesFull;
    private Toolbar toolbarPriceFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precios_full);

        recyclerPricesFull = (RecyclerView)findViewById(R.id.recycler_pricefull_id);
        recyclerPricesFull.setLayoutManager(new LinearLayoutManager(this));

        DataBaseAcces databaseAcces = DataBaseAcces.getInstance(getApplicationContext());
        databaseAcces.open();

        PriceAdapterFull adapter = new PriceAdapterFull(databaseAcces.getPriceItemFull());
        recyclerPricesFull.setAdapter(adapter);

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
}

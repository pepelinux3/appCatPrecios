package com.example.adrprecios;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class PreciosFullActivity extends AppCompatActivity {

    RecyclerView recyclerPricesFull;

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
    }
}

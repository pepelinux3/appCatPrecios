package com.example.adrprecios;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

public class GroupActivity extends AppCompatActivity {

    RecyclerView recyclerGroup;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos);

        setUpToolBar();
        setUpHomeUpIconAndColor(R.drawable.ic_search, R.color.colorWhiteApp);

        recyclerGroup = (RecyclerView)findViewById(R.id.recycler_group_id);
        recyclerGroup.setLayoutManager(new LinearLayoutManager(this));

        final DataBaseAcces databaseAcces = DataBaseAcces.getInstance(getApplicationContext());
        databaseAcces.open();

        GruopAdapter adapter = new GruopAdapter(databaseAcces.getGrupos());
        recyclerGroup.setAdapter(adapter);

        databaseAcces.close();

        adapter.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        "Seleccion: "+databaseAcces.grupos.get
                                (recyclerGroup.getChildAdapterPosition(v)).getGruNombre(), Toast.LENGTH_SHORT).show();

                accessActPrices(databaseAcces.grupos.get(recyclerGroup.getChildAdapterPosition(v)).getGruNombre().toString(),
                                databaseAcces.grupos.get(recyclerGroup.getChildAdapterPosition(v)).getGruId());
            }
        });
    }

    private void accessActPrices(String tittle, int idGroup){
        Intent actPrecios = new Intent(this, PreciosActivity.class);

        actPrecios.putExtra("groupId", idGroup+"");
        actPrecios.putExtra("groupTittle", tittle);
        startActivity(actPrecios);
    }


    private void setUpHomeUpIconAndColor(int drawable, int color) {
        if(getSupportActionBar() != null){
            final Drawable icon = getResources().getDrawable(drawable);
            icon.setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(icon);
        }
    }

    private void setUpToolBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("GRUPOS");
        //showHomeUpIcon();
    }

    private void showHomeUpIcon() {
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           // getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menuIconColor(menu, R.color.colorWhiteApp);

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

}

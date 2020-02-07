package adr.precios.view;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.example.adrprecios.R;

import adr.precios.database.DBHelper;
import adr.precios.adapter.GruopAdapter;

public class GroupActivity extends AppCompatActivity {

    RecyclerView recyclerGroup;
    GruopAdapter adapter;
    Menu menu;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos);

        fillRecyclerView();
        setUpToolBar();
        setUpHomeUpIconAndColor(R.drawable.ic_search, R.color.colorWhiteApp);
    }

    CountDownTimer timer = new CountDownTimer(240 * 60 * 1000, 1000) {

        public void onTick(long millisUntilFinished) {
            //Some code
        }

        public void onFinish() {
            finish();
            System.exit(0);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        timer.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.start();
    }

    private void fillRecyclerView(){
        recyclerGroup = findViewById(R.id.recycler_group_id);
        recyclerGroup.setLayoutManager(new LinearLayoutManager(this));

       // dbHelper = new DBHelper(getApplicationContext());
      //  dbHelper.createDatabase();

        final DBHelper dbHelper = new DBHelper(getApplicationContext());
        dbHelper.openDataBase();

       // final DataBaseAcces databaseAcces = DataBaseAcces.getInstance(getApplicationContext());
       // databaseAcces.open();

        String idbranch1 = getIntent().getStringExtra("noBranch");
        adapter = new GruopAdapter(dbHelper.getGrupos(idbranch1));
        recyclerGroup.setAdapter(adapter);

        dbHelper.close();

        adapter.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                String idbranch2 = getIntent().getStringExtra("noBranch");
                Toast.makeText(getApplicationContext(),
                        "Seleccion: "+dbHelper.grupos.get
                                (recyclerGroup.getChildAdapterPosition(v)).getGruNombre(), Toast.LENGTH_SHORT).show();

                accessActPrices(dbHelper.grupos.get(recyclerGroup.getChildAdapterPosition(v)).getGruNombre(),
                        dbHelper.grupos.get(recyclerGroup.getChildAdapterPosition(v)).getGruId(), idbranch2);
            }
        });
    }

    private void accessActPrices(String tittle, int idGroup, String idBranch){
        Intent actPrecios = new Intent(this, PriceActivity.class);

        actPrecios.putExtra("groupId", idGroup+"");
        actPrecios.putExtra("groupTittle", tittle);
        actPrecios.putExtra("branchId", idBranch);
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
        this.menu = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_group, menu);
        menuIconColor(menu, R.color.colorWhiteApp);

        MenuItem searchGroup = menu.findItem(R.id.me_search);
        SearchView searchView = (SearchView) searchGroup.getActionView();
        searchView.setQueryHint("Buscar...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               // showOverflowMenu(false);
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.me_listaCompleta){
            Toast.makeText(this, "Lista Completa", Toast.LENGTH_SHORT).show();

            String idbranch3 = getIntent().getStringExtra("noBranch");

            Intent preciosFull = new Intent(this, PriceFullActivity.class);
            preciosFull.putExtra("branchId", idbranch3);
            startActivity(preciosFull);
        }

        return super.onOptionsItemSelected(item);
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

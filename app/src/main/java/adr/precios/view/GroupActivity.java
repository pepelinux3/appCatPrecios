package adr.precios.view;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adr.precios.database.DBHelper;
import adr.precios.adapter.GruopAdapter;
import adr.precios.entities.GroupVo;
import adr.precios.wservices.ServGenerator_AWS;
import adr.precios.wservices.servicesGroup;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupActivity extends AppCompatActivity {

    RecyclerView recyclerGroup;
    GruopAdapter adapter;
    Menu menu;

    private DBHelper dbHelper;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos);

        dbHelper = new DBHelper(this);

      //  getWSIdSec_Group();
        setUpToolBar();
        setUpHomeUpIconAndColor(R.drawable.ic_search, R.color.colorWhiteApp);
        fillRecyclerView();
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
/*
    private void getWSIdSec_Group(){
        servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
        Call<ResponseBody> result_idSecGroup = service.getIdSec_LastGroup();

        result_idSecGroup.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if(response.isSuccessful()){
                        String wsIdLastGroup = response.body().string();
                        int awsIdGroup = Integer.parseInt(wsIdLastGroup);

                        dbHelper.openDataBase();
                        int sqlIdGroup = dbHelper.getLastIdGroup();
                        dbHelper.close();

                        if(awsIdGroup > sqlIdGroup){
                            new GetGrupos_asyn().execute();

                            dbHelper.openDataBase();
                            dbHelper.updateSecGroup(awsIdGroup);
                            dbHelper.close();

                            Toast.makeText(GroupActivity.this, "Actualizacion de grupos", Toast.LENGTH_SHORT).show();
                        } else {
                            fillRecyclerView();
                        }
                    } else {
                        Toast.makeText(GroupActivity.this, "Falla response grupos", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(GroupActivity.this, "Falla secuencia Grupos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class GetGrupos_asyn extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
            Call<List<GroupVo>> respon_gru = service.getGroupGet();

            ArrayList<GroupVo> listGroup = new ArrayList<>();

            try {
                for(GroupVo gru: respon_gru.execute().body()){
                    listGroup.add(gru);
                }

                GroupActivity.dbHelper.deleteGroups();
                GroupActivity.dbHelper.addGroups(listGroup);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            fillRecyclerView();
        }
    }
*/
    private void fillRecyclerView(){
        recyclerGroup = findViewById(R.id.recycler_group_id);
        recyclerGroup.setLayoutManager(new LinearLayoutManager(this));

        dbHelper.openDataBase();
        String idbranch1 = getIntent().getStringExtra("noBranch");
        ArrayList<GroupVo> listFinal = dbHelper.getGrupos(idbranch1);
        dbHelper.close();

        adapter = new GruopAdapter(listFinal);
        recyclerGroup.setAdapter(adapter);

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

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

import java.util.ArrayList;
import java.util.List;

import adr.precios.database.DBHelper;
import adr.precios.adapter.GruopAdapter;
import adr.precios.entities.GroupVo;
import adr.precios.entities.ItemVo;
import adr.precios.entities.SequenceVo;
import adr.precios.entities.SubgroupVo;
import adr.precios.wservices.ServGenerator_AWS;
import adr.precios.wservices.servicesGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupActivity extends AppCompatActivity {

    RecyclerView recyclerGroup;
    GruopAdapter adapter;
    Menu menu;

    public DBHelper dbHelper;
    private Toolbar toolbar;

    private ArrayList<SequenceVo> sqlListSeq;
    private ArrayList<SequenceVo> awsListSeq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos);

        dbHelper = new DBHelper(this);

        checkAWS();
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

    private void fillRecyclerView(){
        recyclerGroup = findViewById(R.id.recycler_group_id);
        recyclerGroup.setLayoutManager(new LinearLayoutManager(this));

        String idbranch1 = getIntent().getStringExtra("noBranch");

        dbHelper.openDataBase();
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

    private void checkAWS(){
        sqlListSeq = (ArrayList<SequenceVo>)getIntent().getExtras().getSerializable("sqlList");
        awsListSeq = (ArrayList<SequenceVo>)getIntent().getExtras().getSerializable("awsList");

        System.out.println("ACTIVITI GRUPOS entra a checa");

        System.out.println("ACTIVITI GRUPOS UPDATE   aws ="+awsListSeq.get(4).getTsec_update()+"  -  sql ="+sqlListSeq.get(4).getTsec_update());
        if(awsListSeq.get(4).getTsec_update() > sqlListSeq.get(4).getTsec_update()){
            System.out.println("ACTIVITI GRUPOS ENTRA A AWS UPDATE SUBG");
            awsUpdateSubgroup();
        } else
            if(awsListSeq.get(4).getTsec_final() > sqlListSeq.get(4).getTsec_final()){
                System.out.println("ACTIVITI GRUPOS ENTRA A AWS FINAL SUBG");
                awsAddSubgroup(sqlListSeq.get(4).getTsec_final());
            }


        if(awsListSeq.get(3).getTsec_update() > sqlListSeq.get(3).getTsec_update()){
            System.out.println("ACTIVITI GRUPOS ENTRA A AWS UPDATE ITEM");
            awsUpdateItems();
        } else
            if(awsListSeq.get(3).getTsec_final() > sqlListSeq.get(3).getTsec_final()){
                System.out.println("ACTIVITI GRUPOS ENTRA A AWS FINAL ITEM");
                awsAddItems(sqlListSeq.get(3).getTsec_final());
        }
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

    private void awsUpdateSubgroup(){
        servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<SubgroupVo>> respon_addGroup = service.getAllSubgroups();

        respon_addGroup.enqueue(new Callback<List<SubgroupVo>>() {
            @Override
            public void onResponse(Call<List<SubgroupVo>> call, Response<List<SubgroupVo>> response) {
                if(response.isSuccessful()){
                    int awsUpdate = awsListSeq.get(4).getTsec_update();
                    int awsFinal = awsListSeq.get(4).getTsec_final();

                    ArrayList <SubgroupVo> listSubg = new ArrayList<SubgroupVo>();

                    for(SubgroupVo ob : response.body()){
                        listSubg.add(ob);
                    }

                    dbHelper.deleteTableData("subgrupos");
                    dbHelper.addSubgroups(listSubg);

                    dbHelper.openDataBase();
                    dbHelper.updateSeqUpdate(awsUpdate, 5);
                    dbHelper.close();

                    dbHelper.openDataBase();
                    dbHelper.updateSeqFinal(awsFinal, 5);
                    dbHelper.close();

                    Toast.makeText(GroupActivity.this, "actualizacion todos los subgrupos", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(GroupActivity.this, "Falla response Subgrupos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SubgroupVo>> call, Throwable t) {
                Toast.makeText(GroupActivity.this, "Falla conexion Subgrupos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void awsAddSubgroup (int secFinal){
        servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<SubgroupVo>> respon_addGroup = service.getNewSubgroups(secFinal);

        respon_addGroup.enqueue(new Callback<List<SubgroupVo>>() {
            @Override
            public void onResponse(Call<List<SubgroupVo>> call, Response<List<SubgroupVo>> response) {
                if(response.isSuccessful()){
                    int awsFinal = awsListSeq.get(4).getTsec_final();
                    ArrayList <SubgroupVo> listSubg = new ArrayList<SubgroupVo>();

                    for(SubgroupVo ob : response.body()){
                        listSubg.add(ob);
                    }

                    dbHelper.addSubgroups(listSubg);

                    dbHelper.openDataBase();
                    dbHelper.updateSeqFinal(awsFinal, 5);
                    dbHelper.close();

                    Toast.makeText(GroupActivity.this, "se agrega subgrupos", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(GroupActivity.this, "error al agregar subgrupos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SubgroupVo>> call, Throwable t) {
                Toast.makeText(GroupActivity.this, "error al agregar subgrupos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void awsUpdateItems(){
        servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<ItemVo>> respon_updItem = service.getAllItems();

        respon_updItem.enqueue(new Callback<List<ItemVo>>() {
            @Override
            public void onResponse(Call<List<ItemVo>> call, Response<List<ItemVo>> response) {
                if(response.isSuccessful()){
                    int awsUpdate = awsListSeq.get(3).getTsec_update();
                    int awsFinal = awsListSeq.get(3).getTsec_final();

                    ArrayList<ItemVo> listItem = new ArrayList<ItemVo>();

                    for(ItemVo ob : response.body()){
                        listItem.add(ob);
                    }

                    dbHelper.deleteTableData("articulos");
                    dbHelper.addItems(listItem);

                    dbHelper.openDataBase();
                    dbHelper.updateSeqUpdate(awsUpdate, 4);
                    dbHelper.close();

                    dbHelper.openDataBase();
                    dbHelper.updateSeqFinal(awsFinal, 4);
                    dbHelper.close();

                } else{
                    Toast.makeText(GroupActivity.this, "error al actualizar articulos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ItemVo>> call, Throwable t) {
                Toast.makeText(GroupActivity.this, "falla al actualizar articulos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void awsAddItems(int finalItem){
        servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<ItemVo>> respon_addItem = service.getNewItems(finalItem);

        respon_addItem.enqueue(new Callback<List<ItemVo>>() {
            @Override
            public void onResponse(Call<List<ItemVo>> call, Response<List<ItemVo>> response) {
                if(response.isSuccessful()){
                    int awsFinal = awsListSeq.get(3).getTsec_final();
                    ArrayList<ItemVo> listItem = new ArrayList<ItemVo>();

                    for(ItemVo ob : response.body()){
                        listItem.add(ob);
                    }

                    dbHelper.addItems(listItem);

                    dbHelper.openDataBase();
                    dbHelper.updateSeqFinal(awsFinal, 4);
                    dbHelper.close();

                } else{
                    Toast.makeText(GroupActivity.this, "error al agregar articulos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ItemVo>> call, Throwable t) {
                Toast.makeText(GroupActivity.this, "falla al agregar articulos", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

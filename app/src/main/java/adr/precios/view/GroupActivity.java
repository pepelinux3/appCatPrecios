package adr.precios.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adrprecios.R;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import adr.precios.database.DBHelper;
import adr.precios.adapter.GruopAdapter;
import adr.precios.entities.GroupVo;
import adr.precios.entities.SequenceVo;
import adr.precios.wservices.AwsAsync_Prices;
import adr.precios.wservices.ServGenerator_AWS;
import adr.precios.wservices.servicesGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupActivity extends AppCompatActivity {

    RecyclerView recyclerGroup;
    GruopAdapter adapter;
    Menu menu;

    private Toolbar toolbar;
    public ProgressBar progressBar;
    public TextView txtProgress;

    public DBHelper dbHelper;

    public ArrayList<SequenceVo> sqlListSeq;
    public ArrayList<SequenceVo> awsListSeq;

    private AwsAsync_Prices awsInventory;
    public boolean awsRunning;
    public boolean startAWSPrices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos);

        dbHelper = new DBHelper(this);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        txtProgress = (TextView) findViewById(R.id.txtProgress);

        awsRunning = false;

        sqlListSeq = (ArrayList<SequenceVo>)getIntent().getExtras().getSerializable("sqlList");
        awsListSeq = (ArrayList<SequenceVo>)getIntent().getExtras().getSerializable("awsList");

        startAWSPrices = getIntent().getExtras().getBoolean("startPrices");

        if (savedInstanceState != null) {
            startAWSPrices = savedInstanceState.getBoolean("awsStart");
        }

        setUpToolBar();
        setUpHomeUpIconAndColor(R.drawable.ic_search, R.color.colorWhiteApp);
        fillRecyclerView();

        if(startAWSPrices == true){
            if(awsListSeq.get(1).getTsec_final() > sqlListSeq.get(1).getTsec_final()  || awsListSeq.get(1).getTsec_restore() > awsListSeq.get(1).getTsec_restore()){
                progressBar.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                txtProgress.setTextColor(Color.GREEN);

                awsInventory = new AwsAsync_Prices(GroupActivity.this);
                awsInventory.execute(1);

                Toast.makeText(this, "termina actualizar precios", Toast.LENGTH_SHORT).show();
            }
            startAWSPrices = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putBoolean("awsStart", startAWSPrices);
    }

    // CountDownTimer timer = new CountDownTimer(240 * 60 * 1000, 1000) {
        CountDownTimer timer = new CountDownTimer(  240 * 60 * 1000, 1000) {
        public void onTick(long millisUntilFinished) {
            //Some code
        }

        public void onFinish() {
            finish();
           // System.exit(0);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(awsRunning){
            awsInventory.cancel(true);
            awsRunning = false;
        }
    }

    public void fillRecyclerView(){
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

        if(id == R.id.me_promocion){
            Toast.makeText(this, "Opcion no disponible", Toast.LENGTH_SHORT).show();
        }

        if(id == R.id.me_actualizar){
            if (!awsRunning){
                dbHelper.openDataBase();
                sqlListSeq = dbHelper.getTableSequence();
                dbHelper.close();

                progressBar.getIndeterminateDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                txtProgress.setTextColor(Color.RED);

                awsGetSequence();

                System.out.println("Final INVENTARIO sql = "+sqlListSeq.get(0).getTsec_final()+"  aws = "+awsListSeq.get(0).getTsec_final()+" .......");
                System.out.println("update INVENTARIO sql = "+sqlListSeq.get(0).getTsec_update()+"  aws = "+awsListSeq.get(0).getTsec_restore()+" .......");
            } else {
                Toast.makeText(GroupActivity.this, "hay una actualizacion en proceso", Toast.LENGTH_SHORT).show();
            }
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

    public void awsAsyncTaskFinish(String finis){
        Toast.makeText(GroupActivity.this, finis, Toast.LENGTH_SHORT).show();

        int drawable = R.drawable.ic_action_check;
        msjUpdateDB(finis, drawable);
    }

    private void msjUpdateDB(String msj, int drawable){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(drawable);
        builder.setTitle("Base de datos");
        builder.setMessage(msj);
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void awsGetSequence (){
        servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
        Call<List<SequenceVo>> respon_sequence = service.getSeqUpdate();

        respon_sequence.enqueue(new Callback<List<SequenceVo>>() {
            @Override
            public void onResponse(Call<List<SequenceVo>> call, Response<List<SequenceVo>> response) {

                if(response.isSuccessful()){

                    ArrayList <SequenceVo> listSeq = new ArrayList<SequenceVo>();
                    for(SequenceVo ob : response.body()){
                        listSeq.add(ob);
                    }

                    awsListSeq = listSeq;

                    if(awsListSeq.get(0).getTsec_final() > sqlListSeq.get(0).getTsec_final()  || awsListSeq.get(0).getTsec_restore() > awsListSeq.get(0).getTsec_restore()){
                        awsInventory = new AwsAsync_Prices(GroupActivity.this);
                        awsInventory.execute(2);
                    } else {
                        msjUpdateDB("Los inventarios ya estan actualizado", R.drawable.ic_action_info);
                    }

                } else{
                    Toast.makeText(GroupActivity.this, "Falla response secuencia", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SequenceVo>> call, Throwable t) {
                Toast.makeText(GroupActivity.this, "Error conexion secuencia", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

package adr.precios.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.adrprecios.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import adr.precios.entities.BlogVo;
import adr.precios.entities.PriceVo;
import adr.precios.entities.SequenceVo;
import adr.precios.tools.CreateKey;
import adr.precios.database.DBHelper;
import adr.precios.wservices.AwsAsync_Login;
import adr.precios.wservices.AwsAsync_Prices;
import adr.precios.wservices.ServGenerator_AWS;
import adr.precios.wservices.servicesGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private EditText edUser;
    private EditText edPass;
    private EditText edKey;

    private String tv_key, branchAcces;
    static final int PERMISSION_READ_STATE = 123;

    public DBHelper dbHelper;
    public ArrayList <SequenceVo> awsListSeq;
    public ArrayList <SequenceVo> sqlListSeq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edUser = findViewById(R.id.txt_user);
        edPass = findViewById(R.id.txt_pass);
        edKey = findViewById(R.id.txt_key);

        dbHelper = new DBHelper(this);
        dbHelper.createDatabase();
    }

    public void start(View view)  {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //MyTelephonyManager();
            query();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSION_READ_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_READ_STATE: {
                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    query();
                } else {
                    Toast.makeText(MainActivity.this, "Tinees que aceptar el permiso", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void query(){
        String u = edUser.getText().toString();
        String p = edPass.getText().toString();
        tv_key = edKey.getText().toString();

        sqlListSeq = dbHelper.getTableSequence();
        dbHelper.close();

        dbHelper.openDataBase();
        branchAcces = dbHelper.getLoginBranch(u, p);
        dbHelper.close();

        if(branchAcces.isEmpty()){
            Toast.makeText(this, "Usuario no Existe", Toast.LENGTH_SHORT).show();
        }
        else{
            AwsAsync_Login task1 = new AwsAsync_Login(this);
            task1.execute(1);
            //  AwsAsync_Login task2 = new AwsAsync_Login(this);
            //  task2.execute(2);
        }
    }

    public BlogVo addBlog(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int idBranch = Integer.parseInt(branchAcces);

        String currTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String currDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        BlogVo blog = new BlogVo();
        blog.setTbi_clave(0);
        blog.setTbi_numcel(manager.getLine1Number());
        blog.setTbi_nombre(edUser.getText().toString());
        blog.setTbi_imei(manager.getDeviceId());
        blog.setTbi_mac("");
        blog.setTbi_accion("login  usuario");
        blog.setTbi_fechahora(currDate+"  "+currTime);
        blog.setTbi_descripcion("");
        blog.setTbi_sucursal(idBranch);

        return blog;
    }

    private void saveAWSBlog (BlogVo blogVo){
        servicesGroup service = ServGenerator_AWS.createService(servicesGroup.class);
        final Call<BlogVo> respon_blog = service.saveBlog(blogVo);

        respon_blog.enqueue(new Callback<BlogVo>() {
            @Override
            public void onResponse(Call<BlogVo> call, Response<BlogVo> response) {

                if(response.isSuccessful()){
                    Toast.makeText(MainActivity.this, "POST FUE UN EXITO", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(MainActivity.this, "POST TIENE UN ERROR", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<BlogVo> call, Throwable t) {
                Toast.makeText(MainActivity.this, "POST ESTA MUERTO", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void awsResp_GetDate(String json_date){

        if(json_date != ""){
            CreateKey key = new CreateKey();

            if(key.turnRequest(json_date).equals(tv_key)){
                AwsAsync_Login task2 = new AwsAsync_Login(this);
                task2.execute(2);

                saveAWSBlog(addBlog());
            } else{
                Toast.makeText(MainActivity.this, "Llave Incorrecta", Toast.LENGTH_SHORT).show();
            }

        } else{
            Toast.makeText(MainActivity.this, "no hay internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void awsResp_GetSequence(String json_data){

        try {
            if(json_data != ""){
                awsListSeq = new ArrayList<SequenceVo>();
                JSONArray JA = new JSONArray(json_data);

                for(int i =0;i < JA.length(); i++){
                    JSONObject JO = (JSONObject) JA.get(i);

                    awsListSeq.add(new SequenceVo(JO.getInt("tsec_clave"), JO.getInt("tsec_codigo"),
                            JO.getString("tsec_tabla"), JO.getString("tsec_fecha"),
                            JO.getInt("tsec_final"), JO.getInt("tsec_update"), JO.getInt("tsec_restore")));
                }
                checkSec_aws_sql_Group();
            }
            else {
                Toast.makeText(MainActivity.this, "no hay conexion al servidor", Toast.LENGTH_SHORT).show();
                entraGrupos(branchAcces);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void awsResp_UpdateGroups(){
        int awsRestore_Group = awsListSeq.get(5).getTsec_restore();
        int awsFinal_Group = awsListSeq.get(5).getTsec_final();

        dbHelper.openDataBase();
        dbHelper.updateSeqRestore(awsRestore_Group, 6);
        dbHelper.close();

        dbHelper.openDataBase();
        dbHelper.updateSeqFinal(awsFinal_Group, 6);
        dbHelper.close();

        entraGrupos(branchAcces);
        Toast.makeText(MainActivity.this, "Actualizacio de Grupos", Toast.LENGTH_SHORT).show();
    }

    public void awsResp_NewGroups(){
        int awsFinal_Group = awsListSeq.get(5).getTsec_final();

        dbHelper.openDataBase();
        dbHelper.updateSeqFinal(awsFinal_Group, 6);
        dbHelper.close();

        entraGrupos(branchAcces);
        Toast.makeText(MainActivity.this, "Agrega base de datos nuevos grupos", Toast.LENGTH_SHORT).show();
    }

    private void checkSec_aws_sql_Group(){
        int awsRestore_Group = awsListSeq.get(5).getTsec_restore();
        int sqlRestore_Group = sqlListSeq.get(5).getTsec_restore();

        int awsFinal_IdGroup = awsListSeq.get(5).getTsec_final();
        int sqlFinal_IdGroup = sqlListSeq.get(5).getTsec_final();

        if(awsRestore_Group > sqlRestore_Group){
            AwsAsync_Login task3 = new AwsAsync_Login(this);
            task3.execute(3);
        } else {
            if(awsFinal_IdGroup > sqlFinal_IdGroup){
                AwsAsync_Login task4 = new AwsAsync_Login(this);
                task4.execute(4);
            }
            else{
                System.out.println("NO ENTRA A NADA ");
                entraGrupos(branchAcces);
            }
        }
    }

    private void entraGrupos(String idBranch) {
        Intent act_grupos =  new Intent(this, GroupActivity.class);

        act_grupos.putExtra("awsList", (Serializable) awsListSeq);
        act_grupos.putExtra("sqlList", (Serializable) sqlListSeq);
        act_grupos.putExtra("noBranch", idBranch);
        act_grupos.putExtra("startPrices", true);

        startActivity(act_grupos);
    }
}

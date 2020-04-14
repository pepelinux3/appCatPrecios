package adr.precios.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.adrprecios.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adr.precios.entities.SequenceVo;
import adr.precios.tools.CreateKey;
import adr.precios.database.DBHelper;
import adr.precios.wservices.AwsGetData;


public class MainActivity extends AppCompatActivity {
    private EditText edUser;
    private EditText edPass;
    private EditText edKey;

    private String tv_key, branchAcces;

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

    public void query(View view)  {
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
            AwsGetData task1 = new AwsGetData(this);
            task1.execute(1);
        }
    }

    public void awsResp_GetDate(String json_date){

        if(json_date != ""){
            CreateKey key = new CreateKey();

            if(key.turnRequest(json_date).equals(tv_key)){
                AwsGetData task2 = new AwsGetData(this);
                task2.execute(2);
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
                            JO.getInt("tsec_final"), JO.getInt("tsec_update")));
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
        int awsIdGroup = awsListSeq.get(5).getTsec_update();

        dbHelper.openDataBase();
        dbHelper.updateSecGroup(awsIdGroup);
        dbHelper.close();

        entraGrupos(branchAcces);
        Toast.makeText(MainActivity.this, "Actualiza base de datos WS xxxxxx", Toast.LENGTH_SHORT).show();
    }

    private void checkSec_aws_sql_Group(){
        int awsIdGroup = awsListSeq.get(5).getTsec_update();
        int sqlIdGroup = sqlListSeq.get(5).getTsec_update();

        Toast.makeText(MainActivity.this, awsListSeq.get(5).getTsec_update()+" - "+ awsListSeq.get(5).getTsec_update()+" - "+ awsListSeq.get(5).getTsec_update()+" x", Toast.LENGTH_SHORT).show();
        Toast.makeText(MainActivity.this, "awsGroup = "+awsIdGroup+"  -  "+"sqlGroup = "+sqlIdGroup, Toast.LENGTH_SHORT).show();

        if(awsIdGroup > sqlIdGroup){
            AwsGetData task3 = new AwsGetData(this);
            task3.execute(3);
        } else {
            entraGrupos(branchAcces);
        }
    }

    private void entraGrupos(String idBranch) {
        Intent act_grupos =  new Intent(this, GroupActivity.class);

        act_grupos.putExtra("noBranch", idBranch);
        startActivity(act_grupos);
    }
}

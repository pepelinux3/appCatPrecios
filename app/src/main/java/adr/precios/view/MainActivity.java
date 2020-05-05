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

import java.io.Serializable;
import java.util.ArrayList;

import adr.precios.entities.SequenceVo;
import adr.precios.tools.CreateKey;
import adr.precios.database.DBHelper;
import adr.precios.wservices.AwsAsync_Login;


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
          //  AwsAsync_Login task1 = new AwsAsync_Login(this);
         //   task1.execute(1);
            AwsAsync_Login task2 = new AwsAsync_Login(this);
            task2.execute(2);
        }
    }

    public void awsResp_GetDate(String json_date){

        if(json_date != ""){
            CreateKey key = new CreateKey();

            if(key.turnRequest(json_date).equals(tv_key)){
                AwsAsync_Login task2 = new AwsAsync_Login(this);
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
        int awsUpdate_Group = awsListSeq.get(5).getTsec_update();
        int awsFinal_Group = awsListSeq.get(5).getTsec_final();

        dbHelper.openDataBase();
        dbHelper.updateSeqUpdate(awsUpdate_Group, 6);
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
        int awsUpdate_Group = awsListSeq.get(5).getTsec_update();
        int sqlUpdate_Group = sqlListSeq.get(5).getTsec_update();

        int awsFinal_IdGroup = awsListSeq.get(5).getTsec_final();
        int sqlFinal_IdGroup = sqlListSeq.get(5).getTsec_final();

        if(awsUpdate_Group > sqlUpdate_Group){
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

        startActivity(act_grupos);
    }
}

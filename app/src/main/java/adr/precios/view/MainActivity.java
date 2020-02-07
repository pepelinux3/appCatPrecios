package adr.precios.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adrprecios.R;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import adr.precios.tools.CreateKey;
import adr.precios.database.DBHelper;


public class MainActivity extends AppCompatActivity {
    private EditText edUser;
    private EditText edPass;
    private EditText edKey;

    private DBHelper dbHelper;

    private RequestQueue queue;
    private String dateJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edUser = findViewById(R.id.txt_user);
        edPass = findViewById(R.id.txt_pass);
        edKey = findViewById(R.id.txt_key);

        dbHelper = new DBHelper(getApplicationContext());
        dbHelper.createDatabase();

        queue = Volley.newRequestQueue(this);
        getWSDate();
    }


    public void query(View view) {
       // DataBaseAcces databaseAcces = DataBaseAcces.getInstance(getApplicationContext());
       // databaseAcces.open();

       // dbHelper = new DbHelper(getApplicationContext());
       // dbHelper.createDatabase();

        String u = edUser.getText().toString();
        String p = edPass.getText().toString();
        String k = edKey.getText().toString();

        String branchAcces = dbHelper.getLoginBranch(u, p);

        if(branchAcces.isEmpty()){
            Toast.makeText(this, "Usuario no Existe", Toast.LENGTH_SHORT).show();
        }
        else{
            getWSDate();

            if(dateJson != ""){
                CreateKey key = new CreateKey();

                if(key.turnRequest(dateJson).equals(k)){
                    Toast.makeText(this, "Bienvenido ", Toast.LENGTH_SHORT).show();
                    entraGrupos(branchAcces);
                }
                else{
                    Toast.makeText(this, "Llave Incorrecta", Toast.LENGTH_SHORT).show();
                }
            }


        }
        dbHelper.close();
    }

    public void entraGrupos(String idBranch) {
        Intent act_grupos =  new Intent(this, GroupActivity.class);

        act_grupos.putExtra("noBranch", idBranch);
        startActivity(act_grupos);
    }

    private void getWSDate(){

        String url = "https://worldtimeapi.org/api/timezone/America/Mexico_City";

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dateJson = "";
                try{
                    JSONObject mJsonObject = response;
                    dateJson = mJsonObject.getString("datetime");

                   // Toast.makeText(MainActivity.this, "Fecha: "+dateJson, Toast.LENGTH_SHORT).show();

                } catch (JSONException e){
                    e.printStackTrace();
                }

            }
        },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dateJson = "";
                        Toast.makeText(MainActivity.this, "No hay acceso a Internet ", Toast.LENGTH_LONG).show();
                    }
                });

        queue.add(request);

    }
}

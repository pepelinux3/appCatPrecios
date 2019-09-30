package adr.ejemplo.adrprecios;

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


public class MainActivity extends AppCompatActivity {
    private EditText edUser;
    private EditText edPass;
    private EditText edKey;

    private RequestQueue queue;
    private String dateJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edUser = findViewById(R.id.txt_user);
        edPass = findViewById(R.id.txt_pass);
        edKey = findViewById(R.id.txt_key);

        queue = Volley.newRequestQueue(this);
        obtenerDatosVolley();
    }

    public void query(View view) {
        DataBaseAcces databaseAcces = DataBaseAcces.getInstance(getApplicationContext());
        databaseAcces.open();

        String u = edUser.getText().toString();
        String p = edPass.getText().toString();
        String k = edKey.getText().toString();

        String acceso = databaseAcces.getLogin(u, p);

        if(acceso.isEmpty()){
            Toast.makeText(this, "Usuario no Existe", Toast.LENGTH_SHORT).show();
        }
        else{
            obtenerDatosVolley();

            if(dateJson != ""){
                CreateKey key = new CreateKey();
            //    Toast.makeText(this, key.turnRequest(dateJson), Toast.LENGTH_SHORT).show();

                if(key.turnRequest(dateJson).equals(k)){
                    Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
                    entraGrupos();
                }
                else{
                    Toast.makeText(this, "Llave Incorrecta", Toast.LENGTH_SHORT).show();
                }
            }
        }
        databaseAcces.close();
    }

    public void entraGrupos() {
        Intent act_grupos =  new Intent(this, GroupActivity.class);
        startActivity(act_grupos);
    }

    private void obtenerDatosVolley(){

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
                        Toast.makeText(MainActivity.this, "No hay acceso a Intener ", Toast.LENGTH_LONG).show();
                    }
                });

        queue.add(request);

    }
}

package com.example.adrprecios;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private EditText edUser;
    private EditText edPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edUser = findViewById(R.id.txt_user);
        edPass = findViewById(R.id.txt_pass);
    }

    public void query(View view) {
        DataBaseAcces databaseAcces = DataBaseAcces.getInstance(getApplicationContext());
        databaseAcces.open();

        String u = edUser.getText().toString();
        String p = edPass.getText().toString();

        String acceso = databaseAcces.getLogin(u, p);

        if(acceso.isEmpty()){
            Toast.makeText(this, "Usuario no Existe", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
            entraGrupos();
        }
        databaseAcces.close();
    }

    public void entraGrupos() {
        Intent act_grupos =  new Intent(this, GroupActivity.class);
        startActivity(act_grupos);
    }
}

package br.com.wdespesas.wdespesas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class dashboard extends AppCompatActivity {

    private Button btnUtilitarios;
    private Button btnCategoria;
    private Button btnListarDepesas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        btnUtilitarios = (Button) findViewById(R.id.btnUtilitarios);
        btnListarDepesas = (Button) findViewById(R.id.btnListarDespesas);

        btnUtilitarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(dashboard.this,activity_util_sqlite.class);
                startActivity(i);
            }
        });


        btnCategoria = (Button) findViewById(R.id.btnCategorias);
        btnCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(dashboard.this,categoria_lista.class);
                startActivity(i);
            }
        });

        btnListarDepesas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(dashboard.this, despesa_lista.class);
                startActivity(i);
            }
        });
    }
}

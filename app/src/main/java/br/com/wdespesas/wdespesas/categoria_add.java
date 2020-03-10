package br.com.wdespesas.wdespesas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import br.com.wdespesas.wdespesas.dao.CategoriaDAO;
import br.com.wdespesas.wdespesas.model.Categoria;

/**
 * Created by Master on 08/08/2016.
 */
public class categoria_add extends AppCompatActivity {

    private Button btnCategoriaSalvar;
    private EditText edtDescricao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_categoria_add);
        setTitle("Incluir Categoria");

        edtDescricao = (EditText) findViewById(R.id.edtdescricao);
        btnCategoriaSalvar = (Button) findViewById(R.id.btnCategoriaSalvar);

        btnCategoriaSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(edtDescricao.getText().toString())){
                    edtDescricao.setError("Campo Obrigatório !");
                    return;
                }

                Categoria categoria = new Categoria();
                categoria.setDescricao(edtDescricao.getText().toString());
                CategoriaDAO categoriaDAO = new CategoriaDAO(categoria_add.this);
                categoriaDAO.Inserir(categoria);
                Toast.makeText(categoria_add.this,"Registro inserido com sucesso !",Toast.LENGTH_LONG).show();
                finish();
            }
        });



    }
}

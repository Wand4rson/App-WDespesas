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
public class categoria_alterar extends AppCompatActivity {

    private Button btnCategoriaSalvar;
    private EditText edtDescricao;
    Categoria categoria = new Categoria();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_categoria_add);
        setTitle("Alterar Categoria");

        //Recupera os dados a serem alterados
        categoria = (Categoria) getIntent().getExtras().getSerializable("objcategoria");

        edtDescricao = (EditText) findViewById(R.id.edtdescricao);
        btnCategoriaSalvar = (Button) findViewById(R.id.btnCategoriaSalvar);
        edtDescricao.setText(categoria.getDescricao()); //preenche campo

        btnCategoriaSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(edtDescricao.getText().toString())){
                    edtDescricao.setError("Campo Obrigatório !");
                    return;
                }

                categoria.setDescricao(edtDescricao.getText().toString());

                CategoriaDAO categoriaDAO = new CategoriaDAO(categoria_alterar.this);
                categoriaDAO.Alterar(categoria);
                Toast.makeText(categoria_alterar.this,"Registro alterado com sucesso !",Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }
}

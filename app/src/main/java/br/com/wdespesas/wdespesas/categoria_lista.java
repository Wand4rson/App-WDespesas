package br.com.wdespesas.wdespesas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.List;

import br.com.wdespesas.wdespesas.dao.CategoriaDAO;
import br.com.wdespesas.wdespesas.model.Categoria;

/**
 * Created by Master on 08/08/2016.
 */

public class categoria_lista extends AppCompatActivity {

    private FloatingActionButton btnCategoriaAdd;
    private ListView lvcategoria;
    private Categoria categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_categoria_listagem);
        setTitle("Lista de Categorias");


        lvcategoria = (ListView) findViewById(R.id.lvcategoria);
        btnCategoriaAdd = (FloatingActionButton) findViewById(R.id.btnCategoriaAdd);

        registerForContextMenu(lvcategoria);//Informamos que o ListView possui um arquivo de menu vinculado//
        PreencheLista();

        btnCategoriaAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(categoria_lista.this, categoria_add.class);
                startActivity(i);
            }
        });


       lvcategoria.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               categoria = (Categoria) parent.getItemAtPosition(position); //Pega o Obj
               return false;
           }
       });
    }

    public void PreencheLista(){
        List<Categoria> lstCategorias;
        CategoriaDAO cDAO = new CategoriaDAO(categoria_lista.this);

        lstCategorias = cDAO.ListarTodos(); //Recupera do bd os dados
        ArrayAdapter<Categoria> arrayAdapter = new ArrayAdapter<Categoria>(
                categoria_lista.this,android.R.layout.simple_list_item_1,lstCategorias); //cria o adapter para o listview
        lvcategoria.setAdapter(arrayAdapter);

    }

    private void Excluir(){
        final CategoriaDAO cDAO = new CategoriaDAO(categoria_lista.this);

        //Categoria sendo utilizada em lançamentos, impossível excluir
        if ("sim".equals(cDAO.CategoriaUtilizadaDespesas(categoria.getCodigo()))){
            Toast.makeText(categoria_lista.this,"Categoria em uso ! Impossível excluir.",Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder dialogo = new AlertDialog.Builder(categoria_lista.this);
        dialogo.setMessage("Confirma a exclusão de : " +  categoria.getDescricao());
        dialogo.setIcon(R.drawable.deleteicon);

        dialogo.setPositiveButton("Sim",new DialogInterface.OnClickListener() {

            //Metodo executado se clicar em sim
            @Override
            public void onClick(DialogInterface dialog, int which) {

                cDAO.Excluir(categoria.getCodigo());
                PreencheLista();
            }
        });

        ///Caso clique em nao
        dialogo.setNegativeButton("Não",null);
        AlertDialog dialog = dialogo.create();
        dialogo.setTitle("Atenção");

        //Exibe Mensagem
        dialogo.show();
    }

    private void Alterar(){
        Intent alterar = new Intent(categoria_lista.this,categoria_alterar.class);
        alterar.putExtra("objcategoria",categoria);
        startActivity(alterar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreencheLista();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_lista_categoria,menu);//infla o menu
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.mnucat_alterar:
                Alterar();
                break;

            case R.id.mnucat_remover:
                Excluir();
                break;

            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}

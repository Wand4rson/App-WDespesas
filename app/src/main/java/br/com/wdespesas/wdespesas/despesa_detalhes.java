package br.com.wdespesas.wdespesas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.wdespesas.wdespesas.dao.CategoriaDAO;
import br.com.wdespesas.wdespesas.dao.DespesaDAO;
import br.com.wdespesas.wdespesas.model.Categoria;
import br.com.wdespesas.wdespesas.model.Despesa;

/**
 * Created by Master on 09/08/2016.
 */
public class despesa_detalhes extends AppCompatActivity {


    private EditText edtDescricao;
    private Spinner spnCategorias;
    private EditText edtVencimento;
    private EditText edtValorParcela;
    private Button btnDespesaAlterar;
    private Button btnDespesaPagar;
    private Button btnDespesaExcluir;
    private TextView tvDetalhePago;

    private List<Categoria> lstCategorias; //Lista de todas as categorias do bd no spinner
    private Categoria categoria;
    Despesa despesa = new Despesa();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_despesa_detalhes);
        setTitle("Despesas Detalhes");

        //Recupera a Despesa passada
        despesa = (Despesa) getIntent().getExtras().getSerializable("objdespesa");


        edtDescricao = (EditText) findViewById(R.id.edtdescricao);
        spnCategorias = (Spinner) findViewById(R.id.spnCategorias);
        edtVencimento = (EditText) findViewById(R.id.edtVencimento);
        edtValorParcela = (EditText) findViewById(R.id.edtValorParcela);
        btnDespesaAlterar = (Button) findViewById(R.id.btnDespesaAlterar);
        btnDespesaPagar = (Button) findViewById(R.id.btnDespesaPagar);
        btnDespesaExcluir = (Button) findViewById(R.id.btnDespesaExcluir);
        tvDetalhePago = (TextView) findViewById(R.id.tvDetalhePago);


        PopularSpinner();
        BloqueiaCampo();

        //Seta Valores nos Campos de acordo com valor recebido
        edtDescricao.setText(despesa.getDescricao().toString());
        edtValorParcela.setText(String.valueOf(despesa.getValorparcela()).toString());

        SetaSpinnerCategoriaSelecionada();

        //Vencimento Formatar
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        edtVencimento.setText(dateFormat.format(despesa.getDatavencimento()));

        if ("sim".equals(despesa.getPago().toString())){
            tvDetalhePago.setVisibility(View.VISIBLE);
            btnDespesaPagar.setText("Estornar");
        }else{
            tvDetalhePago.setVisibility(View.INVISIBLE);
            btnDespesaPagar.setText("Pagar");
        }


        btnDespesaAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("sim".equals(despesa.getPago().toString())){
                    Toast.makeText(despesa_detalhes.this,"Atenção lançamento Pago impossível Alterar !",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent alterar = new Intent(despesa_detalhes.this,despesa_alterar.class);
                alterar.putExtra("objdespesa", despesa);
                startActivity(alterar);
                finish();
            }
        });


        btnDespesaExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DespesaDAO dDAO = new DespesaDAO(despesa_detalhes.this);

                if ("sim".equals(despesa.getPago().toString())){
                    Toast.makeText(despesa_detalhes.this,"Atenção lançamento Pago impossível Remover !",Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder dialogo = new AlertDialog.Builder(despesa_detalhes.this);
                dialogo.setMessage("Confirma a exclusão da despesa  : " +  despesa.getDescricao().toString());
                dialogo.setIcon(R.drawable.deleteicon);

                dialogo.setPositiveButton("Sim",new DialogInterface.OnClickListener() {

                    //Metodo executado se clicar em sim
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dDAO.ExcluirParcela(despesa.getCodigo(), despesa.getParcela());
                        Toast.makeText(despesa_detalhes.this,"Lançamento removido com sucesso !",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                ///Caso clique em nao
                dialogo.setNegativeButton("Não",null);
                AlertDialog dialog = dialogo.create();
                dialogo.setTitle("Atenção");

                //Exibe Mensagem
                dialogo.show();
            }
        });


        btnDespesaPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DespesaDAO dDAO = new DespesaDAO(despesa_detalhes.this);

                if ("sim".equals(despesa.getPago().toString())) {

                    //Se já está pago, então estorna
                    despesa.setPago("nao");
                    dDAO.EstornarDocumento(despesa);

                    Toast.makeText(despesa_detalhes.this,"Lançamento estornado com sucesso !",Toast.LENGTH_SHORT).show();
                    finish();
                } else {

                    //Não está pago então, faz pagamento//

                    //Formatação de Datas para Gravar no DB//
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    /*Recuperando data atual do Android*/
                    Date dtPagamento = new Date();
                    despesa.setDatapagamento(dtPagamento);
                    despesa.setPago("sim");

                    dDAO.QuitarDocumento(despesa);
                    Toast.makeText(despesa_detalhes.this,"Lançamento pago com sucesso !",Toast.LENGTH_SHORT).show();
                    finish();

                }
            }
        });

    }

    private void PopularSpinner() {
        CategoriaDAO cDAO = new CategoriaDAO(despesa_detalhes.this);
        lstCategorias = cDAO.ListarTodos();

        ArrayAdapter<Categoria> Adapter = new ArrayAdapter<Categoria>(despesa_detalhes.this, android.R.layout.simple_spinner_item, lstCategorias);
        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spnCategorias.setAdapter(Adapter);
    }

    private void BloqueiaCampo(){
        edtDescricao.setEnabled(false);
        spnCategorias.setEnabled(false);
        edtVencimento.setEnabled(false);
        edtValorParcela.setEnabled(false);
    }

    //Pega o ID da categoria salvo no BD e Seta corretamente sua Descrição no Spinner para a Alteracao//
    private void SetaSpinnerCategoriaSelecionada() {
        for (int index = 0; index < lstCategorias.size(); index++) {

            if ((lstCategorias.get(index).getCodigo() == (despesa.getCategoria()))) {
                spnCategorias.setSelection(index);
                break;
            }
        }
    }


}
package br.com.wdespesas.wdespesas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.wdespesas.wdespesas.dao.CategoriaDAO;
import br.com.wdespesas.wdespesas.dao.DespesaDAO;
import br.com.wdespesas.wdespesas.fragment.DatePickerFragment;
import br.com.wdespesas.wdespesas.model.Categoria;
import br.com.wdespesas.wdespesas.model.Despesa;

/**
 * Created by Master on 09/08/2016.
 */
public class despesa_add extends AppCompatActivity {


    private EditText edtDescricao;
    private Spinner spnCategorias;
    private EditText edtVencimento;
    private EditText edtValorParcela;
    private Button btnDespesaSalvar;
    private EditText edtQtdeParcelas;
    private CheckBox chkRepetirParcelas;
    private TextInputLayout lblQtdeParcelas;

    private List<Categoria> lstCategorias;
    private Categoria categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_despesa_add);
        setTitle("Despesas Adicionar");

        edtDescricao = (EditText) findViewById(R.id.edtdescricao);
        spnCategorias = (Spinner) findViewById(R.id.spnCategorias);
        edtVencimento = (EditText) findViewById(R.id.edtVencimento);
        edtValorParcela = (EditText) findViewById(R.id.edtValorParcela);
        btnDespesaSalvar = (Button) findViewById(R.id.btnDespesaSalvar);
        edtQtdeParcelas = (EditText) findViewById(R.id.edtQtdeParcelas);
        chkRepetirParcelas = (CheckBox) findViewById(R.id.chkRepetirParcelas);
        lblQtdeParcelas = (TextInputLayout) findViewById(R.id.lblQtdeParcelas);

        PopularSpinner();


        //Se Checkbox está Marcado Mostra Campo Qtde Parcela e Obriga preencher//
        chkRepetirParcelas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkRepetirParcelas.isChecked()) {
                    lblQtdeParcelas.setVisibility(View.VISIBLE);
                    edtQtdeParcelas.setVisibility(View.VISIBLE);
                    edtQtdeParcelas.requestFocus();
                } else {
                    //Não Está Marcado
                    lblQtdeParcelas.setVisibility(View.INVISIBLE);
                    edtQtdeParcelas.setText("");
                    edtQtdeParcelas.setVisibility(View.INVISIBLE);
                }
            }
        });


        btnDespesaSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("".equals(edtDescricao.getText().toString())) {
                    edtDescricao.setError("Campo Obrigatório !");
                    return;
                }

                if ("".equals(edtVencimento.getText().toString())) {
                    edtVencimento.setError("Campo Obrigatório !");
                    return;
                }

                if ("".equals(edtValorParcela.getText().toString())) {
                    edtValorParcela.setError("Campo Obrigatório !");
                    return;
                }

                //Está Marcado, Obriga preencher Qtde Parcelas//
                if (chkRepetirParcelas.isChecked()) {

                    if ("".equals(edtQtdeParcelas.getText().toString())) {
                        edtQtdeParcelas.setError("Campo Obrigatório !");
                        return;
                    }

                    if ("0".equals(edtQtdeParcelas.getText().toString())) {
                        edtQtdeParcelas.setError("Qtde de Parcelas não pode ser zero !");
                        return;
                    }

                }


                Despesa despesa = new Despesa();
                DespesaDAO dDAO = new DespesaDAO(despesa_add.this);
                int iQtdeParcelas;
                String sNroDocumentoChave;
                Calendar CalIncrementaVencimento = Calendar.getInstance();

                //Qtde de Parcelas é Zero inicia ela com 1//
                if ("".equals(edtQtdeParcelas.getText().toString())) {
                    iQtdeParcelas = 1;
                } else {
                    iQtdeParcelas = Integer.parseInt(edtQtdeParcelas.getText().toString());
                }

                if (iQtdeParcelas == 0) {
                    iQtdeParcelas = 1;
                }

                //Cria Regra para NroDocChave ser Unico para pode Usar para exclusão//
                sNroDocumentoChave = "";
                sNroDocumentoChave = dDAO.sFormaCampoNroChaveUnico();

                for (int i = 1; i <= iQtdeParcelas; i++) {

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                    despesa.setDescricao(edtDescricao.getText().toString());
                    despesa.setValorparcela(Double.parseDouble(edtValorParcela.getText().toString()));
                    despesa.setParcela(i);
                    despesa.setCategoria(categoria.getCodigo());//Valor Spinner, usando valor recuperado do spinner no onitemselected

                    Date dtLancamento = new Date();
                    despesa.setDatalancamento(dtLancamento);

                    //Data do Vencimento Incrementar Mês//
                    try {

                        if (i == 1) {
                            //É a Primeira Parcela usa Vencimento Selecionado no EditText//
                            Date dtVencimento = dateFormat.parse(edtVencimento.getText().toString());
                            despesa.setDatavencimento(dtVencimento);

                            CalIncrementaVencimento.setTime(dtVencimento);//Armazena o Vencimento da Primeira Parcela
                        } else {
                            //Não é a Primeira Parcela, Incrementa Mês a Mês


                            //Pega o Vencimento da Primeira Parcela armazenado em Variavel e Incrementa o Mês//
                            CalIncrementaVencimento.set(Calendar.MONTH, (CalIncrementaVencimento.get(Calendar.MONTH) + 1));
                            Date dtVencimento = CalIncrementaVencimento.getTime(); //Novo Vencimento
                            despesa.setDatavencimento(dtVencimento); //Salva novo vencimento
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    despesa.setQtdeparcelas(iQtdeParcelas);//Qtde de Parcelas a Lançar
                    despesa.setChavedocparcelas(sNroDocumentoChave);//Nro Documento Chave para Identificar a Exclusao de Todos os Doctos


                    //
                    dDAO.Inserir(despesa);

                }


                Toast.makeText(despesa_add.this, "Registro inserido com sucesso !", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        spnCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Qual codigo no BD do Combo selecionado
                categoria = (Categoria) parent.getItemAtPosition(position);
                //Toast.makeText(despesa_add.this, "Código : " + categoria.getCodigo(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        edtVencimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();

                Calendar cal = Calendar.getInstance();

                Bundle bundle = new Bundle();
                bundle.putInt("dia", cal.get(Calendar.DAY_OF_MONTH));
                bundle.putInt("mes", cal.get(Calendar.MONTH));
                bundle.putInt("ano", cal.get(Calendar.YEAR));

                datePickerFragment.setArguments(bundle);
                datePickerFragment.setDateListener(dateListener);
                datePickerFragment.show(getFragmentManager(), "Data Vencimento");

            }
        });
    }

    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            //Seta a Data Escolhida no campo
            edtVencimento.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        }
    };


    private void PopularSpinner(){
        CategoriaDAO cDAO = new CategoriaDAO(despesa_add.this);
        lstCategorias = cDAO.ListarTodos();

        ArrayAdapter<Categoria> Adapter = new ArrayAdapter<Categoria>(despesa_add.this,android.R.layout.simple_spinner_item,lstCategorias);
        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spnCategorias.setAdapter(Adapter);

    }
}

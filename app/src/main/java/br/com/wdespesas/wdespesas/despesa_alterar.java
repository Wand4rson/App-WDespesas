package br.com.wdespesas.wdespesas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
public class despesa_alterar extends AppCompatActivity {


    private EditText edtDescricao;
    private Spinner spnCategorias;
    private EditText edtVencimento;
    private EditText edtValorParcela;
    private Button btnDespesaSalvar;

    private List<Categoria> lstCategorias; //Lista de todas as categorias do bd no spinner
    private Categoria categoria;
    Despesa despesa = new Despesa();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_despesa_alterar);
        setTitle("Despesas Alterar");

        //Recupera a Despesa passada
        despesa = (Despesa) getIntent().getExtras().getSerializable("objdespesa");


        edtDescricao = (EditText) findViewById(R.id.edtdescricao);
        spnCategorias = (Spinner) findViewById(R.id.spnCategorias);
        edtVencimento = (EditText) findViewById(R.id.edtVencimento);
        edtValorParcela = (EditText) findViewById(R.id.edtValorParcela);
        btnDespesaSalvar = (Button) findViewById(R.id.btnDespesaSalvar);

        PopularSpinner();

        //Seta Valores nos Campos de acordo com valor recebido
        edtDescricao.setText(despesa.getDescricao().toString());
        edtValorParcela.setText(String.valueOf(despesa.getValorparcela()).toString());

        SetaSpinnerCategoriaSelecionada();

        //Vencimento Formatar
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        edtVencimento.setText(dateFormat.format(despesa.getDatavencimento()));

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

                DespesaDAO dDAO = new DespesaDAO(despesa_alterar.this);

                despesa.setDescricao(edtDescricao.getText().toString());
                despesa.setValorparcela(Double.parseDouble(edtValorParcela.getText().toString()));
                despesa.setParcela(despesa.getParcela());

                //Formatação de Datas para Gravar no DB//
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");



                /*Recuperando data atual do Android*/
                Date dtLancamento = new Date();
                despesa.setDatalancamento(dtLancamento);
                /***********************************/


                //Data de Vencimento
                try {
                    Date dtVencimento = dateFormat.parse(edtVencimento.getText().toString());
                    despesa.setDatavencimento(dtVencimento);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Valor Spinner, usando valor recuperado do spinner no onitemselected
                despesa.setCategoria(categoria.getCodigo());

                dDAO.Alterar(despesa);

                Toast.makeText(despesa_alterar.this, "Registro Alterado com sucesso !", Toast.LENGTH_SHORT).show();
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


    private void PopularSpinner() {
        CategoriaDAO cDAO = new CategoriaDAO(despesa_alterar.this);
        lstCategorias = cDAO.ListarTodos();

        ArrayAdapter<Categoria> Adapter = new ArrayAdapter<Categoria>(despesa_alterar.this, android.R.layout.simple_spinner_item, lstCategorias);
        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spnCategorias.setAdapter(Adapter);

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
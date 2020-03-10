package br.com.wdespesas.wdespesas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.wdespesas.wdespesas.adapter.DespesasArrayAdapter;
import br.com.wdespesas.wdespesas.dao.DespesaDAO;
import br.com.wdespesas.wdespesas.model.Despesa;

/**
 * Created by Master on 09/08/2016.
 */
public class despesa_lista extends AppCompatActivity {

    private FloatingActionButton btnDespesasAdd;
    private ListView lvDespesas;
    private Despesa despesa;
    private TextView tvTotalRodape;
    private Button btnAvancarData;
    private Button btnVoltarData;
    private TextView tvMesAno;
    private Calendar dtAtual=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_despesa_listagem);
        setTitle("Lista de Despesas");


        btnDespesasAdd = (FloatingActionButton) findViewById(R.id.btnDespesasAdd);
        lvDespesas = (ListView) findViewById(R.id.lvDespesas);
        tvTotalRodape = (TextView) findViewById(R.id.tvTotalRodape);
        btnAvancarData = (Button) findViewById(R.id.btnRight);
        btnVoltarData = (Button) findViewById(R.id.btnLeft);
        tvMesAno = (TextView) findViewById(R.id.tvMesAno);

        registerForContextMenu(lvDespesas);

        dtAtual =  Calendar.getInstance(); //Data Atual do Smartphone
        ArrayMesAno(dtAtual, 0); //Data e Mes Corrente //Abriu Tela Preenche o Array com o Periodo Corrente//
        PreencheLista();


        btnVoltarData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayMesAno(dtAtual, -1); //Volta um Mes da Data Atual
                PreencheLista();
            }
        });

        btnAvancarData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayMesAno(dtAtual,+1); //Adiciona um mes na data Atual
                PreencheLista();
            }
        });


       /*
        lvDespesas.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                despesa = (Despesa) parent.getItemAtPosition(position);

                Toast.makeText(despesa_lista.this, "" +
                        "Código Lç : " + String.valueOf(despesa.getCodigo()).toString() + "" +
                        "Descrição : " + despesa.getDescricao().toString() + "" +
                        "Valor     : " + String.valueOf(despesa.getValorparcela()).toString() + "" +
                        "Categoria : " + String.valueOf(despesa.getCategoria()).toString() + "" +
                        "Pago      : " + despesa.getPago().toString() + "" +
                        "DTPago    : " + despesa.getDatapagamento().toString(), Toast.LENGTH_LONG).show();


            }
        });
    */


        lvDespesas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                despesa = (Despesa) parent.getItemAtPosition(position);
                ShowDetalhes();
            }
        });

        lvDespesas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                despesa = (Despesa) parent.getItemAtPosition(position);
                return false;
            }
        });



        btnDespesasAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(despesa_lista.this, despesa_add.class);
                startActivity(i);
            }
        });

    }

    private void PreencheLista(){
        List<Despesa> lstDespesas;
        DespesaDAO dDAO = new DespesaDAO(despesa_lista.this);
        String sMes = String.valueOf(dtAtual.get(Calendar.MONTH) + 1);//Mes (Somo mais um pois o indice dos meses começa com zero)
        String sAno = String.valueOf(dtAtual.get(Calendar.YEAR)); //Ano

        try {
            lstDespesas = dDAO.ListarTodosData(sMes, sAno);
            DespesasArrayAdapter despesaDespesasArrayAdapter = new DespesasArrayAdapter(despesa_lista.this,R.layout.layout_linha_despesa, lstDespesas);
            lvDespesas.setAdapter(despesaDespesasArrayAdapter);

            //Labels
            DecimalFormat valorFormat = new DecimalFormat("0.00");
            Double dTotalPago = dDAO.TotalLabes(sMes,sAno,"sim");   //Total Pago
            Double dTotalAPagar = dDAO.TotalLabes(sMes,sAno,"nao"); //Total a Pagar
            tvTotalRodape.setText("Pago R$: " + valorFormat.format(dTotalPago)  + "   A Pagar R$: " + valorFormat.format(dTotalAPagar));


        } catch (ParseException e) {
            Log.e("erro_lista_todos", e.getMessage());
        }


        /*
        ArrayAdapter<Despesa> arrayAdapter = new ArrayAdapter<Despesa>(
                despesa_lista.this,android.R.layout.simple_list_item_1,lstDespesas); //cria o adapter para o listview
        lvDespesas.setAdapter(arrayAdapter);
        */


    }

    @Override
    protected void onResume() {
        super.onResume();
        PreencheLista();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_lista_despesas, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.mnudesp_alterar:
                Alterar();
                break;
            case R.id.mnudesp_remover:
                DialogoExcluir();
                break;
            case R.id.mnudesp_quitar:
                Quitar();
                break;
            case R.id.mnudesp_estornar:
                Estornar();
                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);
    }


    private void DialogoExcluir(){
        String sMessage ="";
        String sMSGPOSITIVO="";
        String sMSGNEGATIVO="";
        String sMSGCANCELAR="";

        if ("sim".equals(despesa.getPago().toString())){
            Toast.makeText(despesa_lista.this,"Atenção lançamento Pago impossível Remover !",Toast.LENGTH_SHORT).show();
            return;
        }


        AlertDialog.Builder dialogo = new AlertDialog.Builder(despesa_lista.this);

        if (despesa.getQtdeparcelas() > 1){
            sMessage="Esta despesa se repete em outras datas. Quais Parcelas Excluir?";
            sMSGPOSITIVO="Somente esta Parcela?";
            sMSGNEGATIVO="Todas as Parcelas?";
            sMSGCANCELAR="Cancelar";
        }else{
            sMessage="Confirma a exclusão da Parcela Selecionada ? : " +despesa.getDescricao().toString();
            //Quando é somente uma parcela, ele Usa só os botoes msgpositivo e cancelar
            sMSGPOSITIVO="Sim";
            sMSGNEGATIVO="Não";
            sMSGCANCELAR="Não";
        }

        dialogo.setMessage(sMessage);
        dialogo.setIcon(R.drawable.deleteicon);

        dialogo.setPositiveButton(sMSGPOSITIVO, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ExcluirParcela();
            }
        });


        //Mostrar este Dialog quando existir mais de uma parcela
        if (despesa.getQtdeparcelas() > 1) {

            dialogo.setNegativeButton(sMSGNEGATIVO, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ExcluirTodos();
                }
            });

        }

        dialogo.setNeutralButton(sMSGCANCELAR, null);

        dialogo.setTitle("Atenção");
        dialogo.show();
    }


    private void ExcluirTodos(){
        //Excluir Todas as Parcelas da Chave de Docto//
        DespesaDAO dDAO = new DespesaDAO(despesa_lista.this);
        dDAO.ExcluirDoctoChave(despesa.getChavedocparcelas());
        PreencheLista();
  }


    private void ExcluirParcela(){
        DespesaDAO dDAO = new DespesaDAO(despesa_lista.this);
        dDAO.ExcluirParcela(despesa.getCodigo(), despesa.getParcela());
        PreencheLista();
    }

    private void ShowDetalhes(){
        Intent detalhes = new Intent(despesa_lista.this,despesa_detalhes.class);
        detalhes.putExtra("objdespesa", despesa);
        startActivity(detalhes);
    }

    private void Alterar(){
        if ("sim".equals(despesa.getPago().toString())){
            Toast.makeText(despesa_lista.this,"Atenção lançamento Pago impossível Alterar !",Toast.LENGTH_SHORT).show();
            return;
        }

        Intent alterar = new Intent(despesa_lista.this,despesa_alterar.class);
        alterar.putExtra("objdespesa", despesa);
        startActivity(alterar);
    }

    private void Quitar(){

        if ("sim".equals(despesa.getPago().toString())){
            Toast.makeText(despesa_lista.this,"Atenção lançamento Pago impossível Pagar Novamente !",Toast.LENGTH_SHORT).show();
            return;
        }

        DespesaDAO dDAO = new DespesaDAO(despesa_lista.this);

        //Formatação de Datas para Gravar no DB//
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        /*Recuperando data atual do Android*/
        Date dtPagamento = new Date();
        despesa.setDatapagamento(dtPagamento);
        despesa.setPago("sim");

        dDAO.QuitarDocumento(despesa);
        PreencheLista();
    }

    private void Estornar(){
        DespesaDAO dDAO = new DespesaDAO(despesa_lista.this);

        despesa.setPago("nao");

        dDAO.EstornarDocumento(despesa);
        PreencheLista();
    }


    public void ArrayMesAno(Calendar DataAtual, int incrementaMes){

        String sMostraTela=null;
        Calendar cal = Calendar.getInstance();
        String sMes [] = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};

        int iMes = 0;
        int iAno = 0;

        cal.setTime(DataAtual.getTime());//Data informada pelo usuario ou a mesma data do sistema

        //Avançou Adiciona mais um Mes na Variavel + 1
        if (incrementaMes > 0) {
            cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH) + 1));//Insere + 1 Mes na Data
            dtAtual.set(Calendar.MONTH, (dtAtual.get(Calendar.MONTH) + 1));//Insere + 1 Mes na Data
            //cal.set(Calendar.YEAR, Calendar.YEAR + 1 ); //Insere um Ano na Data
        }

        //Voltou remove um Mes da Variavel -1
        else if(incrementaMes < 0 ){
            cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH) - 1));//Insere + 1 Mes na Data
            dtAtual.set(Calendar.MONTH, (dtAtual.get(Calendar.MONTH) - 1));//Insere + 1 Mes na Data
        }
        //Se é o Mes Corrente não faz nada e matem o mes atual
        else if (incrementaMes == 0) {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH));//Mes Atual
            dtAtual.set(Calendar.MONTH, dtAtual.get(Calendar.MONTH));//Mes Atual
        }




       // Toast.makeText(despesa_lista.this,"Data Atual :" + dtAtual.toString() +"",Toast.LENGTH_SHORT ).show();

        iMes = cal.get(Calendar.MONTH); //Mes Posição
        iAno = cal.get(Calendar.YEAR); //Pega o Ano


        sMostraTela = sMes[iMes].toString() + "/" + iAno; //Descrição do Mes Corrente
        tvMesAno.setText(sMostraTela);

    }

}

package br.com.wdespesas.wdespesas.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.wdespesas.wdespesas.dbHelper.dbHelper;
import br.com.wdespesas.wdespesas.model.Despesa;

/**
 * Created by Master on 10/08/2016.
 */
public class DespesaDAO {

    private dbHelper conn;

    public DespesaDAO(Context context){
        this.conn = new dbHelper(context);
    }

    public void Inserir(Despesa despesa){

        ContentValues valores = new ContentValues();

        valores.put("des_descricao",despesa.getDescricao());
        valores.put("categoria",despesa.getCategoria());
        valores.put("des_parcela",despesa.getParcela());
        valores.put("des_valorparcela",despesa.getValorparcela());
        valores.put("des_datalancamento",despesa.getDatalancamento().getTime()); //Data em Milessegundos e nao em String
        valores.put("des_datavencimento",despesa.getDatavencimento().getTime());
        valores.put("des_qtdeparcelas", despesa.getQtdeparcelas());
        valores.put("des_chaveqtdeparcelas", despesa.getChavedocparcelas());

        SQLiteDatabase db = conn.getWritableDatabase();
        db.insert("tb_despesas", null, valores);

    }

    public void Alterar(Despesa despesa){

        ContentValues valores = new ContentValues();

        valores.put("des_descricao",despesa.getDescricao());
        valores.put("categoria",despesa.getCategoria());
        valores.put("des_valorparcela",despesa.getValorparcela());
        valores.put("des_datalancamento",despesa.getDatalancamento().getTime()); //Data em Milessegundos e nao em String
        valores.put("des_datavencimento",despesa.getDatavencimento().getTime());
        //valores.put("des_pago", despesa.getPago()); //somente alterar se nao foi pago default nao no bd

        SQLiteDatabase db = conn.getWritableDatabase();
        db.update("tb_despesas", valores, "des_codigo=? AND des_parcela=?", new String[]{String.valueOf(despesa.getCodigo()), String.valueOf(despesa.getParcela())});

    }


    /*Das Tentativas Nenhuma funcionou
    //Retorna o Total das contas usando o Mes e Ano//
    public Double TotalLabes(String Mes, String Ano, String pago) {
        //pago usar string (sim/nao)


        Double TotalSoma = null;
        String[] args = new String[]{Mes, Ano, pago};

        SQLiteDatabase db = conn.getReadableDatabase();
        //Cursor result = db.rawQuery("SELECT SUM(des_valorparcela) FROM tb_despesas",null);// WHERE strftime('%m',des_datavencimento)=? AND strftime('%Y',des_datavencimento)=? AND des_pago=?", new String[]{String.valueOf(Mes), String.valueOf(Ano), String.valueOf(pago)});
        //Cursor result =  db.query("tb_despesas",new String[]{"SUM(des_valorparcela)"},"strftime('%m',des_datavencimento)=? AND strftime('%Y',des_datavencimento)=? AND des_pago=?",new String[]{Mes, Ano, pago},null,null,null);
        //Cursor result =  db.query("tb_despesas",new String[]{"SUM(des_valorparcela)"},null,null,null,null,null);

        Cursor result = db.query("tb_despesas", new String[]{"SUM(des_valorparcela)"}, "strftime('%m',des_datavencimento)=? AND strftime('%Y',des_datavencimento)=? AND des_pago=?", args, null, null, null);

        while (result.moveToNext()){
            TotalSoma = result.getDouble(0);
        }

        return TotalSoma;
    }
  */

    //Retorna o Total das contas usando o Mes e Ano//
    //Esta usando o campo long, por isso ver como resolver
    public Double TotalLabes(String Mes, String Ano, String pago) throws ParseException {

        //----------------------------------------------------------------------------//
        /*Recebe o mês e Ano e Faz uma Concatenação da data
        Criando duas Variaveis de Data de inicio e fim de mes e depois converto em calendar
        */

        String sDataRecebida="";

        String sDataInicioMes="";
        String sDataFinalMes="";

        Long lDataInicioMes;
        Long lDataFinalMes;

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        sDataRecebida  = "01/"+Mes+ "/"+Ano; //Monta a String com a Primeira Data do Mês

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateFormat.parse(sDataRecebida));

        long lUltimoDiaMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); //Ultimo Dia do Mes Selecionado

        sDataInicioMes  = "01/"+Mes+ "/"+Ano; //Monta a String com a Primeira Data do Mês
        sDataFinalMes  = String.valueOf(lUltimoDiaMes) + "/" + Mes+ "/"+Ano; //Monta a String com o Ultimo dia do Mes

        lDataInicioMes = dateFormat.parse(sDataInicioMes).getTime();
        lDataFinalMes = dateFormat.parse(sDataFinalMes).getTime();

       //----------------------------------------------------------------------------//

        Double TotalSoma = null;

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor result =  db.query("tb_despesas", new String[]{"SUM(des_valorparcela)"}, "des_datavencimento>=? AND des_datavencimento<=? AND des_pago=?", new String[]{lDataInicioMes.toString(),lDataFinalMes.toString(), pago}, null, null, null);

        while (result.moveToNext()){
            TotalSoma = result.getDouble(0);
        }

        return TotalSoma;
    }

    public void ExcluirParcela(int DespesaCodigo, int DespesaParcela){
        SQLiteDatabase db = conn.getWritableDatabase();
        db.delete("tb_despesas", "des_codigo=? AND des_parcela=?", new String[]{String.valueOf(DespesaCodigo), String.valueOf(DespesaParcela)});
    }

    //Excluir Todos os Documentos gerados em varias parcelas
    public void ExcluirDoctoChave(String sNroDocumentoChave){
        SQLiteDatabase db = conn.getWritableDatabase();
        db.delete("tb_despesas", "des_chaveqtdeparcelas=? AND des_pago=?", new String[]{sNroDocumentoChave, "nao"});
    }

    public void QuitarDocumento(Despesa despesa){
        ContentValues valores = new ContentValues();

        valores.put("des_datapagamento",despesa.getDatapagamento().getTime());
        valores.put("des_pago",despesa.getPago()); //sim

        SQLiteDatabase db = conn.getWritableDatabase();
        db.update("tb_despesas", valores, "des_codigo=? AND des_parcela=?", new String[]{String.valueOf(despesa.getCodigo()), String.valueOf(despesa.getParcela())});
    }

    public void EstornarDocumento(Despesa despesa){

        ContentValues valores = new ContentValues();

        //valores.put("des_datapagamento",despesa.getDatapagamento().getTime()); //null
        valores.put("des_datapagamento","");
        valores.put("des_pago",despesa.getPago()); //nao

        SQLiteDatabase db = conn.getWritableDatabase();
        db.update("tb_despesas", valores, "des_codigo=? AND des_parcela=?", new String[]{String.valueOf(despesa.getCodigo()), String.valueOf(despesa.getParcela())});
    }


    public List<Despesa> ListarTodosDoctos(){

        ArrayList<Despesa> lstDespesas = new ArrayList<Despesa>();
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor result = db.query("tb_despesas", null, null, null, null, null, "des_datavencimento ASC");

        while (result.moveToNext()){
            Despesa despesa = new Despesa();
            despesa.setCodigo(result.getInt(result.getColumnIndex("des_codigo")));
            despesa.setParcela(result.getInt(result.getColumnIndex("des_parcela")));
            despesa.setDescricao(result.getString(result.getColumnIndex("des_descricao")));
            despesa.setCategoria(result.getInt(result.getColumnIndex("categoria")));
            despesa.setValorparcela(result.getDouble(result.getColumnIndex("des_valorparcela")));
            despesa.setPago(result.getString(result.getColumnIndex("des_pago")));

            despesa.setQtdeparcelas(result.getInt(result.getColumnIndex("des_qtdeparcelas")));
            despesa.setChavedocparcelas(result.getString(result.getColumnIndex("des_chaveqtdeparcelas")));

            //No Caso das Datas como estamos gravando os milessegundos iremos tratar primeiro o retorno
            //em milessegundo da data e depois converter em data

            Long TimedtLancamento=result.getLong(result.getColumnIndex("des_datalancamento"));
            Long TimedtVencimento = result.getLong( result.getColumnIndex("des_datavencimento"));
            Long TimedtPagamento=result.getLong(result.getColumnIndex("des_datapagamento"));

            Date dtLancamento = new Date();
            dtLancamento.setTime(TimedtLancamento);
            despesa.setDatalancamento(dtLancamento);

            Date dtVencimento = new Date();
            dtVencimento.setTime(TimedtVencimento);
            despesa.setDatavencimento(dtVencimento);

            Date dtPagamento = new Date();
            dtPagamento.setTime(TimedtPagamento);
            despesa.setDatapagamento(dtPagamento);

            lstDespesas.add(despesa);
        }


        return lstDespesas;

    }


    public List<Despesa> ListarTodosData(String sMes, String sAno) throws ParseException {


        //----------------------------------------------------------------------------//
        /*Recebe o mês e Ano e Faz uma Concatenação da data
        Criando duas Variaveis de Data de inicio e fim de mes e depois converto em calendar
        */

        String sDataRecebida="";

        String sDataInicioMes="";
        String sDataFinalMes="";

        Long lDataInicioMes;
        Long lDataFinalMes;

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        sDataRecebida  = "01/"+sMes+ "/"+sAno; //Monta a String com a Primeira Data do Mês

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateFormat.parse(sDataRecebida));

        long lUltimoDiaMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); //Ultimo Dia do Mes Selecionado

        sDataInicioMes  = "01/"+sMes+ "/"+sAno; //Monta a String com a Primeira Data do Mês
        sDataFinalMes  = String.valueOf(lUltimoDiaMes) + "/" + sMes+ "/"+sAno; //Monta a String com o Ultimo dia do Mes

        lDataInicioMes = dateFormat.parse(sDataInicioMes).getTime();
        lDataFinalMes = dateFormat.parse(sDataFinalMes).getTime();
        //----------------------------------------------------------------------------//


        ArrayList<Despesa> lstDespesas = new ArrayList<Despesa>();
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor result = db.query("tb_despesas",null,"des_datavencimento>=? AND des_datavencimento<=?",new String[]{lDataInicioMes.toString(), lDataFinalMes.toString()},null,null  , "des_datavencimento ASC");

        while (result.moveToNext()){
            Despesa despesa = new Despesa();
            despesa.setCodigo(result.getInt(result.getColumnIndex("des_codigo")));
            despesa.setParcela(result.getInt(result.getColumnIndex("des_parcela")));
            despesa.setDescricao(result.getString(result.getColumnIndex("des_descricao")));
            despesa.setCategoria(result.getInt(result.getColumnIndex("categoria")));
            despesa.setValorparcela(result.getDouble(result.getColumnIndex("des_valorparcela")));
            despesa.setPago(result.getString(result.getColumnIndex("des_pago")));

            despesa.setQtdeparcelas(result.getInt(result.getColumnIndex("des_qtdeparcelas")));
            despesa.setChavedocparcelas(result.getString(result.getColumnIndex("des_chaveqtdeparcelas")));

            //No Caso das Datas como estamos gravando os milessegundos iremos tratar primeiro o retorno
            //em milessegundo da data e depois converter em data

            Long TimedtLancamento=result.getLong(result.getColumnIndex("des_datalancamento"));
            Long TimedtVencimento = result.getLong( result.getColumnIndex("des_datavencimento"));
            Long TimedtPagamento=result.getLong(result.getColumnIndex("des_datapagamento"));

            Date dtLancamento = new Date();
            dtLancamento.setTime(TimedtLancamento);
            despesa.setDatalancamento(dtLancamento);

            Date dtVencimento = new Date();
            dtVencimento.setTime(TimedtVencimento);
            despesa.setDatavencimento(dtVencimento);

            Date dtPagamento = new Date();
            dtPagamento.setTime(TimedtPagamento);
            despesa.setDatapagamento(dtPagamento);

            lstDespesas.add(despesa);
        }


        return lstDespesas;

    }




    //Criar um Nro Chave Unico para Cada Lançamento//
    //Campo este utilizado para Excluir Doctos em Lote Varias Parcelas//
    public String sFormaCampoNroChaveUnico(){
        int ToTalCont = 0;
        Date date = new Date();
        long GetTimeData=0;

        String sConcatenaCampo="";

        //Pega o Long da Data Atual//
        GetTimeData =  date.getTime();

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor result =  db.query("tb_despesas", new String[]{"COUNT(*)"},null, null, null, null,null);

        while (result.moveToNext()){
            ToTalCont = result.getInt(0);
        }

        sConcatenaCampo = "L" + String.valueOf(ToTalCont).toString() + GetTimeData ;

        return sConcatenaCampo;
    }


}

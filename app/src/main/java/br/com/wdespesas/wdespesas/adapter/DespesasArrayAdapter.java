package br.com.wdespesas.wdespesas.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.wdespesas.wdespesas.R;
import br.com.wdespesas.wdespesas.dao.CategoriaDAO;
import br.com.wdespesas.wdespesas.model.Despesa;

/**
 * Created by Master on 11/08/2016.
 * Extendendo de arrayAdapter e sobrescrevendo alguns metodos que são importantes
 */
public class DespesasArrayAdapter extends ArrayAdapter<Despesa>{

    private List<Despesa> lstDespesas;
    private Context context;

    public DespesasArrayAdapter(Context context, int resource, List<Despesa> lstDespesas) {
        super(context, resource);
        this.lstDespesas = lstDespesas;//recebe a lista de despesas
        this.context=context;//recupera o contexto ou activitie
    }

    @Override
    public int getCount() {
        return lstDespesas.size();
    }

    @Override
    public int getPosition(Despesa item) {
        return super.getPosition(item);
    }

    @Override
    public Despesa getItem(int position) {
        return lstDespesas.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //BUSCA O LAYOUT E SEUS ATRIBUTOS//
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View linha =  layoutInflater.inflate(R.layout.layout_linha_despesa, parent, false);//infla o layout da linha criado

        TextView txtDescricaoDespesa = (TextView) linha.findViewById(R.id.tvDescricao);
        TextView txtDescricaoCategoria = (TextView) linha.findViewById(R.id.tvCategoria);
        TextView txtVencimentoDespesa = (TextView) linha.findViewById(R.id.tvVencimento);
        TextView txtValorDespesa = (TextView) linha.findViewById(R.id.tvValorParcela);
        TextView txtPago = (TextView) linha.findViewById(R.id.tvPago);
        //TextView txtParcelaDespesa = (TextView) linha.findViewById(R.id.tvParcela);

        if (lstDespesas.get(position).getPago().toString().equals("sim")){
            linha.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorPago));
        }else{
            linha.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorNaoPago));

            //Vencido muda cor do texto do vencimento e o valor
            Date dDataAtual = new Date();
            if (lstDespesas.get(position).getDatavencimento().before(dDataAtual)) {
                //txtVencimentoDespesa.setTextColor(ContextCompat.getColor(this.context, R.color.colorVencido));
                txtValorDespesa.setTextColor(ContextCompat.getColor(this.context, R.color.colorVencido));
            }
        }

        //Mostra dados nos campos
        txtDescricaoDespesa.setText(lstDespesas.get(position).getDescricao().toString() + " " + String.valueOf(lstDespesas.get(position).getParcela()).toString() +
                "/" + String.valueOf(lstDespesas.get(position).getQtdeparcelas()).toString());

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        txtVencimentoDespesa.setText("Venc : " + dateFormat.format(lstDespesas.get(position).getDatavencimento()));
        DecimalFormat valorFormat = new DecimalFormat("0.00");
        txtValorDespesa.setText("R$ : " + String.valueOf(valorFormat.format(lstDespesas.get(position).getValorparcela())).toString());
        txtDescricaoCategoria.setText(DescricaoCategoriaID(lstDespesas.get(position).getCategoria()).toString());

        //Documento já Pago, Mostra Escrita Pago//
        if (lstDespesas.get(position).getPago().toString().equals("sim")) {
            txtPago.setVisibility(View.VISIBLE);
            txtPago.setText("Pago");
            //txtPago.setText("pago : " + lstDespesas.get(position).getPago().toString());
        }
        //txtParcelaDespesa.setText("Parc : " + String.valueOf(lstDespesas.get(position).getParcela()).toString());

        return linha;
        //return super.getView(position,convertView,parent);
    }

    //Retorna a Descrição da Categoria, usando o metodo de buscar por ID//
    public String DescricaoCategoriaID(int CodigoCategoria){
        CategoriaDAO cDAO = new CategoriaDAO(getContext());
        return cDAO.ListarPorID_RetornaNome(CodigoCategoria);
    }
}


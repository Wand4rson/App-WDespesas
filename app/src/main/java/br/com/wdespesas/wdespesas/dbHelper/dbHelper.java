package br.com.wdespesas.wdespesas.dbHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Master on 08/08/2016.
 */
public class dbHelper extends SQLiteOpenHelper {


    private static String NAME_DB="fin_pessoal";
    private static int  VERSAO_DB=1;

    public dbHelper(Context context) {
        super(context, NAME_DB, null, VERSAO_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        StringBuilder sql = new StringBuilder();
        //Categorias
        sql.append("CREATE TABLE IF NOT EXISTS tb_categoria(");
        sql.append(" cat_codigo INTEGER PRIMARY KEY AUTOINCREMENT,");
        sql.append(" cat_descricao TEXT);");
        db.execSQL(sql.toString()); //executa no final

        StringBuilder sSQl = new StringBuilder();
        //Despesas
        sSQl.append("CREATE TABLE IF NOT EXISTS tb_despesas(" );
        sSQl.append(" des_codigo INTEGER PRIMARY KEY AUTOINCREMENT," );
        sSQl.append(" des_parcela INTEGER,");
        sSQl.append(" des_descricao TEXT,");
        sSQl.append(" categoria INTEGER,");
        sSQl.append(" des_valorparcela REAL,");
        sSQl.append(" des_datalancamento INTEGER NOT NULL,");
        sSQl.append(" des_datavencimento INTEGER NOT NULL,");
        sSQl.append(" des_datapagamento INTEGER,");
        sSQl.append(" des_pago TEXT default nao,");
        sSQl.append(" des_qtdeparcelas INTEGER,");
        sSQl.append(" des_chaveqtdeparcelas TEXT);");
        db.execSQL(sSQl.toString());

        //Preenche Combo com Categorias Default
        db.execSQL("INSERT INTO tb_categoria(cat_descricao) VALUES('Despesas Pessoais')");
        db.execSQL("INSERT INTO tb_categoria(cat_descricao) VALUES('Despesas Alimentação')");
        db.execSQL("INSERT INTO tb_categoria(cat_descricao) VALUES('Despesas Diversas')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}


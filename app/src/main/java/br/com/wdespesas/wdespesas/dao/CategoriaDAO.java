package br.com.wdespesas.wdespesas.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.wdespesas.wdespesas.dbHelper.dbHelper;
import br.com.wdespesas.wdespesas.model.Categoria;

/**
 * Created by Master on 09/08/2016.
 */
public class CategoriaDAO {

    private dbHelper conn;

    public CategoriaDAO(Context context){
        this.conn = new dbHelper(context);
    }


    public void Inserir(Categoria categoria){
        ContentValues valores = new ContentValues();
        valores.put("cat_descricao",categoria.getDescricao());
        SQLiteDatabase db = conn.getWritableDatabase();
        db.insert("tb_categoria",null,valores);
    }

    public void Alterar(Categoria categoria){
        ContentValues valores = new ContentValues();
        valores.put("cat_descricao",categoria.getDescricao());
        SQLiteDatabase db = conn.getWritableDatabase();
        db.update("tb_categoria", valores, "cat_codigo=?", new String[]{String.valueOf(categoria.getCodigo())});
    }

    public void Excluir(int Codigo){
        SQLiteDatabase db = conn.getWritableDatabase();
        db.delete("tb_categoria", "cat_codigo=?", new String[]{String.valueOf(Codigo)});
    }

    public List<Categoria> ListarTodos(){
        ArrayList<Categoria> lst = new ArrayList<Categoria>();
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor result = db.query("tb_categoria", null, null, null, null, null, null);

        while (result.moveToNext()){
            Categoria categoria = new Categoria();
            categoria.setCodigo(result.getInt(result.getColumnIndex("cat_codigo")));
            categoria.setDescricao(result.getString(result.getColumnIndex("cat_descricao")));
            lst.add(categoria);
        }

        return lst;
    }

    public List<Categoria> ListarPorID(int Codigo){

        ArrayList<Categoria> lst = new ArrayList<Categoria>();
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor result = db.query("tb_categoria", null, "cat_codigo="+Codigo,  null, null, null, null);

        while (result.moveToNext()){
            Categoria categoria = new Categoria();
            categoria.setCodigo(result.getInt(result.getColumnIndex("cat_codigo")));
            categoria.setDescricao(result.getString(result.getColumnIndex("cat_descricao")));
            lst.add(categoria);
        }

        return lst;
    }

    //Retorna o Nome da Categoria pelo Id, para Usar no ListView//
    public String ListarPorID_RetornaNome(int Codigo){
        String sNomeCategoria=null;

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor result = db.query("tb_categoria", null, "cat_codigo="+Codigo,  null, null, null, null);

        while (result.moveToNext()){
            sNomeCategoria = result.getString(result.getColumnIndex("cat_descricao"));
        }

        return sNomeCategoria;
    }


    public String CategoriaUtilizadaDespesas(int CodigoCategoria){

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor result = db.query("tb_despesas",null, "categoria=?",new String[]{String.valueOf(CodigoCategoria)},null,null,null);
        if (result.moveToFirst()){
            return "sim";//Encontrou a categoria em uso
        }
        return "nao";
    }

}

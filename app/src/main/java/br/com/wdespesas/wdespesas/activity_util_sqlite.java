package br.com.wdespesas.wdespesas;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Master on 15/09/2016.
 */
public class activity_util_sqlite extends AppCompatActivity {

    private String DB_PATH="";//Caminho do Banco de Dados no Celular
    private String DB_NAME="";//Nome do Banco de Dados
    private Button btnBackup;
    private Button btnRestore;
    private Button btnSobre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_activity_util_sqlite);
        setTitle("Utilitários");

        //Caminhos do Banco de Dados no Celular//
        DB_NAME="fin_pessoal"; //com o .db da erro
        DB_PATH= String.valueOf(getApplicationContext().getDatabasePath(DB_NAME));

        btnBackup = (Button) findViewById(R.id.btnBackup);
        btnRestore= (Button) findViewById(R.id.btnRestaura);
        btnSobre= (Button) findViewById(R.id.btnSobre);

        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogo = new AlertDialog.Builder(activity_util_sqlite.this);
                dialogo.setMessage("Deseja realizar backup da aplicação ?");

                dialogo.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Backup();
                    }
                });

                ///Caso clique em nao
                dialogo.setNegativeButton("Não", null);
                AlertDialog dialog = dialogo.create();
                dialogo.setTitle("Atenção");
                dialogo.show();
            }
        });

        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogo = new AlertDialog.Builder(activity_util_sqlite.this);
                dialogo.setMessage("Deseja restaurar o backup ? Todos os dados atuais serão substituídos. O backup deve existir no diretório da aplicação.");

                dialogo.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Restore();
                    }
                });

                ///Caso clique em nao
                dialogo.setNegativeButton("Não", null);
                AlertDialog dialog = dialogo.create();
                dialogo.setTitle("Atenção");
                dialogo.show();

            }
        });

        btnSobre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity_util_sqlite.this,sobre.class);
                startActivity(i);
            }
        });

    }

    //Backup do banco de dados//
    private void Backup(){
        try {
            File file = new File(DB_PATH);
            FileInputStream myInput;
            myInput = new FileInputStream(file);


            //Cria a Pasta, caso não exista//
            File PASTANOVA = new File(Environment.getExternalStorageDirectory().getPath()  + "/AppWDespesas/");
            if (!PASTANOVA.exists()) {
                PASTANOVA.mkdir();
            }

            String outFileName = PASTANOVA + "/fin_pessoal.db";//Caminho para Salvar no aparelho o backup criado
            //String outFileName = Environment.getExternalStorageDirectory().getPath() + "/dbtemtrabalho.db";//Caminho para Salvar no aparelho o backup criado


            //Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
            Toast.makeText(activity_util_sqlite.this, "backup realizado com sucesso , salvo em : " + outFileName.toString() + ""   , Toast.LENGTH_LONG).show();

            //Backup realizado com sucesso, pergunta se quer compartilhar//
            CompartilharArquivo();

        } catch (Exception e) {
            Toast.makeText(activity_util_sqlite.this, "Erro não foi possível realizar backup !" + e.getMessage(), Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }


    private void Restore(){
        try {

            File file = new File(Environment.getExternalStorageDirectory().getPath()+"/AppWDespesas/fin_pessoal.db");//Caminho do Arquivo a ser Restaurado
            FileInputStream myInput;
            myInput = new FileInputStream(file);

            String outFileName = DB_PATH; //Caminho onde restaurar o backup, que o usuario possui (Restaurar na pasta databases)

            //Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
            Toast.makeText(activity_util_sqlite.this, "backup restaurado com sucesso !", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            //erro ao restaurar backup
            Toast.makeText(activity_util_sqlite.this, "Erro, arquivo de backup não encontrado !" + e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }

    private void CompartilharArquivo(){

        File PASTANOVA = new File(Environment.getExternalStorageDirectory().getPath()  + "/AppWDespesas/");
        if (!PASTANOVA.exists()) {
            Toast.makeText(activity_util_sqlite.this, "Nenhum arquivo para ser compartilhado !", Toast.LENGTH_LONG).show();
            return;
        }

        String outFileName = PASTANOVA + "/fin_pessoal.db";//Caminho onde está o arquivo

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(outFileName)));
        intent.setType("*/*");
        startActivity(intent);

    }
}




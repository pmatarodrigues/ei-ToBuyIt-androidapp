package com.example.pedro.tobuyit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static android.graphics.Color.rgb;

public class Login extends AppCompatActivity {

    ArrayList<Utilizador> users = new ArrayList<>();
    EditText txfMailLogin;
    EditText txfPasswordLogin;
    EditText txfUsernameRegistar;
    EditText txfPasswordRegistar;
    EditText txfConfirmarPasswordRegistar;
    TextView txvAvisoRegistar;
    TextView txvAvisoLoginUsername;
    TextView txvAvisoLoginPassword;
    Spinner spnPerguntaSeguranca;
    EditText respostaPerguntaSeguranca;

    Boolean existe = false;
    int num; //vai permitir introduzir o login e a password em paginas diferentes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //--------------- RECEBER OS UTILIZADORES GUARDADOS NO FICHEIRO
        try {
            users = lerUtilizadoresGuardados();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // ------------ verifica se este intent foi aberto depois de terminar sessão
        if(getIntent().getExtras() != null){
            for(int i = 0; i < users.size(); i++){
                // --------- termina sessão do utilizador
                users.get(i).setAtivo(false);
                try {
                    gravarUtilizador(users);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // -------------- VERIFICA SE EXISTE ALGUM UTILIZADOR COM SESSAO INICIADA
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getAtivo()){
                num = i;
                existe = true;
                // ---- abre a aplicação principal
                entrar();
            }
        }
        if(!existe){
            // ---- pede para iniciar sessao
            setContentView(R.layout.activity_login);
        }

    }

    //----------------------------- FUNCOES PARA BOTOES ----------------------------------//
    public void irParaRegistar(View view){
        setContentView(R.layout.layout_registar);
    }
    public void voltarALogin(View view) throws ClassNotFoundException {
        setContentView(R.layout.activity_login);
    }

    //--------------------------- FUNCOES EM INICIAR SESSAO ----------------------------//
    // ------ ESTA FUNÇÃO É CHAMADA CASO O UTILIZADOR JÁ TENHA SESSÃO INICIADA
    public void entrar(){
        Intent intent = new Intent(this, MainActivity.class);
        // envia o "id" do utilizador para o novo intent
        intent.putExtra("USER_ATIVO", String.valueOf(num));
        users.get(num).setAtivo(true);
        startActivity(intent);
        finish();
    }


    public void avancarParaPassword(View view){
        txfMailLogin = (EditText) findViewById(R.id.campo_mail_login);
        txvAvisoLoginUsername = (TextView) findViewById(R.id.aviso_login_username);

        if(txfMailLogin.getText().length() == 0){
            txvAvisoLoginUsername.setText("Introduza o nome utilizador");
            txvAvisoLoginUsername.setTextColor(rgb(255, 0, 0));
        }
        else {
            //------------ VERIFICA SE O USERNAME INTRODUZIDO EXISTE NA BASE DE DADOS
            if (users.size() > 0) {
                for (int i = 0; i < users.size(); i++) {
                    if (txfMailLogin.getText().toString().equals(users.get(i).getUsername())) {
                        setContentView(R.layout.layout_login_password);     //----------- MUDA PARA O LAYOUT DA PASSWORD
                        num = i;        //----- FAZ COM QUE NAO SEJA NECESSÁRIO VOLTAR A CORRER TODOS OS UTILIZADORES
                    } else {
                        txvAvisoLoginUsername.setText("O username introduzido não existe");
                        txvAvisoLoginUsername.setTextColor(rgb(255, 0, 0));
                    }
                }
            } else {
                txfMailLogin.setVisibility(view.INVISIBLE);
                txvAvisoLoginUsername.setText("Não existem utilizadores");
                txvAvisoLoginUsername.setTextColor(rgb(255, 0, 0));
            }
        }
    }

    public void iniciarSessao(View view) throws IOException {
        txfPasswordLogin = (EditText) findViewById(R.id.campo_password_login);
        txvAvisoLoginPassword = (TextView) findViewById(R.id.aviso_login_password);

        if (users.get(num).getPassword().equals(txfPasswordLogin.getText().toString())) {
            //--------- mudar para a activity de entrada -----------//
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("USER_ATIVO", String.valueOf(num));
            users.get(num).setAtivo(true);
            gravarUtilizador(users);
            startActivity(intent);
            finish();
        } else {
            txvAvisoLoginPassword.setText("A palavra-passe introduzida está incorreta");
            txvAvisoLoginPassword.setTextColor(rgb(255, 0, 0));
        }
    }


    //------------------------------ FUNCOES EM REGISTAR ---------------------------/
    public void registar(View view) throws IOException {
        txfUsernameRegistar = findViewById(R.id.campo_username_registar);
        txfPasswordRegistar = findViewById(R.id.campo_pass_registar);
        txfConfirmarPasswordRegistar = findViewById(R.id.campo_confpass_registar);
        txvAvisoRegistar = findViewById(R.id.aviso_registar);
        spnPerguntaSeguranca = findViewById(R.id.registar_perguntaseguranca);
        respostaPerguntaSeguranca = findViewById(R.id.registar_perguntaseguranca_resposta);

        Utilizador user = new Utilizador(false);

        if(txfUsernameRegistar.getText().toString().length() < 4){
            txvAvisoRegistar.setText("O nome de utilizador tem que ter pelo menos 5 letras");
            txvAvisoRegistar.setTextColor(rgb(255, 0, 0));
        }
        else if(txfPasswordRegistar.getText().length() == 0){
            txvAvisoRegistar.setText("Introduza uma password");
            txvAvisoRegistar.setTextColor(rgb(255, 0, 0));
        }
        else if(!txfPasswordRegistar.getText().toString().equals(txfConfirmarPasswordRegistar.getText().toString())){
            txvAvisoRegistar.setText("As passwords nao coincidem");
            txvAvisoRegistar.setTextColor(rgb(255, 0, 0));
        }
        else if(txfPasswordRegistar.getText().toString().length() < 4){
            txvAvisoRegistar.setText("A password tem que ter pelo menos 4 digitos");
            txvAvisoRegistar.setTextColor(rgb(255, 0, 0));
        }
        else if(respostaPerguntaSeguranca.getText().length() < 2){
            txvAvisoRegistar.setText("Introduza uma resposta válida à pergunta de segurança");
            txvAvisoRegistar.setTextColor(rgb(255, 0, 0));
        }
        else {
            user.setUsername(txfUsernameRegistar.getText().toString());
            user.setPassword(txfPasswordRegistar.getText().toString());
            user.setPerguntaSeguranca(spnPerguntaSeguranca.getSelectedItem().toString());
            user.setRespostaPerguntaSeguranca(respostaPerguntaSeguranca.getText().toString());

            users.add(user);

            gravarUtilizador(users);
            //volta para a pagina de login
            setContentView(R.layout.activity_login);
        }
    }

    public void irParaRecuperarPassword(View view){
        setContentView(R.layout.layout_login_recuperarpassword);
    }

    public void recuperarPalavraPasse(View view){
        spnPerguntaSeguranca = findViewById(R.id.recuperarpassword_perguntaseguranca);
        respostaPerguntaSeguranca = findViewById(R.id.recuperarpassword_perguntaseguranca_resposta);
        txvAvisoRegistar = findViewById(R.id.aviso_recuperarpassword);

        if(!users.get(num).getPerguntaSeguranca().equals(spnPerguntaSeguranca.getSelectedItem().toString()) ||
                !users.get(num).getRespostaPerguntaSeguranca().equals(respostaPerguntaSeguranca.getText().toString())){
            txvAvisoRegistar.setText("Dados de recuperação incorretos");
            txvAvisoRegistar.setTextColor(rgb(255, 0, 0));
        }
        else{
            txfPasswordRegistar = findViewById(R.id.recuperarpassword_novapass);
            users.get(num).setPassword(txfPasswordRegistar.getText().toString());

            setContentView(R.layout.activity_login);
        }
    }

    //--------------------------------------------- FICHEIROS ------------------------------------------------
    //------------ GRAVAR DADOS EM FICHEIRO
    public void gravarUtilizador(ArrayList<Utilizador> users) throws IOException {

        Context context = this.getApplicationContext();
        try{
            FileOutputStream fos = context.openFileOutput("users.txt", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(users);
            os.close();
            fos.close();

            System.out.println("\n\nUTILIZADOR GUARDADO!");
        }catch (IOException e){
            Log.e("Exception", "Erro ao gravar para ficheiro: " + e.toString());
        }
    }

    //---------- LER DADOS DE FICHEIRO
    public ArrayList<Utilizador> lerUtilizadoresGuardados() throws ClassNotFoundException {

        ArrayList<Utilizador> users = new ArrayList<>();
        try {
            FileInputStream fis = getApplicationContext().openFileInput("users.txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            users = (ArrayList<Utilizador>) is.readObject();
            is.close();
            fis.close();

            System.out.println("\nUTILIZADOR RECEBIDO!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

}


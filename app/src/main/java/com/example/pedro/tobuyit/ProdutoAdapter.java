package com.example.pedro.tobuyit;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedro.tobuyit.*;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.MyViewHolder>{

    ArrayList<Produto> produtos;
    private static LayoutInflater inflater = null;
    private Context context;


    public ProdutoAdapter(ArrayList<Produto> produtos){
        this.produtos = produtos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_lista_produtos, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.nomeProduto.setText(produtos.get(position).getNome());
        holder.precoProduto.setText(produtos.get(position).getPreco() + "€");
        holder.imagemProduto.setImageResource(produtos.get(position).getImagem());

        if(produtos.get(position).getEmPromocao()){
            holder.iconPromocoes.setVisibility(View.VISIBLE);
        } else{
            holder.iconPromocoes.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView nomeProduto;
        private TextView precoProduto;
        private ImageView imagemProduto;
        private ImageView iconPromocoes;
        private MyViewHolder(View itemView){
            super(itemView);
            nomeProduto = (TextView)itemView.findViewById(R.id.lista_produto_nome);
            precoProduto = (TextView) itemView.findViewById(R.id.lista_produto_preco);
            imagemProduto = (ImageView) itemView.findViewById(R.id.lista_produto_imagem);

            iconPromocoes = (ImageView) itemView.findViewById(R.id.lista_produtos_icon_promocao);
        }
    }


}


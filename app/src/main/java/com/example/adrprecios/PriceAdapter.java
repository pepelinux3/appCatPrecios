package com.example.adrprecios;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.text.DecimalFormat;


public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolderDatos> implements View.OnClickListener{

    ArrayList<PriceVo> listItems;
    private View.OnClickListener listener;

    public PriceAdapter(ArrayList<PriceVo> listItems){
        this.listItems = listItems;
    }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup viewItem, int i) {
        View view = LayoutInflater.from(viewItem.getContext()).inflate(R.layout.detail_price,null, false);
        view.setOnClickListener(this);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int i) {
        DecimalFormat precision = new DecimalFormat("#,##0.00");

        holder.tvNoArticulo.setText(listItems.get(i).getNoItem());

        if(listItems.get(i).getIdItem() == 0){
            holder.tvPrArticulo.setText("");
            holder.ivPesos.setImageResource(0);
            holder.tvNoArticulo.setText("");
            holder.tvSuArticulo.setText(listItems.get(i).getDesItem());
            holder.tvSuArticulo.setTextSize(20);
            holder.tvDeArticulo.setText("");
        }
        else{
            holder.tvPrArticulo.setText(precision.format(listItems.get(i).getPriItem()));
            holder.ivPesos.setImageResource(R.drawable.moneda);
            holder.tvSuArticulo.setText("");
            holder.tvSuArticulo.setTextSize(14);
            holder.tvDeArticulo.setText(listItems.get(i).getDesItem());
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onClick(v);
        }
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        TextView tvNoArticulo;
        TextView tvDeArticulo;
        TextView tvSuArticulo;
        TextView tvPrArticulo;
        ImageView ivPesos;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            tvNoArticulo = (TextView)itemView.findViewById(R.id.numberArt);
            tvDeArticulo = (TextView)itemView.findViewById(R.id.descripcionArt);
            tvSuArticulo = (TextView)itemView.findViewById(R.id.subgrupoArt);
            tvPrArticulo = (TextView)itemView.findViewById(R.id.priceArt);

            ivPesos = (ImageView)itemView.findViewById(R.id.monedaView);
        }


    }
}

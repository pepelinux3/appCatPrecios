package com.example.adrprecios;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PriceAdapterFull
        extends RecyclerView.Adapter<PriceAdapterFull.ViewHolderFull> {

    ArrayList<PriceVo> listItemFull;

    public PriceAdapterFull(ArrayList<PriceVo> listItemFull){
        this.listItemFull = listItemFull;
    }

    @NonNull
    @Override
    public ViewHolderFull onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.detail_price_full, null, false);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new ViewHolderFull(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderFull holder, int i) {
        DecimalFormat precision = new DecimalFormat("#,##0.00");

        holder.tvFullNoArticulo.setText(listItemFull.get(i).getNoItem());
        holder.tvFullGrupo.setText(listItemFull.get(i).getGruItem());
        holder.tvFullSubGrupo.setText(listItemFull.get(i).getSubgItem());
        holder.tvFullDescripcion.setText(listItemFull.get(i).getDesItem());
        holder.tvFullDate.setText(listItemFull.get(i).getDateItem());
        holder.tvFullPrice.setText(precision.format(listItemFull.get(i).getPriItem()));
    }

    @Override
    public int getItemCount() {
        return listItemFull.size();
    }

    public class ViewHolderFull extends RecyclerView.ViewHolder {
        TextView tvFullNoArticulo;
        TextView tvFullGrupo;
        TextView tvFullSubGrupo;
        TextView tvFullDescripcion;
        TextView tvFullDate;
        TextView tvFullPrice;

        public ViewHolderFull(@NonNull View itemView) {
            super(itemView);

            tvFullNoArticulo = (TextView)itemView.findViewById(R.id.priceFull_NoArt);
            tvFullGrupo = (TextView)itemView.findViewById(R.id.priceFull_Grupo);
            tvFullSubGrupo = (TextView)itemView.findViewById(R.id.priceFull_Subg);
            tvFullDescripcion = (TextView)itemView.findViewById(R.id.priceFull_Desc);
            tvFullDate = (TextView)itemView.findViewById(R.id.priceFull_Date);
            tvFullPrice = (TextView)itemView.findViewById(R.id.priceFull_Price);
        }
    }
}

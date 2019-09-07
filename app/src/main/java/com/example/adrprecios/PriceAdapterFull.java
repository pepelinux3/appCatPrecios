package com.example.adrprecios;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        return new ViewHolderFull(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderFull holder, int i) {
        holder.tvNoArticulo.setText(listItemFull.get(i).getNoItem());
    }

    @Override
    public int getItemCount() {
        return listItemFull.size();
    }

    public class ViewHolderFull extends RecyclerView.ViewHolder {
        TextView tvNoArticulo;

        public ViewHolderFull(@NonNull View itemView) {
            super(itemView);

            tvNoArticulo = (TextView)itemView.findViewById(R.id.priceFull_NoArt);
        }
    }
}

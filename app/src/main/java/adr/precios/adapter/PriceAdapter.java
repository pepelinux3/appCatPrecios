package adr.precios.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adrprecios.R;

import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.List;

import adr.precios.entities.PriceVo;


public class PriceAdapter
        extends RecyclerView.Adapter<PriceAdapter.ViewHolderDatos>
        implements Filterable {

    List<PriceVo> listItems;
    List<PriceVo> listItemComplet;

    String recyNoParte;
    int recIdItem;

    public PriceAdapter(ArrayList<PriceVo> listItems){
        this.listItems = listItems;
        listItemComplet = new ArrayList<>(listItems);
    }

    public String getNoParteAdapter() { return recyNoParte; }
    public int getIdItem() { return recIdItem; }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup viewItem, int i) {
        View view = LayoutInflater.from(viewItem.getContext()).inflate(R.layout.detail_price,null, false);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int i) {
        DecimalFormat precision = new DecimalFormat("#,##0.00");

        if(listItems.get(i).getIdItem() == 0){
            holder.tvPrArticulo.setText("");
            holder.ivPesos.setImageResource(0);
            holder.tvNoArticulo.setText("");
            holder.tvSuArticulo.setText(listItems.get(i).getDesItem());
            holder.tvSuArticulo.setTextSize(17);
            holder.tvDeArticulo.setText("");
        }
        else{
            holder.tvPrArticulo.setText(precision.format(listItems.get(i).getPriItem()));
            holder.ivPesos.setImageResource(R.drawable.moneda);
            holder.tvNoArticulo.setText(listItems.get(i).getNoItem());
            holder.tvSuArticulo.setText("");
            holder.tvSuArticulo.setTextSize(14);
            holder.tvDeArticulo.setText(listItems.get(i).getDesItem());
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @Override
    public Filter getFilter() {
        return listFilterPrice;
    }

    private Filter listFilterPrice = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<PriceVo> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(listItemComplet);
            } else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(PriceVo onePrice: listItemComplet){
                    if(onePrice.getNoItem().toLowerCase().contains(filterPattern)){
                        filteredList.add(onePrice);
                    }else
                    if(onePrice.getDesItem().toLowerCase().contains(filterPattern)){
                        filteredList.add(onePrice);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listItems.clear();
            listItems.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolderDatos extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
    //    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        TextView tvNoArticulo;
        TextView tvDeArticulo;
        TextView tvSuArticulo;
        TextView tvPrArticulo;
        ImageView ivPesos;
        CardView cardView;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            tvNoArticulo = (TextView)itemView.findViewById(R.id.numberArt);
            tvDeArticulo = (TextView)itemView.findViewById(R.id.descripcionArt);
            tvSuArticulo = (TextView)itemView.findViewById(R.id.subgrupoArt);
            tvPrArticulo = (TextView)itemView.findViewById(R.id.priceArt);

            ivPesos = (ImageView)itemView.findViewById(R.id.monedaView);
            cardView = (CardView)itemView.findViewById(R.id.id_cardPrice);
            cardView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            recyNoParte = "";
            recIdItem = 0;

            if(listItems.get(this.getAdapterPosition()).getIdItem() != 0){
                recyNoParte = listItems.get(this.getAdapterPosition()).getNoItem();
                recIdItem = listItems.get(this.getAdapterPosition()).getIdItem();

                menu.add(this.getAdapterPosition(), 1, 0, "Existencia");
                menu.add(this.getAdapterPosition(), 2, 0, "Imagen");
            }
        }
    }
}
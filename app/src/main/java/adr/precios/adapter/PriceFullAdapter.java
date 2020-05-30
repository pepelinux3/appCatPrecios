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
import android.widget.TextView;

import com.example.adrprecios.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import adr.precios.entities.ActiPriceVo;

public class PriceFullAdapter
        extends RecyclerView.Adapter<PriceFullAdapter.ViewHolderFull>
        implements Filterable {

    List<ActiPriceVo> listItemFull;
    List<ActiPriceVo> listItemFullComplet;

    String recyNoParte;
    int recIdItem;

    public PriceFullAdapter(ArrayList<ActiPriceVo> listItemFull){
        this.listItemFull = listItemFull;
        listItemFullComplet = new ArrayList<>(listItemFull);
    }

    public String getNoParteAdapter() { return recyNoParte; }
    public int getIdItem() { return recIdItem; }

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

        if(listItemFull.get(i).getPriItem() == -999){
            holder.tvFullPrice.setText("****");
        } else{
            holder.tvFullPrice.setText(precision.format(listItemFull.get(i).getPriItem()));
        }

    }

    @Override
    public int getItemCount() {
        return listItemFull.size();
    }

    @Override
    public Filter getFilter() {
        return listFilterPriceFull;
    }

    private  Filter listFilterPriceFull = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ActiPriceVo> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(listItemFullComplet);
            } else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(ActiPriceVo onePrice: listItemFullComplet){
                    if(onePrice.getNoItem().toLowerCase().contains(filterPattern)){
                        filteredList.add(onePrice);
                    }else{
                        if(onePrice.getDesItem().toLowerCase().contains(filterPattern)){
                            filteredList.add(onePrice);
                        }

                   //     if(onePrice.getDateItem().toLowerCase().contains(filterPattern)){
                   //         filteredList.add(onePrice);
                   //     }

                    }

                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listItemFull.clear();
            listItemFull.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolderFull extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

   // public class ViewHolderFull extends RecyclerView.ViewHolder {
        TextView tvFullNoArticulo;
        TextView tvFullGrupo;
        TextView tvFullSubGrupo;
        TextView tvFullDescripcion;
        TextView tvFullDate;
        TextView tvFullPrice;
        CardView cardView;

        public ViewHolderFull(@NonNull View itemView) {
            super(itemView);

            tvFullNoArticulo = (TextView)itemView.findViewById(R.id.priceFull_NoArt);
            tvFullGrupo = (TextView)itemView.findViewById(R.id.priceFull_Grupo);
            tvFullSubGrupo = (TextView)itemView.findViewById(R.id.priceFull_Subg);
            tvFullDescripcion = (TextView)itemView.findViewById(R.id.priceFull_Desc);
            tvFullDate = (TextView)itemView.findViewById(R.id.priceFull_Date);
            tvFullPrice = (TextView)itemView.findViewById(R.id.priceFull_Price);

            cardView = (CardView)itemView.findViewById(R.id.id_cardPriceFull);
            cardView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            recyNoParte = "";
            recIdItem = 0;

            recyNoParte = listItemFull.get(this.getAdapterPosition()).getNoItem();
            recIdItem = listItemFull.get(this.getAdapterPosition()).getIdItem();

            menu.add(this.getAdapterPosition(), 1, 0, "Existencia");
            menu.add(this.getAdapterPosition(), 2, 0, "Imagen");
        }
    }
}

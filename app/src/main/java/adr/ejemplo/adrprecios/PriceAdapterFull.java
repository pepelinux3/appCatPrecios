package adr.ejemplo.adrprecios;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

public class PriceAdapterFull
        extends RecyclerView.Adapter<PriceAdapterFull.ViewHolderFull>
        implements Filterable {

    List<PriceVo> listItemFull;
    List<PriceVo> listItemFullComplet;

    public PriceAdapterFull(ArrayList<PriceVo> listItemFull){
        this.listItemFull = listItemFull;
        listItemFullComplet = new ArrayList<>(listItemFull);
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

    @Override
    public Filter getFilter() {
        return listFilterPriceFull;
    }

    private  Filter listFilterPriceFull = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<PriceVo> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(listItemFullComplet);
            } else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(PriceVo onePrice: listItemFullComplet){
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
            listItemFull.clear();
            listItemFull.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

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

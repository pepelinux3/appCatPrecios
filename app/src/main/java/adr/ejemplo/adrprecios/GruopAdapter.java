package adr.ejemplo.adrprecios;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adrprecios.R;

import java.util.ArrayList;
import java.util.List;

public class GruopAdapter
        extends RecyclerView.Adapter<GruopAdapter.ViewHolderGroup>
        implements View.OnClickListener, Filterable {

    List <GroupVo> groupList;
    List <GroupVo> groupListFull;

    private View.OnClickListener listener;

    public GruopAdapter(ArrayList<GroupVo> groupList) {
        this.groupList = groupList;
        groupListFull = new ArrayList<>(groupList);
    }

    @NonNull
    @Override
    public ViewHolderGroup onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.detail_group, null, false);

        view.setOnClickListener(this);

        // Se agregan estas 2 lineas para que funcione el "match_paren" en recyclerview....
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new ViewHolderGroup(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderGroup holder, int i) {
        holder.vhgVineta.setImageResource(groupList.get(i).getGruVineta());
        holder.vhgId.setText(groupList.get(i).getGruId()+"");
        holder.vhgNombre.setText(groupList.get(i).getGruNombre());
    }

    @Override
    public int getItemCount() {
        return groupList.size();
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

    @Override
    public Filter getFilter() {
        return listFilter;
    }

    private Filter listFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<GroupVo> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(groupListFull);
            } else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(GroupVo oneGroup: groupListFull){
                    if(oneGroup.getGruNombre().toLowerCase().contains(filterPattern)){
                        filteredList.add(oneGroup);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            groupList.clear();
            groupList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolderGroup extends RecyclerView.ViewHolder {
        ImageView vhgVineta;
        TextView vhgId;
        TextView vhgNombre;

        public ViewHolderGroup(@NonNull View itemView) {
            super(itemView);

            vhgVineta = (ImageView)itemView.findViewById(R.id.idImagen);
            vhgId = (TextView)itemView.findViewById(R.id.idClave);
            vhgNombre = (TextView)itemView.findViewById(R.id.idNombre);
        }
    }
}

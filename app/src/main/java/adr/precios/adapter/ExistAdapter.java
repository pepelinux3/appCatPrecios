package adr.precios.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adrprecios.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import adr.precios.entities.StockBranchVo;


public class ExistAdapter extends RecyclerView.Adapter<ExistAdapter.ViewHolderDatos> {

    ArrayList<StockBranchVo> listInv;

    public ExistAdapter(ArrayList<StockBranchVo> listInv){
        this.listInv = listInv;
    }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.detail_exist, null, false);


        // Se agregan estas 2 lineas para que funciones el "match_paren" en recyclerview....
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int i) {
        DecimalFormat precision = new DecimalFormat("#,##0.00");

        holder.etBranch.setText(listInv.get(i).getBranchName());
        holder.etExist.setText(listInv.get(i).getBranchExist());
        holder.etPrice.setText(precision.format(listInv.get(i).getBranchPrice()));
        // holder.foto.setImageResource(listaPersonajes.get(i).getFoto());
    }

    @Override
    public int getItemCount() {
        return listInv.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView etBranch;
        TextView etExist;
        TextView etPrice;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);

            etBranch = (TextView)itemView.findViewById(R.id.tv_branch);
            etExist = (TextView)itemView.findViewById(R.id.tv_exist);
            etPrice = (TextView)itemView.findViewById(R.id.tv_price);
        }
    }
}

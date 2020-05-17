package adr.precios.tools;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.adrprecios.R;

import adr.precios.adapter.ExistAdapter;
import adr.precios.database.DBHelper;


public class MiDialogFragment extends DialogFragment {


    RecyclerView recyclerExist;
    ExistAdapter existAdapter;
    TextView edNoParte;

    String noItem;

    public void setValue(String noItem) {
        this.noItem = noItem;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.dialogfrag_exist3, container);

        edNoParte = (TextView) rootView.findViewById(R.id.fragNoparte);
        edNoParte.setText(noItem);

        //  RECYCLER
        recyclerExist = (RecyclerView) rootView.findViewById(R.id.recycler_exist_id3);
        recyclerExist.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        DBHelper dbHelper = new DBHelper(getContext());
        dbHelper.openDataBase();

        existAdapter = new ExistAdapter(dbHelper.getInventory(noItem));
        recyclerExist.setAdapter(existAdapter);

        dbHelper.close();

        this.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //this.getDialog().setTitle("Existencia");

        return rootView;
    }

}
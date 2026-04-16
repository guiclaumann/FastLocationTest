package com.example.fastlocationtest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fastlocationtest.model.Address;
import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<Address> addresses = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Address address);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address current = addresses.get(position);
        holder.textLogradouro.setText(current.getLogradouro());
        holder.textBairroCidade.setText(String.format("%s - %s/%s", current.getBairro(), current.getLocalidade(), current.getUf()));
        
        String dateStr = "";
        if (current.getSearchDates() != null && !current.getSearchDates().isEmpty()) {
            dateStr = current.getSearchDates().split(",")[0];
        }
        holder.textCepDate.setText(String.format("CEP: %s | Última consulta: %s", current.getCep(), dateStr));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(current);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
        notifyDataSetChanged();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        private TextView textLogradouro;
        private TextView textBairroCidade;
        private TextView textCepDate;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            textLogradouro = itemView.findViewById(R.id.text_logradouro);
            textBairroCidade = itemView.findViewById(R.id.text_bairro_cidade);
            textCepDate = itemView.findViewById(R.id.text_cep_date);
        }
    }
}
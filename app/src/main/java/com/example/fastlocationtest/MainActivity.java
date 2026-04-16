package com.example.fastlocationtest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastlocationtest.model.Address;
import com.example.fastlocationtest.viewmodel.AddressViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private AddressViewModel viewModel;
    private TextInputEditText cepEditText;
    private ProgressBar loadingProgressBar;
    private AddressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        setupUI();
        setupViewModel();
    }

    private void setupUI() {
        cepEditText = findViewById(R.id.cep_edit_text);
        loadingProgressBar = findViewById(R.id.loading_progress_bar);
        RecyclerView recyclerView = findViewById(R.id.history_recycler_view);
        ImageButton btnClear = findViewById(R.id.btn_clear);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(this::showAddressDetails);

        btnClear.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Limpar Histórico")
                    .setMessage("Deseja realmente apagar todo o histórico de consultas?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        viewModel.clearHistory();
                        cepEditText.setText("");
                        Toast.makeText(this, "Histórico removido!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Não", null)
                    .show();
        });

        cepEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 8) {
                    validateAndSearch(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        viewModel.getAllAddresses().observe(this, addresses -> {
            adapter.setAddresses(addresses);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });
    }

    private void validateAndSearch(String cep) {
        if (cep.equals("00000000")) {
            Toast.makeText(this, "CEP Inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.searchCep(cep);
    }

    private void showAddressDetails(Address address) {
        StringBuilder historyBuilder = new StringBuilder();
        if (address.getSearchDates() != null) {
            String[] dates = address.getSearchDates().split(",");
            for (String date : dates) {
                historyBuilder.append("• ").append(date).append("\n");
            }
        }

        String details = "CEP: " + address.getCep() + "\n" +
                "Logradouro: " + address.getLogradouro() + "\n" +
                (address.getComplemento() != null && !address.getComplemento().isEmpty() ? "Complemento: " + address.getComplemento() + "\n" : "") +
                "Bairro: " + address.getBairro() + "\n" +
                "Cidade: " + address.getLocalidade() + "\n" +
                "Estado: " + address.getUf() + "\n\n" +
                "Histórico de consultas:\n" + historyBuilder.toString();

        new AlertDialog.Builder(this)
                .setTitle("Detalhes do Endereço")
                .setMessage(details)
                .setPositiveButton("Fechar", null)
                .setNeutralButton("Ver no Mapa", (dialog, which) -> {
                    openMap(address);
                })
                .show();
    }

    private void openMap(Address address) {
        String query = Uri.encode(address.getLogradouro() + ", " + address.getBairro() + ", " + address.getLocalidade() + " - " + address.getUf());
        Uri mapIntentUri = Uri.parse("geo:0,0?q=" + query);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapIntentUri);

        // This will trigger the Android system's App Chooser (Maps, Waze, etc.)
        Intent chooser = Intent.createChooser(mapIntent, "Escolha um aplicativo de mapa:");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        } else {
            // Fallback to Web Maps
            Intent webMapIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=" + query));
            startActivity(webMapIntent);
        }
    }
}
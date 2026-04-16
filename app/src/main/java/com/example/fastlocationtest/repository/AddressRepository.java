package com.example.fastlocationtest.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.fastlocationtest.api.ViaCepService;
import com.example.fastlocationtest.database.AddressDao;
import com.example.fastlocationtest.database.AppDatabase;
import com.example.fastlocationtest.model.Address;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddressRepository {
    private AddressDao addressDao;
    private ViaCepService viaCepService;
    private LiveData<List<Address>> allAddresses;

    public AddressRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        addressDao = database.addressDao();
        allAddresses = addressDao.getAllAddresses();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        viaCepService = retrofit.create(ViaCepService.class);
    }

    public LiveData<List<Address>> getAllAddresses() {
        return allAddresses;
    }

    public void searchCep(String cep, RepositoryCallback callback) {
        new SearchTask(addressDao, viaCepService, callback).execute(cep);
    }

    public void clearHistory() {
        new DeleteAllTask(addressDao).execute();
    }

    public interface RepositoryCallback {
        void onSuccess(Address address);
        void onError(String message);
    }

    private static class DeleteAllTask extends AsyncTask<Void, Void, Void> {
        private AddressDao dao;

        DeleteAllTask(AddressDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dao.deleteAllAddresses();
            return null;
        }
    }

    private static class SearchTask extends AsyncTask<String, Void, Address> {
        private AddressDao dao;
        private ViaCepService service;
        private RepositoryCallback callback;
        private String errorMsg;

        SearchTask(AddressDao dao, ViaCepService service, RepositoryCallback callback) {
            this.dao = dao;
            this.service = service;
            this.callback = callback;
        }

        @Override
        protected Address doInBackground(String... params) {
            String cep = params[0];
            
            // Check local database first (simulating the backend behavior mentioned)
            Address localAddress = dao.getAddressByCep(cep);
            if (localAddress != null) {
                updateAddressStats(localAddress);
                dao.update(localAddress);
                return localAddress;
            }

            // Not in local DB, fetch from ViaCEP
            try {
                Response<Address> response = service.getAddressByCep(cep).execute();
                if (response.isSuccessful() && response.body() != null) {
                    Address address = response.body();
                    if (address.getCep() == null || address.getCep().isEmpty()) {
                         errorMsg = "CEP Inválido";
                         return null;
                    }
                    address.setCep(address.getCep().replace("-", "")); // Normalize CEP
                    updateAddressStats(address);
                    dao.insert(address);
                    return address;
                } else {
                    errorMsg = "CEP não encontrado";
                }
            } catch (Exception e) {
                errorMsg = "Erro na requisição: " + e.getMessage();
            }
            return null;
        }

        private void updateAddressStats(Address address) {
            long now = System.currentTimeMillis();
            address.setLastSearchDate(now);
            address.setSearchCount(address.getSearchCount() + 1);
            
            String dateStr = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date(now));
            if (address.getSearchDates() == null || address.getSearchDates().isEmpty()) {
                address.setSearchDates(dateStr);
            } else {
                address.setSearchDates(dateStr + "," + address.getSearchDates());
            }
        }

        @Override
        protected void onPostExecute(Address address) {
            if (address != null) {
                callback.onSuccess(address);
            } else {
                callback.onError(errorMsg);
            }
        }
    }
}
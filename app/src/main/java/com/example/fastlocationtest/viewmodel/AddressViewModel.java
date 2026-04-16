package com.example.fastlocationtest.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.fastlocationtest.model.Address;
import com.example.fastlocationtest.repository.AddressRepository;
import java.util.List;

public class AddressViewModel extends AndroidViewModel {
    private AddressRepository repository;
    private LiveData<List<Address>> allAddresses;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public AddressViewModel(@NonNull Application application) {
        super(application);
        repository = new AddressRepository(application);
        allAddresses = repository.getAllAddresses();
    }

    public LiveData<List<Address>> getAllAddresses() {
        return allAddresses;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void searchCep(String cep) {
        if (isLoading.getValue()) return;
        
        isLoading.setValue(true);
        repository.searchCep(cep, new AddressRepository.RepositoryCallback() {
            @Override
            public void onSuccess(Address address) {
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    public void clearError() {
        errorMessage.setValue(null);
    }

    public void clearHistory() {
        repository.clearHistory();
    }
}
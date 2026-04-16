package com.example.fastlocationtest.api;

import com.example.fastlocationtest.model.Address;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ViaCepService {
    @GET("{cep}/json/")
    Call<Address> getAddressByCep(@Path("cep") String cep);
}
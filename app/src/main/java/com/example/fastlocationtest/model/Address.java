package com.example.fastlocationtest.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import java.util.List;
import java.util.ArrayList;

@Entity(tableName = "addresses")
public class Address {
    @PrimaryKey
    @NonNull
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;
    private int searchCount;
    private long lastSearchDate;
    private String searchDates; // Stored as comma-separated string for simplicity in this example

    public Address() {
        this.cep = "";
        this.searchDates = "";
    }

    // Getters and Setters
    @NonNull
    public String getCep() { return cep; }
    public void setCep(@NonNull String cep) { this.cep = cep; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getLocalidade() { return localidade; }
    public void setLocalidade(String localidade) { this.localidade = localidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public int getSearchCount() { return searchCount; }
    public void setSearchCount(int searchCount) { this.searchCount = searchCount; }

    public long getLastSearchDate() { return lastSearchDate; }
    public void setLastSearchDate(long lastSearchDate) { this.lastSearchDate = lastSearchDate; }

    public String getSearchDates() { return searchDates; }
    public void setSearchDates(String searchDates) { this.searchDates = searchDates; }
}
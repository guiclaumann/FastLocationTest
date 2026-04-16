package com.example.fastlocationtest.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fastlocationtest.model.Address;

import java.util.List;

@Dao
public interface AddressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Address address);

    @Update
    void update(Address address);

    @Query("SELECT * FROM addresses WHERE cep = :cep LIMIT 1")
    Address getAddressByCep(String cep);

    @Query("SELECT * FROM addresses ORDER BY lastSearchDate DESC")
    LiveData<List<Address>> getAllAddresses();

    @Query("DELETE FROM addresses")
    void deleteAllAddresses();
}
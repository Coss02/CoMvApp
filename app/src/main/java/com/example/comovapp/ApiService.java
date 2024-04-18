package com.example.comovapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// Define the API response structure directly within the same file
class ApiResponse {
    public int result;
    public String err;
    public Data data;

    public static class Data {
        public double lat;
        public double lon;
        public double range;
        public String time;
    }
}

// Define the API service interface
public interface ApiService {
    @GET("geolocation/cell")
    Call<ApiResponse> getCellLocation(
            @Query("v") String version,
            @Query("data") String data,
            @Query("mcc") int mcc,
            @Query("mnc") int mnc,
            @Query("lac") int lac,
            @Query("cellid") long cellId
    );
}

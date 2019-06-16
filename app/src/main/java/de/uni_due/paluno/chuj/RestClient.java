package de.uni_due.paluno.chuj;


import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
    static final String BASE = "http://palaver.se.paluno.uni-due.de";
    private ApiService apiService;

    private Retrofit retrofit;

    public RestClient() {
        Retrofit retrofit;
        retrofit = new Builder().baseUrl("http://palaver.se.paluno.uni-due.de")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public ApiService getApiService() {
        return apiService;
    }
}
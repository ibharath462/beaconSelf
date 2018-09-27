package com.example.bharath_5493.beaconnew;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface GetDataService {
    @POST("sendDocument?chat_id=-1001256319579")
    @Multipart
    Call<simple> uploadAttachment(@Part MultipartBody.Part part);
}
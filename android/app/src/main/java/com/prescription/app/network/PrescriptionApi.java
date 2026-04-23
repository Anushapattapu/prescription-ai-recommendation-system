package com.prescription.app.network;

import com.prescription.app.model.PrescriptionResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PrescriptionApi {
    @Multipart
    @POST("/api/v1/prescriptions/upload")
    Call<PrescriptionResponse> uploadPrescription(@Part MultipartBody.Part file);
}

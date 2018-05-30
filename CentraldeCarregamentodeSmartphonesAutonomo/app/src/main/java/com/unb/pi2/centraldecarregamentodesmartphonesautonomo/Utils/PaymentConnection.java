package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.Utils;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PaymentConnection {
    @FormUrlEncoded
    @POST("package/ctrl/CtrlPayment.php")
    Call<String> sendPayment(
            @Field("value") int value,
            @Field("token") String token
    );
}

package cyruslee487.donteatalone.Remote;

import cyruslee487.donteatalone.Model.MyResponse;
import cyruslee487.donteatalone.Model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAtzSFXc0:APA91bEAKThVrReGT3_bQb4kHuCMF_oD8QIBzIrX1SYg-JtqNKRWYIdZxr-xAQRwYr4j6YbjFAci7E7J7DOcWkemRCx5HAUrdXIhr29qph9YUC_rKAAmZ5StOh8n8Y8Dv-9FMfDYxsEU"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}

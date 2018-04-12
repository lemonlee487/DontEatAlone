package cyruslee487.donteatalone;

import cyruslee487.donteatalone.Remote.APIService;
import cyruslee487.donteatalone.Remote.RetrofitClient;

public class Common {
    public static String currentToken = "";

    private static String baseUrl = "https://fcm.googleapis.com/";

    public static APIService getFCMClient(){
        return RetrofitClient.getClient(baseUrl).create(APIService.class);
    }

}

package android.utility.common.http;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by lqnhu on 6/20/15.
 */
public class CheckWebStatus {
    // Check the device is connected to the internet
    public static boolean isOnlineOrNot(Context c) {
        NetworkInfo netInfo = null;
        try {
            ConnectivityManager cm = (ConnectivityManager) c
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void getStatus(String weburl, String ways) {
        try {
            List<String> httpWays = new ArrayList<String>();
            httpWays.add(ways.toUpperCase());
            httpWays.add(weburl);
            new checkWebStatus().execute(httpWays);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        }
    }
    //Check the website http response code no
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private static class checkWebStatus extends AsyncTask<List<String>, Void, String> {
        @Override
        protected String doInBackground(List<String>... weburl) {
            Integer httpResponse = 0;
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = null;
            try {
                if (weburl[0].get(0).equals("POST")) {
                    HttpPost httptype = new HttpPost(weburl[0].get(1));
                    try {
                        response = client.execute(httptype);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (weburl[0].get(0).equals("GET")) {
                    HttpGet httptype = new HttpGet(weburl[0].get(1));
                    try {
                        response = client.execute(httptype);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (weburl[0].get(0).equals("PUT")) {
                    HttpPut httptype = new HttpPut(weburl[0].get(1));
                    try {
                        response = client.execute(httptype);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    HttpDelete httptype = new HttpDelete(weburl[0].get(1));
                    try {
                        response = client.execute(httptype);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                assert response != null;
                httpResponse = response.getStatusLine().getStatusCode();
            } catch (SecurityException se) {
                se.printStackTrace();
            }
            return httpResponse.toString();
        }
    }
}

package com.datfusrental.email;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datfusrental.email.template.ConfirmationMessage;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@Component
public class Netcore {
	
	@Autowired
	private ConfirmationMessage confirmationMessage;
	
    public String sentEmail() throws IOException {
        this.netcoreApi(confirmationMessage.confTempl());
        return null;
    }
    
    
    public static String buildJson(String htmlTemp) {

        // "from"
        JSONObject from = new JSONObject();
        from.put("email", "sale@myraanrentals.com");
        from.put("name", "Myraan Rentals");

        // "content"
        JSONArray contentArray = new JSONArray();
        JSONObject contentObj = new JSONObject();
        contentObj.put("type", "html");
        contentObj.put("value", htmlTemp.toString());
        contentArray.put(contentObj);

        // "attributes"
//        JSONObject attributes = new JSONObject();
//        attributes.put("LEAD", "Andy Dwyer");
//        attributes.put("BAND", "Mouse Rat");

        // "to"
        JSONArray toArray = new JSONArray();
        JSONObject to = new JSONObject();
//        to.put("email", "vivekbharti333@gmail.com");
//        to.put("email", "marshadansari10@gmail.com");
//        to.put("email", "rohan@rentmybike.co.in");
//        to.put("email", "mohsin.9187@gmail.com");
        to.put("email", "poojapatidar1994@gmail.com");
        
        to.put("name", "Vivek Bharti");
        toArray.put(to);

        // "personalizations"
        JSONObject personalization = new JSONObject();
//        personalization.put("attributes", attributes);
        personalization.put("to", toArray);

        JSONArray personalizations = new JSONArray();
        personalizations.put(personalization);

        // "settings"
//        JSONObject settings = new JSONObject();
//        settings.put("open_track", true);
//        settings.put("click_track", true);
//        settings.put("unsubscribe_track", false);

        // "tags"
        JSONArray tags = new JSONArray();
        tags.put("Pawnee");

        // Root JSON object
        JSONObject root = new JSONObject();
        root.put("from", from);
//        root.put("reply_to", "vivekbharti333@gmail.com");
        root.put("subject", "Booking Confirmation for Vihaan Dinner Cruise, Dec 12,2025, BI: MYR987509865");
//        root.put("template_id", 1);
        root.put("tags", tags);
        root.put("content", contentArray);
        root.put("personalizations", personalizations);
//        root.put("settings", settings);

        return root.toString(4); // pretty print
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[]{}; }
                }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void netcoreApi(String value) {
        try {
            OkHttpClient client = getUnsafeOkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");

            // ✅ Correct JSON (no trailing commas)
//            String jsonBody = "{"
//            	    + "\"from\": {"
//            	    + "    \"email\": \"sale@myraanrentals.com\","
//            	    + "    \"name\": \"Myraan Rentals\""
//            	    + "},"
//            	    + "\"reply_to\": \"vivekbharti333@gmail.com\","
//            	    + "\"subject\": \"Tribute to Lil'Sebastian\","
//            	    + "\"template_id\": 0,"
//            	    + "\"tags\": [\"Pawnee\"],"
//            	    + "\"content\": ["
//            	    + "    {\"type\": \"html\", \"value\": \"<p>Hi [%LEAD%],</p><p>You and [%BAND%] will be singing a tribute song for our Lil'Sebastian.</p>\"}"
//            	    + "],"
//            	    + "\"personalizations\": ["
//            	    + "    {"
//            	    + "        \"attributes\": {\"LEAD\": \"Andy Dwyer\", \"BAND\": \"Mouse Rat\"},"
//            	    + "        \"to\": [{\"email\": \"vivekbharti333@gmail.com\", \"name\": \"Vivek Bharti\"}]"
//            	    + "    }"
//            	    + "],"
//            	    + "\"settings\": {"
//            	    + "    \"open_track\": true,"
//            	    + "    \"click_track\": true,"
//            	    + "    \"unsubscribe_track\": true"
//            	    + "}"
//            	    + "}";


            RequestBody body = RequestBody.create(mediaType, this.buildJson(value).toString());

            Request request = new Request.Builder()
                    .url("https://emailapi.netcorecloud.net/v5.1/mail/send")
                    .post(body)
                    .addHeader("api_key", "e9a5a3f32ab95dc6a33c629714c3a289")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            System.out.println("Response Code: " + response.code());
            System.out.println("Response Body: " + response.body().string());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

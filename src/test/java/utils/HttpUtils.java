package utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * This class provides static wrapper methods for GET, POST, DELETE.
 *
 * @author Siddha.
 */
public class HttpUtils {
    private static HttpGet get;
    private static HttpPost post;
    private static DefaultHttpClient client = new DefaultHttpClient();

    /**
     * Executes the Http Get method against a provided url.
     *
     * @param url the absolute URL
     * @return HttpResponse encapsulating the headers, code, entity etc
     */
    public static HttpResponse get(String url) {
        get = new HttpGet(url);
        HttpResponse response;
        try {
            response = client.execute(get);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred : " + e);
        }
        return response;
    }

    /**
     * Executes the Http Post method against a provided url[Without request body]
     *
     * @param url the absolute URL
     * @return HttpResponse encapsulating the headers, code, entity etc
     */
    public static HttpResponse post(String url) {
        post = new HttpPost(url);
        HttpResponse response;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred during Post: " + e);
        }
        return response;
    }

    /**
     * Executes the Http Get method against a provided url using Unirest apis.
     *
     * @param url
     * @return
     */
    /*public static com.mashape.unirest.http.HttpResponse<String> unirestGet(String url) {
        com.mashape.unirest.http.HttpResponse<String> response;
        try {
            response = Unirest.get(url)
                    .header("Content-type", "application/json")
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException("While invoking" + url + "UnirestException occurred: " + e);
        }

        return  response;
    }*/
}

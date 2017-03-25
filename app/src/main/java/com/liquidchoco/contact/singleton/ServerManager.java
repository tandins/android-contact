package com.liquidchoco.contact.singleton;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public class ServerManager{
    private static ServerManager instance;
    private RestAdapter host;
    String accept = "application/json";

    /**
     * Returns singleton class instance
     */
    public static ServerManager getInstance() {
        if (instance == null) {
            synchronized (ServerManager.class) {
                if (instance == null) {
                    instance = new ServerManager();
                }
            }
        }
        return instance;
    }

    public ServerManager() {
        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(30, TimeUnit.SECONDS);
        client.setConnectTimeout(30, TimeUnit.SECONDS);

        host = new RestAdapter.Builder().setEndpoint("http://gojek-contacts-app.herokuapp.com/")
                .setLogLevel(RestAdapter.LogLevel.NONE).setClient(new OkClient(client)).build();
    }
}

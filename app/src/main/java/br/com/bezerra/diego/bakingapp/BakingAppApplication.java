package br.com.bezerra.diego.bakingapp;

import android.app.Application;

import com.facebook.stetho.Stetho;

import br.com.bezerra.diego.bakingapp.util.ConnectivityReceiver;

public class BakingAppApplication extends Application {

    private static BakingAppApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Stetho.initializeWithDefaults(this);
    }

    public static synchronized BakingAppApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}

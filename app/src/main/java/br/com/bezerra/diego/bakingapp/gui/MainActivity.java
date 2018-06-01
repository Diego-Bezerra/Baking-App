package br.com.bezerra.diego.bakingapp.gui;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import br.com.bezerra.diego.bakingapp.BakingAppApplication;
import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.data.service.BakingAppServiceUtil;
import br.com.bezerra.diego.bakingapp.util.ConnectivityReceiver;
import br.com.bezerra.diego.bakingapp.util.ConnectivityReceiver.ConnectivityReceiverListener;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiverListener, LoaderManager.LoaderCallbacks {

    private static final int LODER_ID = 1;
    ConnectivityReceiver mConnectivityReceiver;
    private boolean isNetworkReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportLoaderManager().initLoader(LODER_ID, null, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mConnectivityReceiver);
        BakingAppApplication.getInstance().setConnectivityListener(null);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.i("onNetworkConn", "isConnected: " + isConnected);
        if (!isNetworkReceiverRegistered) {
            isNetworkReceiverRegistered = true;
            BakingAppApplication.getInstance().setConnectivityListener(this);
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            mConnectivityReceiver = new ConnectivityReceiver();
            registerReceiver(mConnectivityReceiver, filter);
        }
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        return BakingAppServiceUtil.syncRecipesData(this, this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        String nada = "";
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}

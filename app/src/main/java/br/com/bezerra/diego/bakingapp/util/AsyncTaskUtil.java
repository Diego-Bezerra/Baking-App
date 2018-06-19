package br.com.bezerra.diego.bakingapp.util;

import android.os.AsyncTask;


public class AsyncTaskUtil<B, P, R> extends AsyncTask<B, P, R> {

    private AsyncTaskListener<B, P, R> taskListener;

    public interface AsyncTaskListener<B, P, R> {
        void onPreExecute();
        void onProgressUpdate(P... values);
        void onPostExecute(R r);
        void onCancelled(R r);
        void onCancelled();
        R doInBackground(B... bs);
    }

    public AsyncTaskUtil(AsyncTaskListener<B, P, R> taskListener) {
        this.taskListener = taskListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.taskListener.onPreExecute();
    }

    @Override
    protected final void onProgressUpdate(P... values) {
        super.onProgressUpdate(values);
        this.taskListener.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(R r) {
        super.onPostExecute(r);
        this.taskListener.onPostExecute(r);
    }

    @Override
    protected void onCancelled(R r) {
        super.onCancelled(r);
        this.taskListener.onCancelled(r);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        this.taskListener.onCancelled();
    }

    @Override
    protected R doInBackground(B... bs) {
        return this.taskListener.doInBackground(bs);
    }
}

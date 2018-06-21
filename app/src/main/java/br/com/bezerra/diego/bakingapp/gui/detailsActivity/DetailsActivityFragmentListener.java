package br.com.bezerra.diego.bakingapp.gui.detailsActivity;

import android.support.annotation.NonNull;

import java.io.Serializable;

public interface DetailsActivityFragmentListener extends Serializable {
    void setDefaultTitle();
    void nextStep(@NonNull Integer position);
}
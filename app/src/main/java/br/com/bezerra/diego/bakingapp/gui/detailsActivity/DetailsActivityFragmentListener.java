package br.com.bezerra.diego.bakingapp.gui.detailsActivity;

import android.support.annotation.NonNull;

import java.io.Serializable;

public interface DetailsActivityFragmentListener extends Serializable {
    void clickNextStep(@NonNull Integer position);
    void clickIngredient();
    void setFitSystemWindow(boolean fit);
}
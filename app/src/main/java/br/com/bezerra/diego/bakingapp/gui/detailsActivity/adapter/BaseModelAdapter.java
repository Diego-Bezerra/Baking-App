package br.com.bezerra.diego.bakingapp.gui.detailsActivity.adapter;

public class BaseModelAdapter {

    private int id;
    private int viewType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}

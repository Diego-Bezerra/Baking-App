package br.com.bezerra.diego.bakingapp.gui.detailsActivity;

public class BaseModelAdapter {

    private long id;
    private int viewType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}

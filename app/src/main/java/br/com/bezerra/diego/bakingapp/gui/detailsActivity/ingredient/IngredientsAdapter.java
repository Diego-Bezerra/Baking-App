package br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredient;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import br.com.bezerra.diego.bakingapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private IngredientModelAdapter[] mIngredients;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ingredients_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mIngredients);
    }

    @Override
    public int getItemCount() {
        return mIngredients != null ? mIngredients.length : 0;
    }

    public void swipeData(IngredientModelAdapter[] data) {
        mIngredients = data;
        this.notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ingredientTitle)
        TextView ingredientTitle;
        @BindView(R.id.quantity)
        TextView quantity;
        @BindView(R.id.measure)
        TextView measure;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(IngredientModelAdapter[] ingredients) {

            IngredientModelAdapter model = ingredients[getAdapterPosition()];
            Context context = itemView.getContext();

            String ingredientTitle = model.getIngredient();
            double quantity = model.getQuantity();
            String measure = model.getMeasure();
            String quantityFormat = context.getString(R.string.ingredient_quantity_format);
            String measureFormat = context.getString(R.string.ingredient_measure_format);

            this.ingredientTitle.setText(ingredientTitle);
            this.quantity.setText(String.format(Locale.US, quantityFormat, quantity));
            this.measure.setText(String.format(Locale.US, measureFormat, measure));
        }
    }
}

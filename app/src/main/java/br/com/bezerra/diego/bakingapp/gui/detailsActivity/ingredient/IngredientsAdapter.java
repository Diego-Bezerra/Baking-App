package br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredient;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.data.database.contract.IngredientContract;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private Cursor mCursor;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ingredients_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mCursor);
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public void swipeData(Cursor data) {
        mCursor = data;
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

        void bind(Cursor data) {

            data.moveToPosition(getAdapterPosition());
            Context context = itemView.getContext();

            String ingredientTitle = data.getString(data.getColumnIndex(IngredientContract.INGREDIENT));
            double quantity = data.getDouble(data.getColumnIndex(IngredientContract.QUANTITY));
            String measure = data.getString(data.getColumnIndex(IngredientContract.MEASURE));
            String quantityFormat = context.getString(R.string.ingredient_quantity_format);
            String measureFormat = context.getString(R.string.ingredient_measure_format);

            this.ingredientTitle.setText(ingredientTitle);
            this.quantity.setText(String.format(Locale.US, quantityFormat, quantity));
            this.measure.setText(String.format(Locale.US, measureFormat, measure));
        }
    }
}

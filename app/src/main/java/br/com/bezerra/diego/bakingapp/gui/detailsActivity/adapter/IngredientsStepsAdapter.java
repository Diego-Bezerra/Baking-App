package br.com.bezerra.diego.bakingapp.gui.detailsActivity.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.bezerra.diego.bakingapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsStepsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int INGREDIENT_VIEW_TYPE = 0;
    public static final int STEP_VIEW_TYPE = 1;

    private List<BaseModelAdapter> mData;

    public void swipeData(List<BaseModelAdapter> data) {
        mData = data;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case INGREDIENT_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ingredients, parent, false);
                return new IngredientViewHolder(view);
            case STEP_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_step, parent, false);
                return new StepViewHolder(view);
            default:
                throw new IllegalArgumentException("No viewType found.");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int typeView = holder.getItemViewType();
        switch (typeView) {
            case INGREDIENT_VIEW_TYPE:
                IngredientModelAdapter ingredient = (IngredientModelAdapter) mData.get(position);
                ((IngredientViewHolder) holder).bind(ingredient);
                break;
            case STEP_VIEW_TYPE:
                StepModelAdapter step = (StepModelAdapter) mData.get(position);
                ((StepViewHolder) holder).bind(step);
                break;
            default:
                throw new IllegalArgumentException("No viewType found.");
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getViewType();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;

        IngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(IngredientModelAdapter model) {
            description.setText(model.getIngredient());
        }
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;

        StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(StepModelAdapter model) {
//            String title = String.format(itemView.getContext().getString(R.string.step_title_format), getAdapterPosition() + 1);
//            this.title.setText(title);
            description.setText(model.getDescription());
        }
    }
}

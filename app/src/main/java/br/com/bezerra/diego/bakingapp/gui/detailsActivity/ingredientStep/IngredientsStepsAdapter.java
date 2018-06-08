package br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredientStep;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.BaseModelAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsStepsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    public static final int INGREDIENT_VIEW_TYPE = 0;
    public static final int STEP_VIEW_TYPE = 1;

    private List<BaseModelAdapter> mData;
    private CardItemClickListerner cardItemClickListerner;

    @Override
    public void onClick(View v) {
        if (cardItemClickListerner != null) {

            int position = (int) v.getTag();
            int viewType = getItemViewType(position);

            switch (viewType) {
                case INGREDIENT_VIEW_TYPE:
                    IngredientModelAdapter ingredientModelAdapter = (IngredientModelAdapter) mData.get(position);
                    cardItemClickListerner.onIngredientCardItemClick(ingredientModelAdapter.getRecipeId());
                    break;
                case STEP_VIEW_TYPE:
                    StepModelAdapter stepModelAdapter = (StepModelAdapter) mData.get(position);
                    cardItemClickListerner.onStepCardItemClick(stepModelAdapter.getId());
                    break;
            }
        }
    }

    public interface CardItemClickListerner {
        void onIngredientCardItemClick(long recipeId);

        void onStepCardItemClick(long stepId);
    }

    public void swipeData(List<BaseModelAdapter> data) {
        mData = data;
        this.notifyDataSetChanged();
    }

    public void setCardItemClickListerner(CardItemClickListerner cardItemClickListerner) {
        this.cardItemClickListerner = cardItemClickListerner;
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
        final int typeView = holder.getItemViewType();
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);
        switch (typeView) {
            case INGREDIENT_VIEW_TYPE:
                IngredientViewHolder ingredientViewHolder = (IngredientViewHolder) holder;
                final IngredientModelAdapter ingredient = (IngredientModelAdapter) mData.get(position);
                ingredientViewHolder.bind(ingredient);
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

        @BindView(R.id.description)
        TextView description;

        StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(StepModelAdapter model) {
            description.setText(model.getShortDescription());
        }
    }
}

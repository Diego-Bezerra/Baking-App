package br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredientStep;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.BaseModelAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsStepsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    public static final int INGREDIENT_VIEW_TYPE = 0;
    public static final int STEP_VIEW_TYPE = 1;

    private BaseModelAdapter[] mData;
    private CardItemClickListener cardItemClickListener;
    private int lastClickedViewPosition;
    private boolean isSmallWidth;

    public IngredientsStepsAdapter(boolean isSmallWidth) {
        this.isSmallWidth = isSmallWidth;
    }

    @Override
    public void onClick(View v) {
        if (cardItemClickListener != null) {

            int position = (int) v.getTag();
            int viewType = getItemViewType(position);

            switch (viewType) {
                case INGREDIENT_VIEW_TYPE:
                    clickIngredientPosition();
                    break;
                case STEP_VIEW_TYPE:
                    clickStepPosition(position);
                    break;
            }
        }
    }

    public interface CardItemClickListener {
        void onIngredientCardItemClick(long recipeId);

        void onStepCardItemClick(long stepId, int stepPosition, Long nextStepId, Integer nextStepPosition);
    }

    public void swipeData(BaseModelAdapter[] data) {
        mData = data;
        if (mData != null) {
            if (mData.length > lastClickedViewPosition) {
                mData[lastClickedViewPosition].setSelected(true);
            }
            this.notifyDataSetChanged();
        }
    }

    private void setSelectedViewBackground(int position) {
        if (isSmallWidth) {
            mData[lastClickedViewPosition].setSelected(false);
            mData[position].setSelected(true);
            notifyItemChanged(lastClickedViewPosition);
            notifyItemChanged(position);
            lastClickedViewPosition = position;
        }
    }

    public void clickIngredientPosition() {
        setSelectedViewBackground(0);
        IngredientModelAdapter ingredientModelAdapter = (IngredientModelAdapter) mData[0];
        cardItemClickListener.onIngredientCardItemClick(ingredientModelAdapter.getRecipeId());
        ingredientModelAdapter.setSelected(true);
    }

    public void clickStepPosition(int position) {
        if (mData[position] instanceof StepModelAdapter) {

            setSelectedViewBackground(position);

            StepModelAdapter stepModelAdapter = (StepModelAdapter) mData[position];
            long stepId = stepModelAdapter.getId();
            Long nextStepId = null;
            Integer nextStepPosition = null;

            if (position + 1 < mData.length) {
                StepModelAdapter nextStep = (StepModelAdapter) mData[position + 1];
                nextStepId = nextStep.getId();
                nextStepPosition = position + 1;
            }
            cardItemClickListener.onStepCardItemClick(stepId, position, nextStepId, nextStepPosition);
        }
    }

    public void setCardItemClickListener(CardItemClickListener cardItemClickListener) {
        this.cardItemClickListener = cardItemClickListener;
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
                final IngredientModelAdapter ingredient = (IngredientModelAdapter) mData[position];
                ingredientViewHolder.bind(ingredient, isSmallWidth);
                break;
            case STEP_VIEW_TYPE:
                StepModelAdapter step = (StepModelAdapter) mData[position];
                ((StepViewHolder) holder).bind(step, isSmallWidth);
                break;
            default:
                throw new IllegalArgumentException("No viewType found.");
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.length : 0;
    }

    public void setLastClickedViewPosition(int lastClickedViewPosition) {
        this.lastClickedViewPosition = lastClickedViewPosition;
    }

    public int getLastClickedViewPosition() {
        return lastClickedViewPosition;
    }

    @Override
    public int getItemViewType(int position) {
        return mData[position].getViewType();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.description)
        TextView description;

        IngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(IngredientModelAdapter model, boolean isSmallWidth) {
            description.setText(model.getIngredient());
            if (isSmallWidth) {
                int color = model.isSelected() ? itemView.getContext().getResources().getColor(R.color.colorAccentWithAlpha) : Color.WHITE;
                itemView.setBackgroundColor(color);
            }
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

        void bind(StepModelAdapter model, boolean isSmallWidth) {
            String stepTitle = String.format(itemView.getContext().getText(R.string.step_title_format).toString()
                    , getLayoutPosition());
            title.setText(stepTitle);
            description.setText(model.getShortDescription());
            if (isSmallWidth) {
                int color = model.isSelected() ? itemView.getContext().getResources().getColor(R.color.colorAccentWithAlpha) : Color.WHITE;
                itemView.setBackgroundColor(color);
            }
        }
    }
}

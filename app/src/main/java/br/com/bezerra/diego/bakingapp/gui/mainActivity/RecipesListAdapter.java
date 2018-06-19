package br.com.bezerra.diego.bakingapp.gui.mainActivity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.bezerra.diego.bakingapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesListAdapter extends RecyclerView.Adapter<RecipesListAdapter.ViewHolder> {

    private RecipeModelAdapter[] mRecipes;
    private RecipeAdapterItemClickListerner recipeAdapterItemClickListerner;

    public interface RecipeAdapterItemClickListerner {
        void onItemClick(long recipeId, String recipeTitle);
    }

    public void setRecipeAdapterItemClickListerner(RecipeAdapterItemClickListerner recipeAdapterItemClickListerner) {
        this.recipeAdapterItemClickListerner = recipeAdapterItemClickListerner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recipes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final RecipeModelAdapter recipeModelAdapter = mRecipes[position];
        if (recipeModelAdapter.getImage() != null && !recipeModelAdapter.getImage().isEmpty()) {
            Picasso.get().load(recipeModelAdapter.getImage()).into(holder.recipeImage);
        }

        holder.title.setText(recipeModelAdapter.getName());
        String servingFormat = holder.itemView.getContext().getString(R.string.serving_format);
        holder.serving.setText(String.format(servingFormat, recipeModelAdapter.getServings() + ""));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recipeAdapterItemClickListerner != null) {
                    long recipeId = recipeModelAdapter.getId();
                    String name = recipeModelAdapter.getName();
                    recipeAdapterItemClickListerner.onItemClick(recipeId, name);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecipes != null ? mRecipes.length : 0;
    }

    public void swipeData(RecipeModelAdapter[] recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recipeImage)
        ImageView recipeImage;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.serving)
        TextView serving;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

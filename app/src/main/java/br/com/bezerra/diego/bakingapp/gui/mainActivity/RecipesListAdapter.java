package br.com.bezerra.diego.bakingapp.gui.mainActivity;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.data.database.contract.RecipeContract;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesListAdapter extends RecyclerView.Adapter<RecipesListAdapter.ViewHolder> {

    private Cursor mCursor;
    private RecipeAdapterItemClickListerner recipeAdapterItemClickListerner;

    public interface RecipeAdapterItemClickListerner {
        void onItemClick(int recipeId);
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

        mCursor.moveToPosition(position);
        String imgUrl = mCursor.getString(mCursor.getColumnIndex(RecipeContract.IMAGE));
        String title = mCursor.getString(mCursor.getColumnIndex(RecipeContract.NAME));
        String servings = mCursor.getString(mCursor.getColumnIndex(RecipeContract.SERVINGS));

        if (imgUrl != null && !imgUrl.isEmpty()) {
            Picasso.get().load(imgUrl).into(holder.recipeImage);
        }

        holder.title.setText(title);
        String servingFormat = holder.itemView.getContext().getString(R.string.serving_format);
        holder.serving.setText(String.format(servingFormat, servings));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recipeAdapterItemClickListerner != null) {
                    mCursor.moveToPosition(position);
                    int recipeId = mCursor.getInt(mCursor.getColumnIndex(RecipeContract._ID));
                    recipeAdapterItemClickListerner.onItemClick(recipeId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public void swipeCursor(Cursor cursor) {
        mCursor = cursor;
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

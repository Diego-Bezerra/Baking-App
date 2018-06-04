package br.com.bezerra.diego.bakingapp.gui.detailsActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import br.com.bezerra.diego.bakingapp.R;

public class DetailsActivity extends AppCompatActivity {

    public static final String RECIPE_ID_EXTRA = "recipe_id_extra";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(RECIPE_ID_EXTRA)) {
            int recipeId = bundle.getInt(RECIPE_ID_EXTRA);
            IngredientsStepsFragment fragment = IngredientsStepsFragment.newInstance(recipeId);
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
    }
}

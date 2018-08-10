package br.com.bezerra.diego.bakingapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.bezerra.diego.bakingapp.gui.mainActivity.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityUITest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    //private IdlingResource mIdlingResource;

    @Test
    public void recyclerviewItemClickTest() {
        onView(withId(R.id.recipesList)).perform(actionOnItemAtPosition(0, click()));
        onView((withId(R.id.ingredientsStepsList))).check(matches(isDisplayed()));
    }

    @Test
    public void titleTest() {
        onView(withText(R.string.app_name)).check(matches(withParent(withId(R.id.action_bar))));
    }
}

package com.azisamirul.bakingapp;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.PositionAssertions.isAbove;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


/**
 * Created by azisamirul on 11/10/2017.
 */
@RunWith(AndroidJUnit4.class)

public class BakingAppTest {
    @Rule
    public ActivityTestRule<RecipeListActivity> mRecipeListActivity =
            new ActivityTestRule<>(RecipeListActivity.class);

    @Test
    public void bakingAppTest() {

        ////Idle to load json data
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ViewInteraction textView=onView(
                allOf(withId(R.id.content),withText("Nutella Pie"),
                        childAtPosition(childAtPosition(childAtPosition(withId(R.id.rv_recipelist),0),0),1),
                        isDisplayed()));
        textView.check(matches(withText("Nutella Pie")));

        ViewInteraction recyclerView=onView(allOf(withId(R.id.rv_recipelist),isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.btn_add_to_widget)).check(matches(withText("Add to Widget")));
        onView(withId(R.id.tv_stepsList)).check(matches(withText("Steps")));


       onView(withId(R.id.rv_step_list)).perform(scrollTo(), actionOnItemAtPosition(0,click()));



        //Idle to load video
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.tv_short_description)).check(isAbove(withId(R.id.image_thumbnails)));
        onView(withId(R.id.image_thumbnails)).check(isAbove(withId(R.id.tv_description)));
        onView(withId(R.id.tv_short_description)).check(matches(withText("Recipe Introduction")));
        onView(withId(R.id.tv_description)).check(matches(withText("Recipe Introduction")));

    }

    ////Implementing custom matcher, this codes got from : https://google-developer-training.gitbooks.io/android-developer-fundamentals-course-practicals/content/en/Unit%202/61_p_use_espresso_to_test_your_ui.html

private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher,final int position){
    return new TypeSafeMatcher<View>(){
        @Override
        public void describeTo(Description description) {
            description.appendText("Child at position "+position+" in parent ");
            parentMatcher.describeTo(description);
        }

        @Override
        protected boolean matchesSafely(View item) {
            ViewParent parent=item.getParent();
            return parent instanceof ViewGroup && parentMatcher.matches(parent)
                    && item.equals(((ViewGroup)parent).getChildAt(position));
        }
    };
}
}

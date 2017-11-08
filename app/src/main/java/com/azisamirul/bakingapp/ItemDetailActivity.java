package com.azisamirul.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.azisamirul.bakingapp.model.Step;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.azisamirul.bakingapp.cons.Cons.STEP_INDEX;
import static com.azisamirul.bakingapp.cons.Cons.STEP_PARCELABLE;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {

    @BindView(R.id.btn_next)
    Button btn_next;

    @BindView(R.id.btn_prev)
    Button btn_prev;

    int stepIndex, stepLength;
    List<Step> stepList;
    ItemDetailFragment fragment;
    Step stepParcel;
    Fragment frg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_detail);
        ButterKnife.bind(this);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (getIntent().hasExtra(STEP_INDEX)) {
            stepIndex = getIntent().getIntExtra(STEP_INDEX, 0);
            Log.d("stepindexbundle", String.valueOf(stepIndex));
        }
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //


    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getSteps(List<Step> steps) {
        stepList = steps;
        stepParcel = stepList.get(stepIndex);

        stepLength = stepList.size();
        initializeFragment();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, RecipeListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.btn_next)
    public void btnNext(View v) {
        if (stepIndex < stepLength) {
            stepIndex = stepIndex + 1;
            goToStep(stepIndex);
        } else {
            stepIndex = 0;
            Toast.makeText(this, getString(R.string.msg_last_step), Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.btn_prev)
    public void btnPrev(View v) {

        if (stepIndex == 0) {
            Toast.makeText(this, getString(R.string.msg_first_step), Toast.LENGTH_LONG).show();
            return;
        } else {
            stepIndex = stepIndex - 1;
        }


    }

    private void goToStep(int pos) {
        Step stepParcel = stepList.get(pos);
        Bundle arguments = new Bundle();

        arguments.putParcelable(STEP_PARCELABLE, stepParcel);
        fragment = new ItemDetailFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .commit();
    }

    private void initializeFragment() {
///To handle player show twice
        frg = getSupportFragmentManager().findFragmentById(R.id.item_detail_container);

        if (frg instanceof ItemDetailFragment) {
            return;
        } else {
            Bundle arguments = new Bundle();
            arguments.putParcelable(STEP_PARCELABLE, stepParcel);
            fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }


}

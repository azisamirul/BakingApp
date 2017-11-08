package com.azisamirul.bakingapp;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.azisamirul.bakingapp.model.Ingredient;
import com.azisamirul.bakingapp.model.Model;
import com.azisamirul.bakingapp.model.Step;
import com.azisamirul.bakingapp.widgetdata.BakingAppDbContract;
import com.azisamirul.bakingapp.widgetdata.BakingAppPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.azisamirul.bakingapp.cons.Cons.STEP_INDEX;
import static com.azisamirul.bakingapp.cons.Cons.STEP_PARCELABLE;

public class StepDetailViewActivity extends AppCompatActivity {


    @BindView(R.id.rv_ingredient_list)
    RecyclerView recyclerViewIngredients;

    @BindView(R.id.rv_step_list)
    RecyclerView recyclerViewStep;


    @BindView(R.id.btn_add_to_widget)
    Button btnAddToWidget;

    @BindView(R.id.sv_stepDetailView)
    ScrollView scrollViewStepDetail;

    int id;
    String name;
    String FRAGMENT_SAVE_KEY = "com.azisamirul.bakingapp.savefragmentstep";
    List<Ingredient> ingredientList;
    List<Step> stepList;
    BakingAppPreferences bakingAppPreferences;
    ItemDetailFragment itemDetailFragment;
    private boolean isTwoPane;
    private IngredientsAdapter ingredientsAdapter;
    private StepsAdapter stepsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.LayoutManager layoutManager2;
    private int posX = 0;
    private int posY = -1;
    private String SCROLL_POS_KEY = "scroll_pos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_step_detail_view);
        ButterKnife.bind(this);
        bakingAppPreferences = new BakingAppPreferences(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        if (savedInstanceState != null) {
//            if (savedInstanceState.containsKey(FRAGMENT_SAVE_KEY)){
//                    Log.d("fragment on create", "not null");
//                    itemDetailFragment = (ItemDetailFragment) getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_SAVE_KEY);
//            }
//        }
        if (findViewById(R.id.item_detail_step) != null) {
            isTwoPane = true;
        }

        scrollViewStepDetail.post(new Runnable() {
            @Override
            public void run() {
                scrollViewStepDetail.scrollTo(posX, posY);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(SCROLL_POS_KEY,
                new int[]{scrollViewStepDetail.getScrollX(),
                        scrollViewStepDetail.getScrollY()});


//       ItemDetailFragment itemDetailFragments=(ItemDetailFragment) getSupportFragmentManager().findFragmentById(R.id.item_detail_step);
//        if (itemDetailFragments.isVisible() && itemDetailFragments!=null) {
//            Log.d("fragment save instance", "not null");
//            getSupportFragmentManager().putFragment(outState, FRAGMENT_SAVE_KEY, itemDetailFragment);
//
//        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        final int[] position = savedInstanceState.getIntArray(SCROLL_POS_KEY);
        if (position != null) {
            scrollViewStepDetail.post(new Runnable() {
                @Override
                public void run() {
                    scrollViewStepDetail.scrollTo(position[0], position[1]);
                }
            });
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);

        getSupportActionBar().setTitle(name);

        ingredientsAdapter = new IngredientsAdapter();
        stepsAdapter = new StepsAdapter();
        layoutManager = new LinearLayoutManager(this);
        layoutManager2 = new LinearLayoutManager(this);

        recyclerViewIngredients.setAdapter(ingredientsAdapter);
        recyclerViewIngredients.setLayoutManager(layoutManager);
        ingredientsAdapter.setIngredientList(ingredientList);

        recyclerViewStep.setAdapter(stepsAdapter);
        recyclerViewStep.setLayoutManager(layoutManager2);
        stepsAdapter.setStepList(stepList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getModelData(Model model) {
        id = model.getId();
        name = model.getName();
        ingredientList = model.getIngredients();
        stepList = model.getSteps();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.btn_add_to_widget)
    public void addToWidget() {
        removeWidgetLists();
        int size = ingredientList.size();

        ////Put recipe name into sharedPreferences

        bakingAppPreferences.putRecipeName(name);

        ContentValues[] contentValues = new ContentValues[size];

        for (int i = 0; i < size; i++) {
            ContentValues contentValues1 = new ContentValues();
            contentValues1.put(BakingAppDbContract.BakingAppEntry.COLUMN_INGREDIENT, ingredientList.get(i).getIngredient());
            contentValues1.put(BakingAppDbContract.BakingAppEntry.COLUMN_QUANTITY, ingredientList.get(i).getQuantity());
            contentValues1.put(BakingAppDbContract.BakingAppEntry.COLUMN_MEASURE, ingredientList.get(i).getMeasure());

            contentValues[i] = contentValues1;
        }

        int insertBulk = getContentResolver().bulkInsert(BakingAppDbContract.BakingAppEntry.CONTENT_URI, contentValues);


        //Uri uri = getContentResolver().insert(BakingAppDbContract.BakingAppEntry.CONTENT_URI, contentValues);
        if (insertBulk != 0) {
            Toast.makeText(this, "Added to Widget", Toast.LENGTH_LONG).show();
            BakingWidgetUpdateService.startActionUpdateBakingWidgets(this);
        } else {
            Toast.makeText(this, "Failed to add to widget list!", Toast.LENGTH_LONG).show();
        }


    }

    private void removeWidgetLists() {
        bakingAppPreferences.removeRecipeName();
        Uri uri = BakingAppDbContract.BakingAppEntry.CONTENT_URI;
        int id;
        Cursor c = getContentResolver().query(uri, null, null, null, BakingAppDbContract.BakingAppEntry._ID);
        if (c != null) {
            c.moveToFirst();
            while (c.moveToNext()) {
                int columnIndex = c.getColumnIndex(BakingAppDbContract.BakingAppEntry._ID);
                id = c.getInt(columnIndex);
                Uri uriDelete = BakingAppDbContract.BakingAppEntry.CONTENT_URI;
                uriDelete = uriDelete.buildUpon().appendPath(String.valueOf(id)).build();
                int deletedData = getContentResolver().delete(uriDelete, null, null);
                Log.d("deletedData", String.valueOf(deletedData));
            }
        }
    }

    public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepAdapterViewHolder> {
        List<Step> stepList;

        public StepsAdapter() {

        }

        @Override
        public void onBindViewHolder(final StepAdapterViewHolder holder, final int position) {
            final Step step = stepList.get(position);
            holder.shortDescription.setText(step.getShortDescription());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isTwoPane) {

                        itemDetailFragment = new ItemDetailFragment();
                        Bundle args = new Bundle();

                        args.putParcelable(STEP_PARCELABLE, step);
                        itemDetailFragment.setArguments(args);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_step, itemDetailFragment)
                                .commit();

                    } else {

                        EventBus.getDefault().postSticky(stepList);

                        Intent i = new Intent(StepDetailViewActivity.this, ItemDetailActivity.class);
                        i.putExtra(STEP_INDEX, position);

                        startActivity(i);
                    }
                }
            });
        }

        @Override
        public StepAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layoutIdForListItem = R.layout.steps_list;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            boolean shouldAttachToParentImmediately = false;
            View v = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

            return new StepAdapterViewHolder(v);
        }

        @Override
        public int getItemCount() {
            if (stepList == null) return 0;
            return stepList.size();
        }

        public void setStepList(List<Step> stepList) {
            this.stepList = stepList;
            notifyDataSetChanged();
        }

        class StepAdapterViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_rv_shortDescription)
            TextView shortDescription;

            @BindView(R.id.card_step_list)
            CardView cardStepList;

            View view;

            public StepAdapterViewHolder(View v) {
                super(v);
                view = v;
                ButterKnife.bind(this, v);
            }

        }
    }

    public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {
        List<Ingredient> ingredientList;


        Boolean twoPane;

        @Override
        public void onBindViewHolder(IngredientsViewHolder holder, int position) {
            Ingredient ingredient = ingredientList.get(position);
            holder.ingredient.setText(ingredient.getIngredient());
            holder.quantityMeasure.setText(ingredient.getQuantity() + " " + ingredient.getMeasure());

        }

        @Override
        public int getItemCount() {
            if (ingredientList == null) return 0;

            return ingredientList.size();
        }

        public void setIngredientList(List<Ingredient> ingredientList) {
            this.ingredientList = ingredientList;
            notifyDataSetChanged();
        }

        @Override
        public IngredientsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            int layoutIdForListItem = R.layout.ingredient_list;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;
            View v = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
            return new IngredientsViewHolder(v);

        }

        class IngredientsViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.tv_ingredient)
            TextView ingredient;

            @BindView(R.id.tv_quantity_measure)
            TextView quantityMeasure;

            public IngredientsViewHolder(View v) {

                super(v);
                ButterKnife.bind(this, v);
            }


        }


    }
}

package com.azisamirul.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.azisamirul.bakingapp.model.Ingredient;
import com.azisamirul.bakingapp.model.Model;
import com.azisamirul.bakingapp.model.Step;
import com.azisamirul.bakingapp.network.Network;
import com.azisamirul.bakingapp.utilities.DataService;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private LinearLayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    private String RECIPE_LISTS_SAVED = "recipe_lists_saved";
    List<Model> modelResult;
    private RecyclerView.Adapter adapter;
    int orientation;
    Model model;
    Ingredient ingredient;
    Step step;
    Network network;

    @BindView(R.id.rv_recipelist)
    RecyclerView rvBakingList;


//    @Nullable
//    private RecipeIdlingResource mIdlingResource;
//
//    @VisibleForTesting
//    public IdlingResource getIdlingResource() {
//        if (mIdlingResource == null) {
//            mIdlingResource = new RecipeIdlingResource();
//        }
//        return mIdlingResource;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        ButterKnife.bind(this);
        rvBakingList.setHasFixedSize(true);
        network = new Network(this);

//        getIdlingResource();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RECIPE_LISTS_SAVED)) {
                modelResult = savedInstanceState.getParcelableArrayList(RECIPE_LISTS_SAVED);
                adapter = new SimpleItemRecyclerViewAdapter(modelResult);
                rvBakingList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        } else {
            if (network.checkNetwork()) {
                getData();
            } else {
                displayErrorConnection();
            }
        }
        setLayoutOrientation();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(modelResult!=null) {
            outState.putParcelableArrayList(RECIPE_LISTS_SAVED, new ArrayList<Model>(modelResult));
        }
    }

    private void setLayoutOrientation() {

        orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(this, 3);
            rvBakingList.setLayoutManager(gridLayoutManager);
        } else {
            layoutManager = new LinearLayoutManager(this);
            rvBakingList.setLayoutManager(layoutManager);
        }

    }

    private void getData() {
        Network network = new Network(this);
        final DataService service = network.initialize().create(DataService.class);
        Call<List<Model>> listCall = service.getBakingMenu();
        listCall.enqueue(new Callback<List<Model>>() {
            @Override
            public void onResponse(Call<List<Model>> call, Response<List<Model>> response) {
                List<Model> modelList = response.body();
                List<Ingredient> ingredientsList;
                List<Step> stepList;
                modelResult = new ArrayList<>();
                List<Ingredient> ingredientResult;
                List<Step> stepResult;

                for (int i = 0; i < modelList.size(); i++) {
                    model = new Model();
                    int id = modelList.get(i).getId();
                    String name = modelList.get(i).getName();
                    int servings = modelList.get(i).getServings();
                    String image = modelList.get(i).getImage();
                    ingredientsList = modelList.get(i).getIngredients();
                    stepList = modelList.get(i).getSteps();
                    model.setId(id);
                    model.setName(name);
                    model.setServings(servings);
                    model.setImage(image);
                    ingredientResult = new ArrayList<>();

                    for (int j = 0; j < ingredientsList.size(); j++) {
                        ingredient = new Ingredient();
                        double quantity = ingredientsList.get(j).getQuantity();
                        String measure = ingredientsList.get(j).getMeasure();
                        String ingredients = ingredientsList.get(j).getIngredient();
                        ingredient.setIngredient(ingredients);
                        ingredient.setMeasure(measure);
                        ingredient.setQuantity(quantity);

                        ingredientResult.add(ingredient);
                    }
                    model.setIngredients(ingredientResult);

                    stepResult = new ArrayList<>();
                    for (int k = 0; k < stepList.size(); k++) {
                        step = new Step();
                        int stepId = stepList.get(k).getId();
                        String shortDescription = stepList.get(k).getShortDescription();
                        String description = stepList.get(k).getDescription();
                        String videoURL = stepList.get(k).getVideoURL();
                        String thumbnailURL = stepList.get(k).getThumbnailURL();
                        step.setId(stepId);
                        step.setShortDescription(shortDescription);
                        step.setDescription(description);
                        step.setVideoURL(videoURL);
                        step.setThumbnailURL(thumbnailURL);

                        stepResult.add(step);
                    }
                    model.setSteps(stepResult);

                    modelResult.add(model);


                }

                adapter = new SimpleItemRecyclerViewAdapter(modelResult);
                rvBakingList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Model>> call, Throwable t) {
                Log.d("error", t.getMessage());
                Toast.makeText(RecipeListActivity.this, "Cannot Load Data!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        public final List<Model> models;

        //public final List<Step> steps;
        public SimpleItemRecyclerViewAdapter(List<Model> items) {
            models = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if (models.get(position).getImage().equals("")) {
                holder.mImageList.setImageBitmap(getCakeImage());
            } else {
                //if image available, download image with picasso library.
                Picasso.with(getApplicationContext()).load(models.get(position).getImage()).into(holder.mImageList);
            }
            holder.mContentView.setText(models.get(position).getName());
            String servings = getResources().getString(R.string.total_servings, models.get(position).getServings());
            holder.mContentServings.setText(servings);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().postSticky(models.get(position));
                    Context context = v.getContext();
                    Intent intent = new Intent(context, StepDetailViewActivity.class);
                    context.startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            if (models == null) return 0;
            return models.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public final ImageView mImageList;
            public final TextView mContentServings;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
                mImageList = (ImageView) view.findViewById(R.id.img_list);
                mContentServings = (TextView) view.findViewById(R.id.content_servings);
            }

        }
    }


    private Bitmap getCakeImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_cake);
        return bitmap;
    }

    private void displayErrorConnection() {
        Toast.makeText(this, getString(R.string.error_connection_msg), Toast.LENGTH_LONG).show();
    }
}

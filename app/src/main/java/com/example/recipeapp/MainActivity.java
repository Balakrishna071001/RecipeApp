package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.recipeapp.Adapter.RandomRecipeAdapter;
import com.example.recipeapp.Listners.RandomRecipeResponseListener;
import com.example.recipeapp.Listners.RecipeClickListner;
import com.example.recipeapp.Models.RandomRecipeApiResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ProgressDialog dialog;
    RequestManager manager;
    RandomRecipeAdapter randomRecipeAdapter;
    RecyclerView recyclerView;

    Spinner spinner;

    List<String> tags = new ArrayList<>();

    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loadings...");
        searchView = findViewById(R.id.SearchView_Home);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tags.clear();
                tags.add(query);
                manager.getRandomRecipes(randomRecipeResponseListener, tags);
                dialog.show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        spinner = findViewById(R.id.spinner_tags);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.tags,
                R.layout.spinner_text
        );
        arrayAdapter.setDropDownViewResource(R.layout.spinner_inner_text);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(spinnerSelectedListner);

        manager = new RequestManager(this);
//        manager.getRandomRecipes(randomRecipeResponseListener);
//        dialog.show();

    }
    private  final RandomRecipeResponseListener randomRecipeResponseListener = new RandomRecipeResponseListener() {
        @Override
        public void didFetch(RandomRecipeApiResponse response, String message) {
            recyclerView = findViewById(R.id.recycler_random);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
            randomRecipeAdapter = new RandomRecipeAdapter(MainActivity.this, response.recipes,recipeClickListner);
            recyclerView.setAdapter(randomRecipeAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(MainActivity.this,message, Toast.LENGTH_SHORT);
        }
    };
    private final AdapterView.OnItemSelectedListener spinnerSelectedListner = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        tags.clear();
        tags.add(adapterView.getSelectedItem().toString());
        manager.getRandomRecipes(randomRecipeResponseListener, tags);
        dialog.show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private  final RecipeClickListner recipeClickListner = new RecipeClickListner() {
        @Override
        public void onRecipeClicked(String id) {
              startActivity(new Intent(MainActivity.this, RecipeDetailsActivity.class)
                      .putExtra("id",id));
        }
    };
}
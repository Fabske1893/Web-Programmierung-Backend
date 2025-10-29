package com.rezeptapp;

import com.rezeptapp.data.api.Recipe;
import com.rezeptapp.data.api.RecipeManager;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GraphQLDataFetchers {

    private final RecipeManager recipeManager;

    @Autowired
    public GraphQLDataFetchers(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    // DataFetcher für die Query "recipes"
    public DataFetcher<List<Recipe>> getRecipesDataFetcher() {
        return dataFetchingEnvironment -> recipeManager.getAllRecipes();
    }
}

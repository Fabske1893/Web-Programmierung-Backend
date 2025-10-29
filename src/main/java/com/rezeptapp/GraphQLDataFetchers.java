package com.rezeptapp;

import com.rezeptapp.data.api.Recipe;
import com.rezeptapp.data.api.RecipeManager;
import com.rezeptapp.data.implemented.RecipeImpl;
import com.rezeptapp.data.model.Ingredient;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    // DataFetcher für die Query "recipe(id)"
    public DataFetcher<Recipe> getRecipeByIdDataFetcher() {
        return env -> {
            Integer id = env.getArgument("id");
            Optional<Recipe> r = recipeManager.getRecipeById(id);
            return r.orElse(null);
        };
    }

    // Mutation createRecipe
    public DataFetcher<Boolean> createRecipeDataFetcher() {
        return env -> {
            Map<String, Object> input = env.getArgument("recipe");
            RecipeImpl recipe = new RecipeImpl();
            if (input.get("title") != null) recipe.setTitle((String) input.get("title"));
            if (input.get("imageUrl") != null) recipe.setImageUrl((String) input.get("imageUrl"));
            if (input.get("instructions") != null) recipe.setInstructions((String) input.get("instructions"));
            if (input.get("difficulty") != null) recipe.setDifficulty((String) input.get("difficulty"));
            if (input.get("category") != null) recipe.setCategory((String) input.get("category"));
            if (input.get("likes") != null) recipe.setLikes(((Number) input.get("likes")).intValue());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> ingInputs = (List<Map<String, Object>>) input.get("ingredients");
            if (ingInputs != null) {
                for (Map<String, Object> ing : ingInputs) {
                    double amount = ing.get("amount") == null ? 0.0 : ((Number) ing.get("amount")).doubleValue();
                    String unit = (String) ing.getOrDefault("unit", "");
                    String name = (String) ing.getOrDefault("name", "");
                    recipe.getIngredients().add(new Ingredient(amount, unit, name));
                }
            }
            return recipeManager.addRecipe(recipe);
        };
    }

    // Mutation likeRecipe
    public DataFetcher<Boolean> likeRecipeDataFetcher() {
        return env -> {
            Integer id = env.getArgument("id");
            Optional<Recipe> rOpt = recipeManager.getRecipeById(id);
            if (rOpt.isEmpty()) return false;
            Recipe r = rOpt.get();
            r.setLikes(r.getLikes() + 1);
            return recipeManager.updateRecipe(r);
        };
    }

    // Mutation deleteRecipe
    public DataFetcher<Boolean> deleteRecipeDataFetcher() {
        return env -> {
            Integer id = env.getArgument("id");
            return recipeManager.deleteRecipe(id);
        };
    }
}

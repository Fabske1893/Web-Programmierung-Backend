package com.rezeptapp;

import com.rezeptapp.data.api.Recipe;
import com.rezeptapp.data.api.RecipeManager;
import com.rezeptapp.data.api.User;
import com.rezeptapp.data.api.UserManager;
import com.rezeptapp.data.implemented.UserImpl;
import com.rezeptapp.data.implemented.RecipeImpl;
import com.rezeptapp.data.model.Ingredient;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map;
import java.util.Optional;

@Component
public class GraphQLDataFetchers {

    private final RecipeManager recipeManager;
    private final UserManager userManager;

    @Autowired
    public GraphQLDataFetchers(RecipeManager recipeManager, UserManager userManager) {
        this.recipeManager = recipeManager;
        this.userManager = userManager;
    }

    // DataFetcher für die Query "recipes"
    public DataFetcher<List<Recipe>> getRecipesDataFetcher() {
        return dataFetchingEnvironment -> recipeManager.getAllRecipes();
    }

    // DataFetcher für die Query "myRecipes" - mit Token-Authentifizierung
    public DataFetcher<List<Recipe>> getMyRecipesDataFetcher() {
        return dataFetchingEnvironment -> {
            String token = getTokenFromContext(dataFetchingEnvironment);
            if (token == null || token.equals("OFF") || token.isEmpty()) {
                return new ArrayList<>();
            }
            
            String email = userManager.getEmailFromToken(token);
            if (email == null || email.isEmpty()) {
                return new ArrayList<>();
            }
            
            User user = new UserImpl();
            user.setEmail(email);
            
            return recipeManager.getAllRecipesOfUser(user);
        };
    }

    // Hilfsmethode um Token aus GraphQL Context zu extrahieren
    private String getTokenFromContext(DataFetchingEnvironment environment) {
        try {
            Object context = environment.getContext();
            if (context instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> contextMap = (Map<String, Object>) context;
                Object authHeader = contextMap.get("Authorization");
                if (authHeader != null) {
                    String authStr = authHeader.toString();
                    if (authStr.startsWith("Bearer ")) {
                        return authStr.substring(7);
                    }
                    return authStr;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    // Mutation deleteRecipe
    public DataFetcher<Boolean> deleteRecipeDataFetcher() {
        return env -> {
            Integer id = env.getArgument("id");
            return recipeManager.deleteRecipe(id);
        };
    }

public DataFetcher<Map<String, Object>> getMeDataFetcher() {
    return env -> Map.of(
        "id", 1,
        "username", "Luca",
        "email", "luca@example.com"
    );
}

}

package com.rezeptapp;

import com.rezeptapp.data.api.Recipe;
import com.rezeptapp.data.api.RecipeManager;
import com.rezeptapp.data.api.User;
import com.rezeptapp.data.api.UserManager;
import com.rezeptapp.data.implemented.UserImpl;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

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
}

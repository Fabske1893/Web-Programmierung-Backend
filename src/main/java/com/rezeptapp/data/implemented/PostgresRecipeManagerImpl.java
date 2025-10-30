package com.rezeptapp.data.implemented;
import com.rezeptapp.data.api.Recipe;
import com.rezeptapp.data.api.RecipeManager;
import com.rezeptapp.data.api.User;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.rezeptapp.data.model.Ingredient;



@Component 
public class PostgresRecipeManagerImpl implements RecipeManager {

    private final DataSource dataSource;

   
    public PostgresRecipeManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    
    
    @Override
    public Optional<Recipe> getRecipeById(int id) {
    String recipeSql = "SELECT * FROM recipes WHERE id = ?";
    String ingredientsSql = "SELECT amount, unit, ingredient_name FROM recipe_ingredients WHERE recipe_id = ?"; // Neue Abfrage
    Recipe recipe = null;

    try (Connection connection = dataSource.getConnection();
         PreparedStatement recipePstmt = connection.prepareStatement(recipeSql)) {

        recipePstmt.setInt(1, id);
        try (ResultSet rsRecipe = recipePstmt.executeQuery()) {
            if (rsRecipe.next()) { 
                recipe = new RecipeImpl();
                recipe.setId(rsRecipe.getInt("id"));
                recipe.setTitle(rsRecipe.getString("name"));  // Frontend title <- DB name
                recipe.setImageUrl(rsRecipe.getString("pictureurl")); // Frontend imageUrl <- DB pictureUrl
                recipe.setInstructions(rsRecipe.getString("instructions"));
                recipe.setDifficulty(rsRecipe.getString("difficultylevel")); // Frontend difficulty <- DB difficultyLevel
                recipe.setCategory(rsRecipe.getString("category"));

              
                List<Ingredient> ingredientsList = new ArrayList<>();
                try (PreparedStatement ingredientsPstmt = connection.prepareStatement(ingredientsSql)) {
                    ingredientsPstmt.setInt(1, id);
                    try (ResultSet rsIngredients = ingredientsPstmt.executeQuery()) {
                        while (rsIngredients.next()) {
                            ingredientsList.add(new Ingredient(
                                rsIngredients.getDouble("amount"), 
                                rsIngredients.getString("unit"),
                                rsIngredients.getString("ingredient_name")
                            ));
                        }
                    }
                }
                recipe.setIngredients(ingredientsList); 
                
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return Optional.ofNullable(recipe);
}

@Override
public boolean addRecipe(Recipe recipe) {
    String recipeSql = "INSERT INTO recipes (name, pictureurl, instructions, difficultylevel, category, likes) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
    String ingredientSql = "INSERT INTO recipe_ingredients (recipe_id, amount, unit, ingredient_name) VALUES (?, ?, ?, ?)";
    Connection connection = null; 

    try {
        connection = dataSource.getConnection();
        connection.setAutoCommit(false); 

        int recipeId = -1;

        
        try (PreparedStatement recipePstmt = connection.prepareStatement(recipeSql, Statement.RETURN_GENERATED_KEYS)) {
            recipePstmt.setString(1, recipe.getTitle());  // Frontend title -> DB name
            recipePstmt.setString(2, recipe.getImageUrl()); // Frontend imageUrl -> DB pictureUrl
            recipePstmt.setString(3, recipe.getInstructions());
            recipePstmt.setString(4, recipe.getDifficulty()); // Frontend difficulty -> DB difficultyLevel
            recipePstmt.setString(5, recipe.getCategory());
            recipePstmt.setInt(6, recipe.getLikes());
            recipePstmt.executeUpdate();

            try (ResultSet generatedKeys = recipePstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    recipeId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Konnte keine ID für das neue Rezept erhalten.");
                }
            }
        }

      
        try (PreparedStatement ingredientPstmt = connection.prepareStatement(ingredientSql)) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredientPstmt.setInt(1, recipeId);
                ingredientPstmt.setDouble(2, ingredient.getAmount());
                ingredientPstmt.setString(3, ingredient.getUnit());
                ingredientPstmt.setString(4, ingredient.getName());
                ingredientPstmt.addBatch(); 
            }
            ingredientPstmt.executeBatch(); 
        }

        connection.commit(); 
        return true;

    } catch (SQLException e) {
        e.printStackTrace();
        if (connection != null) {
            try {
                connection.rollback(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    } finally {
        if (connection != null) {
            try {
                connection.setAutoCommit(true); 
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}





    @Override
    public List<Recipe> getAllRecipes() {
    List<Recipe> recipes = new ArrayList<>();
    String recipeSql = "SELECT * FROM recipes";
    String ingredientsSql = "SELECT amount, unit, ingredient_name FROM recipe_ingredients WHERE recipe_id = ?"; 
    try (Connection connection = dataSource.getConnection();
         Statement recipeStmt = connection.createStatement();
         ResultSet rsRecipes = recipeStmt.executeQuery(recipeSql)) {

        while (rsRecipes.next()) {
            Recipe recipe = new RecipeImpl();
            int currentRecipeId = rsRecipes.getInt("id"); 
            recipe.setId(currentRecipeId);
            recipe.setTitle(rsRecipes.getString("name"));
            recipe.setImageUrl(rsRecipes.getString("pictureurl"));
            recipe.setInstructions(rsRecipes.getString("instructions"));
            recipe.setDifficulty(rsRecipes.getString("difficultylevel"));
            recipe.setCategory(rsRecipes.getString("category"));
            recipe.setLikes(rsRecipes.getInt("likes"));

            List<Ingredient> ingredientsList = new ArrayList<>();
            try (PreparedStatement ingredientsPstmt = connection.prepareStatement(ingredientsSql)) {
                ingredientsPstmt.setInt(1, currentRecipeId);
                try (ResultSet rsIngredients = ingredientsPstmt.executeQuery()) {
                    while (rsIngredients.next()) {
                        ingredientsList.add(new Ingredient(
                            rsIngredients.getDouble("amount"),
                            rsIngredients.getString("unit"),
                            rsIngredients.getString("ingredient_name")
                        ));
                    }
                }
            }
            recipe.setIngredients(ingredientsList); 
           

            recipes.add(recipe); 
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return new ArrayList<>();
    }
    return recipes;
}




   

    

    @Override
    public void removeRecipe(Recipe recipe) {
       
    }

    @Override
    public List<String> getIngredientsPerEmail() {
        
        return new ArrayList<>();
    }

    @Override
    public List<Recipe> getAllRecipesOfUser(User user) {
            if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
                return new ArrayList<>();
            }
        
            List<Recipe> recipes = new ArrayList<>();
            String recipeSql = "SELECT * FROM recipes WHERE created_by = ?";
            String ingredientsSql = "SELECT amount, unit, ingredient_name FROM recipe_ingredients WHERE recipe_id = ?";
        
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement recipeStmt = connection.prepareStatement(recipeSql)) {
            
                recipeStmt.setString(1, user.getEmail());
                try (ResultSet rsRecipes = recipeStmt.executeQuery()) {
                    while (rsRecipes.next()) {
                        Recipe recipe = new RecipeImpl();
                        int currentRecipeId = rsRecipes.getInt("id");
                        recipe.setId(currentRecipeId);
                        recipe.setTitle(rsRecipes.getString("name"));
                        recipe.setImageUrl(rsRecipes.getString("pictureurl"));
                        recipe.setInstructions(rsRecipes.getString("instructions"));
                        recipe.setDifficulty(rsRecipes.getString("difficultylevel"));
                        recipe.setCategory(rsRecipes.getString("category"));
                        recipe.setLikes(rsRecipes.getInt("likes"));

                        List<Ingredient> ingredientsList = new ArrayList<>();
                        try (PreparedStatement ingredientsPstmt = connection.prepareStatement(ingredientsSql)) {
                            ingredientsPstmt.setInt(1, currentRecipeId);
                            try (ResultSet rsIngredients = ingredientsPstmt.executeQuery()) {
                                while (rsIngredients.next()) {
                                    ingredientsList.add(new Ingredient(
                                        rsIngredients.getDouble("amount"),
                                        rsIngredients.getString("unit"),
                                        rsIngredients.getString("ingredient_name")
                                    ));
                                }
                            }
                        }
                        recipe.setIngredients(ingredientsList);
                        recipes.add(recipe);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
            return recipes;
    }
    @Override
    public boolean updateRecipe(Recipe recipe) {
    String sql = "UPDATE recipes SET name = ?, pictureurl = ?, instructions = ?, difficultylevel = ?, category = ?, likes = ? WHERE id = ?";
    try (Connection c = dataSource.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, recipe.getTitle());    
        ps.setString(2, recipe.getImageUrl());  
        ps.setString(3, recipe.getInstructions());
        ps.setString(4, recipe.getDifficulty()); 
        ps.setString(5, recipe.getCategory());
        ps.setInt(6, recipe.getLikes());
        ps.setInt(7, recipe.getId());
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    @Override
    public boolean deleteRecipe(int id) {
        String deleteIngredientsSql = "DELETE FROM recipe_ingredients WHERE recipe_id = ?";
        String deleteRecipeSql = "DELETE FROM recipes WHERE id = ?";
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            // Zuerst die Zutaten löschen
            try (PreparedStatement ingredientsPstmt = connection.prepareStatement(deleteIngredientsSql)) {
                ingredientsPstmt.setInt(1, id);
                ingredientsPstmt.executeUpdate();
            }

            // Dann das Rezept löschen
            try (PreparedStatement recipePstmt = connection.prepareStatement(deleteRecipeSql)) {
                recipePstmt.setInt(1, id);
                int rowsAffected = recipePstmt.executeUpdate();
                connection.commit();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
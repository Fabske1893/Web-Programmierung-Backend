package com.rezeptapp.data.implemented;
import com.rezeptapp.data.api.Recipe;
import com.rezeptapp.data.api.RecipeManager;
import com.rezeptapp.data.api.User;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component 
public class PostgresRecipeManagerImpl implements RecipeManager {

    private final DataSource dataSource;

   
    public PostgresRecipeManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean addRecipe(Recipe recipe) {
        String insertSQL = "INSERT INTO recipes (name, picture, ingredients, instructions, difficulty, category) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {

            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getPictureUrl());
            pstmt.setString(3, recipe.getIngredients());
            pstmt.setString(4, recipe.getInstructions());
            pstmt.setString(5, recipe.getDifficultyLevel());
            pstmt.setString(6, recipe.getCategory());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT * FROM recipes";

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Recipe recipe = new RecipeImpl();
                recipe.setName(rs.getString("name"));
                recipe.setPictureUrl(rs.getString("pictureUrl"));
                recipe.setIngredients(rs.getString("ingredients"));
                recipe.setInstructions(rs.getString("instructions"));
                recipe.setDifficultyLevel(rs.getString("difficultyLevel"));
                recipe.setCategory(rs.getString("category"));
                recipes.add(recipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        
        return new ArrayList<>();
    }
}
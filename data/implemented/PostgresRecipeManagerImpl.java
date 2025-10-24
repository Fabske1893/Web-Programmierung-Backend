package data.implemented;
import data.api.Recipe;
import data.api.RecipeManager;
import org.apache.commons.dbcp2.BasicDataSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgresRecipeManagerImpl implements RecipeManager  {

    String databaseURL = "jdbc:postgresql://u66omc8i022k92:p7fc989cf794cb2f78c6d16c2f2704823c9119a9cf51adc666a267dae92e98493@c18qegamsgjut6.cluster-czrs8kj4isg7.us-east-1.rds.amazonaws.com:5432/d4bjcmj7120lb0";
    String username = "u66omc8i022k92";
    String password = "p7fc989cf794cb2f78c6d16c2f2704823c9119a9cf51adc666a267dae92e98493";
    BasicDataSource basicDataSource;

    // Singleton
    static PostgresRecipeManagerImpl postgresRecipeManager = null;
    private PostgresRecipeManagerImpl() {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(databaseURL);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
    }
    public static synchronized PostgresRecipeManagerImpl getPostgresRecipeManagerImpl() {
        if (postgresRecipeManager == null)
            postgresRecipeManager = new PostgresRecipeManagerImpl();
        return postgresRecipeManager;
    }


   

    @Override
    public boolean addRecipe(Recipe recipe) {
        final Logger createRecipeLogger = Logger.getLogger("CreateRecipeLogger");
        createRecipeLogger.log(Level.INFO,"Start creating a Recipe " + recipe.getName());
        Statement stmt = null;
        Connection connection = null;

                // SQL-Befehl mit Platzhaltern (?)
        String insertSQL = "INSERT INTO rezepte (name, picture, ingredients, instructions, difficulty, category) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = basicDataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {

            // Werte sicher an die Platzhalter binden
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getPictureUrl());
            pstmt.setString(3, recipe.getIngredients());
            pstmt.setString(4, recipe.getInstructions());
            pstmt.setString(5, recipe.getDifficultyLevel());
            pstmt.setString(6, recipe.getCategory());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    //nochmal anschauen sollte ja nur 1 mal ausgeführt werden oder?
    public void createRecipeTable() {
        // Be carefull: It deletes data if table already exists.
        Statement stmt = null;
        Connection connection = null;
        try {
            connection = basicDataSource.getConnection();
            stmt = connection.createStatement();

            stmt.executeUpdate("DROP TABLE IF EXISTS recipes");
            stmt.executeUpdate("DROP TABLE IF EXISTS recipe_ingredients");

        String createRecipeTable ="CREATE TABLE recipes (" +
                                    "id SERIAL PRIMARY KEY, " +
                                    "name varchar(100) NOT NULL, " +
                                    "picture varchar(100) NOT NULL, " +
                                    "ingredients varchar(1000) NOT NULL, " +
                                    "instructions varchar(1000) NOT NULL, " +
                                    "difficulty varchar(20) NOT NULL, " +
                                    "category varchar(100) NOT NULL )"
        
        String createIngredientsTable = "CREATE TABLE recipe_ingredients (" +
                                    "id SERIAL PRIMARY KEY, " +
                                    "recipe_id INT NOT NULL, " +
                                    "amount int NOT NULL, " + 
                                    "unit VARCHAR(50) NOT NULL, " +
                                    "ingredient_name VARCHAR(100) NOT NULL, " + 
                                    //"FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)"; 

            
            stmt.executeUpdate(createRecipeTable);
            stmt.executeUpdate(createIngredientsTable);
            

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT * FROM rezepte"; //rezepte is der tabellen name der PostgresDB tabelle

        try (Connection connection = basicDataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Recipe recipe = new RecipeImpl();
                recipe.setName(rs.getString("name"));
                recipe.setPictureUrl(rs.getString("picture"));
                recipe.setIngredients(rs.getString("ingredients"));
                recipe.setInstructions(rs.getString("instructions"));
                recipe.setDifficultyLevel(rs.getString("difficulty"));
                recipe.setCategory(rs.getString("category"));
                recipes.add(recipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return recipes;
    }


}
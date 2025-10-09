import data.api.Task;
import data.api.TaskManager;
import org.apache.commons.dbcp2.BasicDataSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgresRecipeManagerImpl implements RecipeManager  {

    String databaseURL = "";
    String username = "";
    String password = "";
    BasicDataSource basicDataSource;

    // Singleton
    static PostgresRecipeManagerImpl postgresRecipeManager = null;
    private PostgresRecipeManagerImpl() {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(databaseURL);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
    }
    public static PostgresRecipeManagerImpl getPostgresRecipeManagerImpl() {
        if (postgresRecipeManager == null)
            postgresRecipeManager = new PostgresRecipeManagerImpl();
        return postgresRecipeManager;
    }


   

    @Override
    public boolean addRecipe(Recipe recipe) {
        final Logger createRecipeLogger = Logger.getLogger("CreateRecipeLogger");
        createRecipeLogger.log(Level.INFO,"Start creating a Recipe " + task.getName());
        Statement stmt = null;
        Connection connection = null;

        try {
            connection = basicDataSource.getConnection();
            stmt = connection.createStatement();
            String udapteSQL = "INSERT into recipes (name, picture, ingredients, instructions, level, category ) VALUES (" +
                    "'" + task.getName() + "', " + task.getPictureUrl() + "', " + task.getIngredients() + "', " + task.getInstructions() + "', " + task.getDifficultyLevel() +"', " + task.getCategory() +"')";
            stmt.executeUpdate(udapteSQL);

            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        try {
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
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

        String createRecipeTable ="CREATE TABLE tasks (" +
                                    "id SERIAL PRIMARY KEY, " +
                                    "name varchar(100) NOT NULL, " +
                                    "picture varchar(100) NOT NULL, " +
                                    "ingredients varchar(1000) NOT NULL, " +
                                    "instructions varchar(1000) NOT NULL, " +
                                    "difficulty int NOT NULL, " +
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


}
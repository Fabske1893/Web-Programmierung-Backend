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


   
/* 
    @Override
    public boolean addRecipe(Recipe recipe) {
        final Logger createRecipeLogger = Logger.getLogger("CreateRecipeLogger");
        createRecipeLogger.log(Level.INFO,"Start creating task " + task.getName());
        Statement stmt = null;
        Connection connection = null;

        try {
            connection = basicDataSource.getConnection();
            stmt = connection.createStatement();
            String udapteSQL = "INSERT into tasks (name, priority, email) VALUES (" +
                    "'" + task.getName() + "', " + task.getPriority() + ", '" + task.getEmail() + "')";
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

    public void createTaskTable() {
        // Be carefull: It deletes data if table already exists.
        Statement stmt = null;
        Connection connection = null;
        try {
            connection = basicDataSource.getConnection();
            stmt = connection.createStatement();

            stmt.executeUpdate("DROP TABLE IF EXISTS tasks");
            stmt.executeUpdate("CREATE TABLE tasks (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name varchar(100) NOT NULL, " +
                    "priority int NOT NULL, " +
                    "email varchar(100) NOT NULL)");

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

*/
}
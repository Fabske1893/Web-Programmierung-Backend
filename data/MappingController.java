package data;

import data.api.TaskManager;
import data.api.UserManager;
import data.implemented.*;
import data.model.alexa.AlexaRO;
import data.model.alexa.OutputSpeechRO;
import data.model.alexa.ResponseRO;
import data.model.user.Token;
import data.model.user.TokenAnswer;
import data.model.user.User;
import data.model.task.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class MappingController {

    RecipeManager recipeManager =
            
            PostgresRecipeManagerImpl.getPostgresRecipeManagerImpl();

    UserManager userManager =
            
            PostgresUserManagerImpl.getPostgresUserManagerImpl();

    @PostMapping(
            path = "/login",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public TokenAnswer loginUser(@RequestBody User user) {

        Logger myLogger = Logger.getLogger("UserLoggingOn");
        myLogger.info("Received a POST request on login with email " + user.getEmail());

        String token = userManager.logUserOn(user.getEmail(), user.getPassword());
        myLogger.info("Token generated " + token);

        // TODO
        // Fehlerfall behandeln

        return
                new TokenAnswer(token,"200");
    }


    @DeleteMapping(
            path = "/login",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public data.model.user.MessageAnswer logOffUser(@RequestBody Token token) {

        Logger myLogger = Logger.getLogger("UserLoggingOff");
        myLogger.info("Received a DELETE request on login with token " + token.getToken());

        boolean couldLogoffUser =
                userManager.logUserOff(userManager.getUserEmailFromToken(token.getToken()));

        myLogger.info("User logged off " + couldLogoffUser);

        // TODO
        // Fehlerfall behandeln

        return
                new data.model.user.MessageAnswer("User logged out.");
    }


    @PostMapping(
            path = "/user",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public com.mosbach.demo.model.user.MessageAnswer createUser(@RequestBody UserWithName userWithName) {

        Logger myLogger = Logger.getLogger("UserCreate");
        myLogger.info("Received a POST request on user with email " + userWithName.getEmail());

        boolean couldCreateUser = userManager
                        .createUser(
                            new UserImpl(
                                    userWithName.getName(),
                                    userWithName.getEmail(),
                                    userWithName.getPassword(),
                                    "OFF"
                            )
                        );
        myLogger.info("User created " + couldCreateUser);

        // TODO
        // Fehlerfall behandeln

        return
                new com.mosbach.demo.model.user.MessageAnswer("User created.");
    }


    @PostMapping(
            path = "/recipe",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public com.mosbach.demo.model.user.MessageAnswer loginUser(@RequestBody TokenRecipe tokenRecipe) {

        Logger myLogger = Logger.getLogger("AddRecipe");
        myLogger.info("Received a POST request on recipe with token " + tokenRecipe.getToken());

        String email = userManager.getUserEmailFromToken(tokenRecipe.getToken());
        myLogger.info("Found the following email for this token " + email);
        if (email.equals("NOT-FOUND"))
            return
                    new data.model.user.MessageAnswer("No user found or not logged on.");;
        boolean couldCreateTask = recipeManager
                .addRecipe(
                        new RecipeImpl(
                                tokenRecipe.getRecipe().getName(),
                                tokenRecipe.getRecipe().getIngredients(),
                                tokenRecipe.getRecipe().getInstructions(),
                                tokenRecipe.getRecipe().getDifficultyLevel(),
                                tokenRecipe.getRecipe().getCategory(),
                                tokenRecipe.getRecipe().getPictureUrl(),
                                email
                        )
                );

        myLogger.info("Recipe created " + couldCreateRecipe);

        // TODO
        // Fehlerfall behandeln

        return
                new com.mosbach.demo.model.user.MessageAnswer("Recipe created.");
    }
/* 
    @GetMapping("/task")
    public TaskList getTasks(@RequestParam(value = "token", defaultValue = "123") String token) {

        Logger myLogger = Logger.getLogger("TaskLogger");
        myLogger.info("Received a GET request on task with token " + token);

        String email = userManager.getUserEmailFromToken(token);
        List<com.mosbach.demo.data.api.Task> tasks = taskManager.getAllTasksPerEmail(email);
        List<Task> result = new ArrayList<>();
        for (com.mosbach.demo.data.api.Task t : tasks)
            result.add(new Task(t.getName(), t.getPriority()));

        // TODO
        // Fehlerfall behandeln

        return
                new TaskList(result);
    }

    @GetMapping("/task/createtables")
    @ResponseStatus(HttpStatus.OK)
    public String createTask() {
        PostgresTaskManagerImpl.getPostgresTaskManagerImpl().createTaskTable();
        PostgresUserManagerImpl.getPostgresUserManagerImpl().createUserTable();
        return "Database Tables created";
    }






    @PostMapping(
            path = "/alexa",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public AlexaRO createTask(@RequestBody AlexaRO alexaRO) {

        Logger.getLogger("MappingController").log(Level.INFO,"MappingController POST /alexa ");
        String outText = "";

        if (alexaRO.getRequest().getType().equalsIgnoreCase("LaunchRequest"))
            outText += "Welcome to the Mosbach Task Organizer. ";

        if (alexaRO.getRequest().getType().equalsIgnoreCase("IntentRequest")
                &&
                (alexaRO.getRequest().getIntent().getName().equalsIgnoreCase("TaskReadIntent"))
        ) {
            List<com.mosbach.demo.data.api.Task> tasks = taskManager.getAllTasksPerEmail("mh@test.com");
            if (!tasks.isEmpty()) {
                outText += "You have to do the following tasks. ";
                int i = 1;
                for (com.mosbach.demo.data.api.Task t : tasks) {
                    outText += "Task Number " + i + " with Name " + t.getName()
                        + " and priority " + t.getPriority() + " . ";
                    i++;
                }
            }
            else outText += "This is your lucky day. You have no tasks to do. ";
        }
        return
                prepareResponse(alexaRO, outText, true);
    }

*/
    @PostMapping(
            path = "/alexa",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public AlexaRO getRecipeInstructions(@RequestBody AlexaRO alexaRO) {

        String outText = "";


        return alexaRO;
    }

    private AlexaRO prepareResponse(AlexaRO alexaRO, String outText, boolean shouldEndSession) {

        alexaRO.setRequest(null);
        alexaRO.setSession(null);
        alexaRO.setContext(null);
        OutputSpeechRO outputSpeechRO = new OutputSpeechRO();
        outputSpeechRO.setType("PlainText");
        outputSpeechRO.setText(outText);
        ResponseRO response = new ResponseRO(outputSpeechRO, shouldEndSession);
        alexaRO.setResponse(response);
        return alexaRO;
    }

}

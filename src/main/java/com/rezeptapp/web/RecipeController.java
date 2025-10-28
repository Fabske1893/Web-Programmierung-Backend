package com.rezeptapp.web;

import java.util.Optional;
import com.rezeptapp.data.api.Recipe;
import com.rezeptapp.data.api.RecipeManager;
import com.rezeptapp.data.api.UserManager;
import com.rezeptapp.data.implemented.UserImpl;
import com.rezeptapp.data.model.Ingredient;
import com.rezeptapp.web.api.MessageAnswer;
import com.rezeptapp.web.api.tokenAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.rezeptapp.data.implemented.EmailService;
import com.rezeptapp.data.implemented.RecipeImpl;
import java.util.List;
import com.rezeptapp.web.api.ShoppingListRequest;
import java.util.Collections;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RecipeController {

    /* // Konstruktor und Manager VORÜBERGEHEND AUSKOMMENTIEREN
    private final RecipeManager recipeManager;
    private final UserManager userManager;
    private final EmailService emailService;

    @Autowired
    public RecipeController(RecipeManager recipeManager, UserManager userManager, EmailService emailService) {
        this.recipeManager = recipeManager;
        this.userManager = userManager;
        this.emailService = emailService;
    }
    */

    @GetMapping("/recipes/{id}")
    public /*Recipe*/ ResponseEntity<?> getRecipeDetails(@PathVariable int id) {
        // Optional<Recipe> recipeOpt = recipeManager.getRecipeById(id);
        // ...
        // return recipeOpt.get();
         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Temporär deaktiviert"); // Platzhalter
    }

    @PostMapping("/shopping-list/send-email")
    public ResponseEntity<?> sendShoppingListByEmail(@RequestBody /*ShoppingListRequest*/ Object request) {
        // String userEmail = userManager.getEmailFromToken(request.getToken());
        // ...
        return ResponseEntity.ok(Collections.singletonMap("message", "Temporär deaktiviert")); // Platzhalter
    }

    @PostMapping("/recipes/{id}/send-email")
    public ResponseEntity<?> sendRecipeByEmail(@PathVariable int id, @RequestParam String token) {
         // String recipientEmail = userManager.getEmailFromToken(token);
         // ...
         return ResponseEntity.ok(Collections.singletonMap("message", "Temporär deaktiviert")); // Platzhalter
    }

    @GetMapping("/recipes")
    public List<?> getAllRecipes() {
        // return recipeManager.getAllRecipes();
        return Collections.emptyList(); // Leere Liste als Platzhalter
    }

    @PostMapping("/recipes")
    public ResponseEntity<?> createRecipe(@RequestBody /*RecipeImpl*/ Object recipe) {
        // boolean success = recipeManager.addRecipe(recipe);
        // ...
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("message", "Temporär deaktiviert")); // Platzhalter
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody /*UserImpl*/ Object user) {
         // boolean success = userManager.registerUser(user);
         // ...
         return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("message", "Temporär deaktiviert")); // Platzhalter
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody /*UserImpl*/ Object user) {
         // String token = userManager.loginUser(user.getEmail(), user.getPassword());
         // ...
         return ResponseEntity.ok(Collections.singletonMap("token", "TEMP_TOKEN")); // Platzhalter
    }
}
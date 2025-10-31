package com.rezeptapp.web;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/alexa")
public class AlexaController {

    @PostMapping
    public ResponseEntity<Map<String, Object>> handleAlexaRequest(@RequestBody Map<String, Object> request) {
        
        String requestType = ((Map<String, Object>) request.get("request")).get("type").toString();

        if ("LaunchRequest".equals(requestType)) {
            return ResponseEntity.ok(Map.of(
                "version", "1.0",
                "response", Map.of(
                    "outputSpeech", Map.of("type", "PlainText",
                        "text", "Willkommen bei der Rezept-App! Sage mir den Rezeptnamen, den ich vorlesen soll."),
                    "shouldEndSession", false
                )
            ));
        }

        if ("IntentRequest".equals(requestType)) {
            Map<String, Object> intent = (Map<String, Object>) ((Map<String, Object>) request.get("request")).get("intent");
            String intentName = intent.get("name").toString();

            if ("GetRecipeIntent".equals(intentName)) {
                Map<String, Object> slots = (Map<String, Object>) intent.get("slots");
                String recipeName = ((Map<String, Object>) slots.get("recipeName")).get("value").toString();

                String recipeText = "Das Rezept für " + recipeName + 
                    ". Du brauchst Hackfleisch, Tomaten und Nudeln. Die Zubereitung dauert 30 Minuten. Guten Appetit!";

                return ResponseEntity.ok(Map.of(
                    "version", "1.0",
                    "response", Map.of(
                        "outputSpeech", Map.of("type", "PlainText", "text", recipeText),
                        "shouldEndSession", true
                    )
                ));
            }
        }

        return ResponseEntity.ok(Map.of(
            "version", "1.0",
            "response", Map.of(
                "outputSpeech", Map.of("type", "PlainText", "text", "Ich habe das leider nicht verstanden."),
                "shouldEndSession", true
            )
        ));
    }

    @GetMapping("/recipe")
public ResponseEntity<String> readRecipe(@RequestParam("id") int id) {
    try {
        // GraphQL Query bauen
        String query = String.format("""
        {
          recipe(id: %d) {
            title
            ingredients {
              amount
              unit
              name
            }
            instructions
          }
        }
        """, id);

        // Verbindung aufbauen
        java.net.URL url = new java.net.URL("https://rezeptappbackend-a9a2cded5f95.herokuapp.com/graphql");
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Richtiger JSON-Body (kein doppeltes Escaping!)
        String jsonBody = "{\"query\": \"" + query.replace("\n", " ").replace("\"", "\\\"") + "\"}";
        try (var os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }

        // Antwort lesen
        StringBuilder response = new StringBuilder();
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        // JSON parsen
        var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        var root = mapper.readTree(response.toString());
        var recipeNode = root.path("data").path("recipe");

        // Prüfen, ob Rezept vorhanden ist
        if (recipeNode.isMissingNode() || recipeNode.isNull()) {
            return ResponseEntity.ok("Ich konnte das Rezept nicht finden.");
        }

        String title = recipeNode.path("title").asText();
        var ingredients = recipeNode.path("ingredients");
        String instructions = recipeNode.path("instructions").asText();

        // Text zusammenbauen
        StringBuilder text = new StringBuilder();
        text.append("Das Rezept für ").append(title).append(". ");
        text.append("Zutaten: ");
        for (var ing : ingredients) {
            text.append(ing.path("amount").asText()).append(" ")
                .append(ing.path("unit").asText()).append(" ")
                .append(ing.path("name").asText()).append(", ");
        }
        text.append("Zubereitung: ").append(instructions);

        return ResponseEntity.ok(text.toString());

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.ok("Fehler beim Lesen des Rezepts.");
    }
}
}

   


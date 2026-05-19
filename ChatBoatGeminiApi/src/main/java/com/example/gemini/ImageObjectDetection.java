package com.example.gemini;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.genai.types.ThinkingConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ImageObjectDetection {

    public static void main(String[] args) throws Exception {

        String prompt = """
                describe everything in this image
                """;

        // Create Gemini client with API key
        Client client = Client.builder()
                .apiKey("AIzaSyAKMIxus2EUs4NaA7IG30Tl0Q8QmEiBiag")
                .build();

        // Read image bytes
        byte[] imageBytes = Files.readAllBytes(Path.of("my-image.jpg"));

        // Create image part
        // For .jpg or .jpeg files, use "image/jpeg"
        Part imagePart = Part.fromBytes(imageBytes, "image/jpeg");

        // Create prompt part
        Part promptPart = Part.fromText(prompt);

        // Create Content object containing both parts
        Content content = Content.builder()
                .parts(List.of(imagePart, promptPart))
                .build();

        // Build configuration
        GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(1.0f)
                .thinkingConfig(
                        ThinkingConfig.builder()
                                .thinkingBudget(0)
                                .build()
                )
                .build();

        // Call Gemini
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-robotics-er-1.6-preview",
                        List.of(content),
                        config
                );

        // Print result
        System.out.println(response.text());
    }
}
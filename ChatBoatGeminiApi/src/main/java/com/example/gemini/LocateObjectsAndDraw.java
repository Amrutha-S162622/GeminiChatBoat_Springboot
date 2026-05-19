package com.example.gemini;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LocateObjectsAndDraw {

    public static void main(String[] args) throws Exception {

        String prompt = """
                Detect all clearly visible objects in the image.

                Return only valid JSON in this exact format:
                [
                  {
                    "label": "person",
                    "box_2d": [ymin, xmin, ymax, xmax]
                  }
                ]

                Rules:
                1. Coordinates must be normalized from 0 to 1000.
                2. Include every visible object.
                3. Do not include markdown or explanations.
                """;

        // Create Gemini client
        Client client = Client.builder()
                .apiKey("AIzaSyAKMIxus2EUs4NaA7IG30Tl0Q8QmEiBiag")
                .build();

        // Load input image
        String inputImagePath = "ground.jpg";
        byte[] imageBytes = Files.readAllBytes(Path.of(inputImagePath));

        // Create request parts
        Part imagePart = Part.fromBytes(imageBytes, "image/jpg");
        Part promptPart = Part.fromText(prompt);

        // Build content
        Content content = Content.builder()
                .parts(List.of(imagePart, promptPart))
                .build();

        // Call Gemini
        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                List.of(content),
                GenerateContentConfig.builder().build()
        );

        // Get JSON response
        String json = response.text();

        // Remove markdown code fences if present
        json = json.replace("```json", "")
                   .replace("```", "")
                   .trim();

        System.out.println("Gemini Response:");
        System.out.println(json);

        // Output file name
        String outputImagePath = "output.jpg";

        // Draw bounding boxes and labels
        drawBoundingBoxes(inputImagePath, json, outputImagePath);

        System.out.println("Annotated image saved to: " + new File(outputImagePath).getAbsolutePath());
    }

    public static void drawBoundingBoxes(String inputPath,
                                         String json,
                                         String outputPath) throws Exception {

        BufferedImage image = ImageIO.read(new File(inputPath));
        int width = image.getWidth();
        int height = image.getHeight();

        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(3));
        g.setFont(new Font("Arial", Font.BOLD, 20));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        for (JsonNode node : root) {
            String label = node.get("label").asText();
            JsonNode box = node.get("box_2d");

            int ymin = box.get(0).asInt();
            int xmin = box.get(1).asInt();
            int ymax = box.get(2).asInt();
            int xmax = box.get(3).asInt();

            // Convert normalized coordinates to pixels
            int x = xmin * width / 1000;
            int y = ymin * height / 1000;
            int w = (xmax - xmin) * width / 1000;
            int h = (ymax - ymin) * height / 1000;

            // Draw rectangle
            g.drawRect(x, y, w, h);

            // Draw label background
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(label) + 10;
            int textHeight = fm.getHeight();
            int labelY = Math.max(y - textHeight, 0);

            g.setColor(Color.YELLOW);
            g.fillRect(x, labelY, textWidth, textHeight);

            // Draw label text
            g.setColor(Color.BLACK);
            g.drawString(label, x + 5, labelY + fm.getAscent());

            // Restore box color
            g.setColor(Color.RED);
        }

        g.dispose();

        // Save the annotated image
        ImageIO.write(image, "jpg", new File(outputPath));
    }
}
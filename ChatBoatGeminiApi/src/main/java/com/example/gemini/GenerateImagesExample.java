package com.example.gemini;

import com.google.genai.Client;
import com.google.genai.types.GenerateImagesConfig;
import com.google.genai.types.GenerateImagesResponse;
import com.google.genai.types.GeneratedImage;
import com.google.genai.types.Image;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Optional;

public class GenerateImagesExample {

    public static void main(String[] args) throws Exception {

        // Create client
        Client client = Client.builder()
                .apiKey("AIzaSyAKMIxus2EUs4NaA7IG30Tl0Q8QmEiBiag")
                .build();

        // Generate images
        GenerateImagesResponse response =
                client.models.generateImages(
                        "imagen-4.0-generate-001",
                        "Robot holding a red skateboard",
                        GenerateImagesConfig.builder()
                                .numberOfImages(4)
                                .build()
                );

        // generatedImages() returns Optional<List<GeneratedImage>>
        Optional<List<GeneratedImage>> optionalImages =
                response.generatedImages();

        if (optionalImages.isPresent()) {

            List<GeneratedImage> images = optionalImages.get();

            int index = 1;

            for (GeneratedImage generatedImage : images) {

                // image() returns Optional<Image>
                Optional<Image> optionalImage =
                        generatedImage.image();

                if (optionalImage.isPresent()) {

                    Image image = optionalImage.get();

                    // imageBytes() returns Optional<byte[]>
                    Optional<byte[]> optionalBytes =
                            image.imageBytes();

                    if (optionalBytes.isPresent()) {

                        byte[] imageBytes =
                                optionalBytes.get();

                        String fileName =
                                "generated-image-" + index + ".png";

                        try (FileOutputStream fos =
                                     new FileOutputStream(fileName)) {
                            fos.write(imageBytes);
                        }

                        System.out.println("Saved: " + fileName);
                        index++;
                    }
                }
            }
        } else {
            System.out.println("No images were generated.");
        }
    }
}
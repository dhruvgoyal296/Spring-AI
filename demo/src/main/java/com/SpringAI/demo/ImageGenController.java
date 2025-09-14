package com.SpringAI.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImageGenController {
    private ChatClient chatClient;
    private OpenAiImageModel openAiImageModel;

    public ImageGenController (OpenAiImageModel openAiImageModel, ChatClient.Builder builder) {
        this.chatClient = builder.build();
        this.openAiImageModel = openAiImageModel;
    }

    /*
    @GetMapping("/image/{query}")
    public String genImage (@PathVariable String query) {
        ImagePrompt imagePrompt = new ImagePrompt(query);
        ImageResponse response = openAiImageModel.call(imagePrompt);
        return response.getResult().getOutput().getUrl();
    }*/

    @GetMapping("/image/{query}")
    public String genImage (@PathVariable String query) {
        OpenAiImageOptions options = OpenAiImageOptions.builder()
                .quality("hd")
                .height(1024)
                .width(1024)
                .style("natural")
                .build();
        ImagePrompt prompt = new ImagePrompt(query, options);
        ImageResponse response = openAiImageModel.call(prompt);
        return response.getResult().getOutput().getUrl();
    }

    @PostMapping("/image/describe")
    public String descImage (@RequestParam String query, @RequestParam MultipartFile file) {
        return chatClient.prompt()
                .user(u -> u
                        .text(query)
                        .media(MimeTypeUtils.IMAGE_JPEG, file.getResource()))
                .call()
                .content();
    }
}

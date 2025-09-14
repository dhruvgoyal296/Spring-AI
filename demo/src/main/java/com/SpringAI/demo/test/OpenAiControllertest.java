package com.SpringAI.demo.test;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class OpenAiControllertest {
    private ChatClient chatClient;
    private ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
    private OpenAiImageModel openAiImageModel;

    public OpenAiControllertest(ChatClient.Builder builder, OpenAiImageModel openAiImageModel) {
        chatClient = builder.build();
        this.openAiImageModel = openAiImageModel;
    }

    @GetMapping("/api/{message}")
    String getAnswer (@PathVariable String message) {
        ChatResponse chatResponse  = chatClient.prompt(message).call().chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }

    @PostMapping("/api/recommend")
    String getAnswer2(@RequestParam String type, @RequestParam String time) {
        String temp = """
              i want to watch a {type} movie tonight........ 
                """;
        PromptTemplate promptTemplate = new PromptTemplate(temp);
        Prompt prompt = promptTemplate.create(Map.of("type", type, "time", time));
        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }

    @PostMapping("/api/genImage/{text}")
    String genImage (@PathVariable String text) {
        OpenAiImageOptions options = OpenAiImageOptions.builder()
                .quality("hd")
                .build();
        ImagePrompt prompt = new ImagePrompt(text, options);
        ImageResponse imageResponse = openAiImageModel.call(prompt);
        return imageResponse.getResult().getOutput().getUrl();
    }

    @PostMapping("/api/descImage")
    String descImage (@RequestParam String query, @RequestParam MultipartFile file) {
        return chatClient.prompt().user(u -> u.text(query).media((Media) file.getResource())).call().content();
    }
}

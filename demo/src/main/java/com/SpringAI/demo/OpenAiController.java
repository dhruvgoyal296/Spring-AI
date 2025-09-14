package com.SpringAI.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
public class OpenAiController {
    private ChatClient chatClient;
    private ChatModel chatModel;
    ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
    @Autowired
    @Qualifier("openAiEmbeddingModel")
    private EmbeddingModel embeddingModel;

    @Autowired
    private VectorStore vectorStore;

    public OpenAiController (ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultAdvisors(MessageWindowChatMemory.builder(chatMemory).build())
                .build();
    }

    @GetMapping("/api/{message}")
    public ResponseEntity<String> getAnswer (@PathVariable String message) {
        ChatResponse chatResponse = chatClient.prompt(message)
                .call()
                .chatResponse();
        System.out.println(chatResponse.getMetadata().getModel());
        String response = chatResponse.getResult().getOutput().getText();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/recommend")
    public String recommend (@RequestParam String type, @RequestParam String year, @RequestParam String lang) {
        String temp = """
              i want to watch a {type} movie tonight........ 
                """;
        PromptTemplate promptTemplate = new PromptTemplate(temp);
        Prompt prompt = promptTemplate.create(Map.of("type", type, "year", year, "lang", lang));
//        String response = chatClient.prompt(prompt).call().content();
        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
        String response = chatResponse.getResult().getOutput().getText();
        return response;
    }

    @PostMapping("/api/embedding")
    public float[] embedding(@RequestParam String text) {
        return embeddingModel.embed(text);
    }

    @PostMapping("/api/similarity")
    public double getSimilarity (@RequestParam String text1, @RequestParam String text2) {
        float[] embedding1 = embeddingModel.embed(text1);
        float[] embedding2 = embeddingModel.embed(text2);
        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;
        for (int i = 0; i < embedding1.length; i++) {
            dotProduct += embedding1[i] * embedding2[i];
            norm1 += Math.pow(embedding1[i],2);
            norm2 += Math.pow(embedding2[i],2);
        }
        return dotProduct*100/(Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    @PostMapping("/api/product")
    public List<Document> getProducts(@RequestParam String text) {
        return vectorStore.similaritySearch(SearchRequest.builder().query(text).build());
    }

    @PostMapping("/api/ask")
    public String getAnswerUsingRag (@RequestParam String query) {
        return chatClient
                .prompt(query)
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .call()
                .content();
    }
}

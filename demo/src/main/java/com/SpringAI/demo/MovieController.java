package com.SpringAI.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class MovieController {
    private ChatClient chatClient;

    public MovieController (ChatModel chatModel) {
        chatClient = ChatClient.create(chatModel);
    }

    @GetMapping("/movies")
    public List<String> getMovies(@RequestParam String name) {
        List <String> movies = chatClient.prompt()
                .user(u -> u.text("List top 5 movies of {name}"). param("name", name))
                .call()
                .entity(new ListOutputConverter(new DefaultConversionService()));
        return movies;
    }

    @GetMapping("/movie")
    public Movie getMovieData(@RequestParam String name) {
//        BeanOutputConverter<Movie> opCon = new BeanOutputConverter<Movie>(Movie.class);
        Movie movie = chatClient.prompt()
                .user(u -> u.text("Get me the best movie of {name}").param("name",name))
                .call()
                .entity(new BeanOutputConverter<Movie>(Movie.class));
        return movie;
    }

    @GetMapping("/moviesList")
    public List<Movie> getMovieList(@RequestParam String name) {
        List<Movie> movies = chatClient.prompt()
                .user(u -> u.text("Get me the best movie of {name}").param("name",name))
                .call()
                .entity(new BeanOutputConverter<List<Movie>>(new ParameterizedTypeReference<List<Movie>>() {}));
        return movies;
    }

}

package com.SpringAI.demo;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AudioGenController {
    private OpenAiAudioTranscriptionModel audiomodel;
    private OpenAiAudioSpeechModel audioSpeechModel;
    private ChatClient chatClient;

    public AudioGenController(OpenAiAudioTranscriptionModel audiomodel, OpenAiAudioSpeechModel audioSpeechModel) {
        this.audiomodel = audiomodel;
        this.audioSpeechModel = audioSpeechModel;
    }

    /*
    @PostMapping("api/stt")
    public String speechToText(@RequestParam MultipartFile file) {
        return audiomodel.call(file.getResource());
    }*/

    @PostMapping("/api/stt")
    public String speechToText(@RequestParam MultipartFile file) {
        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .language("es")
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.SRT)
                .build();
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(file.getResource(), options);
        return audiomodel.call(prompt)
                .getResult()
                .getOutput();
    }

    @PostMapping("/api/tts")
    public byte[] tts(@RequestParam String text) {
        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .speed(1.5f)
                .voice(OpenAiAudioApi.SpeechRequest.Voice.NOVA)
                .build();
        SpeechPrompt prompt = new SpeechPrompt(text, options);
        return audioSpeechModel.call(prompt)
                .getResult()
                .getOutput();
    }
}

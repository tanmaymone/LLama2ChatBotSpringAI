package com.tanmay.mone.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OllamaLaama2Service {
	
	@Autowired
	private OllamaChatModel chatModel;
	
	public String generateResult(String prompt) {
		ChatResponse response = chatModel.call(
			    new Prompt(
			        prompt + " in 10 words",
			        OllamaOptions.builder()
			            .withModel(OllamaModel.LLAMA2)
			            .withTemperature(0.4)
			            .build()
			    ));
		System.out.println(response.getResult().getOutput().getContent());
		return response.getResult().getOutput().getContent();
	}
}

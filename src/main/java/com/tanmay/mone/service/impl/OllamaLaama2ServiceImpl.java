package com.tanmay.mone.service.impl;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.PineconeVectorStore;
import org.springframework.ai.vectorstore.PineconeVectorStore.PineconeVectorStoreConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.tanmay.mone.service.OllamaLaama2Service;

@Service
public class OllamaLaama2ServiceImpl implements OllamaLaama2Service {

	private static final Map<String, ChatResponse> localCache = new ConcurrentHashMap<>();

	private OllamaChatModel chatModel;
	private VectorStore vectorStore;

	public OllamaLaama2ServiceImpl(OllamaChatModel chatModel, OllamaEmbeddingModel embeddingModel,
			VectorStore vectorStore) throws IOException {
		this.chatModel = chatModel;
		this.vectorStore = new PineconeVectorStore(PineconeVectorStoreConfig.builder()
				.withApiKey("pcsk_6UHNBN_Ma7piMZu4JEh3mEMuPuirjjBZrS5E2NYEMR234tPKJVsBWtmZEJU9oTjseWwfGk")
				.withIndexName("ElasticSearch").build(), embeddingModel);
	}

	private static final List<String> STOP_WORDS = Arrays.asList("a", "an", "the", "is", "of", "to", "and", "in", "for",
			"on", "with", "at", "by", "this", "that", "it", "which");

	public String normalizeText(String text) {
		// Step 1: Convert to lowercase
		text = text.toLowerCase();

		// Step 2: Remove punctuation
		text = removePunctuation(text);

		// Step 4: Remove stop words
		text = removeStopWords(text);
		String sortedText = Arrays.stream(text.trim().split("\\s+")).sorted().collect(Collectors.joining(""));
		return sortedText.trim();
	}

	// Remove stop words from the text
	private String removeStopWords(String text) {
		// Split the text into words
		String[] words = text.split("\\s+");
		StringBuilder normalizedText = new StringBuilder();

		for (String word : words) {
			if (!STOP_WORDS.contains(word)) {
				normalizedText.append(word).append(" ");
			}
		}
		return normalizedText.toString().trim();
	}

	private String removePunctuation(String text) {
		// This regex removes anything that's not a letter or a number (including
		// spaces)
		return text.replaceAll("[^a-zA-Z0-9\\s]", "");
	}

	@Override
	public String generateResult(String question) throws NoSuchAlgorithmException {

		String cacheKey = generateCacheKey(normalizeText(question));

		if (localCache.containsKey(cacheKey)) {
			return localCache.get(cacheKey).getResult().getOutput().getContent();
		}

		List<Document> documents = vectorStore.similaritySearch(SearchRequest.query(question).withTopK(4));
		List<String> contentList = documents.stream().map(Document::getContent).toList();
		String dynamicPrompt = "Based on the following documents: " + String.join(", ", contentList)
				+ ", answer this question: " + question;
		ChatResponse response = chatModel.call(new Prompt(dynamicPrompt,
				OllamaOptions.builder().withModel(OllamaModel.PHI3).withTemperature(0.1).build()));
		localCache.put(cacheKey, response);
		return response.getResult().getOutput().getContent();
	}

	private String generateCacheKey(String question) throws NoSuchAlgorithmException {
		// Using SHA-256 to hash the question (you can also use MD5 if you want a
		// smaller hash)
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hashBytes = digest.digest(question.getBytes());

		// Convert byte array to a hexadecimal string (standard representation of a
		// hash)
		StringBuilder hexString = new StringBuilder();
		for (byte b : hashBytes) {
			hexString.append(String.format("%02x", b));
		}
		return hexString.toString();
	}

}
package com.tanmay.mone.rag.config;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class VectorStoreConfig {

//	@Bean
//	PineconeVectorStoreConfig pineconeVectorStoreConfig() {
//		return PineconeVectorStoreConfig.builder()
//				.withApiKey("pcsk_6UHNBN_Ma7piMZu4JEh3mEMuPuirjjBZrS5E2NYEMR234tPKJVsBWtmZEJU9oTjseWwfGk")
//				.withIndexName("ElasticSearch").build();
//	}
//
//	@Bean
//	@Primary
//	VectorStore vectorStore(PineconeVectorStoreConfig config, OllamaEmbeddingModel embeddingModel) {
//		return new PineconeVectorStore(config, embeddingModel);
//	}

	@Bean
	CommandLineRunner loadDocuments(OllamaEmbeddingModel embeddingClient, Resource document, VectorStore vectorStore) {
		return args -> {
			System.out.println("Processing document: " + document.getFilename());
			try {
				TikaDocumentReader documentReader = new TikaDocumentReader(document);
				List<Document> docs = documentReader.get();
				TextSplitter textSplitter = new TokenTextSplitter(100, 100, 300, 50, true);
				if (docs.isEmpty()) {
					System.out.println("No content found in: " + document.getFilename());
					return;
				}
				List<Document> splitDocs = textSplitter.split(docs);
				vectorStore.add(splitDocs);
				System.out.println("Successfully upserted vectors for document: " + document.getFilename());
			} catch (Exception e) {
				System.err.println("Error processing document: " + document.getFilename());
				e.printStackTrace();
			}
		};
	}
}
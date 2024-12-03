package com.tanmay.mone.rag.config;

import java.io.File;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

	@Bean
	SimpleVectorStore simpleVectorStore(OllamaEmbeddingModel embeddingClient,
			VectorStoreProperties vectorStoreProperties) {
		return new SimpleVectorStore(embeddingClient);
	}

	@Bean
	CommandLineRunner loadDocuments(SimpleVectorStore simpleVectorStore,
			VectorStoreProperties vectorStoreProperties) {
		return args -> {
			File vectorStoreFile = new File(vectorStoreProperties.getVectorStorePath());
			System.out.println("Loading documents into vector store");
			vectorStoreProperties.getDocumentsToLoad().forEach(document -> {
				System.out.println("Loading document: " + document.getFilename());
				TikaDocumentReader documentReader = new TikaDocumentReader(document);
				List<Document> docs = documentReader.get();
				TextSplitter textSplitter = new TokenTextSplitter(100, 100, 300, 50, true);
				if (docs.isEmpty()) {
					System.out.println("No documents found in: " + document.getFilename());
					return;
				}
				List<Document> splitDocs = textSplitter.split(docs);
				simpleVectorStore.add(splitDocs);
			});
			simpleVectorStore.save(vectorStoreFile);
		};
	}
	
}
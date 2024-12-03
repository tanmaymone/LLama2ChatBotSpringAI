package com.tanmay.mone.service;

import java.security.NoSuchAlgorithmException;

public interface OllamaLaama2Service {

	String generateResult(String prompt) throws NoSuchAlgorithmException;
	
}

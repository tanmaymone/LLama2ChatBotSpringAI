package com.tanmay.mone.controller;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tanmay.mone.service.impl.OllamaLaama2ServiceImpl;

@RestController
public class AIController {
	
	@Autowired
	private OllamaLaama2ServiceImpl laamaService;
	
	@CrossOrigin(origins = "http://localhost:9292")
	@GetMapping("/api/v1/generate")
	public String generate(@RequestParam(value="promptMessage") String promptMessage) throws NoSuchAlgorithmException {
		return laamaService.generateResult(promptMessage);
	}
}
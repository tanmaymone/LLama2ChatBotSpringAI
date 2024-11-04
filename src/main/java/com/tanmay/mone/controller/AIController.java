package com.tanmay.mone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tanmay.mone.service.OllamaLaama2Service;

@RestController
public class AIController {
	
	@Autowired
	private OllamaLaama2Service laamaService;
	
	@CrossOrigin(origins = "http://localhost:8080")
	@GetMapping("/api/v1/generate")
	public String generate(@RequestParam(value="promptMessage") String promptMessage) {
		return laamaService.generateResult(promptMessage);
	}
}

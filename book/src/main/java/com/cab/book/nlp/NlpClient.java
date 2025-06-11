package com.cab.book.nlp;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cab.book.dto.NlpRequest;
import com.cab.book.dto.NlpResponse;


@FeignClient(name ="nlp-service",url="${nlp.service.url}")
public interface NlpClient {
	
	@PostMapping("/parse")
	NlpResponse parseText(@RequestBody NlpRequest nlpRequest);
}
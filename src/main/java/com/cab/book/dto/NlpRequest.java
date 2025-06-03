package com.cab.book.dto;

import lombok.Data;

@Data
public class NlpRequest {
	private String text;
	
	public NlpRequest(String text) {
		this.text=text;
	}
	public String getText() {
		// TODO Auto-generated method stub
		return text;
	}
}
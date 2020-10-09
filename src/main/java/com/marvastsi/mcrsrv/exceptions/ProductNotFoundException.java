package com.marvastsi.mcrsrv.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -6835744583752071379L;

	public ProductNotFoundException() {
		super();
	}

	public ProductNotFoundException(String s) {
		super(s);
	}

	
}

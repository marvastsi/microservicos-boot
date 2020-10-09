package com.marvastsi.mcrsrv.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvastsi.mcrsrv.exceptions.ProductNotFoundException;
import com.marvastsi.mcrsrv.model.entity.ProductDetail;
import com.marvastsi.mcrsrv.model.repository.ProductDetailRepository;
import com.marvastsi.mcrsrv.validator.ProductDetailValidator;

@RestController
@RequestMapping("/products")
public class ProductDetailController {

	private final ProductDetailRepository repository;
	private final ProductDetailValidator validator;
	private final ObjectMapper objectMapper;

	@Autowired
	public ProductDetailController(ProductDetailRepository repository, ProductDetailValidator validator,
			ObjectMapper objectMapper) {
		this.repository = repository;
		this.validator = validator;
		this.objectMapper = objectMapper;
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.addValidators(validator);
	}

	@GetMapping
	public Iterable<ProductDetail> findAll(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
			@RequestParam(value = "count", defaultValue = "10", required = false) int count,
			@RequestParam(value = "order", defaultValue = "ASC", required = false) Sort.Direction direction,
			@RequestParam(value = "sort", defaultValue = "productName", required = false) String sortProperty) {
		Page<ProductDetail> result = repository
				.findAll(PageRequest.of(page, count, direction, new String[] { sortProperty }));
		return result.getContent();
	}

	@GetMapping("/{id}")
    public ProductDetail find(@PathVariable String id) {
        Optional<ProductDetail> detail = repository.findById(id);
        if (detail.isEmpty()) {
			throw new ProductNotFoundException();
        } else {
            return detail.get();
        }
    }

	@PostMapping
	public ProductDetail create(@RequestBody @Valid ProductDetail detail) {
		return repository.save(detail);
	}
	
	@PutMapping("/{id}")
    public HttpEntity<?> update(@PathVariable String id, HttpServletRequest request) throws IOException {
        ProductDetail existing = find(id);
        ProductDetail updated = objectMapper.readerForUpdating(existing).readValue(request.getReader());
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("productId", updated.getProductId());
        propertyValues.add("productName", updated.getProductName());
        propertyValues.add("shortDescription", updated.getShortDescription());
        propertyValues.add("longDescription", updated.getLongDescription());
        propertyValues.add("inventoryId", updated.getInventoryId());
        DataBinder binder = new DataBinder(updated);
        binder.addValidators(validator);
        binder.bind(propertyValues);
        binder.validate();
        if (binder.getBindingResult().hasErrors()) {
            return new ResponseEntity<>(binder.getBindingResult().getAllErrors(), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(updated, HttpStatus.ACCEPTED);
        }
    }

    @DeleteMapping("/{id}")
    public HttpEntity<ProductDetail> delete(@PathVariable String id) {
        ProductDetail detail = find(id);
        repository.delete(detail);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}

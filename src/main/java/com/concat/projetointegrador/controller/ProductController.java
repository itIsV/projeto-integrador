package com.concat.projetointegrador.controller;

import com.concat.projetointegrador.dto.ProductDTO;
import com.concat.projetointegrador.model.Category;
import com.concat.projetointegrador.model.Product;
import com.concat.projetointegrador.service.ProductService;
import java.util.List;
import java.util.stream.Collectors;

import com.concat.projetointegrador.dto.*;
import com.concat.projetointegrador.service.SectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.concat.projetointegrador.model.BatchStock;
import com.concat.projetointegrador.service.BatchStockService;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private BatchStockService batchStockService;

	@Autowired
	private SectorService sectorService;

	@GetMapping("/{id}")
	public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id, @PathParam("orderBy") String orderBy) {// usado

		Product product = productService.findById(id);

		List<BatchStock> batchStock = batchStockService.findAllByProductId(product.getId(), orderBy);

		ProductResponseDTO build = ProductResponseDTO
				.builder()
				.productId(product.getId())
				.batchStock(
						batchStock.stream()
								.map(bs-> {
											BatchStockDTO dto = BatchStockDTO.map(bs);
											dto.setProductId(product.getId());
											return dto;
										}
								)
								.collect(Collectors.toList())
				)
				.sector(sectorService.findByCategory(product.getCategory()).stream().map(SectorRequestDTO::map).collect(Collectors.toList())  )
				.productId(product.getId())
				.build();

		return ResponseEntity.ok(build);
	}

	@GetMapping
	public ResponseEntity<List<ProductDTO>> findAll() {
		return ResponseEntity.ok(productService.findAll());

	}

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDTO>> findByCategory(@PathVariable Category category) {
        return ResponseEntity.ok(productService.findByCategory(category));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(@RequestBody Product product) {
        return new ResponseEntity<>(productService.create(product), HttpStatus.CREATED);
    }

}

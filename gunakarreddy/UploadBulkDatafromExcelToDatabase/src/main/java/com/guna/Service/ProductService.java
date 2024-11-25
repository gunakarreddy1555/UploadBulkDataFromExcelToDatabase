package com.guna.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guna.Entity.Product;
import com.guna.Repository.ProductRepository;


@Service
public class ProductService {

	@Autowired
	private ProductRepository prodrepo;
	public void saveAll(List<Product> products) {
		prodrepo.saveAll(products);
	}
	public List<String> getAllProductTypes() {
		
		return prodrepo.findAllDistinctProductTypes();
		
	}
	
	public List<Product> finbynameandtype(String name,String type){
		
		return prodrepo.findProducts(name,type);
	}

}


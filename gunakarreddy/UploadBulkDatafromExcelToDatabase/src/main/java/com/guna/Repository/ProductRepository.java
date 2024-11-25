package com.guna.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.guna.Entity.Product;



@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	@Query("SELECT DISTINCT p.producttype FROM Product p")
    List<String> findAllDistinctProductTypes();
	
//	@Query("SELECT p FROM Product p WHERE (:productname IS NULL OR p.productname LIKE %:productname%) AND (:producttype IS NULL OR p.producttype = :producttype)")
//    List<Product> findProducts(@Param("productname") String productname, @Param("producttype") String producttype);
	
	@Query("select p from Product p WHERE p.productname=:productname and p.producttype = :producttype")
	 List<Product> findProducts(@Param("productname") String productname, @Param("producttype") String producttype);

}

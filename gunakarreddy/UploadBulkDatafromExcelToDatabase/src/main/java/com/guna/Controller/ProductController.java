package com.guna.Controller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.guna.Entity.Product;
import com.guna.Service.ProductService;



@Controller
public class ProductController {

   @Autowired
   private ProductService productService;
	@GetMapping("/getUploadProductPage")
    public String getUploadPage() {
        return "uploadFile";
    }

	@GetMapping("/getSerchpage")
    public String getSerchpage(Model model) {
		
		List<String> all = productService.getAllProductTypes();
		System.out.println("----------------------------------------------");
		System.out.println(all);
    model.addAttribute("alltypes",all);
    	return "Searchpage";
    }
    @PostMapping("/loadFile")
    @ResponseBody
    public List<Product> loadFile(@RequestParam("file") MultipartFile file) {
        List<Product> products = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                Product product = new Product();
                product.setProductid((long) row.getCell(0).getNumericCellValue());
                product.setProductname(row.getCell(1).getStringCellValue());
                product.setProducttype(row.getCell(2).getStringCellValue());
                product.setProductprice(String.valueOf(row.getCell(3).getNumericCellValue()));
                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products; // Return the list of products as JSON
    }

    @PostMapping("/upload")
    @ResponseBody
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<Product> products = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                Product product = new Product();
                product.setProductid((long) row.getCell(0).getNumericCellValue());
                product.setProductname(row.getCell(1).getStringCellValue());
                product.setProducttype(row.getCell(2).getStringCellValue());
                product.setProductprice(String.valueOf(row.getCell(3).getNumericCellValue()));
                products.add(product);
            }

            productService.saveAll(products); // Save all products
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to upload the file.";
        }
        return "File uploaded successfully!";
    }
    @GetMapping("/searchResults")
    @ResponseBody
    public List<Product> searchResults(@RequestParam("productname") String productname,
                                        @RequestParam("producttype") String producttype,
                                        Model model) {
        List<Product> products = productService.finbynameandtype(productname, producttype);

        if (products.isEmpty()) {
            model.addAttribute("message", "No data found for the given name and type.");
        }

        return products; // Return the list of products as JSON
    }
    @PostMapping("/exportData")
    public ResponseEntity<String> exportData(@RequestBody List<Product> products) throws FileNotFoundException, IOException {
    	System.out.println("------------------------------------------------------------------------");
    	System.out.println(products);
    	 Workbook workbook = new XSSFWorkbook();
         Sheet sheet = workbook.createSheet("Customers");

         // Create a header row
         Row headerRow = sheet.createRow(0);
         headerRow.createCell(0).setCellValue("ID");
         headerRow.createCell(1).setCellValue("productid");
         headerRow.createCell(2).setCellValue("productname");
         headerRow.createCell(3).setCellValue("producttype");
         headerRow.createCell(4).setCellValue("productprice");  

         // Populate the sheet with customer data
         int rowNum = 1;
         for (Product pod : products) {
             Row row = sheet.createRow(rowNum++);
             row.createCell(0).setCellValue(pod.getId());
             row.createCell(1).setCellValue(pod.getProductid());
             row.createCell(2).setCellValue(pod.getProductname());
             row.createCell(3).setCellValue(pod.getProducttype());
             row.createCell(4).setCellValue(pod.getProductprice());
            
         }

         // Specify the file path where you want to save the file
         String filePath = "C:\\Users\\gunakar reddy\\Desktop\\js/products.xlsx"; // Adjust the path as needed

         // Write the workbook to the specified file path
         try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
             workbook.write(fileOut);
         }
         workbook.close();
//         model.addAttribute("excelmsg", " successfully  exported customers");
        return ResponseEntity.ok("Export Successful");
    }
}

package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.Product;
import com.restaurant.restaurant_admin.mapper.ProductMapper;
import com.restaurant.restaurant_admin.model.*;
import com.restaurant.restaurant_admin.repository.ProductRepo;
import com.restaurant.restaurant_admin.repository.specification.ProductSpecification;
import com.restaurant.restaurant_admin.repository.specification.SearchCriteria;
import com.restaurant.restaurant_admin.utils.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductService {

    private final ProductRepo productRepo;
    private final MainCategoryService categoryService;
    private final SubcategoryService subcategoryService;
    private final UploadFileUtil fileUtil;

    public Page<ProductShortResponse> getProductPage(int page, int pageSize) {
        log.info("method getProductPage -> start");
        Pageable pageable = PageRequest.of(page, pageSize);
        log.info("method getProductPage -> get products by pageable - page: " + page + " pageSize: " + pageSize);
        Page<Product> productPage = productRepo.findAll(pageable);
        List<ProductShortResponse> shortResponses =
                ProductMapper.MAPPER.productListToShortResponseList(productPage.getContent());
        Page<ProductShortResponse> shortResponsePage =
                new PageImpl<>(shortResponses, pageable, productPage.getTotalElements());
        log.info("method getProductPage -> exit, return products by pageable");
        return shortResponsePage;
    }

    public Page<ProductShortResponse> getProductsBySearch(int page, int pageSize, String search) {
        log.info("method getProductsBySearch -> start");
        Pageable pageable = PageRequest.of(page, pageSize);
        ProductSpecification likeName = new ProductSpecification(
                new SearchCriteria("name", ":", search));
        log.info("method getProductsBySearch -> get products by pageable - page: " + page + " pageSize: " + pageSize);
        Page<Product> productPage = productRepo.findAll(likeName, pageable);
        List<ProductShortResponse> responseList =
                ProductMapper.MAPPER.productListToShortResponseList(productPage.getContent());
        Page<ProductShortResponse> responsePage =
                new PageImpl<>(responseList, pageable, productPage.getTotalElements());
        log.info("method getProductsBySearch -> exit, return products by pageable");
        return responsePage;
    }

    public ProductResponse getProductById(Long productId) {
        log.info("method getProductById -> start");
        Optional<Product> byId = productRepo.findById(productId);
        Product product = byId.orElseThrow(EntityNotFoundException::new);
        log.info("method getProductPage -> product isPresent, map to ProductResponse");
        ProductResponse productResponse = ProductMapper.MAPPER.productToProductResponse(product);
        log.info("method getProductPage -> exit, return ProductResponse");
        return productResponse;
    }

    public Page<MainCategoryDTO> getCategories(int page, int pageSize, String search) {
        log.info("method getCategories -> start");
        return categoryService.getMainCategoriesBySearch(search, page, pageSize);
    }

    public Page<SubcategoryDTO> getSubcategories(int page, int pageSize, String search) {
        log.info("method getSubcategories -> start");
        return subcategoryService.getSubcategories(page, pageSize, search);
    }

    public Page<ProductShortResponse> getIngredients(int page, int pageSize, String search) {
        log.info("method getSubcategories -> start");
        Pageable pageable = PageRequest.of(page, pageSize);
        ProductSpecification isIngredient = new ProductSpecification(
                new SearchCriteria("isIngredient", "", true)
        );
        ProductSpecification likeName = new ProductSpecification(
                new SearchCriteria("name", ":", search)
        );
        log.info("method getIngredients -> get products by pageable - page: " + page + " pageSize: " + pageSize);
        Page<Product> productPage = productRepo.findAll(isIngredient.and(likeName), pageable);
        List<ProductShortResponse> responseList = ProductMapper.MAPPER.productListToShortResponseList(productPage.getContent());
        Page<ProductShortResponse> responsePage = new PageImpl<>(responseList, pageable, productPage.getTotalElements());
        log.info("method getIngredients -> exit, return products by pageable");
        return responsePage;
    }

    public void updateProduct(ProductRequest productRequest) {
        log.info("method updateProduct -> start");
        if (productRequest != null) {
            Product product = ProductMapper.MAPPER.productRequestToProduct(productRequest);
            if (product.getPromotion().getId() == null) {
                product.setPromotion(null);
            }
            if (productRequest.getPhotoFile() != null && !productRequest.getPhotoFile().isEmpty()) {
                String filename = savePhotoFile(productRequest.getPhotoFile());
                product.setPhoto(filename);
            }
            log.info("method updateProduct -> save Product");
            productRepo.save(product);
        }
        log.info("method updateProduct -> exit");
    }

    private String savePhotoFile(MultipartFile photoFile) {
        String filename;
        try {
            log.info("method savePhotoFile -> start save image");
            filename = fileUtil.saveImage(photoFile);
            return filename;
        } catch (IOException e) {
            log.error("method savePhotoFile -> error saving image :" + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public void updateIngredient(IngredientRequest ingredientRequest) {
        log.info("method updateIngredient -> start");
        if (ingredientRequest != null) {
            Product product = ProductMapper.MAPPER.ingredientReguestToProduct(ingredientRequest);
            log.info("method updateIngredient -> save Product");

            if (ingredientRequest.getPhotoFile() != null && !ingredientRequest.getPhotoFile().isEmpty()) {
                String filename = savePhotoFile(ingredientRequest.getPhotoFile());
                product.setPhoto(filename);
            }
            productRepo.save(product);
        }
        log.info("method updateIngredient -> exit");
    }

    public void createProduct(ProductRequest productRequest) {
        log.info("method createProduct -> start");
        if (productRequest != null) {
            Product product = ProductMapper.MAPPER.productRequestToProduct(productRequest);
            product.setIsNovelty(Boolean.TRUE);
            if (product.getPromotion().getId() == null) {
                product.setPromotion(null);
            }
            if (productRequest.getPhotoFile() != null && !productRequest.getPhotoFile().isEmpty()) {
                String filename = savePhotoFile(productRequest.getPhotoFile());
                product.setPhoto(filename);
            }
            log.info("method createProduct -> save Product");
            productRepo.save(product);
        }
        log.info("method createProduct -> exit");
    }

    public void createProduct(IngredientRequest ingredientRequest) {
        log.info("method createProduct -> start");
        if (ingredientRequest != null) {
            Product product = ProductMapper.MAPPER.ingredientReguestToProduct(ingredientRequest);
            log.info("method createProduct -> save Product");
            if (ingredientRequest.getPhotoFile() != null && !ingredientRequest.getPhotoFile().isEmpty()) {
                String filename = savePhotoFile(ingredientRequest.getPhotoFile());
                product.setPhoto(filename);
            }
            productRepo.save(product);
        }
        log.info("method createProduct -> exit");
    }

    public boolean deleteProductById(Long productId) {
        log.info("method deleteProductById -> start");
        if (productId != null) {
            log.info("method deleteProductById -> product id not null");
            if (!productRepo.existsById(productId)) {
                log.info(String.format("method deleteProductById -> product with id: %s not exists in DB", productId));
                throw new EntityNotFoundException();
            }
            Product product = productRepo.findById(productId).orElseThrow();

            if (!product.getPhoto().isEmpty()) {
                fileUtil.deleteUploadFile(product.getPhoto());
            }
            productRepo.delete(product);
            log.info(String.format("method deleteProductById -> product with id: %s deleted", productId));
            return true;
        }
        log.info("method deleteProductById -> exit, return false");
        return false;
    }

    public Page<ProductShortResponse> getProductsWithoutIngredients(int page, int pageSize, String search) {
        log.info("method getProductsWithoutIngredients -> start");
        Pageable pageable = PageRequest.of(page, pageSize);
        ProductSpecification notIngredient = new ProductSpecification(
                new SearchCriteria("isIngredient", "", false)
        );
        ProductSpecification likeName = new ProductSpecification(
                new SearchCriteria("name", ":", search)
        );
        log.info("method getProductsWithoutIngredients -> get products by pageable - page: " + page + " pageSize: " + pageSize);
        Page<Product> productPage = productRepo.findAll(notIngredient.and(likeName), pageable);
        List<ProductShortResponse> responseList =
                ProductMapper.MAPPER.productListToShortResponseList(productPage.getContent());
        Page<ProductShortResponse> responsePage =
                new PageImpl<>(responseList, pageable, productPage.getTotalElements());
        log.info("method getProductsWithoutIngredients -> exit, return products by pageable");
        return responsePage;
    }
}

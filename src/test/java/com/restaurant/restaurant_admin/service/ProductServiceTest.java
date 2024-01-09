package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.Product;
import com.restaurant.restaurant_admin.model.category.CategoryCriteria;
import com.restaurant.restaurant_admin.model.category.MainCategoryDTO;
import com.restaurant.restaurant_admin.model.category.SubcategoryDTO;
import com.restaurant.restaurant_admin.model.product.IngredientRequest;
import com.restaurant.restaurant_admin.model.product.ProductRequest;
import com.restaurant.restaurant_admin.model.product.ProductResponse;
import com.restaurant.restaurant_admin.model.product.ProductShortResponse;
import com.restaurant.restaurant_admin.repository.ProductRepo;
import com.restaurant.restaurant_admin.repository.specification.ProductSpecification;
import com.restaurant.restaurant_admin.utils.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepo productRepo;
    @Mock
    private MainCategoryService mainCategoryService;
    @Mock
    private SubcategoryService subcategoryService;
    @Mock
    private UploadFileUtil fileUtil;
    @InjectMocks
    private ProductService productService;

    private List<Product> products;
    private Product product;

    @BeforeEach
    void setUp() {
        products = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Product tempProduct = new Product();
            tempProduct.setId((long) i);
            tempProduct.setName("Product_" + i);
            tempProduct.setPrice(BigDecimal.valueOf(150));
            products.add(tempProduct);
        }

        product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setPrice(BigDecimal.valueOf(150));
        product.setIsActive(Boolean.TRUE);
    }

    @Test
    void getProductPage() {
        Pageable pageable = PageRequest.ofSize(10);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        when(productRepo.findAll(any(Pageable.class))).thenReturn(productPage);
        Page<ProductShortResponse> productPage1 =
                productService.getProductPage(pageable.getPageNumber(), pageable.getPageSize());
        List<ProductShortResponse> content = productPage1.getContent();
        assertFalse(content.isEmpty());
        for (int i = 0; i < products.size(); i++) {
            Product product1 = products.get(i);
            ProductShortResponse productShortResponse = content.get(i);
            assertEquals(product1.getId(), productShortResponse.getProductId());
            assertEquals(product1.getName(), productShortResponse.getName());
            assertEquals(product1.getPrice(), productShortResponse.getPrice());

        }
    }

    @Test
    void getProductsBySearch() {

        Pageable pageable = PageRequest.ofSize(10);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        when(productRepo.findAll(any(ProductSpecification.class), any(Pageable.class))).thenReturn(productPage);
        Page<ProductShortResponse> productPage1 =
                productService.getProductsBySearch(pageable.getPageNumber(), pageable.getPageSize(), "", null);
        List<ProductShortResponse> content = productPage1.getContent();
        assertFalse(content.isEmpty());
        for (int i = 0; i < products.size(); i++) {
            Product product1 = products.get(i);
            ProductShortResponse productShortResponse = content.get(i);
            assertEquals(product1.getId(), productShortResponse.getProductId());
            assertEquals(product1.getName(), productShortResponse.getName());
            assertEquals(product1.getPrice(), productShortResponse.getPrice());
        }
    }

    @Test
    void getProductById() {
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        ProductResponse productById = productService.getProductById(product.getId());
        assertEquals(product.getId(), productById.getProductId());
        assertEquals(product.getPrice(), productById.getPrice());
        assertEquals(product.getName(), productById.getName());
    }

    @Test
    void getCategories() {
        Pageable pageable = PageRequest.ofSize(10);
        List<MainCategoryDTO> mainCategories = List.of(new MainCategoryDTO(), new MainCategoryDTO(), new MainCategoryDTO());
        Page<MainCategoryDTO> categoryPage = new PageImpl<>(mainCategories, pageable, mainCategories.size());
        when(mainCategoryService.getMainCategoriesBySearch(
                any(CategoryCriteria.class), any(int.class), any(int.class))).thenReturn(categoryPage);
        Page<MainCategoryDTO> mainCategoriesByPage =
                productService.getCategories(pageable.getPageNumber(), pageable.getPageSize(), "");
        List<MainCategoryDTO> content = mainCategoriesByPage.getContent();
        assertNotNull(content);
        assertFalse(content.isEmpty());
        assertEquals(3, content.size());
    }

    @Test
    void getSubcategories() {
        Pageable pageable = PageRequest.ofSize(10);
        List<SubcategoryDTO> subcategories = List.of(new SubcategoryDTO(), new SubcategoryDTO(), new SubcategoryDTO());
        Page<SubcategoryDTO> subcategoryPage = new PageImpl<>(subcategories, pageable, subcategories.size());
        when(subcategoryService.getSubcategories(pageable.getPageNumber(), pageable.getPageSize(), ""))
                .thenReturn(subcategoryPage);

        Page<SubcategoryDTO> subcategories1 =
                productService.getSubcategories(pageable.getPageNumber(), pageable.getPageSize(), "");
        List<SubcategoryDTO> content = subcategories1.getContent();
        assertFalse(content.isEmpty());
        assertEquals(3, content.size());
    }

    @Test
    void getIngredients() {
        Pageable pageable = PageRequest.ofSize(10);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        when(productRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);
        Page<ProductShortResponse> ingredients =
                productService.getIngredients(pageable.getPageNumber(), pageable.getPageSize(), "");
        List<ProductShortResponse> content = ingredients.getContent();
        assertFalse(content.isEmpty());

        for (int i = 0; i < products.size(); i++) {
            Product product1 = products.get(i);
            ProductShortResponse productShortResponse = content.get(i);
            assertEquals(product1.getId(), productShortResponse.getProductId());
            assertEquals(product1.getName(), productShortResponse.getName());
            assertEquals(product1.getPrice(), productShortResponse.getPrice());
        }
    }

    @Test
    void updateProduct() throws IOException {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductId(product.getId());
        productRequest.setName(product.getName());
        productRequest.setPrice(product.getPrice());
        productRequest.setIsActive(product.getIsActive());
        productRequest.setIsIngredient(Boolean.FALSE);
        productRequest.setPhotoFile(new MockMultipartFile("file", "fileContent".getBytes()));
        when(fileUtil.saveImage(any(MultipartFile.class))).thenReturn("newFilename");
        productService.updateProduct(productRequest);
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo).save(captor.capture());
        Product value = captor.getValue();
        assertEquals("newFilename", value.getPhoto());
        assertEquals(product.getName(), value.getName());
    }

    @Test
    void updateIngredient() throws IOException {
        IngredientRequest productRequest = new IngredientRequest();
        productRequest.setProductId(product.getId());
        productRequest.setName(product.getName());
        productRequest.setPrice(product.getPrice());
        productRequest.setIsActive(product.getIsActive());
        productRequest.setIsIngredient(Boolean.FALSE);
        productRequest.setPhotoFile(new MockMultipartFile("file", "fileContent".getBytes()));
        when(fileUtil.saveImage(any(MultipartFile.class))).thenReturn("newFilename");
        productService.updateIngredient(productRequest);
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo).save(captor.capture());
        Product value = captor.getValue();
        assertEquals("newFilename", value.getPhoto());
        assertEquals(product.getName(), value.getName());
    }

    @Test
    void createProduct() throws IOException {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductId(product.getId());
        productRequest.setName(product.getName());
        productRequest.setPrice(product.getPrice());
        productRequest.setIsActive(product.getIsActive());
        productRequest.setIsIngredient(Boolean.FALSE);
        productRequest.setPhotoFile(new MockMultipartFile("file", "fileContent".getBytes()));
        when(fileUtil.saveImage(any(MultipartFile.class))).thenReturn("newFilename");
        productService.createProduct(productRequest);
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo).save(captor.capture());
        Product value = captor.getValue();
        assertEquals("newFilename", value.getPhoto());
        assertEquals(product.getName(), value.getName());
        assertTrue(value.getIsNovelty());
    }

    @Test
    void testCreateProduct() throws IOException {
        IngredientRequest productRequest = new IngredientRequest();
        productRequest.setProductId(product.getId());
        productRequest.setName(product.getName());
        productRequest.setPrice(product.getPrice());
        productRequest.setIsActive(product.getIsActive());
        productRequest.setIsIngredient(Boolean.FALSE);
        productRequest.setPhotoFile(new MockMultipartFile("file", "fileContent".getBytes()));
        when(fileUtil.saveImage(any(MultipartFile.class))).thenReturn("newFilename");
        productService.createProduct(productRequest);
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo).save(captor.capture());
        Product value = captor.getValue();
        assertEquals("newFilename", value.getPhoto());
        assertEquals(product.getName(), value.getName());
    }

    @Test
    void testCreateProduct_ifFileUtilCatchException() throws IOException {
        IngredientRequest productRequest = new IngredientRequest();
        productRequest.setProductId(product.getId());
        productRequest.setName(product.getName());
        productRequest.setPrice(product.getPrice());
        productRequest.setIsActive(product.getIsActive());
        productRequest.setIsIngredient(Boolean.FALSE);
        productRequest.setPhotoFile(new MockMultipartFile("file", "fileContent".getBytes()));
        when(fileUtil.saveImage(any(MultipartFile.class))).thenThrow(IOException.class);
        assertThrows(RuntimeException.class, () -> productService.createProduct(productRequest));
    }

    @Test
    void deleteProductById_ifIdIsNull() {
        boolean isDeleted = productService.deleteProductById(null);
        assertFalse(isDeleted);
    }

    @Test
    void deleteProductById_ifProductNotFound() {
        when(productRepo.existsById(product.getId())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () ->
                productService.deleteProductById(product.getId()));
    }

    @Test
    void deleteProductById() {
        product.setPhoto("filename");
        when(productRepo.existsById(product.getId())).thenReturn(true);
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));

        boolean isDeleted = productService.deleteProductById(product.getId());
        verify(fileUtil).deleteUploadFile(product.getPhoto());
        verify(productRepo).delete(any(Product.class));
        assertTrue(isDeleted);
    }

    @Test
    void getProductsWithoutIngredients() {
        Pageable pageable = PageRequest.ofSize(10);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        when(productRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);
        Page<ProductShortResponse> ingredients =
                productService.getProductsWithoutIngredients(pageable.getPageNumber(), pageable.getPageSize(), "");
        List<ProductShortResponse> content = ingredients.getContent();
        assertFalse(content.isEmpty());

        for (int i = 0; i < products.size(); i++) {
            Product product1 = products.get(i);
            ProductShortResponse productShortResponse = content.get(i);
            assertEquals(product1.getId(), productShortResponse.getProductId());
            assertEquals(product1.getName(), productShortResponse.getName());
            assertEquals(product1.getPrice(), productShortResponse.getPrice());
        }
    }
}
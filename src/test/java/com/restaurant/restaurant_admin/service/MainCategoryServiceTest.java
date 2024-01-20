package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.MainCategory;
import com.restaurant.restaurant_admin.entity.Product;
import com.restaurant.restaurant_admin.entity.Subcategory;
import com.restaurant.restaurant_admin.mapper.MainCategoryMapper;
import com.restaurant.restaurant_admin.model.category.CategoryCriteria;
import com.restaurant.restaurant_admin.model.category.MainCategoryDTO;
import com.restaurant.restaurant_admin.model.category.MainCategoryShortResponse;
import com.restaurant.restaurant_admin.repository.MainCategoryRepo;
import com.restaurant.restaurant_admin.repository.ProductRepo;
import com.restaurant.restaurant_admin.repository.specification.MainCategorySpecification;
import com.restaurant.restaurant_admin.utils.UploadFileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MainCategoryServiceTest {
    @Mock
    private MainCategoryRepo categoryRepo;
    @Mock
    private ProductRepo productRepo;
    @Mock
    private UploadFileUtil fileUtil;
    @InjectMocks
    private MainCategoryService categoryService;
    private MainCategory mainCategory;
    private List<MainCategory> mainCategories;

    @BeforeEach
    void setUp() {
        mainCategory = new MainCategory();
        mainCategory.setId(1L);
        mainCategory.setCategoryName("Category");
        mainCategory.setDateOfCreate(new Date());
        mainCategory.setIsActive(Boolean.TRUE);
        mainCategories = new ArrayList<>();
        mainCategory.setSubcategories(new ArrayList<>());

        for (int i = 0; i < 10; i++) {
            MainCategory category = new MainCategory();
            category.setId((long) (i + 2));
            category.setCategoryName("category_" + i);
            category.setIsActive(Boolean.TRUE);
            category.setDateOfCreate(new Date());
            mainCategories.add(category);
        }
    }

    @Test
    void getMainCategoryById_ifCategoryIsPresent() {
        when(categoryRepo.findById(mainCategory.getId())).thenReturn(Optional.of(mainCategory));

        MainCategoryDTO mainCategoryById = categoryService.getMainCategoryById(mainCategory.getId());
        assertNotNull(mainCategoryById);
        assertEquals(mainCategory.getId(), mainCategoryById.getId());
        assertEquals(mainCategory.getCategoryName(), mainCategoryById.getCategoryName());
        assertEquals(mainCategory.getDateOfCreate(), mainCategoryById.getDateOfCreate());
        assertEquals(mainCategory.getIsActive(), mainCategoryById.getIsActive());
    }

    @Test
    void getMainCategoryById_ifCategoryIsEmpty() {
        when(categoryRepo.findById(mainCategory.getId())).thenReturn(Optional.empty());

        MainCategoryDTO mainCategoryById = categoryService.getMainCategoryById(mainCategory.getId());
        assertNotNull(mainCategoryById);
        assertNull(mainCategoryById.getId());
    }

    @Test
    void getAllMainCategories() {
        when(categoryRepo.findAll()).thenReturn(mainCategories);

        List<MainCategoryShortResponse> allMainCategories = categoryService.getAllMainCategories();
        assertFalse(allMainCategories.isEmpty());
        assertEquals(10, allMainCategories.size());
    }

    @Test
    void getAllMainCategories_ifListIsEmpty() {
        when(categoryRepo.findAll()).thenReturn(new ArrayList<>());
        List<MainCategoryShortResponse> allMainCategories = categoryService.getAllMainCategories();
        assertTrue(allMainCategories.isEmpty());
    }

    @Test
    void deleteCategoryById_ifCategoryIsPresent() {
        Product product = new Product();
        product.setMainCategory(mainCategory);
        List<Product> products = new ArrayList<>();
        products.add(product);
        products.add(product);
        when(categoryRepo.findById(mainCategory.getId())).thenReturn(Optional.of(mainCategory));
        when(productRepo.findAllByForMainCategoryIn(Set.of(mainCategory))).thenReturn(products);
        Boolean isDeleted = categoryService.deleteCategoryById(mainCategory.getId());
        assertFalse(isDeleted);
    }

    @Test
    void deleteCategoryById_ifCategoryIsPresentAndNotChildProducts() {
        Product product = new Product();
        product.setMainCategory(mainCategory);
        when(categoryRepo.findById(mainCategory.getId())).thenReturn(Optional.of(mainCategory));
        when(productRepo.findAllByForMainCategoryIn(Set.of(mainCategory))).thenReturn(new ArrayList<>());
        Boolean isDeleted = categoryService.deleteCategoryById(mainCategory.getId());
        verify(categoryRepo).delete(mainCategory);
        assertTrue(isDeleted);
    }

    @Test
    void deleteCategoryById_ifCategoryIsEmpty() {
        when(categoryRepo.findById(mainCategory.getId())).thenReturn(Optional.empty());
        Boolean isDeleted = categoryService.deleteCategoryById(mainCategory.getId());
        assertFalse(isDeleted);
    }

    @Test
    void createMainCategory_ifCategoryWithoutFile() {
        MainCategoryDTO mainCategoryDTO = MainCategoryMapper.MAPPER.mainCategoryToMainCategoryDto(mainCategory);
        MultipartFile file = new MockMultipartFile("image", new byte[]{});
        categoryService.createMainCategory(mainCategoryDTO, file);
        verify(categoryRepo).save(any(MainCategory.class));
    }

    @Test
    void createMainCategory_ifCategoryWithFile() throws IOException {
        when(fileUtil.saveImage(any(MultipartFile.class))).thenReturn("new filename");
        MainCategoryDTO mainCategoryDTO = MainCategoryMapper.MAPPER.mainCategoryToMainCategoryDto(mainCategory);
        MultipartFile file = new MockMultipartFile("image", "This is content on new file for test".getBytes());
        categoryService.createMainCategory(mainCategoryDTO, file);
        verify(fileUtil).saveImage(any(MultipartFile.class));
        verify(categoryRepo).save(any(MainCategory.class));
    }

    @Test
    void createMainCategory_ifCategoryWithFileAndIOException() throws IOException {
        when(fileUtil.saveImage(any(MultipartFile.class))).thenThrow(IOException.class);
        MainCategoryDTO mainCategoryDTO = MainCategoryMapper.MAPPER.mainCategoryToMainCategoryDto(mainCategory);
        MultipartFile file = new MockMultipartFile(
                "image", "image","image/png", "This is content on new file for test".getBytes());
        assertThrows(RuntimeException.class, () -> categoryService.createMainCategory(mainCategoryDTO, file));
    }

    @Test
    void updateMainCategory_ifSubcategoriesIsNull() throws IOException {
        MainCategoryDTO categoryDTO = MainCategoryMapper.MAPPER.mainCategoryToMainCategoryDto(mainCategory);
        when(fileUtil.saveImage(any(MultipartFile.class))).thenReturn("saveName");
        categoryService.updateMainCategory(categoryDTO, new MockMultipartFile("File", "content".getBytes()));
        verify(categoryRepo).save(any(MainCategory.class));
    }

    @Test
    void updateMainCategory_ifSubcategoriesIsPresent() throws IOException {
        List<Subcategory> subcategories = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Subcategory subcategory = new Subcategory();
            subcategory.setId((long) i);
            subcategory.setSubcategoryName("subcategory_" + i);
            subcategories.add(subcategory);
        }
        mainCategory.setSubcategories(subcategories);

        MainCategoryDTO categoryDTO = MainCategoryMapper.MAPPER.mainCategoryToMainCategoryDto(mainCategory);
        when(fileUtil.saveImage(any(MultipartFile.class))).thenReturn("saveName");
        categoryService.updateMainCategory(categoryDTO, new MockMultipartFile("File", "content".getBytes()));
        verify(categoryRepo).save(any(MainCategory.class));
    }

    @Test
    void getMainCategoriesByPage() {
        Pageable pageable = PageRequest.ofSize(10);
        Page<MainCategory> categoryPage = new PageImpl<>(mainCategories, pageable, mainCategories.size());
        when(categoryRepo.findAll(pageable)).thenReturn(categoryPage);
        Page<MainCategoryDTO> mainCategoriesByPage =
                categoryService.getMainCategoriesByPage(pageable.getPageNumber(), pageable.getPageSize());
        List<MainCategoryDTO> content = mainCategoriesByPage.getContent();

        assertNotNull(content);
        assertFalse(content.isEmpty());

        for (int i = 0; i < mainCategories.size(); i++) {
            MainCategory category = mainCategories.get(i);
            MainCategoryDTO mainCategoryDTO = content.get(i);
            assertEquals(category.getId(), mainCategoryDTO.getId());
            assertEquals(category.getCategoryName(), mainCategoryDTO.getCategoryName());
        }
    }

    @Test
    void getMainCategoriesBySearch() {
        Pageable pageable = PageRequest.ofSize(10);
        Page<MainCategory> categoryPage = new PageImpl<>(mainCategories, pageable, mainCategories.size());
        String search = "";
        when(categoryRepo.findAll(any(MainCategorySpecification.class), any(Pageable.class))).thenReturn(categoryPage);
        Page<MainCategoryDTO> mainCategoriesByPage =
                categoryService.getMainCategoriesBySearch(
                        CategoryCriteria.builder().search(search).build(), pageable.getPageNumber(), pageable.getPageSize());
        List<MainCategoryDTO> content = mainCategoriesByPage.getContent();

        assertNotNull(content);
        assertFalse(content.isEmpty());

        for (int i = 0; i < mainCategories.size(); i++) {
            MainCategory category = mainCategories.get(i);
            MainCategoryDTO mainCategoryDTO = content.get(i);
            assertEquals(category.getId(), mainCategoryDTO.getId());
            assertEquals(category.getCategoryName(), mainCategoryDTO.getCategoryName());
        }
    }
}
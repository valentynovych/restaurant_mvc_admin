package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.Promotion;
import com.restaurant.restaurant_admin.entity.enums.PromotionCondition;
import com.restaurant.restaurant_admin.entity.enums.PromotionType;
import com.restaurant.restaurant_admin.model.category.CategoryCriteria;
import com.restaurant.restaurant_admin.model.category.MainCategoryDTO;
import com.restaurant.restaurant_admin.model.category.SubcategoryDTO;
import com.restaurant.restaurant_admin.model.product.ProductShortResponse;
import com.restaurant.restaurant_admin.model.promotion.PromotionRequest;
import com.restaurant.restaurant_admin.model.promotion.PromotionResponse;
import com.restaurant.restaurant_admin.repository.PromotionRepo;
import com.restaurant.restaurant_admin.repository.specification.PromotionSpecification;
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
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    private PromotionRepo promotionRepo;
    @Mock
    private MainCategoryService mainCategoryService;
    @Mock
    private SubcategoryService subcategoryService;
    @Mock
    private ProductService productService;
    @Mock
    private UploadFileUtil fileUtil;
    @InjectMocks
    private PromotionService promotionService;

    private Promotion promotion;
    private List<Promotion> promotions;


    @BeforeEach
    void setUp() {
        promotion = new Promotion();
        promotions = new ArrayList<>();

        promotion.setId(1L);
        promotion.setDateOfCreate(Instant.now());
        promotion.setIsActive(Boolean.TRUE);
        promotion.setName("Promotion");
        promotion.setPromotionType(PromotionType.FOR_PRODUCT);

        for (int i = 0; i < 10; i++) {
            Promotion promotion1 = new Promotion();
            promotion1.setId((long) i);
            promotion1.setDateOfCreate(Instant.now());
            promotion1.setIsActive(Boolean.TRUE);
            promotion1.setName("Promotion_" + i);
            promotion1.setPromotionType(PromotionType.FOR_PRODUCT);
            promotions.add(promotion1);

        }
    }

    @Test
    void getAllPromotions() {
        Pageable pageable = PageRequest.ofSize(10);
        Page<Promotion> promotionPage = new PageImpl<>(promotions, pageable, promotions.size());
        when(promotionRepo.findAll(any(Pageable.class))).thenReturn(promotionPage);
        Page<PromotionResponse> allPromotions =
                promotionService.getAllPromotions(pageable.getPageNumber(), pageable.getPageSize());
        List<PromotionResponse> content = allPromotions.getContent();
        assertFalse(content.isEmpty());

        for (int i = 0; i < promotions.size(); i++) {
            Promotion promotion1 = promotions.get(i);
            PromotionResponse promotionResponse = content.get(i);
            assertEquals(promotion1.getId(), promotionResponse.getId());
            assertEquals(promotion1.getName(), promotionResponse.getName());
            assertEquals(promotion1.getPromotionType(), promotionResponse.getPromotionType());
        }
    }

    @Test
    void getAllPromotionsBySearch() {
        Pageable pageable = PageRequest.ofSize(10);
        Page<Promotion> promotionPage = new PageImpl<>(promotions, pageable, promotions.size());
        when(promotionRepo.findAll(any(PromotionSpecification.class), any(Pageable.class))).thenReturn(promotionPage);
        Page<PromotionResponse> allPromotions =
                promotionService.getAllPromotionsBySearch(pageable.getPageNumber(), pageable.getPageSize(), "");
        List<PromotionResponse> content = allPromotions.getContent();
        assertFalse(content.isEmpty());

        for (int i = 0; i < promotions.size(); i++) {
            Promotion promotion1 = promotions.get(i);
            PromotionResponse promotionResponse = content.get(i);
            assertEquals(promotion1.getId(), promotionResponse.getId());
            assertEquals(promotion1.getName(), promotionResponse.getName());
            assertEquals(promotion1.getPromotionType(), promotionResponse.getPromotionType());
        }
    }

    @Test
    void getPromotion_ifPromotionIsPresent() {
        when(promotionRepo.findById(promotion.getId())).thenReturn(Optional.of(promotion));
        PromotionResponse promotion1 = promotionService.getPromotion(promotion.getId());
        assertNotNull(promotion1);
        assertEquals(promotion.getId(), promotion1.getId());
        assertEquals(promotion.getName(), promotion1.getName());
        assertEquals(promotion.getPromotionType(), promotion1.getPromotionType());
    }

    @Test
    void getPromotion_ifPromotionNotFound() {
        when(promotionRepo.findById(promotion.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                promotionService.getPromotion(promotion.getId()));
    }

    @Test
    void getPromotion_ifParameterIsNull() {
        PromotionResponse promotion1 = promotionService.getPromotion(null);
        assertNull(promotion1);
    }

    @Test
    void getPromotionTypes() {
        Map<String, String> promotionValuesLabels = new HashMap<>();
        Arrays.stream(PromotionType.values()).forEach(
                promotionType -> promotionValuesLabels.put(promotionType.name(), promotionType.label));
        Map<String, String> promotionTypes = promotionService.getPromotionTypes();
        assertEquals(promotionValuesLabels.size(), promotionTypes.size());
    }

    @Test
    void getCategoriesForPromo() {
        Pageable pageable = PageRequest.ofSize(10);
        Page<MainCategoryDTO> mainCategoryDTOPage = new PageImpl<>(
                List.of(new MainCategoryDTO(), new MainCategoryDTO(), new MainCategoryDTO(), new MainCategoryDTO()),
                pageable, 4);
        when(mainCategoryService.getMainCategoriesBySearch(
                any(CategoryCriteria.class), anyInt(), anyInt()))
                .thenReturn(mainCategoryDTOPage);
        Page<MainCategoryDTO> categoriesForPromo =
                promotionService.getCategoriesForPromo("", pageable.getPageNumber(), pageable.getPageSize());
        List<MainCategoryDTO> content = categoriesForPromo.getContent();
        assertFalse(content.isEmpty());
    }

    @Test
    void getSubcategoriesByCategory() {
        List<SubcategoryDTO> subcategoryDTOS =
                List.of(new SubcategoryDTO(), new SubcategoryDTO(), new SubcategoryDTO(), new SubcategoryDTO());
        MainCategoryDTO mainCategoryDTO = new MainCategoryDTO();
        mainCategoryDTO.setId(1L);
        mainCategoryDTO.setSubcategories(subcategoryDTOS);
        when(mainCategoryService.getMainCategoryById(mainCategoryDTO.getId())).thenReturn(mainCategoryDTO);

        List<SubcategoryDTO> subcategoriesByCategory = promotionService.getSubcategoriesByCategory(mainCategoryDTO.getId());
        assertFalse(subcategoriesByCategory.isEmpty());
        assertEquals(subcategoryDTOS.size(), subcategoriesByCategory.size());
    }

    @Test
    void getSubcategories() {
        Pageable pageable = PageRequest.ofSize(10);
        List<SubcategoryDTO> subcategoryDTOS =
                List.of(new SubcategoryDTO(), new SubcategoryDTO(), new SubcategoryDTO(), new SubcategoryDTO());
        Page<SubcategoryDTO> subcategoryDTOPage = new PageImpl<>(subcategoryDTOS, pageable, subcategoryDTOS.size());
        when(subcategoryService.getSubcategories(pageable.getPageNumber(), pageable.getPageSize(), ""))
                .thenReturn(subcategoryDTOPage);

        Page<SubcategoryDTO> subcategories =
                promotionService.getSubcategories("", pageable.getPageNumber(), pageable.getPageSize());
        List<SubcategoryDTO> content = subcategories.getContent();
        assertFalse(content.isEmpty());
        assertEquals(subcategoryDTOS.size(), content.size());
    }

    @Test
    void getPromotionConditions() {
        Map<String, String> promotionConditionValuesLabels = new HashMap<>();
        Arrays.stream(PromotionCondition.values()).forEach(
                promotionCondition -> promotionConditionValuesLabels
                        .put(promotionCondition.name(), promotionCondition.label));
        Map<String, String> promotionConditions = promotionService.getPromotionConditions();
        assertEquals(promotionConditionValuesLabels, promotionConditions);
        assertEquals(promotionConditionValuesLabels.size(), promotionConditions.size());
    }

    @Test
    void getProducts() {
        Pageable pageable = PageRequest.ofSize(10);
        List<ProductShortResponse> productShortResponses = List.of(
                new ProductShortResponse(), new ProductShortResponse(), new ProductShortResponse(), new ProductShortResponse());
        Page<ProductShortResponse> page = new PageImpl<>(productShortResponses, pageable, productShortResponses.size());
        when(productService.getProductsBySearch(pageable.getPageNumber(), pageable.getPageSize(), ""))
                .thenReturn(page);
        Page<ProductShortResponse> products = promotionService.getProducts("", pageable.getPageNumber(), pageable.getPageSize());
        List<ProductShortResponse> content = products.getContent();
        assertFalse(content.isEmpty());
        assertEquals(productShortResponses.size(), content.size());
    }

    @Test
    void updatePromotion() {
        PromotionRequest promotionRequest = new PromotionRequest();
        promotionRequest.setId(1L);
        promotionRequest.setPromotionType(PromotionType.FOR_PRODUCT);
        promotionRequest.setName("Promotion");
        promotionRequest.setPhotoFile(new MockMultipartFile("file", "fileContent".getBytes()));
        promotion.setDateOfCreate(Instant.now());
        promotion.setUsedCount(12);

        when(promotionRepo.findById(promotion.getId())).thenReturn(Optional.of(promotion));
        when(promotionRepo.save(any(Promotion.class))).thenReturn(promotion);
        PromotionResponse promotionResponse = promotionService.updatePromotion(promotionRequest);

        assertEquals(promotion.getId(), promotionResponse.getId());
        assertEquals(promotion.getDateOfCreate(), promotionResponse.getDateOfCreate());
        assertEquals(promotion.getPromotionType(), promotionResponse.getPromotionType());
    }

    @Test
    void createPromotion() {
        PromotionRequest promotionRequest = new PromotionRequest();
        promotionRequest.setId(1L);
        promotionRequest.setPromotionType(PromotionType.FOR_PRODUCT);
        promotionRequest.setName("Promotion");
        promotionRequest.setPhotoFile(new MockMultipartFile("file", "fileContent".getBytes()));

        ArgumentCaptor<Promotion> promotionArgumentCaptor = ArgumentCaptor.forClass(Promotion.class);

        when(promotionRepo.save(any(Promotion.class))).thenReturn(promotion);
        PromotionResponse promotionResponse = promotionService.createPromotion(promotionRequest);
        verify(promotionRepo).save(promotionArgumentCaptor.capture());
        Promotion value = promotionArgumentCaptor.getValue();
        assertNotNull(value.getDateOfCreate());
        assertEquals(0, value.getUsedCount());
    }

    @Test
    void createPromotion_ifSavePhotoCatchException() throws IOException {
        PromotionRequest promotionRequest = new PromotionRequest();
        promotionRequest.setId(1L);
        promotionRequest.setPromotionType(PromotionType.FOR_PRODUCT);
        promotionRequest.setName("Promotion");
        promotionRequest.setPhotoFile(new MockMultipartFile("file", "fileContent".getBytes()));

        when(fileUtil.saveImage(any(MultipartFile.class))).thenThrow(IOException.class);
        assertThrows(RuntimeException.class, () -> promotionService.createPromotion(promotionRequest));
    }

    @Test
    void getAllActivePromotionsBySearch() {
        Pageable pageable = PageRequest.ofSize(10);
        Page<Promotion> promotionPage = new PageImpl<>(promotions, pageable, promotions.size());
        when(promotionRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(promotionPage);
        Page<PromotionResponse> allPromotions =
                promotionService.getAllActivePromotionsBySearch(pageable.getPageNumber(), pageable.getPageSize(), "");
        List<PromotionResponse> content = allPromotions.getContent();
        assertFalse(content.isEmpty());

        for (int i = 0; i < promotions.size(); i++) {
            Promotion promotion1 = promotions.get(i);
            PromotionResponse promotionResponse = content.get(i);
            assertEquals(promotion1.getId(), promotionResponse.getId());
            assertEquals(promotion1.getName(), promotionResponse.getName());
            assertEquals(promotion1.getPromotionType(), promotionResponse.getPromotionType());
        }
    }
}
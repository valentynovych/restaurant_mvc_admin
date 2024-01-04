package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.Promotion;
import com.restaurant.restaurant_admin.entity.enums.PromotionCondition;
import com.restaurant.restaurant_admin.entity.enums.PromotionType;
import com.restaurant.restaurant_admin.mapper.PromotionMapper;
import com.restaurant.restaurant_admin.model.category.CategoryCriteria;
import com.restaurant.restaurant_admin.model.category.MainCategoryDTO;
import com.restaurant.restaurant_admin.model.category.SubcategoryDTO;
import com.restaurant.restaurant_admin.model.product.ProductShortResponse;
import com.restaurant.restaurant_admin.model.promotion.PromotionRequest;
import com.restaurant.restaurant_admin.model.promotion.PromotionResponse;
import com.restaurant.restaurant_admin.repository.PromotionRepo;
import com.restaurant.restaurant_admin.repository.specification.PromotionSpecification;
import com.restaurant.restaurant_admin.repository.specification.SearchCriteria;
import com.restaurant.restaurant_admin.utils.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class PromotionService {

    private final PromotionRepo promotionRepo;
    private final MainCategoryService mainCategoryService;
    private final SubcategoryService subcategoryService;
    private final ProductService productService;
    private final UploadFileUtil fileUtil;

    public Page<PromotionResponse> getAllPromotions(int page, int pageSize) {
        log.info(String.format("getAllPromotions() -> start, with page: %s, pageSize: %s", page, pageSize));
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Promotion> promotionPage = promotionRepo.findAll(pageable);
        List<PromotionResponse> promotionShortList =
                PromotionMapper.MAPPER.listPromotionToPromotionResponse(promotionPage.getContent());
        Page<PromotionResponse> responsePage =
                new PageImpl<>(promotionShortList, pageable, promotionPage.getTotalElements());
        log.info("getAllPromotions() -> exit");
        return responsePage;
    }

    public Page<PromotionResponse> getAllPromotionsBySearch(int page, int pageSize, String search) {
        log.info(String.format("getAllPromotionsBySearch() -> start, with page: %s, pageSize: %s, by search: %s",
                page, pageSize, search));
        Pageable pageable = PageRequest.of(page, pageSize);
        SearchCriteria likeName = new SearchCriteria("name", ":", search);
        Page<Promotion> promotionPage = promotionRepo.findAll(new PromotionSpecification(likeName), pageable);
        List<PromotionResponse> promotionShortList =
                PromotionMapper.MAPPER.listPromotionToPromotionResponse(promotionPage.getContent());
        Page<PromotionResponse> responsePage =
                new PageImpl<>(promotionShortList, pageable, promotionPage.getTotalElements());
        log.info("getAllPromotionsBySearch() -> exit");
        return responsePage;
    }

    public PromotionResponse getPromotion(Long promotionId) {
        log.info(String.format("getPromotion() -> start, with promotionId: %s", promotionId));
        if (promotionId != null) {
            Optional<Promotion> byId = promotionRepo.findById(promotionId);
            Promotion promotion = byId.orElseThrow(EntityNotFoundException::new);
            PromotionResponse response = PromotionMapper.MAPPER.promotionToPromotionResponse(promotion);
            log.info(String.format("getPromotion() -> exit, return promotionResponse: %s", response));
            return response;
        }
        log.info("getPromotion() -> exit, return null");
        return null;
    }

    public Map<String, String> getPromotionTypes() {
        log.info("getPromotionTypes() -> start");
        Map<String, String> promotionValuesLabels = new HashMap<>();
        Arrays.stream(PromotionType.values()).forEach(
                promotionType -> promotionValuesLabels.put(promotionType.name(), promotionType.label));
        log.info(String.format("getPromotionTypes() -> exit, return Map: %s", promotionValuesLabels));
        return promotionValuesLabels;
    }

    public Page<MainCategoryDTO> getCategoriesForPromo(String search, int page, int pageSize) {
        log.info(String.format("getCategoriesForPromo() -> start, with page: %s, pageSize: %s, by search: %s", page, pageSize, search));
        Page<MainCategoryDTO> mainCategoriesBySearch = mainCategoryService.getMainCategoriesBySearch(
                CategoryCriteria.builder().search(search).build(), page, pageSize);
        log.info("getCategoriesForPromo() -> exit");
        return mainCategoriesBySearch;
    }

    public List<SubcategoryDTO> getSubcategoriesByCategory(Long categoryId) {
        log.info(String.format("getSubcategoriesByCategory() -> start, with categoryId: %s", categoryId));
        MainCategoryDTO mainCategoryById = mainCategoryService.getMainCategoryById(categoryId);
        log.info(String.format("getSubcategoriesByCategory() -> exit, return MainCategoryDTO: %s", mainCategoryById));
        return mainCategoryById.getSubcategories();
    }

    public Page<SubcategoryDTO> getSubcategories(String search, int page, int pageSize) {
        log.info(String.format("getSubcategories() -> start, with page: %s, pageSize: %s, by search: %s",
                page, pageSize, search));
        Page<SubcategoryDTO> subcategories = subcategoryService.getSubcategories(page, pageSize, search);
        log.info("getSubcategories() -> exit");
        return subcategories;
    }

    public Map<String, String> getPromotionConditions() {
        log.info("getPromotionConditions() -> start");
        Map<String, String> promotionConditionValuesLabels = new HashMap<>();
        Arrays.stream(PromotionCondition.values()).forEach(
                promotionCondition -> promotionConditionValuesLabels
                        .put(promotionCondition.name(), promotionCondition.label));
        log.info("getPromotionConditions() -> exit, return Map: " + promotionConditionValuesLabels);
        return promotionConditionValuesLabels;
    }

    public Page<ProductShortResponse> getProducts(String search, int page, int pageSize) {
        log.info(String.format("getProducts() -> start, with page: %s, pageSize: %s", page, pageSize));
        Page<ProductShortResponse> productsBySearch = productService.getProductsBySearch(page, pageSize, search);
        log.info("getProducts() -> exit");
        return productsBySearch;
    }

    public PromotionResponse updatePromotion(PromotionRequest promotionRequest) {
        log.info(String.format("updatePromotion() -> start, with id: %s", promotionRequest.getId()));
        Promotion request = PromotionMapper.MAPPER.promotionRequestToPromotion(promotionRequest);
        Optional<Promotion> byId = promotionRepo.findById(request.getId());
        Promotion promotion = byId.orElseThrow(EntityNotFoundException::new);
        request.setDateOfCreate(promotion.getDateOfCreate());
        request.setUsedCount(promotion.getUsedCount());
        if (request.getPromoCode().isEmpty()) {
            request.setPromoCode(null);
        }

        if (promotionRequest.getPhotoFile() != null) {
            request.setPhoto(savePhotoFile(promotionRequest.getPhotoFile()));
        }

        Promotion save = promotionRepo.save(request);
        PromotionResponse promotionResponse = Mappers.getMapper(PromotionMapper.class).promotionToPromotionResponse(save);
        log.info(String.format("updatePromotion() -> exit, success update promotion, with id: %s", save.getId()));
        return promotionResponse;
    }

    public PromotionResponse createPromotion(PromotionRequest promotionRequest) {
        log.info(String.format("createPromotion() -> start, promotionRequest: %s", promotionRequest));
        Promotion request = PromotionMapper.MAPPER.promotionRequestToPromotion(promotionRequest);
        request.setDateOfCreate(Instant.now());
        request.setUsedCount(0);
        if (request.getPromoCode().isEmpty()) {
            request.setPromoCode(null);
        }

        if (promotionRequest.getPhotoFile() != null) {
            request.setPhoto(savePhotoFile(promotionRequest.getPhotoFile()));
        }

        Promotion save = promotionRepo.save(request);
        log.info(String.format("createPromotion() -> exit, new promotion id: %s", save.getId()));
        return Mappers.getMapper(PromotionMapper.class).promotionToPromotionResponse(save);
    }

    private String savePhotoFile(MultipartFile photoFile) {
        log.info(String.format("savePhotoFile() -> start, with fileName: %s, fileType: %s",
                photoFile.getName(), photoFile.getContentType()));
        String filename;
        try {
            filename = fileUtil.saveImage(photoFile);
            log.info(String.format("savePhotoFile() -> success save file, with fileName: %s", filename));
            return filename;
        } catch (IOException e) {
            log.error("Error on save file image, error message: " + e.getMessage());
            throw new RuntimeException("Error save file for Promotion");
        }
    }

    public Page<PromotionResponse> getAllActivePromotionsBySearch(int page, int pageSize, String search) {
        log.info(String.format("getAllActivePromotionsBySearch() -> start, with page: %s, pageSize: %s, by search: %s", page, pageSize, search));
        Pageable pageable = PageRequest.of(page, pageSize);
        SearchCriteria likeName = new SearchCriteria("name", ":", search);
        SearchCriteria isActive = new SearchCriteria("isActive", "!", true);
        Page<Promotion> promotionPage = promotionRepo.findAll(
                new PromotionSpecification(likeName)
                        .and(new PromotionSpecification(isActive)),
                pageable);
        List<PromotionResponse> promotionShortList =
                PromotionMapper.MAPPER.listPromotionToPromotionResponse(promotionPage.getContent());
        Page<PromotionResponse> responsePage =
                new PageImpl<>(promotionShortList, pageable, promotionPage.getTotalElements());
        log.info("getAllActivePromotionsBySearch() -> exit");
        return responsePage;
    }
}

package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.Promotion;
import com.restaurant.restaurant_admin.entity.enums.PromotionCondition;
import com.restaurant.restaurant_admin.entity.enums.PromotionType;
import com.restaurant.restaurant_admin.mapper.PromotionMapper;
import com.restaurant.restaurant_admin.model.*;
import com.restaurant.restaurant_admin.repository.PromotionRepo;
import com.restaurant.restaurant_admin.repository.specification.PromotionSpecification;
import com.restaurant.restaurant_admin.repository.specification.SearchCriteria;
import com.restaurant.restaurant_admin.utils.UploadFileUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Promotion> promotionPage = promotionRepo.findAll(pageable);
        List<PromotionResponse> promotionShortList =
                PromotionMapper.MAPPER.listPromotionToPromotionResponse(promotionPage.getContent());
        Page<PromotionResponse> responsePage =
                new PageImpl<>(promotionShortList, pageable, promotionPage.getTotalElements());
        return responsePage;
    }

    public Page<PromotionResponse> getAllPromotionsBySearch(int page, int pageSize, String search) {
        Pageable pageable = PageRequest.of(page, pageSize);
        SearchCriteria likeName = new SearchCriteria("name", ":", search);
        Page<Promotion> promotionPage = promotionRepo.findAll(new PromotionSpecification(likeName), pageable);
        List<PromotionResponse> promotionShortList =
                PromotionMapper.MAPPER.listPromotionToPromotionResponse(promotionPage.getContent());
        Page<PromotionResponse> responsePage =
                new PageImpl<>(promotionShortList, pageable, promotionPage.getTotalElements());
        return responsePage;
    }

    public PromotionResponse getPromotion(Long promotionId) {
        if (promotionId != null) {
            Optional<Promotion> byId = promotionRepo.findById(promotionId);
            Promotion promotion = byId.orElseThrow(EntityExistsException::new);
            PromotionResponse response = PromotionMapper.MAPPER.promotionToPromotionResponse(promotion);
            return response;
        }
        return null;
    }

    public Map<String, String> getPromotionTypes() {
        Map<String, String> promotionValuesLabels = new HashMap<>();
        Arrays.stream(PromotionType.values()).forEach(
                promotionType -> promotionValuesLabels.put(promotionType.name(), promotionType.label));
        return promotionValuesLabels;
    }

    public Page<MainCategoryDTO> getCategoriesForPromo(String search, int page, int pageSize) {
        return mainCategoryService.getMainCategoriesBySearch(search, page, pageSize);
    }

    public List<SubcategoryDTO> getSubcategoriesByCategory(Long categoryId) {
        MainCategoryDTO mainCategoryById = mainCategoryService.getMainCategoryById(categoryId);
        return mainCategoryById.getSubcategories();
    }

    public Page<SubcategoryDTO> getSubcategories(String search, int page, int pageSize) {
        return subcategoryService.getSubcategories(page, pageSize, search);
    }

    public Map<String, String> getPromotionConditions() {
        Map<String, String> promotionConditionValuesLabels = new HashMap<>();
        Arrays.stream(PromotionCondition.values()).forEach(
                promotionCondition -> promotionConditionValuesLabels
                        .put(promotionCondition.name(), promotionCondition.label));
        return promotionConditionValuesLabels;
    }

    public Page<ProductShortResponse> getProducts(String search, int page, int pageSize) {
        return productService.getProductsBySearch(page, pageSize, search);
    }

    public PromotionResponse updatePromotion(PromotionRequest promotionRequest) {
        Promotion request = PromotionMapper.MAPPER.promotionRequestToPromotion(promotionRequest);
        Optional<Promotion> byId = promotionRepo.findById(request.getId());
        Promotion promotion = byId.orElseThrow(EntityNotFoundException::new);
        request.setDateOfCreate(promotion.getDateOfCreate());
        request.setUsedCount(promotion.getUsedCount());

        if (promotionRequest.getPhotoFile() != null) {
            request.setPhoto(savePhotoFile(promotionRequest.getPhotoFile()));
        }

        Promotion save = promotionRepo.save(request);
        return Mappers.getMapper(PromotionMapper.class).promotionToPromotionResponse(save);
    }

    public PromotionResponse createPromotion(PromotionRequest promotionRequest) {
        Promotion request = PromotionMapper.MAPPER.promotionRequestToPromotion(promotionRequest);
        request.setDateOfCreate(Instant.now());
        request.setUsedCount(0);

        if (promotionRequest.getPhotoFile() != null) {
            request.setPhoto(savePhotoFile(promotionRequest.getPhotoFile()));
        }

        Promotion save = promotionRepo.save(request);
        return Mappers.getMapper(PromotionMapper.class).promotionToPromotionResponse(save);
    }

    private String savePhotoFile(MultipartFile photoFile) {
        String filename;
        try {
            filename = fileUtil.saveImage(photoFile);
            return filename;
        } catch (IOException e) {
            log.error("Error on save file image, error message: " + e.getMessage());
            throw new RuntimeException();
        }
    }

    public Page<PromotionResponse> getAllActivePromotionsBySearch(int page, int pageSize, String search) {
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
        return responsePage;
    }
}

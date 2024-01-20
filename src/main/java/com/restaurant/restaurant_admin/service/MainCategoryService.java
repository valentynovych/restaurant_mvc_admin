package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.MainCategory;
import com.restaurant.restaurant_admin.entity.Product;
import com.restaurant.restaurant_admin.mapper.MainCategoryMapper;
import com.restaurant.restaurant_admin.model.category.CategoryCriteria;
import com.restaurant.restaurant_admin.model.category.MainCategoryDTO;
import com.restaurant.restaurant_admin.model.category.MainCategoryShortResponse;
import com.restaurant.restaurant_admin.repository.MainCategoryRepo;
import com.restaurant.restaurant_admin.repository.ProductRepo;
import com.restaurant.restaurant_admin.repository.specification.MainCategorySpecification;
import com.restaurant.restaurant_admin.repository.specification.SearchCriteria;
import com.restaurant.restaurant_admin.utils.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Log4j2
public class MainCategoryService {

    private final MainCategoryRepo mainCategoryRepo;
    private final ProductRepo productRepo;
    private final UploadFileUtil fileUtil;

    public MainCategoryDTO getMainCategoryById(Long id) {
        log.info("method getMainCategoryById() -> start");
        Optional<MainCategory> byId = mainCategoryRepo.findById(id);
        MainCategoryDTO dto;
        if (byId.isPresent()) {
            log.info("method getMainCategoryById() -> category isPresent, start map to DTO");
            dto = MainCategoryMapper.MAPPER.mainCategoryToMainCategoryDto(byId.get());
        } else {
            log.info("method getMainCategoryById() -> category isEmpty, create empty DTO");
            dto = new MainCategoryDTO();
        }
        log.info("method getMainCategoryById() -> exit");
        return dto;
    }

    public List<MainCategoryShortResponse> getAllMainCategories() {
        log.info("method getAllMainCategories() -> start");
        List<MainCategory> mainCategories = mainCategoryRepo.findAll();
        log.info("method getAllMainCategories() -> exit, return MainCategoryTablesResponse list");
        return mainCategories
                .stream()
                .map(MainCategoryMapper.MAPPER::toMainCategoryTablesResponse)
                .toList();
    }

    public Boolean deleteCategoryById(Long categoryId) {
        log.info("Start deleting MainCategory by id: " + categoryId);
        Optional<MainCategory> byId = mainCategoryRepo.findById(categoryId);
        if (byId.isEmpty()) {
            log.info("MainCategory not found, return false");
            return false;
        }
        MainCategory mainCategory = byId.get();
        List<Product> allByForMainCategory = productRepo.findAllByForMainCategoryIn(Set.of(mainCategory));
        if (!allByForMainCategory.isEmpty()) {
            log.info("MainCategory has child products, return false");
            return false;
        }
        mainCategoryRepo.delete(mainCategory);
        log.info("MainCategory has been deleted, return true");
        return true;
    }

    public void createMainCategory(MainCategoryDTO mainCategoryDTO, MultipartFile previewIconFile) {
        log.info("method createMainCategory -> start map DTO to Entity");
        MainCategory mainCategory = MainCategoryMapper.MAPPER.mainCategoryDtoToMainCategory(mainCategoryDTO);
        mainCategory.setDateOfCreate(new Date());
        mainCategory.setCountChildProduct(0);
        mainCategory.getSubcategories()
                .forEach(subcategory -> subcategory.setParentCategory(mainCategory));

        if (previewIconFile != null && !previewIconFile.isEmpty()) {
            log.info("method updateMainCategory -> start update previewIcon on entity");
            mainCategory.setPreviewIcon(savePreviewIcon(previewIconFile));
        }
        log.info("method updateMainCategory -> save category");
        mainCategoryRepo.save(mainCategory);
        log.info("method updateMainCategory -> exit");
    }

    public void updateMainCategory(MainCategoryDTO mainCategoryDTO, MultipartFile previewIconFile) {
        log.info("method updateMainCategory -> start map DTO to Entity");
        MainCategory mainCategory = MainCategoryMapper.MAPPER.mainCategoryDtoToMainCategory(mainCategoryDTO);
        if (mainCategoryDTO.getSubcategories() != null) {
            log.info("method updateMainCategory -> start update subcategories on entity");
            mainCategory.getSubcategories()
                    .stream()
                    .filter(subcategory -> !subcategory.getSubcategoryName().isEmpty())
                    .forEach(subcategoryDTO -> subcategoryDTO.setParentCategory(mainCategory));
        }
        if (previewIconFile != null && !previewIconFile.isEmpty()) {
            log.info("method updateMainCategory -> start update previewIcon on entity");
            mainCategory.setPreviewIcon(savePreviewIcon(previewIconFile));
        }
        log.info("method updateMainCategory -> save category");
        mainCategoryRepo.save(mainCategory);
        log.info("method updateMainCategory -> exit");
    }

    private String savePreviewIcon(MultipartFile file) {
        String filename;
        try {
            log.info("method savePreviewIcon -> start save image");
            filename = fileUtil.saveImage(file);
            return filename;
        } catch (IOException e) {
            log.error("method savePreviewIcon -> error saving image :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Page<MainCategoryDTO> getMainCategoriesByPage(int page, int pageSize) {
        log.info("method getMainCategoriesByPage -> start getting categories by page");
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<MainCategory> mainCategories = mainCategoryRepo.findAll(pageable);
        List<MainCategoryDTO> mainCategoryDTOS = MainCategoryMapper.MAPPER.listMainCategoryToDtoList(mainCategories.getContent());
        Page<MainCategoryDTO> mainCategoriesDTOPage =
                new PageImpl<>(mainCategoryDTOS, pageable, mainCategories.getTotalElements());
        log.info("method getMainCategoriesByPage -> exit, return page with categories");
        return mainCategoriesDTOPage;
    }

    public Page<MainCategoryDTO> getMainCategoriesBySearch(CategoryCriteria categoryCriteria, int page, int pageSize) {
        log.info("method getMainCategoriesBySearch -> start getting categories by page");
        Pageable pageable = PageRequest.of(page, pageSize);
        MainCategorySpecification specification = new MainCategorySpecification(categoryCriteria);

        Page<MainCategory> mainCategoryPage = mainCategoryRepo.findAll(Specification.where(specification), pageable);
        List<MainCategoryDTO> mainCategoryDTOS = MainCategoryMapper.MAPPER.listMainCategoryToDtoList(mainCategoryPage.getContent());
        Page<MainCategoryDTO> mainCategoriesDTOPage =
                new PageImpl<>(mainCategoryDTOS, pageable, mainCategoryPage.getTotalElements());
        log.info("method getMainCategoriesBySearch -> exit, return page with categories");
        return mainCategoriesDTOPage;
    }
}

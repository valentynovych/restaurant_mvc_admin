package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.MainCategory;
import com.restaurant.restaurant_admin.mapper.MainCategoryMapper;
import com.restaurant.restaurant_admin.model.MainCategoryDTO;
import com.restaurant.restaurant_admin.model.MainCategoryShortResponse;
import com.restaurant.restaurant_admin.repository.MainCategoryRepo;
import com.restaurant.restaurant_admin.repository.specification.MainCategorySpecification;
import com.restaurant.restaurant_admin.repository.specification.SearchCriteria;
import com.restaurant.restaurant_admin.utils.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Log4j2
public class MainCategoryService {
    private final MainCategoryRepo mainCategoryRepo;
    private final MainCategoryMapper mapper;
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
        List<MainCategory> mainCategories = (List<MainCategory>) mainCategoryRepo.findAll();
        log.info("method getAllMainCategories() -> exit, return MainCategoryTablesResponse list");
        return mainCategories
                .stream()
                .map(mapper::toMainCategoryTablesResponse)
                .toList();
    }

    public Boolean deleteCategoryById(Long categoryId) {
        log.info("Start deleting MainCategory by id: " + categoryId);
        Optional<MainCategory> byId = mainCategoryRepo.findById(categoryId);
        if (byId.isEmpty()) {
            log.info("MainCategory not found, return false");
            return false;
        }
        mainCategoryRepo.deleteById(categoryId);
        log.info("MainCategory has been deleted, return true");
        return true;
    }

    public void createMainCategory(MainCategoryDTO mainCategoryDTO, MultipartFile previewIconFile) {
        log.info("method createMainCategory -> start map DTO to Entity");
        MainCategory mainCategory = mapper.mainCategoryDtoToMainCategory(mainCategoryDTO);
        mainCategory.setDateOfCreate(new Date());
        mainCategory.setCountChildProduct(0);

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
        MainCategory mainCategory = mapper.mainCategoryDtoToMainCategory(mainCategoryDTO);
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
        List<MainCategoryDTO> mainCategoryDTOS = mapper.listMainCategoryToDtoList(mainCategories.getContent());
        Page<MainCategoryDTO> mainCategoriesDTOPage =
                new PageImpl<>(mainCategoryDTOS, pageable, mainCategories.getTotalElements());
        log.info("method getMainCategoriesByPage -> exit, return page with categories");
        return mainCategoriesDTOPage;
    }

    public Page<MainCategoryDTO> getMainCategoriesBySearch(String search, int page, int pageSize) {
        log.info("method getMainCategoriesBySearch -> start getting categories by page");
        Pageable pageable = PageRequest.of(page, pageSize);
        MainCategorySpecification spec =
                new MainCategorySpecification(
                        new SearchCriteria("categoryName", ":", search));
        Page<MainCategory> mainCategoryPage = mainCategoryRepo.findAll(spec, pageable);
        List<MainCategoryDTO> mainCategoryDTOS = mapper.listMainCategoryToDtoList(mainCategoryPage.getContent());
        Page<MainCategoryDTO> mainCategoriesDTOPage =
                new PageImpl<>(mainCategoryDTOS, pageable, mainCategoryPage.getTotalElements());
        log.info("method getMainCategoriesBySearch -> exit, return page with categories");
        return mainCategoriesDTOPage;
    }
}

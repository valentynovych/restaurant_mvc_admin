package com.restaurant.restaurant_admin.controller;

import com.restaurant.restaurant_admin.model.MainCategoryDTO;
import com.restaurant.restaurant_admin.model.MainCategoryShortResponse;
import com.restaurant.restaurant_admin.model.SubcategoryDTO;
import com.restaurant.restaurant_admin.service.MainCategoryService;
import com.restaurant.restaurant_admin.service.SubcategoryService;
import com.restaurant.restaurant_admin.utils.UploadFileUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoriesController {
    private final MainCategoryService categoryService;
    private final SubcategoryService subcategoryService;
    private final UploadFileUtil fileUtil;
    private final int pageSize = 10; // TODO: pageSize = 10

    @GetMapping
    public ModelAndView viewCategories() {
        return new ModelAndView("admin/categories/categories");
    }

    @GetMapping("/add")
    public ModelAndView viewAddNewCategory() {
        return new ModelAndView("admin/categories/edit-category")
                .addObject("mainCategoryDTO", new MainCategoryDTO());
    }

    @PostMapping("/add")
    public @ResponseBody ResponseEntity<?> saveCategory(@Valid @ModelAttribute MainCategoryDTO mainCategoryDTO,
                                                        BindingResult bindingResult,
                                                        @ModelAttribute("previewIconFile") MultipartFile previewIconFile) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        categoryService.createMainCategory(mainCategoryDTO, previewIconFile);
        return new ResponseEntity<>("Збереження успішне !", HttpStatus.OK);
    }

    @GetMapping("/edit-category/{categoryId}")
    public ModelAndView editMainCategory(@PathVariable Long categoryId) {
        return new ModelAndView("admin/categories/edit-category");
    }

    @PostMapping("/edit-category/{categoryId}")
    public @ResponseBody ResponseEntity<?> editCategory(@PathVariable Long categoryId,
                                                        @Valid @ModelAttribute MainCategoryDTO mainCategoryDTO,
                                                        BindingResult bindingResult,
                                                        @ModelAttribute("previewIconFile") MultipartFile previewIconFile) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        categoryService.updateMainCategory(mainCategoryDTO, previewIconFile);
        return new ResponseEntity<>("Оновлення успішне", HttpStatus.OK);
    }

    @GetMapping("/category/{categoryId}")
    public @ResponseBody MainCategoryDTO getMainCategoryById(@PathVariable Long categoryId) {
        return categoryService.getMainCategoryById(categoryId);
    }

    @GetMapping("/getSubcategoriesByMain/{categoryId}")
    public @ResponseBody List<SubcategoryDTO> getSubcategoriesByMain(@PathVariable Long categoryId) {
        return subcategoryService.getSubcategoriesByParent(categoryId);
    }

    @GetMapping("/getAll")
    public @ResponseBody List<MainCategoryShortResponse> getAllMainCategories() {
        return categoryService.getAllMainCategories();
    }

    @DeleteMapping("/delete/{categoryId}")
    public @ResponseBody ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        Boolean isRemoved = categoryService.deleteCategoryById(categoryId);

        if (!isRemoved) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @GetMapping("/getPage")
    public @ResponseBody Page<MainCategoryDTO> getCategoriesOnPage(@RequestParam int page) {
        return categoryService.getMainCategoriesByPage(page, pageSize);
    }

    @GetMapping("/getPageSearch")
    public @ResponseBody Page<MainCategoryDTO> getCategoriesOnPage(@RequestParam int page, @RequestParam(required = false) String search) {
        return categoryService.getMainCategoriesBySearch(search, page, pageSize);
    }

    @DeleteMapping("delete-image/{filename}")
    public @ResponseBody ResponseEntity<?> deleteCategoryImage(@PathVariable String filename) {
        fileUtil.deleteUploadFile(filename);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}


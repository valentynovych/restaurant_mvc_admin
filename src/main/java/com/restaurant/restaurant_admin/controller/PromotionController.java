package com.restaurant.restaurant_admin.controller;

import com.restaurant.restaurant_admin.model.PromotionRequest;
import com.restaurant.restaurant_admin.model.PromotionResponse;
import com.restaurant.restaurant_admin.service.PromotionService;
import com.restaurant.restaurant_admin.validator.PromotionValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/promotions")
public class PromotionController {

    private final PromotionService promotionService;
    private final PromotionValidator promotionValidator;
    private final int pageSize = 10;

    @InitBinder(value = "promotionRequest")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(promotionValidator);
    }

    @GetMapping()
    public ModelAndView viewPromotions() {
        return new ModelAndView("admin/promotions/promotions");
    }

    @GetMapping("/edit-promotion/{promotionId}")
    public ModelAndView viewEditPromotion(@PathVariable Long promotionId) {
        return new ModelAndView("admin/promotions/edit-promotion");
    }

    @GetMapping("/add")
    public ModelAndView viewAddPromotion() {
        return new ModelAndView("admin/promotions/edit-promotion");
    }

    @GetMapping("/getPromotions")
    public @ResponseBody ResponseEntity<?> getAllPromotions(@RequestParam int page) {
        return new ResponseEntity<>(promotionService.getAllPromotions(page, pageSize), HttpStatus.OK);
    }

    @GetMapping("/getPromotionsBySearch")
    public @ResponseBody ResponseEntity<?> getAllPromotionsSearch(@RequestParam int page, @RequestParam String search) {
        return new ResponseEntity<>(promotionService.getAllPromotionsBySearch(page, pageSize, search), HttpStatus.OK);
    }

    @GetMapping("/getActivePromotions")
    public @ResponseBody ResponseEntity<?> getAllActivePromotionsSearch(@RequestParam int page, @RequestParam String search) {
        return new ResponseEntity<>(promotionService.getAllActivePromotionsBySearch(page, pageSize, search), HttpStatus.OK);
    }

    @GetMapping("/getPromotion/{promotionId}")
    public @ResponseBody ResponseEntity<PromotionResponse> getAllPromotions(@PathVariable Long promotionId) {
        return new ResponseEntity<>(promotionService.getPromotion(promotionId), HttpStatus.OK);
    }

    @GetMapping("/getPromotionTypes")
    public @ResponseBody ResponseEntity<?> getAllPromotions() {
        return new ResponseEntity<>(promotionService.getPromotionTypes(), HttpStatus.OK);
    }

    @GetMapping("/getPromotionConditions")
    public @ResponseBody ResponseEntity<?> getPromotionConditions() {
        return new ResponseEntity<>(promotionService.getPromotionConditions(), HttpStatus.OK);
    }

    @GetMapping("/getCategories")
    public @ResponseBody ResponseEntity<?> getCategoriesForPromo(@RequestParam String search,
                                                                 @RequestParam int page) {
        return new ResponseEntity<>(promotionService.getCategoriesForPromo(search, page, pageSize), HttpStatus.OK);
    }

    @GetMapping("/getSubcategoriesByCategory/{categoryId}")
    public @ResponseBody ResponseEntity<?> getCategoriesForPromo(@PathVariable Long categoryId) {
        return new ResponseEntity<>(promotionService.getSubcategoriesByCategory(categoryId), HttpStatus.OK);
    }

    @GetMapping("/getSubcategories")
    public @ResponseBody ResponseEntity<?> getCategories(@RequestParam String search, @RequestParam int page) {
        return new ResponseEntity<>(promotionService.getSubcategories(search, page, pageSize), HttpStatus.OK);
    }

    @GetMapping("/getProducts")
    public @ResponseBody ResponseEntity<?> getProducts(@RequestParam String search, @RequestParam int page) {
        return new ResponseEntity<>(promotionService.getProducts(search, page, pageSize), HttpStatus.OK);
    }

    @PostMapping("/update-promotion")
    public @ResponseBody ResponseEntity<?> updatePromotion(@Valid @ModelAttribute PromotionRequest promotionRequest,
                                                           BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(promotionService.updatePromotion(promotionRequest), HttpStatus.OK);
    }

    @PostMapping("/add-promotion")
    public @ResponseBody ResponseEntity<?> addPromotion(@Valid @ModelAttribute PromotionRequest promotionRequest,
                                                        BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        promotionService.createPromotion(promotionRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

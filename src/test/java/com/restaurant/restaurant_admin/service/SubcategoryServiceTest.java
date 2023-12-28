package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.MainCategory;
import com.restaurant.restaurant_admin.entity.Subcategory;
import com.restaurant.restaurant_admin.model.SubcategoryDTO;
import com.restaurant.restaurant_admin.repository.SubcategoryRepo;
import com.restaurant.restaurant_admin.repository.specification.SubcategorySpecification;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubcategoryServiceTest {

    @Mock
    private SubcategoryRepo subcategoryRepo;
    @InjectMocks
    private SubcategoryService subcategoryService;
    private List<Subcategory> subcategories;

    @BeforeEach
    void setUp() {
        subcategories = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Subcategory subcategory = new Subcategory();
            subcategory.setId((long) i);
            subcategory.setSubcategoryName("subcategory_" + i);
            subcategories.add(subcategory);
        }
    }

    @Test
    void getSubcategoriesByParent() {
        MainCategory mainCategory = new MainCategory();
        mainCategory.setId(1L);

        when(subcategoryRepo.findAllByParentCategory(any(MainCategory.class))).thenReturn(subcategories);

        List<SubcategoryDTO> subcategoriesByParent = subcategoryService.getSubcategoriesByParent(mainCategory.getId());
        assertFalse(subcategoriesByParent.isEmpty());
        for (int i = 0; i < subcategories.size(); i++) {
            Subcategory subcategory = subcategories.get(i);
            SubcategoryDTO subcategoryDTO = subcategoriesByParent.get(i);
            assertEquals(subcategory.getId(), subcategoryDTO.getSubcategoryId());
            assertEquals(subcategory.getSubcategoryName(), subcategoryDTO.getSubcategoryName());
        }
    }

    @Test
    void getSubcategories() {
        Pageable pageable = PageRequest.ofSize(10);
        Page<Subcategory> subcategoryPage = new PageImpl<>(subcategories, pageable, subcategories.size());
        when(subcategoryRepo.findAll(any(SubcategorySpecification.class), any(Pageable.class)))
                .thenReturn(subcategoryPage);

        Page<SubcategoryDTO> subcategories1 =
                subcategoryService.getSubcategories(pageable.getPageNumber(), pageable.getPageSize(), "");
        List<SubcategoryDTO> content = subcategories1.getContent();
        assertFalse(content.isEmpty());

        for (int i = 0; i < subcategories.size(); i++) {
            Subcategory subcategory = subcategories.get(i);
            SubcategoryDTO subcategoryDTO = content.get(i);
            assertEquals(subcategory.getId(), subcategoryDTO.getSubcategoryId());
            assertEquals(subcategory.getSubcategoryName(), subcategoryDTO.getSubcategoryName());
        }
    }

    @Test
    void deleteSubcategoryById_Success() {
        Subcategory subcategory = new Subcategory();
        subcategory.setId(1L);

        when(subcategoryRepo.existsById(subcategory.getId())).thenReturn(true);
        boolean isDeleted = subcategoryService.deleteSubcategoryById(subcategory.getId());
        assertTrue(isDeleted);
        verify(subcategoryRepo).deleteById(subcategory.getId());
    }

    @Test
    void deleteSubcategoryById_ErrorDeleting() {
        Subcategory subcategory = new Subcategory();
        subcategory.setId(1L);

        when(subcategoryRepo.existsById(subcategory.getId())).thenReturn(false);
        boolean isDeleted = subcategoryService.deleteSubcategoryById(subcategory.getId());
        assertFalse(isDeleted);
    }
}
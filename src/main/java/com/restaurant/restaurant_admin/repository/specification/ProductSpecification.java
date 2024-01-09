package com.restaurant.restaurant_admin.repository.specification;

import com.restaurant.restaurant_admin.entity.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ProductSpecification implements Specification<Product> {
    private final String search;
    private final Boolean isIngredient;

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (search != null) {
            predicates.add(criteriaBuilder.like(root.get("name"), "%" + search+ "%"));
        }

        if (isIngredient != null) {
            predicates.add(criteriaBuilder.equal(root.get("isIngredient"), isIngredient));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}

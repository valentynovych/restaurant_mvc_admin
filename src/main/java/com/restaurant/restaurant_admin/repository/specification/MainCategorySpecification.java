package com.restaurant.restaurant_admin.repository.specification;

import com.restaurant.restaurant_admin.entity.MainCategory;
import com.restaurant.restaurant_admin.model.category.CategoryCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class MainCategorySpecification implements Specification<MainCategory> {

    private final CategoryCriteria criteria;

    @Override
    public Predicate toPredicate(Root<MainCategory> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getStatus() != null) {
            predicates.add(criteriaBuilder.equal(root.get("isActive"), criteria.getStatus()));
        }

        if (criteria.getSearch() != null && !criteria.getSearch().isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("categoryName"), "%" + criteria.getSearch() + "%"));
        }

        if (criteria.getDate() != null && !criteria.getDate().isEmpty()) {
            LocalDate date = LocalDate.parse(criteria.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            Predicate dateOfCreate = criteriaBuilder.greaterThan(root.get("dateOfCreate"),
                    Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC)));
            Predicate dateOfCreate1 = criteriaBuilder.lessThan(root.get("dateOfCreate"),
                    Date.from(date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)));

            predicates.add(criteriaBuilder.and(dateOfCreate, dateOfCreate1));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}

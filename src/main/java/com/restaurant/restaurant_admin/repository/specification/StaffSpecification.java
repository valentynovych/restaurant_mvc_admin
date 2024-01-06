package com.restaurant.restaurant_admin.repository.specification;

import com.restaurant.restaurant_admin.entity.Staff;
import com.restaurant.restaurant_admin.entity.enums.Role;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class StaffSpecification implements Specification<Staff> {

    private final Role role;
    private final String search;

    @Override
    public Predicate toPredicate(Root<Staff> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        if (role != null) {
            predicates.add(criteriaBuilder.equal(root.get("staffRole"), role));
        }

        if (search != null && !search.isEmpty()) {
            Predicate likeFirstName = criteriaBuilder.like(root.get("firstName"), "%" + search + "%");
            Predicate likeLastName = criteriaBuilder.like(root.get("lastName"), "%" + search + "%");
            Predicate likeEmail = criteriaBuilder.like(root.get("email"), "%" + search + "%");
            Predicate likePhone = criteriaBuilder.like(root.get("phone"), "%" + search + "%");

            predicates.add(criteriaBuilder.or(likeFirstName, likeLastName, likeEmail, likePhone));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}

package com.restaurant.restaurant_admin.repository.specification;

import com.restaurant.restaurant_admin.entity.Staff;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class StaffSpecification implements Specification<Staff> {

    private final SearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<Staff> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString());
        } else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return criteriaBuilder.lessThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString());
        } else if (criteria.getOperation().equalsIgnoreCase(":")) {
            return criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
        } else {
            return criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue().toString());
        }
    }
}

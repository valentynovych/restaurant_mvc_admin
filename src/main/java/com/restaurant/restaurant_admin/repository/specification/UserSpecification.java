package com.restaurant.restaurant_admin.repository.specification;

import com.restaurant.restaurant_admin.entity.User;
import com.restaurant.restaurant_admin.entity.UserDetails;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class UserSpecification {

    public static Specification<User> user(SearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {

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
        };
    }

    public static Specification<User> joinDetails(SearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            if (criteria.getOperation().equalsIgnoreCase(">")) {
                Join<UserDetails, User> userDetailsJoin = root.join("userDetails");
                return criteriaBuilder.greaterThanOrEqualTo(
                        userDetailsJoin.get(criteria.getKey()), criteria.getValue().toString());
            } else if (criteria.getOperation().equalsIgnoreCase("<")) {
                Join<UserDetails, User> userDetailsJoin = root.join("userDetails");
                return criteriaBuilder.lessThanOrEqualTo(
                        userDetailsJoin.get(criteria.getKey()), criteria.getValue().toString());
            } else if (criteria.getOperation().equalsIgnoreCase(":")) {
                Join<UserDetails, User> userDetailsJoin = root.join("userDetails");
                return criteriaBuilder.like(userDetailsJoin.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            } else {
                Join<UserDetails, User> userDetailsJoin = root.join("userDetails");
                return criteriaBuilder.equal(userDetailsJoin.get(criteria.getKey()), criteria.getValue().toString());
            }
        };
    }
}

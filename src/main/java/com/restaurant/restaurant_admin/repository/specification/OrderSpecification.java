package com.restaurant.restaurant_admin.repository.specification;

import com.restaurant.restaurant_admin.entity.Order;
import com.restaurant.restaurant_admin.entity.User;
import com.restaurant.restaurant_admin.entity.UserDetails;
import com.restaurant.restaurant_admin.model.OrderCriteria;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class OrderSpecification implements Specification<Order> {
    private final OrderCriteria orderCriteria;

    public static Specification<Order> joinUser(OrderCriteria orderCriteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (orderCriteria.getSearch() != null) {
                Join<User, Order> joinUser = root.join("user", JoinType.RIGHT);
                Join<User, UserDetails> joinUserDetails = joinUser.join("userDetails");
                predicates.add(criteriaBuilder.like(
                        joinUserDetails.get("phone"), "%" + orderCriteria.getSearch() + "%"));
                predicates.add(criteriaBuilder.like(
                        joinUserDetails.get("firstName"), "%" + orderCriteria.getSearch() + "%"));
                predicates.add(criteriaBuilder.like(
                        joinUserDetails.get("lastName"), "%" + orderCriteria.getSearch() + "%"));
                Pattern pattern = Pattern.compile("\\d+");
                if (pattern.matcher(orderCriteria.getSearch()).matches()) {
                    predicates.add(criteriaBuilder.like(root.get("id"), orderCriteria.getSearch()));
                }
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            }
            return null;
        };
    }

    @Override
    public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (orderCriteria.getDateTo() != null && orderCriteria.getDateFrom() != null) {
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.greaterThan(root.get("datetimeOfCreate"), orderCriteria.getDateFrom())
            ));
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.lessThan(root.get("datetimeOfCreate"), orderCriteria.getDateTo())
            ));
        }

        if (orderCriteria.getOrderStatus() != null) {
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("status"), orderCriteria.getOrderStatus())
            ));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}

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

    @Override
    public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (orderCriteria.getDateTo() != null && orderCriteria.getDateFrom() != null) {
            predicates.add(criteriaBuilder.greaterThan(root.get("datetimeOfCreate"), orderCriteria.getDateFrom()));
            predicates.add(criteriaBuilder.or(criteriaBuilder.lessThan(root.get("datetimeOfCreate"), orderCriteria.getDateTo())));
        }

        if (orderCriteria.getOrderStatus() != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), orderCriteria.getOrderStatus()));
        }

        if (orderCriteria.getSearch() != null) {
            Join<User, Order> joinUser = root.join("user");
            Join<User, UserDetails> joinUserDetails = joinUser.join("userDetails");
            Pattern pattern = Pattern.compile("\\d+");
            List<Predicate> predicatesOr = new ArrayList<>();
            predicatesOr.add(criteriaBuilder.like(joinUserDetails.get("phone"), "%" + orderCriteria.getSearch() + "%"));
            predicatesOr.add(criteriaBuilder.like(joinUserDetails.get("firstName"), "%" + orderCriteria.getSearch() + "%"));
            predicatesOr.add(criteriaBuilder.like(joinUserDetails.get("lastName"), "%" + orderCriteria.getSearch() + "%"));

            if (pattern.matcher(orderCriteria.getSearch()).matches()) {
                predicatesOr.add(criteriaBuilder.or(criteriaBuilder.like(root.get("id"), orderCriteria.getSearch())));
            }
            predicates.add(criteriaBuilder.or(predicatesOr.toArray(new Predicate[0])));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}

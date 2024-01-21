package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.User;
import com.restaurant.restaurant_admin.entity.UserDetails;
import com.restaurant.restaurant_admin.mapper.UserMapper;
import com.restaurant.restaurant_admin.model.user.UserRequest;
import com.restaurant.restaurant_admin.model.user.UserShortDetails;
import com.restaurant.restaurant_admin.model.user.UserShortResponse;
import com.restaurant.restaurant_admin.repository.UserRepo;
import com.restaurant.restaurant_admin.repository.specification.SearchCriteria;
import com.restaurant.restaurant_admin.repository.specification.UserSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepo userRepo;

    public Page<UserShortResponse> getUsersOnPage(int page, int pageSize) {
        log.info("method getUsersOnPage -> start, get page: " + page);
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<User> users = userRepo.findAll(pageable);
        List<UserShortResponse> userList = UserMapper.MAPPER.userListToShortResponceList(users.getContent());
        Page<UserShortResponse> userShortResponsePage = new PageImpl<>(userList, pageable, users.getTotalElements());
        log.info("method getUsersOnPage -> exit, return Page<UserShortResponse>");
        return userShortResponsePage;
    }

    public Page<UserShortResponse> getUsersPageBySearch(int page, int pageSize, String search) {
        if (search == null) {
            return getUsersOnPage(page, pageSize);
        }
        log.info(String.format("method getUsersPageBySearch -> start, get page: %s, by text: %s", page, search));
        Pageable pageable = PageRequest.of(page, pageSize);
        SearchCriteria likeEmail = new SearchCriteria("email", ":", search);
        SearchCriteria likePhone = new SearchCriteria("phone", ":", search);
        SearchCriteria likeFirstName = new SearchCriteria("firstName", ":", search);
        SearchCriteria likeLastName = new SearchCriteria("lastName", ":", search);

        Page<User> users = userRepo.findAll(Specification.where(UserSpecification.user(likeEmail))
                .or(Specification.where(UserSpecification.joinDetails(likePhone)))
                .or(Specification.where(UserSpecification.joinDetails(likeFirstName)))
                .or(Specification.where(UserSpecification.joinDetails(likeLastName))), pageable);
        List<UserShortResponse> userList = UserMapper.MAPPER.userListToShortResponceList(users.getContent());
        Page<UserShortResponse> userShortResponsePage = new PageImpl<>(userList, pageable, users.getTotalElements());
        log.info("method getUsersOnPage -> exit, return Page<UserShortResponse>");
        return userShortResponsePage;
    }

    public UserShortDetails getUserDetailsById(Long userId) {
        log.info(String.format("method getUserDetailsById -> start, get user by id: %s", userId));
        Optional<User> byId = userRepo.findById(userId);
        User user = byId.orElseThrow(EntityNotFoundException::new);
        UserShortDetails userShortDetails = UserMapper.MAPPER.userToShortDetails(user);
        log.info("method getUserDetailsById -> exit, return UserShortDetails()");
        return userShortDetails;
    }

    public void updateUser(UserRequest userRequest) {
        log.info(String.format("method updateUser -> start, find user by id: %s", userRequest.getUserId()));
        Optional<User> byId = userRepo.findById(userRequest.getUserId());
        User user = byId.orElseThrow(EntityNotFoundException::new);
        log.info("method updateUser -> user isPresent, set new fields value");
        user.setEmail(userRequest.getEmail());
        user.setIsActive(userRequest.getIsActive());
        UserDetails userDetails = user.getUserDetails();
        userDetails.setPhone(userRequest.getPhone());
        userDetails.setDateOfBirth(userRequest.getDateOfBirth());
        userDetails.setFirstName(userRequest.getFirstName());
        userDetails.setLastName(userRequest.getLastName());
        user.setUserDetails(userDetails);
        userRepo.save(user);
        log.info("method updateUser -> user saved, exit");
    }

    public Boolean changeUserStatus(Long userId, boolean status) {
        log.info(String.format("method changeUserStatus -> start, find user by id: %s", userId));
        Optional<User> byId = userRepo.findById(userId);
        if (byId.isPresent()) {
            log.info(String.format("method changeUserStatus -> user isPresent, change status to: %s", status));
            User user = byId.get();
            user.setIsActive(status);
            log.info("method changeUserStatus -> status changed, exit");
            userRepo.save(user);
            return true;
        }
        log.info("method changeUserStatus -> status is not changed, exit");
        return false;
    }
}

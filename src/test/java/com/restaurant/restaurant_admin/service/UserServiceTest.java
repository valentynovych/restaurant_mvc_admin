package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.User;
import com.restaurant.restaurant_admin.entity.UserDetails;
import com.restaurant.restaurant_admin.model.user.UserRequest;
import com.restaurant.restaurant_admin.model.user.UserShortDetails;
import com.restaurant.restaurant_admin.model.user.UserShortResponse;
import com.restaurant.restaurant_admin.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;
    @InjectMocks
    private UserService userService;
    private User user;
    private List<User> users;

    @BeforeEach
    void setUp() {
        UserDetails userDetails = new UserDetails();
        userDetails.setId(1L);
        userDetails.setPhone("380501212121");
        userDetails.setDateOfBirth(
                new Date(LocalDate.of(1998, 10, 23)
                        .getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH)));
        userDetails.setFirstName("First");
        userDetails.setLastName("Last");
        userDetails.setRegistrationDate(new Date());
        user = new User();
        user.setUserDetails(userDetails);
        user.setId(1L);
        user.setEmail("user@gmail.com");
        user.setIsActive(Boolean.TRUE);
        user.setDateTimeOfLastLogin(Instant.now());

        users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user1 = new User();
            user1.setId((long) i);
            user1.setUserDetails(new UserDetails());
            user1.setIsActive(Boolean.TRUE);
            user1.setEmail("user" + i + "@gmail,com");
            users.add(user1);
        }
    }

    @Test
    void getUsersOnPage() {
        Pageable pageable = PageRequest.ofSize(10);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepo.findAll(any(Pageable.class))).thenReturn(userPage);

        Page<UserShortResponse> usersOnPage = userService.getUsersOnPage(pageable.getPageNumber(), pageable.getPageSize());
        List<UserShortResponse> content = usersOnPage.getContent();
        assertFalse(content.isEmpty());

        for (int i = 0; i < users.size(); i++) {
            User user1 = users.get(i);
            UserShortResponse userShortResponse = content.get(i);
            assertEquals(user1.getId(), userShortResponse.getUserId());
            assertEquals(user1.getEmail(), userShortResponse.getEmail());
        }
    }

    @Test
    void getUsersPageBySearch() {
        Pageable pageable = PageRequest.ofSize(10);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(userPage);

        Page<UserShortResponse> usersOnPage = userService.getUsersPageBySearch(pageable.getPageNumber(), pageable.getPageSize(), "");
        List<UserShortResponse> content = usersOnPage.getContent();
        assertFalse(content.isEmpty());

        for (int i = 0; i < users.size(); i++) {
            User user1 = users.get(i);
            UserShortResponse userShortResponse = content.get(i);
            assertEquals(user1.getId(), userShortResponse.getUserId());
            assertEquals(user1.getEmail(), userShortResponse.getEmail());
        }
    }

    @Test
    void getUserDetailsById_ifUserIsPresent() {
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        UserShortDetails userDetailsById = userService.getUserDetailsById(user.getId());
        assertEquals(user.getId(), userDetailsById.getUserId());
        assertEquals(user.getIsActive(), userDetailsById.getIsActive());
        assertEquals(user.getEmail(), userDetailsById.getEmail());
    }

    @Test
    void getUserDetailsById_ifUserIsEmpty() {
        when(userRepo.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                userService.getUserDetailsById(user.getId()));
    }

    @Test
    void updateUser() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUserId(1L);
        userRequest.setEmail("newUser@gmail.com");
        userRequest.setPhone("380501231223");
        userRequest.setIsActive(Boolean.TRUE);
        userRequest.setFirstName("First Name");
        userRequest.setLastName("Last Name");
        userRequest.setLastName("Last Name");
        userRequest.setDateOfBirth(user.getUserDetails().getDateOfBirth());
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        userService.updateUser(userRequest);
        verify(userRepo).save(any(User.class));
    }

    @Test
    void updateUser_ifUserNotFound() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUserId(1L);
        userRequest.setEmail("newUser@gmail.com");
        userRequest.setPhone("380501231223");
        userRequest.setIsActive(Boolean.TRUE);
        userRequest.setFirstName("First Name");
        userRequest.setLastName("Last Name");
        userRequest.setLastName("Last Name");
        userRequest.setDateOfBirth(user.getUserDetails().getDateOfBirth());
        when(userRepo.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                userService.updateUser(userRequest));
    }

    @Test
    void changeUserStatus() {
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        Boolean isChanged = userService.changeUserStatus(user.getId(), true);
        verify(userRepo).save(any(User.class));
        assertTrue(isChanged);

    }

    @Test
    void changeUserStatus_ifUserNotFound() {
        when(userRepo.findById(user.getId())).thenReturn(Optional.empty());
        Boolean isChanged = userService.changeUserStatus(user.getId(), true);
        assertFalse(isChanged);
    }
}
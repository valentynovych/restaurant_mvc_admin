package com.restaurant.restaurant_admin.controller;

import com.restaurant.restaurant_admin.model.user.UserRequest;
import com.restaurant.restaurant_admin.model.user.UserShortDetails;
import com.restaurant.restaurant_admin.model.user.UserShortResponse;
import com.restaurant.restaurant_admin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;
    private final int pageSize = 10;

    @GetMapping()
    public ModelAndView viewUsers() {
        return new ModelAndView("admin/users/users");
    }

    @GetMapping("/edit-user/{userId}")
    public ModelAndView viewEditUser(@PathVariable Long userId) {
        return new ModelAndView("admin/users/edit-user");
    }

    @GetMapping("/user-details/{userId}")
    public ModelAndView viewUserDetails(@PathVariable Long userId) {
        return new ModelAndView("admin/users/user-details");
    }

    @GetMapping("/getPage")
    public @ResponseBody Page<UserShortResponse> getUsersOnPage(@RequestParam int page) {
        return userService.getUsersOnPage(page, pageSize);
    }

    @GetMapping("/getPageSearch")
    public @ResponseBody Page<UserShortResponse> getUsersPageBySearch(@RequestParam int page, @RequestParam String search) {
        return userService.getUsersPageBySearch(page, pageSize, search);
    }

    @GetMapping("/getUserDetails/{userId}")
    public @ResponseBody UserShortDetails getUserDetails(@PathVariable Long userId) {
        return userService.getUserDetailsById(userId);
    }

    @PostMapping("/edit-user/{userId}")
    public @ResponseBody ResponseEntity<?> updateUser(@Valid @ModelAttribute UserRequest userRequest,
                                                      BindingResult result,
                                                      Long userId) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        userService.updateUser(userRequest);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/blockUser/{userId}")
    public @ResponseBody ResponseEntity<?> blockUser(@PathVariable Long userId) {
        Boolean isBlocked = userService.changeUserStatus(userId, false);
        if (isBlocked) {
            return ResponseEntity.ok().build();
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}

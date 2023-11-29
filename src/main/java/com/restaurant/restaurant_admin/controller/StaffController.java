package com.restaurant.restaurant_admin.controller;

import com.restaurant.restaurant_admin.entity.enums.Role;
import com.restaurant.restaurant_admin.model.StaffRequest;
import com.restaurant.restaurant_admin.model.StaffResponse;
import com.restaurant.restaurant_admin.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.AuthenticationException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("admin/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    private final int pageSize = 10;

    @GetMapping
    public ModelAndView viewStaff() {
        return new ModelAndView("admin/staff/staff");
    }

    @GetMapping("/add")
    public ModelAndView addStaff() {
        return new ModelAndView("admin/staff/edit-staff")
                .addObject("staff", new StaffResponse());
    }

    @GetMapping("/edit-staff/{staffId}")
    public ModelAndView editStaff(@PathVariable Long staffId) {
        return new ModelAndView("admin/staff/edit-staff")
                .addObject("staff", new StaffResponse());
    }

    @GetMapping("/getPage")
    public @ResponseBody Page<StaffResponse> getStaffOnPage(@RequestParam int page) {
        return staffService.getStaffOnPage(page, pageSize);
    }

    @GetMapping("/getPageSearch")
    public @ResponseBody Page<StaffResponse> getStaffOnPage(@RequestParam int page,
                                                            @RequestParam String search) {
        return staffService.getStaffPageOnSearch(search, page, pageSize);
    }

    @GetMapping("/get-staff/{staffId}")
    public @ResponseBody StaffResponse getStaffById(@PathVariable Long staffId) {
        return staffService.getStaffById(staffId);
    }

    @GetMapping("/getAllStaffRole")
    public @ResponseBody List<Role> getListStaffRole() {
        return Arrays.stream(Role.values()).toList();
    }

    @PostMapping("/edit-staff/{staffId}")
    public @ResponseBody ResponseEntity<?> updateStaffById(@PathVariable Long staffId,
                                                           @Valid @ModelAttribute StaffRequest staffRequest,
                                                           BindingResult result) {
        if (!staffRequest.getPhotoFile().isEmpty()
                && checkValidFileType(staffRequest.getPhotoFile())) {
            result.addError(
                    new ObjectError("photo", "Не вірний формат зображення"));
        }
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        staffService.updateStaff(staffRequest);
        return new ResponseEntity<>("Оновлення успішне", HttpStatus.OK);
    }

    @PostMapping("/add")
    public @ResponseBody ResponseEntity<?> updateStaffById(@Valid @ModelAttribute StaffRequest staffRequest,
                                                           BindingResult result) {
        if (!staffRequest.getPhotoFile().isEmpty()
                && checkValidFileType(staffRequest.getPhotoFile())) {
            result.addError(
                    new ObjectError("photo", "Не вірний формат зображення"));
        }
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        staffService.createStaff(staffRequest);
        return new ResponseEntity<>("Оновлення успішне", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{staffId}")
    public @ResponseBody ResponseEntity<?> deleteStaff(@PathVariable Long staffId) {
        boolean isDeleted = false;
        try {
            isDeleted = staffService.deleteStaffById(staffId);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/blockStaff/{staffId}")
    public @ResponseBody ResponseEntity<?> blockStaff(@PathVariable Long staffId) {
        var isChanged = staffService.changeStaffStatus(staffId, false);
        if (isChanged) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private boolean checkValidFileType(MultipartFile file) {
        return !Objects.equals(file.getContentType(), "image/jpeg")
                && !Objects.equals(file.getContentType(), "image/jpg")
                && !Objects.equals(file.getContentType(), "image/png");
    }
}

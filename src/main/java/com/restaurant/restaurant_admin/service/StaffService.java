package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.Staff;
import com.restaurant.restaurant_admin.entity.enums.Role;
import com.restaurant.restaurant_admin.mapper.StaffMapper;
import com.restaurant.restaurant_admin.model.staff.StaffRequest;
import com.restaurant.restaurant_admin.model.staff.StaffResponse;
import com.restaurant.restaurant_admin.repository.StaffRepo;
import com.restaurant.restaurant_admin.repository.specification.SearchCriteria;
import com.restaurant.restaurant_admin.repository.specification.StaffSpecification;
import com.restaurant.restaurant_admin.utils.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class StaffService implements UserDetailsService {

    private final StaffRepo staffRepo;
    private final UploadFileUtil fileUtil;
    private final PasswordEncoder passwordEncoder;

    public StaffService(StaffRepo staffRepo, UploadFileUtil fileUtil, @Lazy PasswordEncoder passwordEncoder) {
        this.staffRepo = staffRepo;
        this.fileUtil = fileUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<StaffResponse> getStaffOnPage(int page, int pageSize) {
        log.info("method getStaffOnPage -> start get staffOnPage, page: " + page + " , pageSize: " + pageSize);
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Staff> staffPage = staffRepo.findAll(pageable);
        List<StaffResponse> staffResponses = StaffMapper.MAPPER.staffListToModelResponseList(staffPage.getContent());
        log.info("method getStaffOnPage -> exit");
        return new PageImpl<>(staffResponses, pageable, staffPage.getTotalElements());
    }

    public Page<StaffResponse> getStaffPageOnSearch(String search, Role role, int page, int pageSize) {
        log.info(String.format("getStaffPageOnSearch() -> start, with page: %s, pageSize: %s, by search: %s",
                page, pageSize, search));
        Pageable pageable = PageRequest.of(page, pageSize);
        StaffSpecification spec1 =
                new StaffSpecification(role, search);
        Page<Staff> staffPage = staffRepo.findAll(spec1, pageable);
        List<StaffResponse> staffResponses = StaffMapper.MAPPER.staffListToModelResponseList(staffPage.getContent());
        Page<StaffResponse> staffResponses1 = new PageImpl<>(staffResponses, pageable, staffPage.getTotalElements());
        log.info("getStaffPageOnSearch() -> exit");
        return staffResponses1;

    }

    public StaffResponse getStaffById(Long staffId) {
        log.info("method getStaffById -> start get staff by id: " + staffId);
        Optional<Staff> byId = staffRepo.findById(staffId);
        Staff staff = byId.orElseThrow(EntityNotFoundException::new);
        StaffResponse staffResponse = StaffMapper.MAPPER.staffToModelResponse(staff);
        log.info("method getStaffById -> exit, return staffResponse");
        return staffResponse;
    }

    public void updateStaff(StaffRequest staffRequest) {
        log.info("method updateStaff -> start update Staff");
        if (staffRequest != null) {
            log.info("method updateStaff -> staffRequest isPresent");
            Staff staff = StaffMapper.MAPPER.requestToStaff(staffRequest);
            if (staffRequest.getPhotoFile() != null && !staffRequest.getPhotoFile().isEmpty()) {
                log.info("method updateStaff -> save upload image");
                staff.setPhoto(savePhotoFile(staffRequest.getPhotoFile()));
            }
            if (staffRequest.getPassword().isEmpty()) {
                staff.setPassword(staffRepo.getPasswordById(staff.getId()));
            } else {
                staff.setPassword(passwordEncoder.encode(staffRequest.getPassword()));
            }

            log.info("method updateStaff -> save Staff entity");
            staffRepo.save(staff);
            log.info("method updateStaff -> exit");
        }

    }

    private String savePhotoFile(MultipartFile file) {
        String filename;
        try {
            filename = fileUtil.saveImage(file);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createStaff(StaffRequest staffRequest) {
        log.info("method createStaff -> start update Staff");
        if (staffRequest != null) {
            log.info("method createStaff -> staffRequest isPresent");
            Staff staff = StaffMapper.MAPPER.requestToStaff(staffRequest);
            staff.setPassword(passwordEncoder.encode(staffRequest.getPassword()));
            if (staffRequest.getPhotoFile() != null && !staffRequest.getPhotoFile().isEmpty()) {
                log.info("method createStaff -> save upload image");
                staff.setPhoto(savePhotoFile(staffRequest.getPhotoFile()));
            }
            log.info("method createStaff -> save Staff entity");
            staffRepo.save(staff);
            log.info("method createStaff -> exit");
        }
    }

    public boolean deleteStaffById(Long staffId) throws AuthenticationException {
        Optional<Staff> byId = staffRepo.findById(staffId);
        if (byId.isPresent()) {
            Staff staff = byId.get();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication.getName().equals(staff.getUsername())) {
                log.info("deleteStaffById() -> Can`t delete current staff");
                throw new AuthenticationException("Can`t delete current staff");
            } else if (staff.getStaffRole().equals(Role.ROLE_ADMIN)) {
                log.info("deleteStaffById() -> Admin staff can`t delete");
                throw new AuthenticationException("Admin staff can`t delete");
            }
            staffRepo.delete(staff);
            return true;
        }
        return false;
    }

    public boolean changeStaffStatus(Long staffId, Boolean status) {
        if (staffId != null && status != null) {
            Optional<Staff> byId = staffRepo.findById(staffId);
            if (byId.isPresent()) {
                Staff staff = byId.get();
                staff.setStatus(status);
                staffRepo.save(staff);
                return true;
            }
            return false;
        }
        return false;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("method loadUserByUsername -> find user by email: " + username);
        Staff staff = staffRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User %s not found", username)
        ));
        log.info("method loadUserByUsername -> user by email: " + staff);
        return new User(
                staff.getUsername(),
                staff.getPassword(),
                staff.getAuthorities()
        );
    }

    public Staff getStaffByUsername(String currUsername) {
        Optional<Staff> byEmail = staffRepo.findByEmail(currUsername);
        return byEmail.orElseThrow(EntityNotFoundException::new);
    }
}

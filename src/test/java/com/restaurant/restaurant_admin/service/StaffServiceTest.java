package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.Staff;
import com.restaurant.restaurant_admin.entity.enums.Role;
import com.restaurant.restaurant_admin.model.staff.StaffRequest;
import com.restaurant.restaurant_admin.model.staff.StaffResponse;
import com.restaurant.restaurant_admin.repository.StaffRepo;
import com.restaurant.restaurant_admin.utils.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock
    private StaffRepo staffRepo;
    @Mock
    private UploadFileUtil fileUtil;
    private PasswordEncoder passwordEncoder;
    private StaffService staffService;

    private Staff staff;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        staffService = new StaffService(staffRepo, fileUtil, passwordEncoder);
        staff = new Staff();
        staff.setId(1L);
        staff.setStaffRole(Role.ROLE_ADMIN);
        staff.setStatus(Boolean.TRUE);
        staff.setEmail("staff@gmail.com");
        staff.setFirstName("FirstName");
        staff.setLastName("LastName");
        staff.setPassword(passwordEncoder.encode("admin"));
    }

    @Test
    void getStaffOnPage() {
        Pageable pageable = PageRequest.ofSize(5);
        List<Staff> staffList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Staff staffTemp = new Staff();
            staffTemp.setId((long) i);
            staffTemp.setStaffRole(Role.ROLE_ADMIN);
            staffTemp.setStatus(Boolean.TRUE);
            staffTemp.setEmail("staff" + i + "@gmail.com");
            staffTemp.setFirstName("FirstName");
            staffTemp.setLastName("LastName");
            staffTemp.setPassword(passwordEncoder.encode("admin" + i));
            staffList.add(staffTemp);
        }
        Page<Staff> staffPage = new PageImpl<>(staffList, pageable, staffList.size());
        when(staffRepo.findAll(any(Pageable.class))).thenReturn(staffPage);
        Page<StaffResponse> staffOnPage = staffService.getStaffOnPage(pageable.getPageNumber(), pageable.getPageSize());
        List<StaffResponse> content = staffOnPage.getContent();
        assertFalse(content.isEmpty());
        assertEquals(staffList.size(), content.size());
    }

    @Test
    void getStaffPageOnSearch() {
        Pageable pageable = PageRequest.ofSize(5);
        List<Staff> staffList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Staff staffTemp = new Staff();
            staffTemp.setId((long) i);
            staffTemp.setStaffRole(Role.ROLE_ADMIN);
            staffTemp.setStatus(Boolean.TRUE);
            staffTemp.setEmail("staff" + i + "@gmail.com");
            staffTemp.setFirstName("FirstName");
            staffTemp.setLastName("LastName");
            staffTemp.setPassword(passwordEncoder.encode("admin" + i));
            staffList.add(staffTemp);
        }
        Page<Staff> staffPage = new PageImpl<>(staffList, pageable, staffList.size());
        when(staffRepo.findAll(ArgumentMatchers.<Specification>any(Specification.class), any(Pageable.class))).thenReturn(staffPage);
        Page<StaffResponse> staffOnPage =
                staffService.getStaffPageOnSearch("", pageable.getPageNumber(), pageable.getPageSize());
        List<StaffResponse> content = staffOnPage.getContent();
        assertFalse(content.isEmpty());
        assertEquals(staffList.size(), content.size());
    }

    @Test
    void getStaffById() {
        when(staffRepo.findById(staff.getId())).thenReturn(Optional.of(staff));
        StaffResponse staffById = staffService.getStaffById(staff.getId());
        assertEquals(staff.getId(), staffById.getStaffId());
        assertEquals(staff.getStaffRole(), staffById.getStaffRole());
        assertEquals(staff.getUsername(), staffById.getEmail());
    }

    @Test
    void getStaffById_ifStaffNotFound() {
        when(staffRepo.findById(staff.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                staffService.getStaffById(staff.getId()));
    }

    @Test
    void updateStaff() {
        StaffRequest staffRequest = new StaffRequest();
        staffRequest.setPhotoFile(new MockMultipartFile(
                "fileName",
                "originalFilename",
                "image/png",
                "fileContent".getBytes()));
        staffRequest.setPassword("adminNew");
        staffService.updateStaff(staffRequest);
        ArgumentCaptor<Staff> staffArgumentCaptor = ArgumentCaptor.forClass(Staff.class);
        verify(staffRepo).save(staffArgumentCaptor.capture());

        assertTrue(passwordEncoder.matches(staffRequest.getPassword(),
                staffArgumentCaptor.getValue().getPassword()));
    }

    @Test
    void updateStaff_ifRequestWithoutPassword() {
        StaffRequest staffRequest = new StaffRequest();
        staffRequest.setPhotoFile(new MockMultipartFile(
                "fileName",
                "originalFilename",
                "image/png",
                "fileContent".getBytes()));
        staffRequest.setPassword("");
        staffRequest.setStaffId(staff.getId());
        when(staffRepo.getPasswordById(staff.getId())).thenReturn(staff.getPassword());
        staffService.updateStaff(staffRequest);
        ArgumentCaptor<Staff> staffArgumentCaptor = ArgumentCaptor.forClass(Staff.class);
        verify(staffRepo).save(staffArgumentCaptor.capture());

        assertEquals(staff.getPassword(), staffArgumentCaptor.getValue().getPassword());
    }

    @Test
    void updateStaff_ifSaveFileCatchException() throws IOException {
        StaffRequest staffRequest = new StaffRequest();
        staffRequest.setPhotoFile(new MockMultipartFile(
                "fileName",
                "originalFilename",
                "image/png",
                "fileContent".getBytes()));
        staffRequest.setPassword("adminNew");
        when(fileUtil.saveImage(any(MultipartFile.class))).thenThrow(IOException.class);
        assertThrows(RuntimeException.class, () -> staffService.updateStaff(staffRequest));
    }

    @Test
    void createStaff() {
        StaffRequest staffRequest = new StaffRequest();
        staffRequest.setPhotoFile(new MockMultipartFile(
                "fileName",
                "originalFilename",
                "image/png",
                "fileContent".getBytes()));
        staffRequest.setPassword(staff.getPassword());
        staffRequest.setStaffRole(staff.getStaffRole());
        staffRequest.setEmail(staff.getEmail());
        staffRequest.setStatus(staff.getStatus());

        staffService.createStaff(staffRequest);
        ArgumentCaptor<Staff> staffArgumentCaptor = ArgumentCaptor.forClass(Staff.class);
        verify(staffRepo).save(staffArgumentCaptor.capture());

        Staff value = staffArgumentCaptor.getValue();
        assertTrue(passwordEncoder.matches(staffRequest.getPassword(),
                value.getPassword()));
        assertEquals(staff.getEmail(), value.getEmail());
        assertEquals(staff.getStaffRole(), value.getStaffRole());
    }

    @Test
    void deleteStaffById_ifDeleteCurrentStaff() {
        when(staffRepo.findById(staff.getId())).thenReturn(Optional.of(staff));
        SecurityContext contextHolder = new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken(staff.getEmail(), staff.getPassword()));
        SecurityContextHolder.setContext(contextHolder);
        assertThrows(AuthenticationException.class, () -> staffService.deleteStaffById(staff.getId()));
    }

    @Test
    void deleteStaffById_Success() throws AuthenticationException {
        staff.setStaffRole(Role.ROLE_ACCOUNTANT);
        when(staffRepo.findById(staff.getId())).thenReturn(Optional.of(staff));
        SecurityContext contextHolder = new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken("ustaff@Gmail.com", staff.getPassword()));
        SecurityContextHolder.setContext(contextHolder);
        boolean isDeleted = staffService.deleteStaffById(staff.getId());
        assertTrue(isDeleted);
    }

    @Test
    void deleteStaffById_ifDeleteAdmin() {
        when(staffRepo.findById(staff.getId())).thenReturn(Optional.of(staff));
        SecurityContext contextHolder = new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken("ustaff@Gmail.com", staff.getPassword()));
        SecurityContextHolder.setContext(contextHolder);
        assertThrows(AuthenticationException.class, () -> staffService.deleteStaffById(staff.getId()));
    }

    @Test
    void deleteStaffById_ifStaffNotFound() throws AuthenticationException {
        when(staffRepo.findById(staff.getId())).thenReturn(Optional.empty());
        boolean isDeleted = staffService.deleteStaffById(staff.getId());
        assertFalse(isDeleted);
    }

    @Test
    void changeStaffStatus_ifStaffNotFound() {
        when(staffRepo.findById(staff.getId())).thenReturn(Optional.empty());
        boolean changed = staffService.changeStaffStatus(staff.getId(), true);
        assertFalse(changed);
    }

    @Test
    void changeStaffStatus() {
        when(staffRepo.findById(staff.getId())).thenReturn(Optional.of(staff));
        boolean changed = staffService.changeStaffStatus(staff.getId(), true);
        ArgumentCaptor<Staff> staffArgumentCaptor = ArgumentCaptor.forClass(Staff.class);
        verify(staffRepo).save(staffArgumentCaptor.capture());
        assertTrue(changed);
        assertTrue(staffArgumentCaptor.getValue().getStatus());
    }

    @Test
    void changeStaffStatus_ifStatusIsNull() {
        boolean changed = staffService.changeStaffStatus(staff.getId(), null);
        assertFalse(changed);
    }

    @Test
    void loadUserByUsername() {
        when(staffRepo.findByEmail(staff.getEmail())).thenReturn(Optional.of(staff));
        UserDetails userDetails = staffService.loadUserByUsername(staff.getEmail());
        assertEquals(staff.getEmail(), userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_ifStaffNotFound() {
        when(staffRepo.findByEmail(staff.getEmail())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () ->
                staffService.loadUserByUsername(staff.getEmail()));
    }

    @Test
    void getStaffByUsername_ifStaffNotFound() {
        when(staffRepo.findByEmail(staff.getEmail())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                staffService.getStaffByUsername(staff.getEmail()));
    }

    @Test
    void getStaffByUsername() {
        when(staffRepo.findByEmail(staff.getEmail())).thenReturn(Optional.of(staff));
        Staff staffByUsername = staffService.getStaffByUsername(staff.getEmail());
        assertEquals(staff.getId(), staffByUsername.getId());
    }
}
package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/users")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class UserController {

    UserService userService;
    
    @PostMapping
    @ApiMessage("Create new users")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User postManUser) throws IdInvalidException{
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.handleCreateUser(postManUser));
    }

    @GetMapping
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser(
        @Filter Specification<User> spec,
        Pageable pageable
    ){
        return ResponseEntity.ok(userService.handleFetchAllUser(spec,pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch users by id")
    public ResponseEntity<ResUserDTO> fetchUserById(@PathVariable("id") Long userId) throws IdInvalidException{
        return ResponseEntity.ok(userService.handleFetchUserById(userId));
    }

    @PutMapping
    @ApiMessage("Update users")
    public ResponseEntity<ResUpdateUserDTO> updateUserById(@RequestBody User userPostMan) throws IdInvalidException{
        return ResponseEntity.ok().body(userService.handleUpdateUser(userPostMan));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete users")
    public ResponseEntity<Void> deletedUser(@PathVariable("id") Long userId) throws IdInvalidException{
        userService.handleDeleteUserById(userId);
        return ResponseEntity.ok(null);
    }

}
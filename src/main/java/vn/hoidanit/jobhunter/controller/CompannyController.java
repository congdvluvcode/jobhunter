package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/companies")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class CompannyController {

    CompanyService companyService;

    @PostMapping
    public ResponseEntity<Company> createNewCompany(@Valid @RequestBody Company company){
        Company newCompany = companyService.handleCreateCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @GetMapping
    @ApiMessage("Fetch all companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompany(
        @Filter Specification<Company> spec,
        Pageable pageable
    ){
        return ResponseEntity.ok(companyService.fetchAllCompany(spec,pageable));
    }

    @PutMapping
    @ApiMessage("Update companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company){
        return ResponseEntity.ok(companyService.handleUpdateCompany(company));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete companies")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id){
        companyService.handleDeleteCompany(id);
        return ResponseEntity.ok().body(null);
    }
}

package vn.hoidanit.jobhunter.service;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class CompanyService {

    CompanyRepository companyRepository;

    public Company handleCreateCompany(Company company){
        return companyRepository.save(company);
    }

    public Company handleUpdateCompany(Company companyUpdate){
        Optional<Company> optional = companyRepository.findById(companyUpdate.getId());
        if(optional.isPresent()){
            Company company = optional.get();
            company.setAddress(companyUpdate.getAddress());
            company.setDescription(companyUpdate.getDescription());
            company.setLogo(companyUpdate.getLogo());
            company.setName(companyUpdate.getName());
            return companyRepository.save(company);
        }
        return null;
    }

    public ResultPaginationDTO fetchAllCompany(Specification<Company> specification,Pageable pageable){
        Page<Company> page = companyRepository.findAll(specification,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();

        Meta meta = new Meta();
        meta.setCurrent(page.getNumber()+ 1);
        meta.setPageSize(page.getSize());

        meta.setTotal(page.getTotalElements());
        meta.setPages(page.getTotalPages());
        
        rs.setMeta(meta);
        rs.setResult(page.getContent());
        return rs;
    }

    public void handleDeleteCompany(Long id){
        companyRepository.deleteById(id);
    }
    
}

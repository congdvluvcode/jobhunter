package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.AccessLevel;
import org.springframework.data.jpa.domain.Specification;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.dto.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class UserService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    public ResCreateUserDTO handleCreateUser(User user) throws IdInvalidException{
        if(userRepository.existsByEmail(user.getEmail())){
            throw new IdInvalidException("Email " + user.getEmail() + " đã tồn tại, vui lòng sử dụng email khác");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User newUser = userRepository.save(user);
        ResCreateUserDTO res = ResCreateUserDTO.builder()
                                .id(newUser.getId())
                                .address(newUser.getAddress())
                                .name(newUser.getName())
                                .email(newUser.getEmail())
                                .age(newUser.getAge())
                                .gender(newUser.getGender())
                                .createdAt(newUser.getCreatedAt())
                                .build();
        return res;
    }

    public ResUpdateUserDTO handleUpdateUser(User userUpdate) throws IdInvalidException{
        Optional<User> uOptional = userRepository.findById(userUpdate.getId());
        if(uOptional.isEmpty()){
            throw new IdInvalidException("User với id = " + userUpdate.getId() + " không tồn tại");
        }
        User user = uOptional.get();
        user.setName(userUpdate.getName());
        user.setGender(userUpdate.getGender());
        user.setAddress(userUpdate.getAddress());
        user.setAge(userUpdate.getAge());
        User newUser = userRepository.save(user);
        ResUpdateUserDTO res = ResUpdateUserDTO.builder()
                                .id(newUser.getId())
                                .address(newUser.getAddress())
                                .name(newUser.getName())
                                .email(newUser.getEmail())
                                .age(newUser.getAge())
                                .gender(newUser.getGender())
                                .updateAt(newUser.getUpdatedAt())
                                .build();
        return res;
    }

    public ResultPaginationDTO handleFetchAllUser(Specification<User> specification, Pageable pageable){
        Page<User> page = userRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setCurrent(page.getNumber() + 1);
        meta.setPageSize(page.getSize());

        meta.setTotal(page.getTotalElements());
        meta.setPages(page.getTotalPages());
        
        rs.setMeta(meta);
        List<ResUserDTO> list = page.getContent()
            .stream().map(item -> new ResUserDTO(
                item.getId(),
                item.getEmail(),
                item.getName(),
                item.getGender(),
                item.getAddress(),
                item.getAge(),
                item.getUpdatedAt(),
                item.getCreatedAt()
            )).collect(Collectors.toList());

        rs.setResult(list);
        return rs;
    }

    public ResUserDTO handleFetchUserById(Long id) throws IdInvalidException{
        Optional<User> uOptional = userRepository.findById(id);
        if(uOptional.isEmpty()){
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        User newUser = uOptional.get();
        ResUserDTO res = ResUserDTO.builder()
                                .id(newUser.getId())
                                .address(newUser.getAddress())
                                .name(newUser.getName())
                                .email(newUser.getEmail())
                                .age(newUser.getAge())
                                .gender(newUser.getGender())
                                .updateAt(newUser.getUpdatedAt())
                                .createAt(newUser.getCreatedAt())
                                .build();
        return res;
    }

    public User handleFetchUserByEmail(String email){
        return userRepository.findByEmail(email);
    }


    public void handleDeleteUserById(Long id) throws IdInvalidException{
        Optional<User> uOptional = userRepository.findById(id);
        if(uOptional.isEmpty()){
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        userRepository.deleteById(id);;
    }
    
    public void updateTokenUser(String token,String email){
        User cUser = handleFetchUserByEmail(email);
        if(cUser!=null){
            cUser.setRefreshToken(token);
            userRepository.save(cUser);
        }
    }

    public User getUserByEmailAndRefreshToken(String email, String token){
        return userRepository.findByEmailAndRefreshToken(email, token);
    }
}

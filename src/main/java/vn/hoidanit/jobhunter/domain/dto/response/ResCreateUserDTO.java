package vn.hoidanit.jobhunter.domain.dto.response;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ResCreateUserDTO {
    
    Long id;
    String name;
    String email;
    GenderEnum gender;
    String address;
    Integer age;
    Instant createdAt;

}
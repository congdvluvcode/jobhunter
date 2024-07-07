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
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResUpdateUserDTO {
    
    Long id;
    String name;
    String email;
    GenderEnum gender;
    String address;
    Integer age;
    Instant updateAt;
    
}

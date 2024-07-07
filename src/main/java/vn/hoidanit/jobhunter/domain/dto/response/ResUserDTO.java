package vn.hoidanit.jobhunter.domain.dto.response;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResUserDTO {
 
    Long id;
    String name;
    String email;
    GenderEnum gender;
    String address;
    Integer age;
    Instant updateAt;
    Instant createAt;

}

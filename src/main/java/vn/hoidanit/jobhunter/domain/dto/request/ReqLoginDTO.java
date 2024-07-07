package vn.hoidanit.jobhunter.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ReqLoginDTO {

    @NotBlank(message = "Username không được để trống")
    String username;

    @NotBlank(message = "Password không được để trống")
    String password;

}

package vn.hoidanit.jobhunter.domain.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Meta {

    int current;//trang hiện tại
    int pageSize; //số lượng bản ghi đã lấy
    int pages; //tổng số trang với điều kiện query
    Long total; // tổng số phần tử (số bản ghi)

}

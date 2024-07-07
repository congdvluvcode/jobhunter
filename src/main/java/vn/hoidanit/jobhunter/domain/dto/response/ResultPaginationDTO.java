package vn.hoidanit.jobhunter.domain.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResultPaginationDTO {

    Meta meta;
    Object result;
    
    @Getter
    @Setter
    public static class  Meta {
    
        int current;//trang hiện tại
        int pageSize; //số lượng bản ghi đã lấy
        int pages; //tổng số trang với điều kiện query
        Long total; // tổng số phần tử (số bản ghi)
        
    }
}

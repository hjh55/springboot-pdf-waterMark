package org.example.pdf.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Accessors(chain = true)
public class Pdf implements Serializable {

    @NotBlank(message = "name不能为空")
    String name;//姓名

    @NotBlank(message = "idCardNum不能为空")
    @Size(min = 18, max = 18, message = "身份证号必须为18位")
    String idCardNum;

    @NotBlank(message = "orderNum不能为空")
    String orderNum;//订单号，作为文件名

    @NotBlank(message = "time不能为空")
    String time;
}

package com.prgrms.coretime.common.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel
public class MyPageable {
  @ApiModelProperty(value = "페이지 번호", example = "1")
  private Integer page;

  @ApiModelProperty(value = "페이지 크기", allowableValues = "range[0, 100]", example = "20")
  private Integer size;

  @ApiModelProperty(value = "정렬(컬럼명, ASC|DESC)")
  private List<String> sort;
}

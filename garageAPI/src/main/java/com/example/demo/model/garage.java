package com.example.demo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@ApiModel(value = "Garage Api model documentation", description = "Model")
@AllArgsConstructor
@NoArgsConstructor
public class garage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(value = "Unique id field of Islem object")
    private Integer id;
    @ApiModelProperty(value = "tip field of Islem object")
    private int tip;
    @ApiModelProperty(value = "plaka field of Islem object")
    private String plaka;
    @ApiModelProperty(value = "alan field of Islem object")
    private int alan;
}

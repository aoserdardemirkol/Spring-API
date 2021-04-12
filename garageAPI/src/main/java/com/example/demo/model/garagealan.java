package com.example.demo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@ApiModel(value = "Garage Alan Api model documentation", description = "Model")
@AllArgsConstructor
@NoArgsConstructor
public class garagealan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(value = "Unique id field of Islem object")
    private Integer id;
    @ApiModelProperty(value = "tip field of Islem object")
    private int alan;
}

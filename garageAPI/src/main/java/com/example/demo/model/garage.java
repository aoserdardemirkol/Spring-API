package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.enterprise.inject.Model;
import javax.persistence.*;

// Veritabanında bulunan garage tablosunun verileri belirlendi.
@Data
@Entity
@ApiModel(value = "Garage Api model documentation", description = "Model")
// @AllArgsConstructor ve @NoArgsConstructor ile yapıcı metotlar oluşturuldu.
@AllArgsConstructor
@NoArgsConstructor
@Model
public class garage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // @JsonIgnore ile döndürülen değerde gösterilmesi istenilmeyen değerler engellendi.
    @JsonIgnore
    @ApiModelProperty(value = "Unique id field of Islem object")
    private Integer id;
    @ApiModelProperty(value = "tip field of Islem object")
    private int tip;
    @ApiModelProperty(value = "plaka field of Islem object")
    private String plaka;
    @JsonIgnore
    @ApiModelProperty(value = "alan field of Islem object")
    private int alan;
}


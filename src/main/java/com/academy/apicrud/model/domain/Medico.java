package com.academy.apicrud.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("medico")
public class Medico {

    @Id
    private Long id;

    private String nombre;

    @Column("especialidad_id")
    private Long especialidadId;
}
package com.academy.apicrud.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMedico {
    @Column("id")
    private Long id;

    @Column("nombreMedico")
    private String nombreMedico;

    @Column("especialidadId")
    private Long especialidadId;

    @Column("nombreEspecialidad")
    private String nombreEspecialidad;

    // Getters y setters
}
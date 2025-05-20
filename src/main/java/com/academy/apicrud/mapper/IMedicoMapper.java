package com.academy.apicrud.mapper;

import com.academy.apicrud.model.domain.Medico;
import com.academy.apicrud.model.dto.MedicoDto;
import com.academy.apicrud.model.response.ResponseMedico;
import reactor.core.publisher.Mono;

public interface IMedicoMapper {
    MedicoDto toDto(Medico medico);
    Medico toEntity(MedicoDto medicoDto);

    // Métodos adicionales para ResponseMedico
    ResponseMedico toResponseMedico(Medico medico, String nombreEspecialidad);
    Mono<ResponseMedico> toResponseMedicoMono(Medico medico, Mono<String> nombreEspecialidad);
}
package com.academy.apicrud.mapper.impl;

import com.academy.apicrud.mapper.IMedicoMapper;
import com.academy.apicrud.model.domain.Medico;
import com.academy.apicrud.model.dto.MedicoDto;
import com.academy.apicrud.model.response.ResponseMedico;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MedicoMapperImpl implements IMedicoMapper {

    @Override
    public MedicoDto toDto(Medico medico) {
        if (medico == null) {
            return null;
        }

        return MedicoDto.builder()
                .id(medico.getId())
                .nombre(medico.getNombre())
                .especialidadId(medico.getEspecialidadId())
                .build();
    }

    @Override
    public Medico toEntity(MedicoDto medicoDto) {
        if (medicoDto == null) {
            return null;
        }

        Medico medico = new Medico();
        medico.setId(medicoDto.getId());
        medico.setNombre(medicoDto.getNombre());
        medico.setEspecialidadId(medicoDto.getEspecialidadId());

        return medico;
    }

    @Override
    public ResponseMedico toResponseMedico(Medico medico, String nombreEspecialidad) {
        if (medico == null) {
            return null;
        }

        ResponseMedico response = new ResponseMedico();
        response.setId(medico.getId());
        response.setNombreMedico(medico.getNombre());
        response.setEspecialidadId(medico.getEspecialidadId());
        response.setNombreEspecialidad(nombreEspecialidad);

        return response;
    }

    @Override
    public Mono<ResponseMedico> toResponseMedicoMono(Medico medico, Mono<String> nombreEspecialidad) {
        if (medico == null) {
            return Mono.empty();
        }

        return nombreEspecialidad.map(nombre -> {
            ResponseMedico response = new ResponseMedico();
            response.setId(medico.getId());
            response.setNombreMedico(medico.getNombre());
            response.setEspecialidadId(medico.getEspecialidadId());
            response.setNombreEspecialidad(nombre);
            return response;
        });
    }
}
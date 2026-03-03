package com.jonassavas.spring_task_api.mappers.impl.user;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.jonassavas.spring_task_api.domain.dto.user.UserResponseDto;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;

@Component
public class UserResponseMapper implements Mapper<UserEntity, UserResponseDto>{
    private ModelMapper modelMapper;

    public UserResponseMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;

        // Skip taskGroup when mapping DTO -> Entity
        // this.modelMapper.typeMap(UserRegisterRequestDtoDto.class, UserEntity.class)
        //         .addMappings(mapper -> mapper.skip(UserEntity::));

    }

    @Override
    public UserResponseDto mapTo(UserEntity userEntity){
        UserResponseDto dto = modelMapper.map(userEntity, UserResponseDto.class);
        return dto;
    }

    @Override
    public UserEntity mapFrom(UserResponseDto userResponseDto){
        return modelMapper.map(userResponseDto, UserEntity.class);
    }
}
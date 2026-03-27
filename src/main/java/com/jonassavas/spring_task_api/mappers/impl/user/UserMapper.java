package com.jonassavas.spring_task_api.mappers.impl.user;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.jonassavas.spring_task_api.domain.dto.user.UserDto;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;

@Component
public class UserMapper implements Mapper<UserEntity, UserDto>{
    private ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;

        // Skip taskGroup when mapping DTO -> Entity
        // this.modelMapper.typeMap(UserRegisterRequestDtoDto.class, UserEntity.class)
        //         .addMappings(mapper -> mapper.skip(UserEntity::));

    }

    @Override
    public UserDto mapTo(UserEntity userEntity){
        UserDto dto = modelMapper.map(userEntity, UserDto.class);
        return dto;
    }

    @Override
    public UserEntity mapFrom(UserDto userResponseDto){
        return modelMapper.map(userResponseDto, UserEntity.class);
    }
}
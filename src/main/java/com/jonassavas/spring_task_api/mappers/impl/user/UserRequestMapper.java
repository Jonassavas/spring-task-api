package com.jonassavas.spring_task_api.mappers.impl.user;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.jonassavas.spring_task_api.domain.dto.user.UserRequestDto;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;

@Component
public class UserRequestMapper implements Mapper<UserEntity, UserRequestDto>{
    private ModelMapper modelMapper;

    public UserRequestMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;

        // Skip taskGroup when mapping DTO -> Entity
        // this.modelMapper.typeMap(UserRegisterRequestDtoDto.class, UserEntity.class)
        //         .addMappings(mapper -> mapper.skip(UserEntity::));

    }

    @Override
    public UserRequestDto mapTo(UserEntity userEntity){
        UserRequestDto dto = modelMapper.map(userEntity, UserRequestDto.class);
        return dto;
    }

    @Override
    public UserEntity mapFrom(UserRequestDto userRequestDto){
        return modelMapper.map(userRequestDto, UserEntity.class);
    }
}

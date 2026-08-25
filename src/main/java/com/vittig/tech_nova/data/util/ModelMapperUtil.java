package com.vittig.tech_nova.data.util;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor()
public class ModelMapperUtil {
    private final ModelMapper modelMapper;
    public ModelMapper modelMapper() {
        return modelMapper;
    }
    public <S, T> T map(S source, Class<T> targetClass) {
        return modelMapper().map(source, targetClass);
    }

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source
                .stream()
                .map(element -> modelMapper().map(element, targetClass))
                .toList();
    }
}

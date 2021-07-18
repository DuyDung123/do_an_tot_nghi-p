package com.dinhvandung.smarthome.SEVICE;

import com.dinhvandung.smarthome.DTO.CategoryDTO;

import java.util.ArrayList;
import java.util.List;

public class CategoryService {

    CategoryDTO DTO = new CategoryDTO(1,"Đèn");
    CategoryDTO DTO1 = new CategoryDTO(2,"Quạt");
    CategoryDTO DTO2 = new CategoryDTO(3,"Nhiệt độ và độ ẩm");

    public List<CategoryDTO> findAll(){
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        categoryDTOS.add(DTO);
        categoryDTOS.add(DTO1);
        categoryDTOS.add(DTO2);
        return categoryDTOS;
    }

}

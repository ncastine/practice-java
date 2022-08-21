package org.example;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface EmployeeMapper {
	@Mapping(source = "id", target = "uuid")
	EmployeeDto toDto(Employee employee);

	@Mapping(source = "id", target = "uuid")
	EmployeeDto fromRecord(EmployeeRecord employee);
}

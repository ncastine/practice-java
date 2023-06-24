package practice.mapStruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeMapperTest {
	@Test
	void testMapping() {
		EmployeeMapper mapper = Mappers.getMapper(EmployeeMapper.class);
		Employee original = new Employee();
		original.department = "Test 1";
		original.id = UUID.randomUUID().toString();
		original.name = "Jane Doe";
		EmployeeDto dto = mapper.toDto(original);
		assertEquals(original.name, dto.name);
		assertEquals(original.department, dto.department);
		assertEquals(original.id, dto.uuid);
		Gson gson = new Gson();
		System.out.println(gson.toJson(original));
		System.out.println(gson.toJson(dto));
	}
	@Test
	void testRecordMapping() throws Exception {
		EmployeeMapper mapper = Mappers.getMapper(EmployeeMapper.class);
		final String id = UUID.randomUUID().toString();
		final String department = "Test 1";
		final String name = "Jane Doe";
		EmployeeRecord record = new EmployeeRecord(id, name, department);
		EmployeeDto dto = mapper.fromRecord(record);
		assertEquals(name, dto.name);
		assertEquals(department, dto.department);
		assertEquals(id, dto.uuid);
		Gson gson = new Gson();
		System.out.println(gson.toJson(record));
		System.out.println(gson.toJson(dto));
		System.out.println(new ObjectMapper().writeValueAsString(record));
	}
	@Test
	public void testJacksonRecordDeserialize() {
		final ObjectMapper mapper = new ObjectMapper();
		final String id = UUID.randomUUID().toString();
		final String department = "Test 1";
		final String name = "Jane Doe";
		ObjectNode json = mapper.createObjectNode();
		json.put("id", id);
		json.put("department", department);
		json.put("name", name);
		// Ensure Jackson can populate the immutable record class
		EmployeeRecord record = mapper.convertValue(json, EmployeeRecord.class);
		assertEquals(name, record.name());
		assertEquals(department, record.department());
		assertEquals(id, record.id());
	}
}

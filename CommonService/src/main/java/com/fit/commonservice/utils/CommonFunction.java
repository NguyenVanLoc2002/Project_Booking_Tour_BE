package com.fit.commonservice.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fit.commonservice.common.ValidateException;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@NoArgsConstructor
public class CommonFunction {

    @SneakyThrows
    public static void jsonValidate(InputStream inputStream, String json){
        JsonSchema schema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(inputStream);
        ObjectMapper om = new ObjectMapper();
        JsonNode jsonNode = om.readTree(json); //Đọc các thuộc tính của file JSON
        Set<ValidationMessage> errors = schema.validate(jsonNode);
        Map<String, String> errorMap = new HashMap<>();
        for (ValidationMessage error : errors) {
            if (errorMap.containsKey(formatStringValidate(error.getPath()))) {
                String message = errorMap.get(formatStringValidate(error.getPath()));
                errorMap.put(formatStringValidate(error.getPath()), message + ", "+ formatStringValidate(error.getMessage()));
            }else {
                errorMap.put(formatStringValidate(error.getPath()), formatStringValidate(error.getMessage()));
            }
        }
        if(!errorMap.isEmpty()){
            throw new ValidateException("RQ01", errorMap, HttpStatus.BAD_REQUEST);
        }
    }

    public static String formatStringValidate(String message){
        return message.replaceAll("\\$.","");
    }
}

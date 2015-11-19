package cn.cqupt.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fubingdao on 14/11/29.
 */
public class JacksonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static String serialize(Object data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (IOException e) {
            logger.error("serialize data error", e);
            return null;
        }
    }

    public static <T> T deSerialize(String content, Class<T> clazz) {
        try {
            return mapper.readValue(content, clazz);
        } catch (IOException e) {
            logger.error("deserialize object " + content + "error: {}", e);
            return null;
        }
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        try {
            return mapper.convertValue(fromValue, toValueType);
        } catch (IllegalArgumentException e) {
            logger.error("convert object " + fromValue + "error: {}", e);
            return null;
        }
    }

    public static Map<?, ?> jsonToMap(String json) throws IOException {
        return mapper.readValue(json, HashMap.class);
    }

}

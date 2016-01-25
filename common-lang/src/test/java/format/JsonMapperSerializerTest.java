package format;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jingcai.apps.common.lang.format.JsonMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by lejing on 16/1/23.
 */
public class JsonMapperSerializerTest {
	@Test
	public void test1() {
		TestVo testVo = new TestVo();
		testVo.setUsertype(UserType.admin);
		testVo.setChannel(Channel.android);
		JsonMapper instance = JsonMapper.getInstance();

		// 进行HTML解码。
		instance.registerModule(new SimpleModule().addSerializer(UserType.class, new Serialize()));
		instance.registerModule(new SimpleModule().addSerializer(Channel.class, new Serialize2()));

		System.out.println(instance.toJson(testVo));
	}/*
	new JsonSerializer<String>() {
			@Override
			public void serialize(String value, JsonGenerator jgen,
								  SerializerProvider provider) throws IOException,
					JsonProcessingException {
				jgen.writeString(StringEscapeUtils.unescapeHtml4(value));
			}
		}*/

	@Getter
	@Setter
	class TestVo {
		private UserType usertype;
		private Channel channel;
	}

	enum UserType {
		student, admin;

		public String val() {
			return String.valueOf(ordinal() + 10);
		}
	}

	enum Channel {
		web, android, ios;

		public String val() {
			return String.valueOf(ordinal() + 10);
		}
	}

	class Serialize extends JsonSerializer<UserType> {
		@Override
		public void serialize(UserType value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
			jgen.writeString(value.val());
		}
	}

	class Serialize2 extends JsonSerializer<Channel> {
		@Override
		public void serialize(Channel value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
			jgen.writeString(value.val());
		}
	}
}

package com.jingcai.apps.common.lang.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoProxy {
	private Kryo target;

	public KryoProxy() {
		target = new Kryo();
	}

	public void setRegistrationRequired(boolean registrationRequired){
		target.setRegistrationRequired(registrationRequired);
	}
	public void setReferences(boolean references){
		target.setReferences(references);
	}

	public void register(Class aClass, Serializer serializer, Integer id) {
		target.register(aClass, serializer, id);
	}

	public byte[] writeClassAndObject(Object object) {
		Output output = new Output(256, -1);
		target.writeClassAndObject(output, object);
		return output.toBytes();
	}

	public Object readClassAndObject(byte[] bytes) {
		return target.readClassAndObject(new Input(bytes));
	}
}

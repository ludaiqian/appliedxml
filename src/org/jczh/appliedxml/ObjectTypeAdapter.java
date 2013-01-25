package org.jczh.appliedxml;

import java.io.IOException;

import com.google.gsoncode.internal.TypeToken;

final class ObjectTypeAdapter extends TypeAdapter<Object> {
	public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
		@SuppressWarnings("unchecked")
		public <T> TypeAdapter<T> create(Serializer context, TypeToken<T> type) {
			if (type.getRawType() == Object.class) {
				return (TypeAdapter<T>) new ObjectTypeAdapter(context);
			}
			return null;
		}
	};

	private final Serializer context;

	ObjectTypeAdapter(Serializer context) {
		this.context = context;
	}

	@Override
	public Object read(XmlReader in) throws IOException {
		EventNode token = in.peek();
		if (token.isText()) {
			return token.getValue();
		}
		throw new IllegalStateException();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void write(XmlWriter out, Object value) throws IOException {
		if (value == null) {
			return;
		}
		TypeAdapter typeAdapter = context.getAdapter(value.getClass());
		if (typeAdapter instanceof ObjectTypeAdapter) {
			out.writeText(value.toString());
		} else {
			typeAdapter.write(out, value);
		}
	}
}

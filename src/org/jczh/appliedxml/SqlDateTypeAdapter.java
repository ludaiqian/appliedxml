package org.jczh.appliedxml;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jczh.appliedxml.PullReader.Text;

import com.google.gsoncode.internal.TypeToken;

/**
 * Adapter for java.sql.Date. Although this class appears stateless, it is not.
 * DateFormat captures its time zone and locale when it is created, which gives
 * this class state. DateFormat isn't thread safe either, so this class has to
 * synchronize its read and write methods.
 */

final class SqlDateTypeAdapter extends TypeAdapter<java.sql.Date> {
	public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
		@SuppressWarnings("unchecked")
		// we use a runtime check to make sure the 'T's equal
		public <T> TypeAdapter<T> create(Serializer context, TypeToken<T> typeToken) {
			return typeToken.getRawType() == java.sql.Date.class ? (TypeAdapter<T>) new SqlDateTypeAdapter() : null;
		}
	};

	private final DateFormat format = new SimpleDateFormat("MMM d, yyyy");

	@Override
	public synchronized java.sql.Date read(XmlReader in) throws IOException {
		if (in.peek() == null || !in.peek().isText()) {
			return null;
		}
		Text text = (Text) in.next();
		try {
			final long utilDate = format.parse(text.getValue()).getTime();
			return new java.sql.Date(utilDate);
		} catch (ParseException e) {
			throw new NodeException(e);
		}
	}

	@Override
	public synchronized void write(XmlWriter out, java.sql.Date value) throws IOException {
		out.writeText(value == null ? "" : format.format(value));
	}
}

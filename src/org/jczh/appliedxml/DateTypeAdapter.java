

package org.jczh.appliedxml;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.jczh.appliedxml.PullReader.Text;

import com.google.gsoncode.internal.TypeToken;

final class DateTypeAdapter extends TypeAdapter<Date> {
	public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
		@SuppressWarnings("unchecked")
		// we use a runtime check to make sure the 'T's equal
		public <T> TypeAdapter<T> create(Serializer context, TypeToken<T> typeToken) {
			return typeToken.getRawType() == Date.class ? (TypeAdapter<T>) new DateTypeAdapter() : null;
		}
	};

	private final DateFormat enUsFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US);
	private final DateFormat localFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT);
	private final DateFormat iso8601Format = buildIso8601Format();

	private static DateFormat buildIso8601Format() {
		DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
		iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
		return iso8601Format;
	}

	@Override
	public Date read(XmlReader in) throws IOException {
		if (in.peek() == null || !in.peek().isText()) {
			return null;
		}
		Text text = (Text) in.next();
		return deserializeToDate(text.getValue());
	}

	private synchronized Date deserializeToDate(String text) {
		try {
			return localFormat.parse(text);
		} catch (ParseException ignored) {
		}
		try {
			return enUsFormat.parse(text);
		} catch (ParseException ignored) {
		}
		try {
			return iso8601Format.parse(text);
		} catch (ParseException e) {
			throw new NodeException(text, e);
		}

	}

	@Override
	public synchronized void write(XmlWriter out, Date value) throws IOException {
		if (value == null) {
			return;
		}
		String dateFormatAsString = localFormat.format(value);
		out.writeText(dateFormatAsString);
	}
}

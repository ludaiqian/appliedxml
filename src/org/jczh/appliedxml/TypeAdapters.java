package org.jczh.appliedxml;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import org.jczh.appliedxml.PullReader.Text;
import org.jczh.appliedxml.utils.StringUtil;

import com.google.gsoncode.internal.LazilyParsedNumber;
import com.google.gsoncode.internal.TypeToken;

public class TypeAdapters {

	@SuppressWarnings("rawtypes")
	public static final TypeAdapter<Class> CLASS = new TypeAdapter<Class>() {
		@Override
		public void write(XmlWriter out, Class value) throws IOException {
			throw new UnsupportedOperationException("Attempted to serialize java.lang.Class: " + value.getName()
					+ ". Forgot to register a type adapter?");
		}

		@Override
		public Class read(XmlReader in) throws IOException {
			throw new UnsupportedOperationException(
					"Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
		}
	};

	public static final TypeAdapterFactory CLASS_FACTORY = newFactory(Class.class, CLASS);

	public static final TypeAdapter<BitSet> BIT_SET = new TypeAdapter<BitSet>() {
		public BitSet read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			BitSet bitset = new BitSet();
			Text text = (Text) in.next();
			if (!StringUtil.isEmpty(text.getValue())) {
				char[] bits = text.getValue().toCharArray();
				for (int i = 0; i < bits.length; i++) {
					char c = bits[i];
					if (c == '1')
						bitset.set(i);
				}
			}
			return bitset;
		}

		public void write(XmlWriter out, BitSet src) throws IOException {
			StringBuilder buf = new StringBuilder();
			for (int i = 0; i < src.length(); i++) {
				int value = (src.get(i)) ? 1 : 0;
				buf.append(String.valueOf(value));
			}
			out.writeText(buf.toString());
		}
	};

	public static final TypeAdapterFactory BIT_SET_FACTORY = newFactory(BitSet.class, BIT_SET);

	public static final TypeAdapter<Boolean> BOOLEAN = new TypeAdapter<Boolean>() {
		@Override
		public Boolean read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			if (isNullValue(text))
				return null;
			return Boolean.parseBoolean(text.getValue());
		}

		@Override
		public void write(XmlWriter out, Boolean value) throws IOException {
			writeAsText(out, value);
		}

	};

	public static final TypeAdapterFactory BOOLEAN_FACTORY = newFactory(boolean.class, Boolean.class, BOOLEAN);

	public static <TT> TypeAdapterFactory newFactory(final Class<TT> unboxed, final Class<TT> boxed,
			final TypeAdapter<? super TT> typeAdapter) {
		return new TypeAdapterFactory() {
			@SuppressWarnings("unchecked")
			// we use a runtime check to make sure the 'T's equal
			public <T> TypeAdapter<T> create(Serializer context, TypeToken<T> typeToken) {
				Class<? super T> rawType = typeToken.getRawType();
				return (rawType == unboxed || rawType == boxed) ? (TypeAdapter<T>) typeAdapter : null;
			}

			@Override
			public String toString() {
				return "Factory[type=" + boxed.getName() + "+" + unboxed.getName() + ",adapter=" + typeAdapter + "]";
			}
		};
	}

	public static final TypeAdapter<Number> BYTE = new TypeAdapter<Number>() {
		@Override
		public Number read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			if (isNullValue(text)) {
				return (byte) 0;
			}
			try {
				return Byte.parseByte(text.getValue());
			} catch (NumberFormatException e) {
				throw new NodeException(e);
			}
		}

		@Override
		public void write(XmlWriter out, Number value) throws IOException {
			writeAsText(out, value);
		}
	};

	public static final TypeAdapterFactory BYTE_FACTORY = newFactory(byte.class, Byte.class, BYTE);
	public static final TypeAdapter<Number> SHORT = new TypeAdapter<Number>() {
		@Override
		public Number read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			if (isNullValue(text)) {
				return (short) 0;
			}
			try {
				return Short.parseShort(text.getValue());
			} catch (NumberFormatException e) {
				throw new NodeException(e);
			}
		}

		@Override
		public void write(XmlWriter out, Number value) throws IOException {
			writeAsText(out, value);
		}
	};
	public static final TypeAdapterFactory SHORT_FACTORY = newFactory(short.class, Short.class, SHORT);

	public static final TypeAdapter<Number> INTEGER = new TypeAdapter<Number>() {
		@Override
		public Number read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			if (isNullValue(text)) {
				return 0;
			}
			try {
				return Integer.parseInt(text.getValue());
			} catch (NumberFormatException e) {
				throw new NodeException(e);
			}
		}

		@Override
		public void write(XmlWriter out, Number value) throws IOException {
			writeAsText(out, value);
		}
	};

	public static final TypeAdapterFactory INTEGER_FACTORY = newFactory(int.class, Integer.class, INTEGER);
	public static final TypeAdapter<Number> LONG = new TypeAdapter<Number>() {
		@Override
		public Number read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			if (isNullValue(text)) {
				return (long) 0;
			}
			try {
				return Long.parseLong(text.getValue());
			} catch (NumberFormatException e) {
				throw new NodeException(e);
			}
		}

		@Override
		public void write(XmlWriter out, Number value) throws IOException {
			writeAsText(out, value);
		}
	};
	public static final TypeAdapter<Number> FLOAT = new TypeAdapter<Number>() {
		@Override
		public Number read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			if (isNullValue(text)) {
				return (float) 0;
			}
			try {
				return Float.parseFloat(text.getValue());
			} catch (NumberFormatException e) {
				throw new NodeException(e);
			}
		}

		@Override
		public void write(XmlWriter out, Number value) throws IOException {
			writeAsText(out, value);
		}
	};
	public static final TypeAdapter<Number> DOUBLE = new TypeAdapter<Number>() {
		@Override
		public Number read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			if (isNullValue(text)) {
				return (double) 0;
			}
			try {
				return Double.parseDouble(text.getValue());
			} catch (NumberFormatException e) {
				throw new NodeException(e);
			}
		}

		@Override
		public void write(XmlWriter out, Number value) throws IOException {
			writeAsText(out, value);
		}
	};
	public static final TypeAdapter<Number> NUMBER = new TypeAdapter<Number>() {
		@Override
		public Number read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			if (isNullValue(text)) {
				return (Number) 0;
			}
			return new LazilyParsedNumber(text.getValue());
		}

		@Override
		public void write(XmlWriter out, Number value) throws IOException {
			writeAsText(out, value);
		}
	};
	public static final TypeAdapterFactory NUMBER_FACTORY = newFactory(Number.class, NUMBER);

	public static final TypeAdapter<Character> CHARACTER = new TypeAdapter<Character>() {
		@Override
		public Character read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			String str = text.getValue();
			if (str.length() != 1) {
				throw new NodeException("Expecting character, got: " + str);
			}
			return str.charAt(0);
		}

		@Override
		public void write(XmlWriter out, Character value) throws IOException {
			writeAsText(out, value);
		}
	};
	public static final TypeAdapterFactory CHARACTER_FACTORY = newFactory(char.class, Character.class, CHARACTER);

	public static final TypeAdapter<String> STRING = new TypeAdapter<String>() {
		@Override
		public String read(XmlReader in) throws IOException {

			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			/* coerce booleans to strings for backwards compatibility */
			return text.getValue();
		}

		@Override
		public void write(XmlWriter out, String value) throws IOException {
			writeAsText(out, value);
		}
	};
	public static final TypeAdapterFactory STRING_FACTORY = newFactory(String.class, STRING);
	public static final TypeAdapter<BigDecimal> BIG_DECIMAL = new TypeAdapter<BigDecimal>() {
		@Override
		public BigDecimal read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			if (isNullValue(text))
				return null;
			try {
				return new BigDecimal(text.getValue());
			} catch (NumberFormatException e) {
				throw new NodeException(e);
			}
		}

		@Override
		public void write(XmlWriter out, BigDecimal value) throws IOException {
			writeAsText(out, value);
		}
	};
	public static final TypeAdapter<BigInteger> BIG_INTEGER = new TypeAdapter<BigInteger>() {
		@Override
		public BigInteger read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			if (isNullValue(text))
				return null;
			try {
				return new BigInteger(text.getValue());
			} catch (NumberFormatException e) {
				throw new NodeException(e);
			}
		}

		@Override
		public void write(XmlWriter out, BigInteger value) throws IOException {
			writeAsText(out, value);
		}
	};
	public static final TypeAdapter<StringBuilder> STRING_BUILDER = new TypeAdapter<StringBuilder>() {
		@Override
		public StringBuilder read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			try {
				return new StringBuilder(text.getValue());
			} catch (NumberFormatException e) {
				throw new NodeException(e);
			}
		}

		@Override
		public void write(XmlWriter out, StringBuilder value) throws IOException {
			writeAsText(out, value);
		}
	};
	public static final TypeAdapterFactory STRING_BUILDER_FACTORY = newFactory(StringBuilder.class, STRING_BUILDER);

	public static final TypeAdapter<StringBuffer> STRING_BUFFER = new TypeAdapter<StringBuffer>() {
		@Override
		public StringBuffer read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			try {
				return new StringBuffer(text.getValue());
			} catch (NumberFormatException e) {
				throw new NodeException(e);
			}
		}

		@Override
		public void write(XmlWriter out, StringBuffer value) throws IOException {
			writeAsText(out, value);
		}
	};

	public static final TypeAdapterFactory STRING_BUFFER_FACTORY = newFactory(StringBuffer.class, STRING_BUFFER);
	public static final TypeAdapter<URL> URL = new TypeAdapter<URL>() {
		@Override
		public URL read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			if (isNullValue(text))
				return null;
			return new URL(text.getValue());
		}

		@Override
		public void write(XmlWriter out, URL value) throws IOException {
			out.writeText(value == null ? "" : value.toExternalForm());
		}
	};
	public static final TypeAdapterFactory URL_FACTORY = newFactory(URL.class, URL);
	public static final TypeAdapter<URI> URI = new TypeAdapter<URI>() {
		@Override
		public URI read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			if (isNullValue(text))
				return null;
			try {
				return new URI(text.getValue());
			} catch (URISyntaxException e) {
				throw new NodeException(e);
			}
		}

		@Override
		public void write(XmlWriter out, URI value) throws IOException {
			out.writeText(value == null ? "" : value.toASCIIString());
		}
	};

	public static final TypeAdapterFactory URI_FACTORY = newFactory(URI.class, URI);
	public static final TypeAdapter<InetAddress> INET_ADDRESS = new TypeAdapter<InetAddress>() {
		@Override
		public InetAddress read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			if (isNullValue(text))
				return null;
			// regrettably, this should have included both the host name and the
			// host address
			return InetAddress.getByName(text.getValue());
		}

		@Override
		public void write(XmlWriter out, InetAddress value) throws IOException {
			out.writeText(value == null ? "" : value.getHostAddress());
		}
	};

	public static final TypeAdapterFactory INET_ADDRESS_FACTORY = newTypeHierarchyFactory(InetAddress.class, INET_ADDRESS);
	public static final TypeAdapter<UUID> UUID = new TypeAdapter<UUID>() {
		@Override
		public UUID read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			if (isNullValue(text))
				return null;
			return java.util.UUID.fromString(text.getValue());
		}

		@Override
		public void write(XmlWriter out, UUID value) throws IOException {
			writeAsText(out, value);
		}
	};

	public static final TypeAdapterFactory UUID_FACTORY = newFactory(UUID.class, UUID);
	public static final TypeAdapterFactory TIMESTAMP_FACTORY = new TypeAdapterFactory() {
		@SuppressWarnings("unchecked")
		// we use a runtime check to make sure the 'T's equal
		public <T> TypeAdapter<T> create(Serializer context, TypeToken<T> typeToken) {
			if (typeToken.getRawType() != Timestamp.class) {
				return null;
			}

			final TypeAdapter<Date> dateTypeAdapter = context.getAdapter(Date.class);
			return (TypeAdapter<T>) new TypeAdapter<Timestamp>() {
				@Override
				public Timestamp read(XmlReader in) throws IOException {
					Date date = dateTypeAdapter.read(in);
					return date != null ? new Timestamp(date.getTime()) : null;
				}

				@Override
				public void write(XmlWriter out, Timestamp value) throws IOException {
					dateTypeAdapter.write(out, value);
				}
			};
		}
	};

	public static final TypeAdapter<Calendar> CALENDAR = new TypeAdapter<Calendar>() {
		private static final String YEAR = "year";
		private static final String MONTH = "month";
		private static final String DAY_OF_MONTH = "dayOfMonth";
		private static final String HOUR_OF_DAY = "hourOfDay";
		private static final String MINUTE = "minute";
		private static final String SECOND = "second";

		@Override
		public Calendar read(XmlReader in) throws IOException {
			if (in.peek() == null) {
				return null;
			}
			int year = 0;
			int month = 0;
			int dayOfMonth = 0;
			int hourOfDay = 0;
			int minute = 0;
			int second = 0;
			EventNode last = in.last();
			while (in.hasNext()) {
				EventNode node = in.next();
				if (node.isStart()) {
					if (node.getName().equals(YEAR)) {
						if (in.peek().isText()) {
							try {
								year = Integer.parseInt(in.next().getValue());
							} catch (NumberFormatException e) {
							}
						}
					} else if (node.getName().equals(MONTH)) {
						if (in.peek().isText()) {
							try {
								month = Integer.parseInt(in.next().getValue());
							} catch (NumberFormatException e) {
							}
						}
					} else if (node.getName().equals(DAY_OF_MONTH)) {
						if (in.peek().isText()) {
							try {
								dayOfMonth = Integer.parseInt(in.next().getValue());
							} catch (NumberFormatException e) {
							}
						}
					} else if (node.getName().equals(HOUR_OF_DAY)) {
						if (in.peek().isText()) {
							try {
								hourOfDay = Integer.parseInt(in.next().getValue());
							} catch (NumberFormatException e) {
							}
						}
					} else if (node.getName().equals(MINUTE)) {
						if (in.peek().isText()) {
							try {
								minute = Integer.parseInt(in.next().getValue());
							} catch (NumberFormatException e) {
							}
						}
					} else if (node.getName().equals(SECOND)) {
						if (in.peek().isText()) {
							try {
								second = Integer.parseInt(in.next().getValue());
							} catch (NumberFormatException e) {
							}
						}
					}

				}

				if (node.isEnd() && last.getName().equals(node.getName()) && last.depth() == node.depth()) {
					break;
				}
			}
			return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
		}

		@Override
		public void write(XmlWriter out, Calendar value) throws IOException {
			if (value == null) {
				return;
			}
			out.writeStart(YEAR, null);// (YEAR);
			out.writeText(String.valueOf(value.get(Calendar.YEAR)));
			out.writeEnd(YEAR, null);
			out.writeStart(MONTH, null);// (MONTH);
			out.writeText(String.valueOf(value.get(Calendar.MONTH)));
			out.writeEnd(MONTH, null);

			out.writeStart(DAY_OF_MONTH, null);// (DAY_OF_MONTH);
			out.writeText(String.valueOf(value.get(Calendar.DAY_OF_MONTH)));
			out.writeEnd(DAY_OF_MONTH, null);

			out.writeStart(HOUR_OF_DAY, null);// (HOUR_OF_DAY);
			out.writeText(String.valueOf(value.get(Calendar.HOUR_OF_DAY)));
			out.writeEnd(HOUR_OF_DAY, null);

			out.writeStart(MINUTE, null);// (MINUTE);
			out.writeText(String.valueOf(value.get(Calendar.MINUTE)));
			out.writeEnd(MINUTE, null);

			out.writeStart(SECOND, null);// (SECOND);
			out.writeText(String.valueOf(value.get(Calendar.SECOND)));
			out.writeEnd(SECOND, null);

		}
	};

	public static final TypeAdapterFactory CALENDAR_FACTORY = newFactoryForMultipleTypes(Calendar.class, GregorianCalendar.class,
			CALENDAR);

	public static final TypeAdapter<Locale> LOCALE = new TypeAdapter<Locale>() {
		@Override
		public Locale read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			Text text = (Text) in.next();
			if (isNullValue(text))
				return null;
			String locale = text.getValue();
			StringTokenizer tokenizer = new StringTokenizer(locale, "_");
			String language = null;
			String country = null;
			String variant = null;
			if (tokenizer.hasMoreElements()) {
				language = tokenizer.nextToken();
			}
			if (tokenizer.hasMoreElements()) {
				country = tokenizer.nextToken();
			}
			if (tokenizer.hasMoreElements()) {
				variant = tokenizer.nextToken();
			}
			if (country == null && variant == null) {
				return new Locale(language);
			} else if (variant == null) {
				return new Locale(language, country);
			} else {
				return new Locale(language, country, variant);
			}
		}

		@Override
		public void write(XmlWriter out, Locale value) throws IOException {
			writeAsText(out, value);
		}
	};
	public static final TypeAdapterFactory LOCALE_FACTORY = newFactory(Locale.class, LOCALE);

	private static final class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
		private final Map<String, T> nameToConstant = new HashMap<String, T>();
		private final Map<T, String> constantToName = new HashMap<T, String>();

		public EnumTypeAdapter(Class<T> classOfT) {
			try {
				for (T constant : classOfT.getEnumConstants()) {
					String name = constant.name();
					org.jczh.appliedxml.annotation.Element annotation = classOfT.getField(name).getAnnotation(
							org.jczh.appliedxml.annotation.Element.class);
					if (annotation != null) {
						name = annotation.name();
					}
					nameToConstant.put(name, constant);
					constantToName.put(constant, name);
				}
			} catch (NoSuchFieldException e) {
				throw new AssertionError();
			}
		}

		public T read(XmlReader in) throws IOException {
			if (in.peek() == null || !in.peek().isText()) {
				return null;
			}
			return nameToConstant.get(in.next().getValue());
		}

		public void write(XmlWriter out, T value) throws IOException {
			out.writeText(value == null ? "" : constantToName.get(value));
		}
	}

	public static final TypeAdapterFactory ENUM_FACTORY = newEnumTypeHierarchyFactory();

	public static TypeAdapterFactory newEnumTypeHierarchyFactory() {
		return new TypeAdapterFactory() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public <T> TypeAdapter<T> create(Serializer context, TypeToken<T> typeToken) {
				Class<? super T> rawType = typeToken.getRawType();
				if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
					return null;
				}
				if (!rawType.isEnum()) {
					rawType = rawType.getSuperclass(); // handle anonymous
														// subclasses
				}
				return (TypeAdapter<T>) new EnumTypeAdapter(rawType);
			}
		};
	}

	public static <TT> TypeAdapterFactory newFactory(final TypeToken<TT> type, final TypeAdapter<TT> typeAdapter) {
		return new TypeAdapterFactory() {
			@SuppressWarnings("unchecked")
			// we use a runtime check to make sure the 'T's equal
			public <T> TypeAdapter<T> create(Serializer context, TypeToken<T> typeToken) {
				return typeToken.equals(type) ? (TypeAdapter<T>) typeAdapter : null;
			}
		};
	}

	public static <TT> TypeAdapterFactory newFactory(final Class<TT> type, final TypeAdapter<TT> typeAdapter) {
		return new TypeAdapterFactory() {
			@SuppressWarnings("unchecked")
			// we use a runtime check to make sure the 'T's equal
			public <T> TypeAdapter<T> create(Serializer context, TypeToken<T> typeToken) {
				return typeToken.getRawType() == type ? (TypeAdapter<T>) typeAdapter : null;
			}

			@Override
			public String toString() {
				return "Factory[type=" + type.getName() + ",adapter=" + typeAdapter + "]";
			}
		};
	}

	public static <TT> TypeAdapterFactory newFactoryForMultipleTypes(final Class<TT> base, final Class<? extends TT> sub,
			final TypeAdapter<? super TT> typeAdapter) {
		return new TypeAdapterFactory() {
			@SuppressWarnings("unchecked")
			// we use a runtime check to make sure the 'T's equal
			public <T> TypeAdapter<T> create(Serializer context, TypeToken<T> typeToken) {
				Class<? super T> rawType = typeToken.getRawType();
				return (rawType == base || rawType == sub) ? (TypeAdapter<T>) typeAdapter : null;
			}

			@Override
			public String toString() {
				return "Factory[type=" + base.getName() + "+" + sub.getName() + ",adapter=" + typeAdapter + "]";
			}
		};
	}

	public static <TT> TypeAdapterFactory newTypeHierarchyFactory(final Class<TT> clazz, final TypeAdapter<TT> typeAdapter) {
		return new TypeAdapterFactory() {
			@SuppressWarnings("unchecked")
			public <T> TypeAdapter<T> create(Serializer context, TypeToken<T> typeToken) {
				return clazz.isAssignableFrom(typeToken.getRawType()) ? (TypeAdapter<T>) typeAdapter : null;
			}

			@Override
			public String toString() {
				return "Factory[typeHierarchy=" + clazz.getName() + ",adapter=" + typeAdapter + "]";
			}
		};
	}

	private static void writeAsText(XmlWriter out, Object value) throws IOException {
		out.writeText(value == null ? "" : String.valueOf(value));
	}

	private static boolean isNullValue(Text text) {
		if (StringUtil.isEmpty(text.getValue()) || text.getValue().equals("null"))
			return true;
		return false;
	}
}

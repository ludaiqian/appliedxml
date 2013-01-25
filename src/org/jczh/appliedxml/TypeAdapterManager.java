package org.jczh.appliedxml;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jczh.appliedxml.ContainerDefine.ContainerType;
import org.jczh.appliedxml.Element.Namespace;
import org.jczh.appliedxml.utils.StringUtil;

import com.google.gsoncode.internal.ConstructorConstructor;
import com.google.gsoncode.internal.TypeToken;

public class TypeAdapterManager {
	private Serializer context;
	private final ConstructorConstructor constructorConstructor;

	TypeAdapterManager(Serializer context) {
		super();
		this.constructorConstructor = new ConstructorConstructor();
		this.context = context;
		factories.add(TypeAdapters.STRING_FACTORY);
		factories.add(TypeAdapters.INTEGER_FACTORY);
		factories.add(ObjectTypeAdapter.FACTORY);
		factories.add(TypeAdapters.BIT_SET_FACTORY);
		factories.add(TypeAdapters.BOOLEAN_FACTORY);
		factories.add(TypeAdapters.BYTE_FACTORY);
		factories.add(TypeAdapters.CHARACTER_FACTORY);
		factories.add(TypeAdapters.CLASS_FACTORY);
		factories.add(TypeAdapters.INET_ADDRESS_FACTORY);
		factories.add(TypeAdapters.NUMBER_FACTORY);
		factories.add(TypeAdapters.SHORT_FACTORY);
		factories.add(TypeAdapters.newFactory(long.class, Long.class, TypeAdapters.LONG));
		factories.add(TypeAdapters.newFactory(double.class, Double.class, TypeAdapters.DOUBLE));
		factories.add(TypeAdapters.newFactory(float.class, Float.class, TypeAdapters.FLOAT));
		factories.add(TypeAdapters.newFactory(BigDecimal.class, TypeAdapters.BIG_DECIMAL));
		factories.add(TypeAdapters.newFactory(BigInteger.class, TypeAdapters.BIG_INTEGER));
		factories.add(TypeAdapters.STRING_BUFFER_FACTORY);
		factories.add(TypeAdapters.STRING_BUILDER_FACTORY);
		factories.add(TypeAdapters.URI_FACTORY);
		factories.add(TypeAdapters.URL_FACTORY);
		factories.add(TypeAdapters.UUID_FACTORY);
		factories.add(TypeAdapters.LOCALE_FACTORY);
		factories.add(TypeAdapters.INET_ADDRESS_FACTORY);
		factories.add(TypeAdapters.BIT_SET_FACTORY);
		factories.add(DateTypeAdapter.FACTORY);
		factories.add(TypeAdapters.CALENDAR_FACTORY);
		factories.add(TimeTypeAdapter.FACTORY);
		factories.add(SqlDateTypeAdapter.FACTORY);
		factories.add(TypeAdapters.TIMESTAMP_FACTORY);
		factories.add(TypeAdapters.ENUM_FACTORY);
		factories.add(TypeAdapters.CLASS_FACTORY);
		factories.add(new ArrayTypeAdapterFactory());
		factories.add(new CollectionTypeAdapterFactory(constructorConstructor));
		factories.add(new MapTypeAdapterFactory(constructorConstructor));
		factories.add(new ReflectiveTypeAdapterFactory(constructorConstructor));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> void registerTypeAdapter(Type type, final TypeAdapter typeAdapter) {
		factories.add(0, TypeAdapters.newFactory(TypeToken.get(type), typeAdapter));
	}

	private List<TypeAdapterFactory> factories = new ArrayList<TypeAdapterFactory>();
	private final ThreadLocal<Map<TypeToken<?>, FutureTypeAdapter<?>>> calls = new ThreadLocal<Map<TypeToken<?>, FutureTypeAdapter<?>>>() {
		@Override
		protected Map<TypeToken<?>, FutureTypeAdapter<?>> initialValue() {
			return new HashMap<TypeToken<?>, FutureTypeAdapter<?>>();
		}
	};
	private final Map<TypeToken<?>, TypeAdapter<?>> typeTokenCache = Collections
			.synchronizedMap(new HashMap<TypeToken<?>, TypeAdapter<?>>());

	@SuppressWarnings("unchecked")
	<T> TypeAdapter<T> getAdapter(TypeToken<T> type) {
		TypeAdapter<?> cached = typeTokenCache.get(type);
		if (cached != null) {
			return (TypeAdapter<T>) cached;
		}

		Map<TypeToken<?>, FutureTypeAdapter<?>> threadCalls = calls.get();
		// the key and value type parameters always agree
		FutureTypeAdapter<T> ongoingCall = (FutureTypeAdapter<T>) threadCalls.get(type);
		if (ongoingCall != null) {
			return ongoingCall;
		}
		FutureTypeAdapter<T> call = new FutureTypeAdapter<T>();
		threadCalls.put(type, call);
		try {
			for (TypeAdapterFactory factory : factories) {
				TypeAdapter<T> candidate = factory.create(context, type);
				if (candidate != null) {
					call.setDelegate(candidate);
					typeTokenCache.put(type, candidate);
					return candidate;
				}
			}
			throw new IllegalArgumentException("xml cannot handle " + type);
		} finally {
			threadCalls.remove(type);
		}
	}

	static class FutureTypeAdapter<T> extends TypeAdapter<T> {
		private TypeAdapter<T> delegate;

		private FutureTypeAdapter() {
		}

		public void setDelegate(TypeAdapter<T> typeAdapter) {
			if (delegate != null) {
				throw new AssertionError();
			}
			delegate = typeAdapter;
		}

		@Override
		public T read(XmlReader in) throws IOException {
			if (delegate == null) {
				throw new IllegalStateException();
			}
			return delegate.read(in);
		}

		@Override
		public void write(XmlWriter out, T value) throws IOException {
			if (delegate == null) {
				throw new IllegalStateException();
			}
			delegate.write(out, value);
		}
	}

	DocumentAdapter getDocumentAdapter() {
		return new DocumentAdapter();
	}

	final class DocumentAdapter {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void writeElement(XmlWriter writer, Element element) throws IOException {
			try {
				if (!element.isEmpty() || element.isRequired()) {
					writer.writeStart(element.getName(), element.getPrefix());
					if (!element.isEmpty()) {
						if (element.isSerialized()) {
							if (element.hasNamespaces())
								for (Namespace namespace : element.getNamespaces()) {
									if (namespace.getPrefix() != null && namespace.getReference() != null) {
										writer.writeNamespace(namespace.getReference(), namespace.getPrefix());
									}
								}
							if (element.hasAttributes())
								for (Attribute attr : element.getAttributes()) {
									if (!StringUtil.isEmpty(attr.getValue()) || attr.isRequired())
										writer.writeAttribute(attr.getName(), attr.getValue(), attr.getPrefix());
								}
							if (element.hasChildElements())
								for (Element detail : element.getChildElements()) {
									writeElement(writer, detail);

								}
							if (element.isText()) {
								writer.writeText(((TextElement) element).getText());
							}
						} else {
							UnSerializedElement unSerializedElement = (UnSerializedElement) element;
							TypeAdapter actualContentAdapter = null;
							if (unSerializedElement.getContainerDefine() != null) {
								TypeToken type = TypeToken.get(unSerializedElement.getUnSerializedChildType());
								actualContentAdapter = getContainerTypeAdater(type, unSerializedElement.getContainerDefine());
							}
							if (actualContentAdapter == null) {
								actualContentAdapter = getAdapter(TypeToken.get(unSerializedElement.getUnSerializedChildType()));
							}
							actualContentAdapter.write(writer, unSerializedElement.getUnSerializedChildValue());
						}
					}
					writer.writeEnd(element.getName(), element.getPrefix());
				}

			} finally {
				writer.flush();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	TypeAdapter getContainerTypeAdater(TypeToken<?> type, ContainerDefine container) {
		if (container.type == ContainerType.List)
			return new CollectionTypeAdapterFactory(constructorConstructor, container).create(context, type);
		else if (container.type == ContainerType.Map)
			return new MapTypeAdapterFactory(constructorConstructor, container).create(context, type);
		else if (container.type == ContainerType.Array)
			return new ArrayTypeAdapterFactory(container).create(context, type);
		throw new IllegalArgumentException("ContainerDefine cannot handle " + container);
	}
}

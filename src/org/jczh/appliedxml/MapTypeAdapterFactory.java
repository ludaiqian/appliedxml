package org.jczh.appliedxml;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jczh.appliedxml.PullReader.Start;
import org.jczh.appliedxml.utils.StringUtil;
import org.jczh.appliedxml.utils.TextTypeUtil;

import com.google.gsoncode.internal.$Gson$Types;
import com.google.gsoncode.internal.ConstructorConstructor;
import com.google.gsoncode.internal.ObjectConstructor;
import com.google.gsoncode.internal.TypeToken;

final class MapTypeAdapterFactory implements TypeAdapterFactory {
	private final ConstructorConstructor constructorConstructor;
	private ContainerDefine container;

	MapTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
		this(constructorConstructor, null);
	}

	public MapTypeAdapterFactory(ConstructorConstructor constructorConstructor, ContainerDefine container) {
		this.constructorConstructor = constructorConstructor;
		this.container = container;
	}

	public <T> TypeAdapter<T> create(Serializer context, TypeToken<T> typeToken) {
		Type type = typeToken.getType();

		Class<? super T> rawType = typeToken.getRawType();
		if (!Map.class.isAssignableFrom(rawType)) {
			return null;
		}

		Class<?> rawTypeOfSrc = $Gson$Types.getRawType(type);
		Type[] keyAndValueTypes = $Gson$Types.getMapKeyAndValueTypes(type, rawTypeOfSrc);
		TypeAdapter<?> keyAdapter = getKeyAdapter(context, keyAndValueTypes[0]);
		TypeAdapter<?> valueAdapter = context.getAdapter(TypeToken.get(keyAndValueTypes[1]));
		ObjectConstructor<T> constructor = constructorConstructor.get(typeToken);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		// we don't define a type parameter for the key or value types
		TypeAdapter<T> result = new Adapter(context, keyAndValueTypes[0], keyAdapter, keyAndValueTypes[1], valueAdapter,
				constructor, container);
		return result;
	}

	private TypeAdapter<?> getKeyAdapter(Serializer context, Type keyType) {
		return context.getAdapter(TypeToken.get(keyType));
	}

	private final class Adapter<K, V> extends TypeAdapter<Map<K, V>> {
		private String valueName = "value";
		private String keyName = "key";
		private String entryName = "entry";
		private boolean keyIsAttr;
		private boolean valueAsText;
		private final Serializer context;
		private final Type keyType;
		private final Type valueType;
		private final TypeAdapter<K> keyTypeAdapter;
		private final TypeAdapter<V> valueTypeAdapter;
		private final ObjectConstructor<? extends Map<K, V>> constructor;

		public Adapter(Serializer context, Type keyType, TypeAdapter<K> keyTypeAdapter, Type valueType,
				TypeAdapter<V> valueTypeAdapter, ObjectConstructor<? extends Map<K, V>> constructor,
				ContainerDefine containerDefine) {
			this.context = context;
			this.keyType = keyType;
			this.valueType = valueType;
			this.keyTypeAdapter = new TypeAdapterRuntimeTypeWrapper<K>(context, keyTypeAdapter, keyType);
			this.valueTypeAdapter = new TypeAdapterRuntimeTypeWrapper<V>(context, valueTypeAdapter, valueType);
			this.constructor = constructor;
			if (containerDefine != null) {
				if (!StringUtil.isEmpty(containerDefine.key)) {
					keyName = containerDefine.key;
				}
				if (!StringUtil.isEmpty(containerDefine.value))
					valueName = containerDefine.value;
				if (!StringUtil.isEmpty(containerDefine.entry))
					entryName = containerDefine.entry;
				keyIsAttr = containerDefine.keyAsAttr;
				valueAsText = containerDefine.valueAsText;
			}

		}

		@SuppressWarnings("unchecked")
		public Map<K, V> read(XmlReader in) throws IOException {

			if (in.peek() == null) {
				return null;
			}
			EventNode last = in.last();
			Map<K, V> map = constructor.construct();
			K key = null;
			V value = null;
			while (in.hasNext()) {
				EventNode node = in.next();

				if (node.isStart()) {
					Start start = (Start) node;
					if (entryName.equals(start.getName())) {
						if (keyIsAttr) {
							key = (K) TextTypeUtil.convert(keyType, start.getEventAttributes().get(0).getValue());

						} else {
							boolean hasKey = true;
							while (in.hasNext()) {
								EventNode keyNode = in.next();
								if (keyNode.isStart() && keyName.equals(keyNode.getName())) {
									start = (Start) keyNode;
									break;
								} else if (keyNode.isEnd() && entryName.endsWith(keyNode.getName())) {
									hasKey = false;
								}
							}
							if (!hasKey)
								continue;
							if (!in.hasNext())
								break;
							key = keyTypeAdapter.read(in);
						}
						if (valueAsText) {
							if (in.peek().isText()) {
								value = (V) TextTypeUtil.convert(valueType, in.next().getValue());
							}

						} else {
							boolean hasValue = true;
							while (in.hasNext()) {
								EventNode valueNode = in.next();
								if (valueNode.isStart() && valueName.equals(valueNode.getName())) {
									start = (Start) valueNode;
									break;
								} else if (valueNode.isEnd() && entryName.endsWith(valueNode.getName())) {
									hasValue = false;
								}
							}
							if (!hasValue)
								continue;
							if (!in.hasNext())
								break;
							if (valueName.equals(start.getName()))
								value = valueTypeAdapter.read(in);
						}
						map.put(key, value);

					}
				}
				if (node.isEnd() && last.getName().equals(node.getName()) && last.depth() == node.depth()) {
					break;

				}
			}
			return map;

		}

		public void write(XmlWriter out, Map<K, V> map) throws IOException {
			if (map == null) {
				return;
			}
			Set<Entry<K, V>> entrys = map.entrySet();
			for (Entry<K, V> entry : entrys) {
				out.writeStart(entryName, context.getDefaultElementPrefix());
				K key = entry.getKey();
				V value = entry.getValue();
				if (key != null || context.isNullValueSerializeRequired()) {
					if (keyIsAttr) {
						out.writeAttribute(keyName, key != null ? key.toString() : "", context.getDefaultAttributePrefix());
					} else {
						out.writeStart(keyName, context.getDefaultElementPrefix());
						if (key != null) {
							keyTypeAdapter.write(out, key);

						}
						out.writeEnd(keyName, context.getDefaultElementPrefix());
					}
				}
				if (value != null || context.isNullValueSerializeRequired()) {
					if (valueAsText) {
						out.writeText(value == null ? "" : value.toString());
					} else {
						out.writeStart(valueName, context.getDefaultElementPrefix());
						if (value != null) {
							valueTypeAdapter.write(out, value);
						}
						out.writeEnd(valueName, context.getDefaultElementPrefix());

					}
				}
				out.writeEnd(entryName, context.getDefaultElementPrefix());
			}
		}

	}
}

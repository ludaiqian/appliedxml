package org.jczh.appliedxml;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.jczh.appliedxml.utils.StringUtil;

import com.google.gsoncode.internal.$Gson$Types;
import com.google.gsoncode.internal.TypeToken;

/**
 * Adapt an array of objects.
 * 
 * @author ludaiqian@126.com
 * @version 1.0
 * @since 1.0
 */

final class ArrayTypeAdapterFactory implements TypeAdapterFactory {
	private ContainerDefine container;

	ArrayTypeAdapterFactory() {
		super();
	}

	ArrayTypeAdapterFactory(ContainerDefine container) {
		super();
		this.container = container;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> TypeAdapter<T> create(Serializer context, TypeToken<T> typeToken) {
		Type type = typeToken.getType();
		if (!(type instanceof GenericArrayType || type instanceof Class && ((Class<?>) type).isArray())) {
			return null;
		}

		Type componentType = $Gson$Types.getArrayComponentType(type);
		TypeAdapter<?> componentTypeAdapter = context.getAdapter(TypeToken.get(componentType));
		return new ArrayTypeAdapter(context, componentTypeAdapter, $Gson$Types.getRawType(componentType), container);
	}

	public final class ArrayTypeAdapter<E> extends TypeAdapter<Object> {
		private String itemName = "item";
		private final Class<E> componentType;
		private final TypeAdapter<E> componentTypeAdapter;
		private Serializer context;

		public ArrayTypeAdapter(Serializer context, TypeAdapter<E> componentTypeAdapter, Class<E> componentType,
				ContainerDefine container) {
			this.context = context;
			this.componentTypeAdapter = new TypeAdapterRuntimeTypeWrapper<E>(context, componentTypeAdapter, componentType);
			this.componentType = componentType;
			itemName = context.getClassNamingStrategy().translateName(TypeToken.get(componentType).getRawType());
			if (container != null && !StringUtil.isEmpty(container.entry))
				itemName = container.entry;
		}

		public Object read(XmlReader in) throws IOException {

			if (in.peek() == null) {
				return null;
			}
			EventNode last = in.last();
			List<E> list = new ArrayList<E>();
			while (in.hasNext()) {
				EventNode node = in.next();
				if (node.isStart() && itemName.equals(node.getName())) {
					E instance = componentTypeAdapter.read(in);
					list.add(instance);
				}
				if (node.isEnd() && last.getName().equals(node.getName()) && last.depth() == node.depth())
					break;
			}
			Object array = Array.newInstance(componentType, list.size());
			for (int i = 0; i < list.size(); i++) {
				Array.set(array, i, list.get(i));
			}
			return array;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void write(XmlWriter out, Object array) throws IOException {
			if (array == null) {
				return;
			}

			for (int i = 0, length = Array.getLength(array); i < length; i++) {

				E value = (E) Array.get(array, i);
				if (value != null || context.isNullValueSerializeRequired()) {
					out.writeStart(itemName, context.getDefaultElementPrefix());
					if (value != null)
						componentTypeAdapter.write(out, value);
					out.writeEnd(itemName, context.getDefaultElementPrefix());
				}
			}
		}
	}
}

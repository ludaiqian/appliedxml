package org.jczh.appliedxml;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

import org.jczh.appliedxml.utils.StringUtil;

import com.google.gsoncode.internal.$Gson$Types;
import com.google.gsoncode.internal.ConstructorConstructor;
import com.google.gsoncode.internal.ObjectConstructor;
import com.google.gsoncode.internal.TypeToken;

/**
 * Adapt a homogeneous collection of objects.
 */
final class CollectionTypeAdapterFactory implements TypeAdapterFactory {
	private final ConstructorConstructor constructorConstructor;
	private ContainerDefine container;

	CollectionTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
		this.constructorConstructor = constructorConstructor;
	}

	public CollectionTypeAdapterFactory(ConstructorConstructor constructorConstructor, ContainerDefine container) {
		this.constructorConstructor = constructorConstructor;
		this.container = container;
	}

	public <T> TypeAdapter<T> create(Serializer context, TypeToken<T> typeToken) {
		Type type = typeToken.getType();

		Class<? super T> rawType = typeToken.getRawType();
		if (!Collection.class.isAssignableFrom(rawType)) {
			return null;
		}

		Type elementType = $Gson$Types.getCollectionElementType(type, rawType);
		TypeAdapter<?> elementTypeAdapter = context.getAdapter(TypeToken.get(elementType));
		ObjectConstructor<T> constructor = constructorConstructor.get(typeToken);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		TypeAdapter<T> result = new Adapter(context, elementType, elementTypeAdapter, constructor, container);
		return result;
	}

	private final class Adapter<E> extends TypeAdapter<Collection<E>> {
		private String itemName = "item";
		private final Serializer context;
		@SuppressWarnings("unused")
		private final Type elementType;
		private final TypeAdapter<E> elementTypeAdapter;
		private final ObjectConstructor<? extends Collection<E>> constructor;

		public Adapter(Serializer context, Type elementType, TypeAdapter<E> elementTypeAdapter,
				ObjectConstructor<? extends Collection<E>> constructor, ContainerDefine container) {
			this.context = context;
			this.elementType = elementType;
			itemName = context.getClassNamingStrategy().translateName(TypeToken.get(elementType).getRawType());
			this.elementTypeAdapter = new TypeAdapterRuntimeTypeWrapper<E>(context, elementTypeAdapter, elementType);
			this.constructor = constructor;
			if (container != null && !StringUtil.isEmpty(container.entry))
				itemName = container.entry;

		}

		public Collection<E> read(XmlReader in) throws IOException {
			if (in.peek() == null) {
				return null;
			}
			Collection<E> collection = constructor.construct();
			EventNode last = in.last();
			while (in.hasNext()) {
				EventNode node = in.next();
				if ((node.isStart() && itemName.equals(node.getName()))) {
					E instance = elementTypeAdapter.read(in);
					collection.add(instance);
				}
				if (node.isEnd() && last.getName().equals(node.getName()) && last.depth() == node.depth())
					break;
			}
			return collection;
		}

		public void write(XmlWriter out, Collection<E> collection) throws IOException {
			for (E element : collection) {
				if (element != null || context.isNullValueSerializeRequired()) {
					out.writeStart(itemName, context.getDefaultElementPrefix());
					if (element != null)
						elementTypeAdapter.write(out, element);
					out.writeEnd(itemName, context.getDefaultElementPrefix());
				}
			}
		}
	}
}

package org.jczh.appliedxml;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;

import com.google.gsoncode.internal.TypeToken;

public class ElementConstructorManager {
	private Serializer context;
	private LinkedList<ElementConstructorFactory> factorys = new LinkedList<ElementConstructorFactory>();

	ElementConstructorManager(Serializer context) {
		this.context = context;
		factorys.add(new CollectionElementFactory());
		factorys.add(new DocumentTreeBuilderFactory(context));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	ElementConstructor getConstructor(TypeToken type) {
		for (ElementConstructorFactory e : factorys) {
			ElementConstructor constructor = e.create(context, type);
			if (constructor != null) {
				return constructor;
			}
		}
		throw new IllegalArgumentException("xml cannot handle " + type);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void registerConstructorFactory(Type type, ElementConstructor elementConstructor) {
		factorys.addFirst(newFactory(TypeToken.get(type), elementConstructor));
	}

	public static <TT> ElementConstructorFactory newFactory(final TypeToken<TT> type, final ElementConstructor<TT> typeAdapter) {
		return new ElementConstructorFactory() {
			@SuppressWarnings("unchecked")
			public <T> ElementConstructor<T> create(Serializer context, TypeToken<T> typeToken) {
				return typeToken.equals(type) ? (ElementConstructor<T>) typeAdapter : null;
			}
		};
	}

	@SuppressWarnings("rawtypes")
	public class CollectionElementFactory implements ElementConstructorFactory {
		@SuppressWarnings("unchecked")
		@Override
		public ElementConstructor create(Serializer context, TypeToken type) {
			Class rawType = type.getRawType();
			if (!Collection.class.isAssignableFrom(rawType))
				return null;
			return new CollectionElementConstructor();
		}

		public class CollectionElementConstructor implements ElementConstructor {

			@SuppressWarnings("unchecked")
			@Override
			public Element construct(Object object) {
				Element element = new Element();
				Collection collection = (Collection) object;
				element.setName(collection.getClass().getSimpleName());
				for (Object o : collection) {
					ElementConstructor c = getConstructor(TypeToken.get(o.getClass()));
					element.addChildElement(c.construct(o));
				}
				return element;
			}
		}
	}

}

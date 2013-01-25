package org.jczh.appliedxml.utils;

import java.lang.reflect.Type;

import com.google.gsoncode.internal.$Gson$Preconditions;
import com.google.gsoncode.internal.Primitives;
import com.google.gsoncode.internal.TypeToken;

public class TextTypeUtil {

	@SuppressWarnings("rawtypes")
	public static boolean isTextType(Type rawType) {
		return Primitives.isPrimitive(rawType) || Primitives.isWrapperType(rawType)
				|| (rawType instanceof Class && (CharSequence.class.isAssignableFrom((Class) rawType)));
	}

	@SuppressWarnings("rawtypes")
	public static Object convert(Type rawType, Object value) {
		if (value == null)
			return null;
		if (!(value instanceof CharSequence))
			return value;
		String valueStr = value.toString();
		if ("".equals(valueStr))
			return null;
		$Gson$Preconditions.checkNotNull(rawType);
		Class type = TypeToken.get(rawType).getRawType();
		if (CharSequence.class.isAssignableFrom(type))
			return valueStr;
		if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type))
			return Boolean.valueOf(valueStr);
		else if (Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type))
			return Byte.valueOf(valueStr);
		else if (Character.class.isAssignableFrom(type) || char.class.isAssignableFrom(type))
			return valueStr.charAt(0);
		else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type))
			return Double.valueOf(valueStr);
		else if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type))
			return Float.valueOf(valueStr);
		else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type))
			return Integer.valueOf(valueStr);
		else if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type))
			return Long.valueOf(valueStr);
		else if (Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type))
			return Short.valueOf(valueStr);
		return null;
	}
}

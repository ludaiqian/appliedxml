/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jczh.appliedxml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jczh.appliedxml.annotation.Serializable;
import org.jczh.appliedxml.annotation.Transient;

/**
 * This class selects which fields and types to omit. It is configurable,
 * supporting version attributes {@link Since} and {@link Until}, modifiers,
 * synthetic fields, anonymous and local classes, inner classes, and fields with
 * the {@link Expose} annotation.
 * 
 * <p>
 * This class is a type adapter factory; types that are excluded will be adapted
 * to null. It may delegate to another type adapter if only one direction is
 * excluded.
 * 
 * @author Joel Leitch
 * @author Jesse Wilson
 */
public final class Excluder implements Cloneable {
	public static final Excluder DEFAULT = new Excluder();

	private int modifiers = Modifier.TRANSIENT | Modifier.STATIC;
	private boolean serializeInnerClasses = true;
	private List<ExclusionStrategy> serializationStrategies = Collections.emptyList();
	private List<ExclusionStrategy> deserializationStrategies = Collections.emptyList();

	@Override
	protected Excluder clone() {
		try {
			return (Excluder) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

	public Excluder withExclusionStrategy(ExclusionStrategy exclusionStrategy, boolean serialization, boolean deserialization) {
		Excluder result = clone();
		if (serialization) {
			result.serializationStrategies = new ArrayList<ExclusionStrategy>(serializationStrategies);
			result.serializationStrategies.add(exclusionStrategy);
		}
		if (deserialization) {
			result.deserializationStrategies = new ArrayList<ExclusionStrategy>(deserializationStrategies);
			result.deserializationStrategies.add(exclusionStrategy);
		}
		return result;
	}

	public boolean excludeField(Field field, boolean serialize) {
		Transient annotation = field.getAnnotation(Transient.class);
		if (annotation != null) {
			return true;
		}
		if ((modifiers & field.getModifiers()) != 0) {
			return true;
		}

		if (field.isSynthetic()) {
			return true;
		}

		if (!serializeInnerClasses && isInnerClass(field.getType())) {
			return true;
		}

		if (isAnonymousOrLocal(field.getType())) {
			return true;
		}

		List<ExclusionStrategy> list = serialize ? serializationStrategies : deserializationStrategies;
		if (!list.isEmpty()) {
			FieldAttributes fieldAttributes = new FieldAttributes(field);
			for (ExclusionStrategy exclusionStrategy : list) {
				if (exclusionStrategy.shouldSkipField(fieldAttributes)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean excludeClass(Class<?> clazz, boolean serialize) {
		Serializable annotation = clazz.getAnnotation(Serializable.class);
		if (annotation != null) {
			return !annotation.value();
		}

		if (!serializeInnerClasses && isInnerClass(clazz)) {
			return true;
		}

		if (isAnonymousOrLocal(clazz)) {
			return true;
		}

		List<ExclusionStrategy> list = serialize ? serializationStrategies : deserializationStrategies;
		for (ExclusionStrategy exclusionStrategy : list) {
			if (exclusionStrategy.shouldSkipClass(clazz)) {
				return true;
			}
		}

		return false;
	}

	private boolean isAnonymousOrLocal(Class<?> clazz) {
		return !Enum.class.isAssignableFrom(clazz) && (clazz.isAnonymousClass() || clazz.isLocalClass());
	}

	private boolean isInnerClass(Class<?> clazz) {
		return clazz.isMemberClass() && !isStatic(clazz);
	}

	private boolean isStatic(Class<?> clazz) {
		return (clazz.getModifiers() & Modifier.STATIC) != 0;
	}

}

package org.jczh.appliedxml.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.jczh.appliedxml.Element.Namespace;
import org.jczh.appliedxml.annotation.NamespaceList;

public class ReflectUtil {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList<Namespace> extractNamespaces(Class type) {
		org.jczh.appliedxml.annotation.Namespace namespaceAnnotation = (org.jczh.appliedxml.annotation.Namespace) type
				.getAnnotation(org.jczh.appliedxml.annotation.Namespace.class);
		NamespaceList namespaceList = (NamespaceList) type.getAnnotation(NamespaceList.class);
		return extractFrom(namespaceAnnotation, namespaceList);
	}

	public static ArrayList<Namespace> extractNamespaces(Field field) {
		org.jczh.appliedxml.annotation.Namespace namespaceAnnotation = (org.jczh.appliedxml.annotation.Namespace) field
				.getAnnotation(org.jczh.appliedxml.annotation.Namespace.class);
		NamespaceList namespaceList = (NamespaceList) field.getAnnotation(NamespaceList.class);
		return extractFrom(namespaceAnnotation, namespaceList);
	}

	private static ArrayList<Namespace> extractFrom(org.jczh.appliedxml.annotation.Namespace namespaceAnnotation,
			NamespaceList namespaceList) {
		ArrayList<Namespace> namespaces = null;
		if (namespaceAnnotation != null) {
			namespaces = new ArrayList<Namespace>();
			namespaces.add(namespaceAnnotatino2Namespace(namespaceAnnotation));
		}
		if (namespaceList != null && namespaceList.value() != null) {
			if (namespaces == null)
				namespaces = new ArrayList<Namespace>();
			for (org.jczh.appliedxml.annotation.Namespace n : namespaceList.value()) {
				namespaces.add(namespaceAnnotatino2Namespace(n));
			}
		}
		return namespaces;
	}

	private static Namespace namespaceAnnotatino2Namespace(org.jczh.appliedxml.annotation.Namespace namespace) {
		return new Namespace(namespace.prefix(), namespace.reference());
	}


}

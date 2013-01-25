package org.jczh.appliedxml;

import com.google.gsoncode.internal.TypeToken;

public interface TypeAdapterFactory {

  /**
   * Returns a type adapter for {@code type}, or null if this factory doesn't
   * support {@code type}.
   */
  <T> TypeAdapter<T> create(Serializer context, TypeToken<T> type);
}

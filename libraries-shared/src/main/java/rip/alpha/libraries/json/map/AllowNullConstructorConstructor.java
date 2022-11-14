package rip.alpha.libraries.json.map;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.UnsafeAllocator;
import com.google.gson.internal.reflect.ReflectionHelper;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@SuppressWarnings("ALL")
public final class AllowNullConstructorConstructor {
    private final Map<Type, InstanceCreator<?>> instanceCreators;
    private final boolean useJdkUnsafe;

    public AllowNullConstructorConstructor(Map<Type, InstanceCreator<?>> instanceCreators, boolean useJdkUnsafe) {
        this.instanceCreators = instanceCreators;
        this.useJdkUnsafe = useJdkUnsafe;
    }

    public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
        final Type type = typeToken.getType();
        Class<? super T> rawType = typeToken.getRawType();
        final InstanceCreator<T> typeCreator = (InstanceCreator)this.instanceCreators.get(type);
        if (typeCreator != null) {
            return () -> typeCreator.createInstance(type);
        } else {
            final InstanceCreator<T> rawTypeCreator = (InstanceCreator)this.instanceCreators.get(rawType);
            if (rawTypeCreator != null) {
                return () -> rawTypeCreator.createInstance(type);
            } else {
                ObjectConstructor<T> defaultConstructor = this.newDefaultConstructor(rawType);
                if (defaultConstructor != null) {
                    return defaultConstructor;
                } else {
                    ObjectConstructor<T> defaultImplementation = this.newDefaultImplementationConstructor(type, rawType);
                    return defaultImplementation != null ? defaultImplementation : this.newUnsafeAllocator(rawType);
                }
            }
        }
    }

    private <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType) {
        if (Modifier.isAbstract(rawType.getModifiers())) {
            return null;
        } else {
            final Constructor constructor;
            try {
                constructor = rawType.getDeclaredConstructor();
            } catch (NoSuchMethodException var4) {
                return null;
            }

            final String exceptionMessage = ReflectionHelper.tryMakeAccessible(constructor);
            return exceptionMessage != null ? () -> {
                throw new JsonIOException(exceptionMessage);
            } : () -> {
                try {
                    return (T) constructor.newInstance();
                } catch (InstantiationException var2) {
                    throw new RuntimeException("Failed to invoke " + constructor + " with no args", var2);
                } catch (InvocationTargetException var3) {
                    throw new RuntimeException("Failed to invoke " + constructor + " with no args", var3.getTargetException());
                } catch (IllegalAccessException var4) {
                    throw new AssertionError(var4);
                }
            };
        }
    }

    private <T> ObjectConstructor<T> newDefaultImplementationConstructor(final Type type, Class<? super T> rawType) {
        if (Collection.class.isAssignableFrom(rawType)) {
            if (SortedSet.class.isAssignableFrom(rawType)) {
                return () -> (T) new TreeSet<>();
            } else if (EnumSet.class.isAssignableFrom(rawType)) {
                return () -> {
                    if (type instanceof ParameterizedType) {
                        Type elementType = ((ParameterizedType)type).getActualTypeArguments()[0];
                        if (elementType instanceof Class) {
                            return (T) EnumSet.noneOf((Class)elementType);
                        } else {
                            throw new JsonIOException("Invalid EnumSet type: " + type.toString());
                        }
                    } else {
                        throw new JsonIOException("Invalid EnumSet type: " + type.toString());
                    }
                };
            } else if (Set.class.isAssignableFrom(rawType)) {
                return () -> (T) new LinkedHashSet<>();
            } else {
                return Queue.class.isAssignableFrom(rawType) ? () -> (T) new ArrayDeque<>() : () -> (T) new ArrayList<>();
            }
        } else if (Map.class.isAssignableFrom(rawType)) {
            if (rawType == EnumMap.class) {
                return () -> {
                    if (type instanceof ParameterizedType) {
                        Type elementType = ((ParameterizedType)type).getActualTypeArguments()[0];
                        if (elementType instanceof Class) {
                            T map = (T) new EnumMap((Class)elementType);
                            return map;
                        } else {
                            throw new JsonIOException("Invalid EnumMap type: " + type.toString());
                        }
                    } else {
                        throw new JsonIOException("Invalid EnumMap type: " + type.toString());
                    }
                };
            } else if (ConcurrentNavigableMap.class.isAssignableFrom(rawType)) {
                return () -> (T) new ConcurrentSkipListMap<>();
            } else if (ConcurrentMap.class.isAssignableFrom(rawType)) {
                return () -> (T) new ConcurrentHashMap<>();
            } else if (SortedMap.class.isAssignableFrom(rawType)) {
                return () -> (T) new TreeMap<>();
            } else {
                return type instanceof ParameterizedType && !String.class.isAssignableFrom(TypeToken.get(((ParameterizedType)type).getActualTypeArguments()[0]).getRawType()) ? new ObjectConstructor<T>() {
                    public T construct() {
                        return (T) new LinkedHashMap<>();
                    }
                } : () -> (T) new LinkedTreeMap<>();
            }
        } else {
            return null;
        }
    }

    private <T> ObjectConstructor<T> newUnsafeAllocator(final Class<? super T> rawType) {
        if (this.useJdkUnsafe) {
            return new ObjectConstructor<T>() {
                private final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();

                public T construct() {
                    try {
                        return (T) this.unsafeAllocator.newInstance(rawType);
                    } catch (Exception var2) {
                        throw new RuntimeException("Unable to create instance of " + rawType + ". Registering an InstanceCreator or a TypeAdapter for this type, or adding a no-args constructor may fix this problem.", var2);
                    }
                }
            };
        } else {
            final String exceptionMessage = "Unable to create instance of " + rawType + "; usage of JDK Unsafe is disabled. Registering an InstanceCreator or a TypeAdapter for this type, adding a no-args constructor, or enabling usage of JDK Unsafe may fix this problem.";
            return new ObjectConstructor<T>() {
                public T construct() {
                    throw new JsonIOException(exceptionMessage);
                }
            };
        }
    }

    public String toString() {
        return this.instanceCreators.toString();
    }
}

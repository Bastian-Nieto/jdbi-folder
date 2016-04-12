package com.github.rkmk.mapper;

import com.github.rkmk.annotations.TypeUse;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;

public class FieldHelper {

    private static final Map<Class<?>, TypeFactory> TYPE_FACTORIES = new HashMap<>();

    public static void set(Field field, Object object, Object value) {
        field.setAccessible(true);

        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.format("The field %s is not accessible",field.getName()), e);
        }
    }

    public static Object get(Field field, Object object) {
        field.setAccessible(true);

        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.format("The field %s is not accessible",field.getName()), e);
        }
    }

    public static <T> T getInstance(Class<T> type) {
        try {
            return type.newInstance();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("A bean, %s, was mapped " +
                    "which was not instantiable", type.getName()), e);
        }

    }

    public static <T> T getInstance(Class<T> type, ResultSet rs, int index) throws SQLException {
        Class<? extends T> realType;

        TypeUse typeUse = type.getAnnotation(TypeUse.class);
        if (typeUse != null) {
            TypeFactory typeFactory = TYPE_FACTORIES.get(type);

            if (typeFactory == null) {
                try {
                    typeFactory = typeUse.value().newInstance();
                    TYPE_FACTORIES.put(type, typeFactory);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new IllegalArgumentException(
                            String.format("Can't instantiate TypeUse %s for type: %s",
                                    typeUse.value().getName(),
                                    type.getName()),
                            e);
                }
            }

            realType = typeFactory.getType(type, rs, index);
        } else {
            realType = type;
        }

        return getInstance(realType);
    }


    public static Class<?> getParameterisedReturnType(Field field){
        Class<?> result = null ;
        Type genericFieldType = field.getGenericType();
        if(genericFieldType instanceof ParameterizedType){
            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            result = fieldArgTypes.length >0 ? (Class)fieldArgTypes[0] : null ;
        }

        if(nonNull(result)) {
            return result;
        }
        throw new IllegalArgumentException(String.format("The field type %s is not inferrable",field.getName()));
    }

    public static List<Field> getFields(Class<?> type) {
        List<Field> result = new ArrayList<>();
        Class<?> clazz = type;
        while(clazz.getSuperclass() != null) {
            result.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return result;
    }

    public static <O> Object accessField(String fieldName, O o) {
        Field field = null;
        List<Field> optionalField = getFields(o.getClass());
        Optional<Field> field2 = optionalField.stream().filter(field1 -> {
            return field1.getName().equals(fieldName);
        }).findFirst();
        if (!field2.isPresent()) {
            throw new IllegalArgumentException("Missing field in class");
        } else {
            field = field2.get();
        }
        field.setAccessible(true);
        try {
            return field.get(o);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("The field not accessible");
        }
    }


}

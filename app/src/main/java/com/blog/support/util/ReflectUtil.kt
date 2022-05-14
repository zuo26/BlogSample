@file:JvmName("ReflectUtil")
package com.blog.support.util

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

@Throws(NoSuchFieldException::class)
fun findField(instance: Any, name: String): Field {
    var clazz: Class<*>? = instance.javaClass
    while (clazz != null) {
        try {
            val field = clazz.getDeclaredField(name)
            if (!field.isAccessible) {
                field.isAccessible = true
            }
            return field
        } catch (e: NoSuchFieldException) {
        }
        clazz = clazz.superclass
    }
    throw NoSuchFieldException("Field " + name + " not found in " + instance.javaClass)
}

@Throws(NoSuchFieldException::class)
fun findField(originClazz: Class<*>, name: String): Field {
    var clazz: Class<*>? = originClazz
    while (clazz != null) {
        try {
            val field = clazz.getDeclaredField(name)
            if (!field.isAccessible) {
                field.isAccessible = true
            }
            return field
        } catch (e: NoSuchFieldException) {
        }
        clazz = clazz.superclass
    }
    throw NoSuchFieldException("Field $name not found in $originClazz")
}

@Throws(NoSuchMethodException::class)
fun findMethod(instance: Any, name: String, vararg parameterTypes: Class<*>?): Method {
    var clazz: Class<*>? = instance.javaClass
    while (clazz != null) {
        try {
            val method = clazz.getDeclaredMethod(name, *parameterTypes)
            if (!method.isAccessible) {
                method.isAccessible = true
            }
            return method
        } catch (e: NoSuchMethodException) {
        }
        clazz = clazz.superclass
    }
    throw NoSuchMethodException(
        "Method "
                + name
                + " with parameters "
                + Arrays.asList<Class<*>>(*parameterTypes)
                + " not found in " + instance.javaClass
    )
}
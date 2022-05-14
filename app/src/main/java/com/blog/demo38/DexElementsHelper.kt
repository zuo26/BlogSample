package com.blog.demo38

import android.util.Log
import com.blog.support.util.findField
import dalvik.system.DexFile
import java.lang.reflect.Field

@Throws(Throwable::class)
fun install(classLoader: ClassLoader, classLoader2: ClassLoader) {
    val dexPathList = getDexPathList(classLoader)
    val dexElementsField = getDexElementsField(dexPathList)
    val baseElements = getDexElementsByClassLoader(dexPathList, dexElementsField)
    val dex1Elements = getDexElementsByClassLoader(classLoader2)
    val baseLength = baseElements.size
    val dex1Length = dex1Elements.size
    val size = baseLength + dex1Length

    // Element[] 类型 Class
    val elementClass = baseElements.javaClass.componentType!!
    val elementArray = java.lang.reflect.Array.newInstance(elementClass, size)

    // 拷贝，test.dex 放在前面，base.dex 放后面
    run {
        var i = 0
        while (i < dex1Length && i < size) {
            java.lang.reflect.Array.set(
                elementArray,
                i,
                java.lang.reflect.Array.get(dex1Elements, i)
            )
            i++
        }
    }
    for (i in 0 until baseLength) {
        java.lang.reflect.Array.set(
            elementArray,
            dex1Length + i,
            java.lang.reflect.Array.get(baseElements, i)
        )
    }

    // 通过反射，重新给 dexElements 赋值
    dexElementsField.isAccessible = true
    dexElementsField[dexPathList] = elementArray
}

@Throws(Throwable::class)
fun getDexPathList(classLoader: ClassLoader): Any {
    val pathListField = findField(classLoader, "pathList")
    return pathListField[classLoader]
}

@Throws(Throwable::class)
fun getDexElementsField(dexPathList: Any): Field {
    return findField(dexPathList, "dexElements")
}

@Throws(Throwable::class)
fun getDexElementsByClassLoader(dexPathList: Any?, dexElementsField: Field): Array<Any> {
    return dexElementsField[dexPathList] as Array<Any>
}

// 获取 dexElements 元素集合
@Throws(Throwable::class)
fun getDexElementsByClassLoader(classLoader: ClassLoader): Array<Any> {
    val pathListField = findField(classLoader, "pathList")
    val dexPathList = pathListField[classLoader]!!
    val dexElementsField = findField(dexPathList, "dexElements")
    return dexElementsField[dexPathList] as Array<Any>
}

// 打印 dexElements 元素
@Throws(Throwable::class)
fun printDexElements(classLoader: ClassLoader) {
    for (dexElement in getDexElementsByClassLoader(classLoader)) {
        val pathField = findField(dexElement, "dexFile")
        val file = pathField[dexElement] as DexFile?
        Log.d("zuo", "【dexFile name】" + file?.name)
    }
}
package com.vildanov.randomdog.utils

import com.vildanov.randomdog.data.DogItemExtension
import java.lang.IllegalArgumentException

fun combine(vararg strings: String): String {
    if (strings.isEmpty()) {
        throw IllegalArgumentException("String list is empty")
    }
    var result = strings[0]
    for (i in 1 until strings.size) {
        result += "/${strings[i]}"
    }
    return result
}

fun extractExtension(name: String): String {
    val splitted = name.split(".")
    if (splitted.size > 1 && splitted.last().lowercase() in DogItemExtension.values().map { it.extensionName }) {
        return splitted.last().lowercase()
    }
    throw IllegalArgumentException("There is no valid extension in passed string: $name")
}
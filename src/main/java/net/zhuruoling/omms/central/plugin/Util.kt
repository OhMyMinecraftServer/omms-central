package net.zhuruoling.omms.central.plugin

import java.lang.module.ModuleDescriptor

typealias Version = ModuleDescriptor.Version

fun compareVersion(left: Version, right: Version, comparator: (Version, Version) -> Boolean): Boolean{
    return comparator(left,right)
}
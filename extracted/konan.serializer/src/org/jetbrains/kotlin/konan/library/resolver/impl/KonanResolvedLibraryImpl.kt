package org.jetbrains.kotlin.konan.library.resolver.impl

import org.jetbrains.kotlin.konan.library.KonanLibrary
import org.jetbrains.kotlin.konan.library.resolver.KonanResolvedLibrary
import org.jetbrains.kotlin.serialization.konan.SourceFileMap
import org.jetbrains.kotlin.serialization.konan.parseModuleHeader

internal class KonanResolvedLibraryImpl(override val library: KonanLibrary): KonanResolvedLibrary {

    private val _resolvedDependencies = mutableListOf<KonanResolvedLibrary>()
    private val _emptyPackages by lazy {
        parseModuleHeader(library.moduleHeaderData).emptyPackageList.also { it.forEach { println("Empty: ${library.libraryName}.$it") } }
    }

    override val resolvedDependencies: List<KonanResolvedLibrary>
        get() = _resolvedDependencies

    internal fun addDependency(resolvedLibrary: KonanResolvedLibrary) = _resolvedDependencies.add(resolvedLibrary)

    override var isNeededForLink: Boolean = false
        private set

    override val isDefault: Boolean
        get() = library.isDefault

    override fun markPackageAccessed(fqName: String) {
        if (!isNeededForLink // fast path
                && !_emptyPackages.contains(/*if (fqName == "<root>") "" else*/ fqName)) {
            println("ZZZ: $fqName")
            if (fqName.contains("AppKit"))
                Throwable().printStackTrace()
            isNeededForLink = true
        }
    }

    override fun toString() = "library=$library, dependsOn=${_resolvedDependencies.joinToString { it.library.toString() }}"
}

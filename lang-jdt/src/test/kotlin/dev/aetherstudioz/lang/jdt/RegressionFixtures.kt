package dev.aetherstudioz.lang.jdt

import dev.aetherstudioz.index.ClassNameValue
import dev.aetherstudioz.index.IndexService
import dev.aetherstudioz.testkit.defaultClassNames as kitDefaultClassNames
import dev.aetherstudioz.testkit.defaultPackages as kitDefaultPackages
import dev.aetherstudioz.testkit.fakeIndex as kitFakeIndex

// The deterministic fake IndexService the regression suites query for auto-import / type-position / package
// completion now lives in dev.aetherstudioz.testkit (shared with index-impl and the completion tests). These thin
// re-exports keep the existing lang-jdt call sites unchanged.

fun defaultClassNames(): List<ClassNameValue> = kitDefaultClassNames()

fun defaultPackages(): List<String> = kitDefaultPackages()

fun fakeIndex(
    classNames: List<ClassNameValue> = defaultClassNames(),
    packages: List<String> = defaultPackages(),
): IndexService = kitFakeIndex(classNames, packages)

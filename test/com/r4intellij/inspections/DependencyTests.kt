package com.r4intellij.inspections

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.r4intellij.RTestCase
import org.jetbrains.annotations.NotNull

/**
 * Test if package dependencies are resolved correctly.
 *
 * @author Holger Brandl
 */
//@RunWith(JUnit4::class) // http://stackoverflow.com/questions/2794407/how-to-configure-intellij-for-running-test-with-junit-4
class DependencyTests : RTestCase() {


    override fun configureFixture(@NotNull myFixture: CodeInsightTestFixture) {
        myFixture.enableInspections(UnresolvedReferenceInspection::class.java)
    }

    /**
     * since it is associated with base the package should be resolvable. This should come from  stub-index
     */
    fun testIris() {
        // myFixture.addFileToProject("base.R", readFileAsString(getSkeletonPath("utils").toPath()));

        // rather use actual library here to see if stub-index is working correctly
        createSkeletonLibrary("datasets")
        //        addPckgsToSkeletonLibrary("dplyr")
        doExprTest("iris")
    }


    fun testPackageDataWithoutImport() {
        doExprTest(unresolvedError("nasa")) // todo why unresolved --> should be be captured by import inspection
    }


    fun testPackageData() {
        createSkeletonLibrary("dplyr")
        doExprTest("require(dplyr); nasa")
    }


    fun testTransitiveDependencies() {
        addPckgsToSkeletonLibrary("caret", "ggplot2");
        doExprTest("require(caret); ggplot(iris)")
    }


    fun testForwardImportAfterUsage() {
        createSkeletonLibrary("dplyr", "datasets")
        doExprTest(noImportWarning("glimpse", "dplyr") + "(iris) ; require(dplyr)")
    }


    // see https://github.com/tidyverse/tidyverse/issues/40
    fun testTransitiveTidyverseDependencies() {
        createSkeletonLibrary("dplyr", "datasets")
        doExprTest("require(tidyverse); group_by(iris)")
    }


    fun testTidyrImportMissing() {
        createSkeletonLibrary("tidyr", "datasets")
        doExprTest("${noImportWarning("gather", "tidyr")}(iris)")
    }

    fun testMissingImportInPipe() {
        createSkeletonLibrary("tibble", "magrittr", "datasets")
        doExprTest("require(magrittr); iris %>% ${noImportWarning("glimpse", "tibble")}")
    }


    fun testResolveNamespaceCall() {
        addPckgsToSkeletonLibrary("dplyr")
        doExprTest("dplyr::group_by(iris)")
    }

    fun testMissingOperatorImport() {
        addPckgsToSkeletonLibrary("dplyr") // we use the rexported version here to see if its picked up correctly

        // note: re-exporting packages are not listed intentionally, because origin should be preferred over reexport
        doExprTest("iris ${noImportWarning("%>%", listOf("dplyr"))} head")
    }
}

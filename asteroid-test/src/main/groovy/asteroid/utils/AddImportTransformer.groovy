package asteroid.utils

import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.ast.ClassNode

import asteroid.A
import asteroid.global.ClassNodeTransformer

/**
 * Using a {@link ClassNodeTransformer} to add imports
 *
 * @since 0.1.6
 */
// tag::classnodetransformer[]
class AddImportTransformer extends ClassNodeTransformer { // <1>

    public AddImportTransformer(final SourceUnit sourceUnit) {
        super(sourceUnit, byAnnotationName(AddImport.simpleName)) // <2>
    }

    /**
     * {@inheritDocs}
     */
    @Override
    void transformClass(final ClassNode target) { // <3>
        A.UTIL.CLASS.addImport(target, groovy.json.JsonOutput) // <4>
    }
}
// end::classnodetransformer[]

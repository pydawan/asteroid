package asteroid.internal;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.first;

import java.util.Arrays;
import java.util.List;

import asteroid.A;
import asteroid.AbstractLocalTransformation;
import asteroid.Phase;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.transform.GroovyASTTransformation;

/**
 * This transformation makes easier to declare a given local transformation. It narrows the available
 * compilation phases to those only capable of being used in a local transformation. The way of declaring
 * the transformation makes clearer the fact that it is a local transformation.
 *
 * @since 0.2.0
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class PhaseTransformation extends AbstractLocalTransformation<Phase,ClassNode> {

    private static final String METHOD_DOVISIT = "doVisit";

    /**
     * Constructor using abstraction {@link AbstractLocalTransformation}
     *
     * @since 0.2.0
     */
    public PhaseTransformation() {
        super(Phase.class);
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.2.0
     */
    @Override
    public void doVisit(final AnnotationNode annotationNode, final ClassNode annotatedNode) {
        if(!A.UTIL.NODE.isOrExtends(annotatedNode, AbstractLocalTransformation.class)) {
            return;
        }

        final CompilePhase phase = extractCompilePhaseFrom(annotationNode);

        TransformationUtils.addASTAnnotationsFromTo(annotatedNode, phase);
        addClassConstructor(annotatedNode);

        // tag::addCheckTo[]
        A.UTIL.NODE.addCheckTo(A.UTIL.NODE.findMethodByName(annotatedNode, METHOD_DOVISIT));
        // end::addCheckTo[]
        A.UTIL.NODE.removeAnnotation(annotatedNode, annotationNode);
    }

    private void addClassConstructor(final ClassNode annotatedNode) {
        final List<GenericsType> generics = Arrays.asList(annotatedNode.getSuperClass().getGenericsTypes());
        final ClassNode annotationType    = first(generics).getType();

        final Statement callSuper = A.STMT
            .ctorSuperS(A.EXPR.classX(annotationType));

        final ConstructorNode constructorNode = A.NODES
            .constructor(A.ACC.ACC_PUBLIC)
            .code(callSuper)
            .build();

        annotatedNode.addConstructor(constructorNode);
    }

    private CompilePhase extractCompilePhaseFrom(final AnnotationNode annotationNode) {
        String compilePhaseAsString = A.UTIL.NODE.get(annotationNode, String.class);

        return CompilePhase.valueOf(compilePhaseAsString);
    }
}

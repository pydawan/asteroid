package asteroid.internal;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.first;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.last;

import asteroid.A;
import asteroid.Local;
import groovy.lang.Closure;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

/**
 * Reduces the boiler plate code when declaring an annotation as possible target for a given
 * transformation.
 *
 * It adds code such as the {@link java.lang.annotation.Retention} and {@link java.lang.annotation.Target} annotations as well as the
 * {@link org.codehaus.groovy.transform.GroovyASTTransformationClass}.
 *
 * This code:
 *
 * <pre>
 * <code>
 *   import my.transformation.MyTransformation
 *
 *   {@literal @}Local(MyTransformation)
 *   public @interface MyAnnotation { }
 * </code>
 * </pre>
 *
 * Will become:
 *
 * <pre>
 * <code>
 *    {@literal @}Retention(RetentionPolicy.SOURCE)
 *    {@literal @}Target([ElementType.METHOD])
 *    {@literal @}GroovyASTTransformation("my.transformation.MyTransformation")
 *    public @interface MyAnnotation { }
 * </code>
 * </pre>
 *
 * @since 0.1.0
 *
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class LocalTransformation extends AbstractASTTransformation {

    /**
     * {@inheritDoc}
     *
     * @since 0.1.0
     */
    @Override
    public void visit(final ASTNode[] nodes, final SourceUnit source) {
        this.sourceUnit = source;

        if (!check(nodes)) {
            return;
        }

        final AnnotationNode annotationNode = A.UTIL.NODE.getFirstNodeAs(nodes, AnnotationNode.class);
        final ClassNode      annotatedNode  = A.UTIL.NODE.getLastNodeAs(nodes, ClassNode.class);

        addAnnotationsFromTo(annotationNode, annotatedNode);
    }

    private void addAnnotationsFromTo(final AnnotationNode annotationNode, final ClassNode annotatedNode) {
        final String qualifiedName = A.UTIL.NODE.get(annotationNode, String.class);
        final String target        = A.UTIL.NODE.get(annotationNode, "applyTo", String.class);

        annotatedNode.addAnnotation(getTargetAnnotation(target));
        annotatedNode.addAnnotation(getRetentionAnnotation());
        annotatedNode.addAnnotation(getGroovyAnnotationWith(qualifiedName));
    }

    private AnnotationNode getTargetAnnotation(final String resolvedTarget) {
        final List<ElementType> types = resolveAnnotationTarget(resolvedTarget);
        final ListExpression listExpr = resolveTargetFromElementType(types);

        return A.NODES.annotation(Target.class)
                .member(A.UTIL.NODE.ANNOTATION_VALUE, listExpr)
                .build();
    }

    private ListExpression resolveTargetFromElementType(final List<ElementType> types) {
        final List<PropertyExpression> expressions =
                collect(types, new Closure<PropertyExpression>(null) {
                    PropertyExpression doCall(final ElementType type) {
                        return A.EXPR.propX(
                                A.EXPR.classX(ElementType.class),
                                A.EXPR.constX(type.toString()));
                    }
                });

        return A.EXPR.listX(expressions.toArray(new PropertyExpression[expressions.size()]));

    }

    private List<ElementType> resolveAnnotationTarget(final String target) {
        if (target == null) {
            return Arrays.asList(ElementType.TYPE);
        }

        if ("ANNOTATED".equals(target)) {
            return Arrays
                    .asList(ElementType.ANNOTATION_TYPE,
                            ElementType.CONSTRUCTOR,
                            ElementType.FIELD,
                            ElementType.LOCAL_VARIABLE,
                            ElementType.METHOD,
                            ElementType.PACKAGE,
                            ElementType.PARAMETER,
                            ElementType.TYPE);
        }

        return Arrays.asList(ElementType.valueOf(target));
    }

    private AnnotationNode getRetentionAnnotation() {
        final ClassExpression    classExpr    = A.EXPR.classX(RetentionPolicy.class);
        final ConstantExpression constantExpr = A.EXPR.constX(RetentionPolicy.SOURCE.toString());
        final PropertyExpression propertyExpr = A.EXPR.propX(classExpr, constantExpr);

        return A.NODES.annotation(Retention.class)
                .member(A.UTIL.NODE.ANNOTATION_VALUE, propertyExpr)
                .build();
    }

    private AnnotationNode getGroovyAnnotationWith(final String qualifiedName) {
        final ConstantExpression constant = A.EXPR.constX(qualifiedName);

        return A.NODES.annotation(GroovyASTTransformationClass.class)
                .member(A.UTIL.NODE.ANNOTATION_VALUE, constant)
                .build();
    }

    private boolean check(final ASTNode... nodes) {
        return !(thereIsNoNodes(nodes)                               ||
                thereAreOtherThanTwo(nodes)                          ||
                firstNodeIsNotAnAnnotation(nodes)                    ||
                firstNodeIsNotAnAnnotationOfType(nodes, Local.class) ||
                lastNodeIsNotAnAnnotatedNode(nodes));
    }

    private boolean thereIsNoNodes(final ASTNode... nodes) {
        return nodes == null;
    }

    private boolean thereAreOtherThanTwo(final ASTNode... nodes) {
        return nodes.length != 2;
    }

    private boolean firstNodeIsNotAnAnnotation(final ASTNode... nodes) {
        return !(first(nodes) instanceof AnnotationNode);
    }

    private boolean lastNodeIsNotAnAnnotatedNode(final ASTNode... nodes) {
        return !(last(nodes) instanceof AnnotatedNode);
    }

    private boolean firstNodeIsNotAnAnnotationOfType(final ASTNode[] nodes, final Class annotationType) {
        final AnnotationNode annotation = A.UTIL.NODE.getFirstNodeAs(nodes, AnnotationNode.class);
        final ClassNode annotationClass = A.NODES.clazz(annotationType).build();

        return !annotation.getClassNode().isDerivedFrom(annotationClass);
    }

}

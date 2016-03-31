package asteroid.nodes;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.tools.GenericsUtils;

/**
 * Builder to create instances of type {@link ClassNode}
 *
 * @since 0.1.0
 */
public class ClassNodeBuilder {

    private final ClassNode classNode;

    private ClassNodeBuilder(Class clazz) {
        classNode = new ClassNode(clazz);
    }

    private ClassNodeBuilder(String fullyQualifiedName) {
        this.classNode = ClassHelper.make(fullyQualifiedName);
    }

    private ClassNodeBuilder(ClassNode classNode) {
        this.classNode = classNode;
    }

    /**
     * Creates a new instance of {@link ClassNodeBuilder} by using the
     * {@link Class} type.
     * <br><br>
     *
     * <strong>AST</strong>
     * <pre><code>clazz(String)
     * .build()</code></pre>
     *
     * <strong>Result</strong>
     * <pre><code>String.class</code></pre>
     *
     * @param clazz
     * @return an instance of {@link ClassNodeBuilder}
     * @since 0.1.0
     */
    public static ClassNodeBuilder clazz(Class<?> clazz) {
        return new ClassNodeBuilder(clazz);
    }

    /**
     * Creates a new instance of {@link ClassNodeBuilder} by using
     * the fully qualified name of a {@link Class}
     * <br><br>
     *
     * <strong>AST</strong>
     * <pre><code>clazz('java.lang.String')
     * .build()</code></pre>
     *
     * <strong>Result</strong>
     * <pre><code>String.class</code></pre>
     *
     * @param fullyQualifiedName a {@link String} withe the complete
     * name of the class
     * @return current instance of {@link ClassNodeBuilder}
     * @since 0.1.0
     */
    public static ClassNodeBuilder clazz(String fullyQualifiedName) {
        return new ClassNodeBuilder(fullyQualifiedName);
    }

    /**
     * Creates a new instance of {@link ClassNodeBuilder} by using
     * the fully qualified name of a {@link Class}
     *
     * @param clazz The type of the class node
     * @param genericsTypes the generic types the class node will have
     * @return current instance of {@link ClassNodeBuilder}
     * @since 0.1.0
     */
    public static ClassNodeBuilder clazzWithGenerics(Class<?> clazz, GenericsType... genericsTypes) {
        return new ClassNodeBuilder(GenericsUtils
                .makeClassSafeWithGenerics(
                        ClassHelper.make(clazz),
                        genericsTypes
                ));
    }

    /**
     * Informs the builder whether to use generics in the resulting {@link ClassNode}
     * or not.
     *
     * @param useGenerics true if it should use generics
     * @return current instance of {@link ClassNodeBuilder}
     * @since 0.1.0
     */
    public ClassNodeBuilder usingGenerics(Boolean useGenerics) {
        this.classNode.setUsingGenerics(useGenerics);
        return this;
    }

    /**
     * Informs the builder whether to use a generic placeholder in the
     * resulting {@link ClassNode} or not.
     *
     * @param usePlaceholder true if the builder should use it
     * @return current instance of {@link ClassNodeBuilder}
     * @since 0.1.0
     */
    public ClassNodeBuilder genericsPlaceHolder(Boolean usePlaceholder) {
        this.classNode.setGenericsPlaceHolder(usePlaceholder);
        return this;
    }

    /**
     * Sets generic types for the resulting {@link ClassNode}
     *
     * @param genericsTypes
     * @return current instance of {@link ClassNodeBuilder}
     * @since 0.1.0
     */
    public ClassNodeBuilder genericsTypes(GenericsType... genericsTypes) {
        this.classNode.setGenericsTypes(genericsTypes);
        return this;
    }

    /**
     * Returns the instance of type {@link ClassNode} configured by
     * this builder
     *
     * @return an instance of {@link ClassNode}
     * @since 0.1.0
     */
    public ClassNode build() {
        return this.classNode;
    }

}

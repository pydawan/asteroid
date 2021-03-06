package asteroid.nodes;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GenericsType;

/**
 * Builder to create instances of type {@link GenericsType}
 *
 * @since 0.1.0
 */
final public class GenericsTypeBuilder {

    private final GenericsType genericsType;

    private GenericsTypeBuilder(final ClassNode classNode) {
        this.genericsType = new GenericsType(classNode);
    }

    /**
     * @param classNode
     * @return an instance of type {@link GenericsTypeBuilder}
     */
    public static GenericsTypeBuilder generics(final ClassNode classNode) {
        return new GenericsTypeBuilder(classNode);
    }

    /**
     * @return an instance of type {@link GenericsType}
     */
    public GenericsType build() {
        return this.genericsType;
    }
}

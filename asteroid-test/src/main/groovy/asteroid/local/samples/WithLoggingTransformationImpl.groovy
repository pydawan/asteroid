package asteroid.local.samples

import asteroid.A
import asteroid.local.LocalTransformation
import asteroid.local.LocalTransformationImpl
import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.control.SourceUnit

@CompileStatic
@LocalTransformation(A.PHASE_LOCAL.SEMANTIC_ANALYSIS) // <1>
class WithLoggingTransformationImpl extends LocalTransformationImpl<WithLogging, MethodNode> {

    @Override
    void doVisit(final AnnotationNode annotation, final MethodNode methodNode) {
        def oldCode   = methodNode.code   // <2>
        def startCode = printlnS("start") // <3>
        def endCode   = printlnS("end")   // <4>

        methodNode.code = A.STMT.blockS(startCode, oldCode, endCode) // <5>
    }

    Statement printlnS(String message) {
        return A.STMT.stmt(A.EXPR.callThisX("println", A.EXPR.constX(message))) // <6>
    }
}

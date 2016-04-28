package asteroid.utils

import java.util.List
import java.util.ArrayList

import asteroid.A
import asteroid.global.Transformer
import asteroid.global.GlobalTransformation
import asteroid.global.GlobalTransformationImpl

@GlobalTransformation(A.PHASE_GLOBAL.CONVERSION)
class AddImportImpl extends GlobalTransformationImpl {

    @Override
    List<Class<? extends Transformer>> getTransformers() {
        return [AddImportTransformer]
    }
}
package innovimax.quixproc.datamodel.generator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import innovimax.quixproc.datamodel.generator.AGenerator.FileExtension;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Generator {
	FileExtension ext();
}

package nz.co.senanque.madura_objects_plugin;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

/**
 * This just runs the plugin under a test. The result is the generated classes.
 * There is no automated verification of these files, they need to be manually inspected
 * for correctness.
 * 
 * @author Roger Parkinson
 *
 */
public class MOMojoTest {

	@Test
	public void testExecuteWithLogging() throws MojoExecutionException, MojoFailureException {
		MOMojo mojo = new MOMojo();
		File generateDirectory = new File("target/generate");
		generateDirectory.mkdirs();
		mojo.setSchemaDirectory(new File("src/test/resources"));
		mojo.setSchemaIncludes(new String[]{"Customer.xsd"});
		mojo.setGenerateDirectory(generateDirectory);
		mojo.setProject(new MavenProject());
		mojo.execute();
	}

}

/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nz.co.senanque.madura_objects_plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.peachjean.slf4j.mojo.AbstractLoggingMojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.jfrog.maven.annomojo.annotations.MojoParameter;
import org.jvnet.mjiip.v_2.XJC2Mojo;

/**
 * Goal which generates java objects used by Madura Objects
 * 
 * @goal generate
 * 
 * @phase generate-sources
 */
public class MOMojo
    extends AbstractLoggingMojo
{
	
	/**
	 * Project
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;
	
	public MavenProject getProject() {
		return project;
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}


	/**
	 * schemadirectory
	 * 
	 * @parameter expression="src/main/resources/"
	 * @required
	 */
	private File schemaDirectory;

	/**
	 * The source directory containing *.xsd schema files. Notice that binding
	 * files are searched by default in this directory.
	 * 
	 */
	public File getSchemaDirectory() {
		return schemaDirectory;
	}

	public void setSchemaDirectory(File schemaDirectory) {
		this.schemaDirectory = schemaDirectory;
	}

	private String[] schemaIncludes = new String[] { "*.xsd" };

	/**
	 * <p>
	 * A list of regular expression file search patterns to specify the schemas
	 * to be processed. Searching is based from the root of
	 * <code>schemaDirectory</code>.
	 * </p>
	 * <p>
	 * If left udefined, then all *.xsd files in schemaDirectory will be
	 * processed.
	 * </p>
	 * 
	 */
	@MojoParameter(description = "Specifies file patterns to include as schemas. Default value is *.xsd.")
	public String[] getSchemaIncludes() {
		return schemaIncludes;
	}

	public void setSchemaIncludes(String[] schemaIncludes) {
		this.schemaIncludes = schemaIncludes;
	}

	private String[] schemaExcludes;

	/**
	 * A list of regular expression file search patterns to specify the schemas
	 * to be excluded from the <code>schemaIncludes</code> list. Searching is
	 * based from the root of schemaDirectory.
	 * 
	 */
	@MojoParameter(description = "Specifies file patterns of schemas to exclude. By default, nothing is excluded.")
	public String[] getSchemaExcludes() {
		return schemaExcludes;
	}

	public void setSchemaExcludes(String[] schemaExcludes) {
		this.schemaExcludes = schemaExcludes;
	}
	/**
	 * generateDirectory
	 * 
	 * @parameter expression="${basedir}/generated-sources/xjc"
	 * @required
	 */
	private File generateDirectory;

	/**
	 * <p>
	 * Generated code will be written under this directory.
	 * </p>
	 * <p>
	 * For instance, if you specify <code>generateDirectory="doe/ray"</code> and
	 * <code>generatePackage="org.here"</code>, then files are generated to
	 * <code>doe/ray/org/here</code>.
	 * </p>
	 */
	@MojoParameter(defaultValue = "${project.build.directory}/generated-sources/xjc", expression = "${maven.xjc2.generateDirectory}", required = true, description = "Target directory for the generated code.")
	public File getGenerateDirectory() {
		return generateDirectory;
	}

	public void setGenerateDirectory(File generateDirectory) {
		this.generateDirectory = generateDirectory;

	}

	private List<String> args = new LinkedList<String>();

	/**
	 * <p>
	 * A list of extra XJC's command-line arguments (items must include the dash
	 * '-').
	 * </p>
	 * <p>
	 * Arguments set here take precedence over other mojo parameters.
	 * </p>
	 */
	@MojoParameter(description = "A list of extra XJC's command-line arguments (items must include the dash \"-\"). Use this argument to enable the JAXB2 plugins you want to use.")
	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args.addAll(args);
	}

	public void executeWithLogging()
        throws MojoExecutionException
    {
    	XJC2Mojo mojo = new XJC2Mojo();
    	mojo.setProject(getProject());
    	mojo.setSchemaDirectory(schemaDirectory);
    	mojo.setExtension(true);
    	mojo.setSchemaIncludes(schemaIncludes);
    	mojo.setSchemaExcludes(schemaExcludes);
    	mojo.setGenerateDirectory(generateDirectory);
    	mojo.setAddCompileSourceRoot(true);
    	mojo.setForceRegenerate(true);
    	mojo.setRemoveOldOutput(false);
    	args.addAll(getInitialList() );
    	mojo.setArgs(args);
    	mojo.execute();
    }
	
	private List<String> getInitialList() {
		List<String> initialList = new ArrayList<>();
		initialList.add("-extension");
		initialList.add("-extension");
		initialList.add("-Xequals");
		initialList.add("-XtoString");
		initialList.add("-Xannotate");
		initialList.add("-XhashCode");
		initialList.add("-Xhyperjaxb3-ejb");
		initialList.add("-Xmadura-objects");
		return initialList;
	}

}

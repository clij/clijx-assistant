/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2020 ImageJ developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.haesleinhuepf.clijx.te_oki;

import ij.IJ;
import net.haesleinhuepf.clijx.assistant.options.AssistantOptions;
import net.imagej.legacy.IJ1Helper;
import net.imagej.legacy.plugin.IJ1MacroEngine;
import org.scijava.ui.swing.script.TextEditor;
import org.scijava.util.ProcessUtils;

import javax.script.ScriptException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Robert Haase
 */
public class TeOkiEngine extends IJ1MacroEngine {

	public static String teOkiDirectory = null;

	public TeOkiEngine(IJ1Helper ij1Helper) {
		super(ij1Helper);
		if (teOkiDirectory == null && IJ.getDirectory("imagej") != null) {
			teOkiDirectory = IJ.getDirectory("imagej") + "/";
		}
		if (teOkiDirectory == null || teOkiDirectory.startsWith("null")) {
			teOkiDirectory = "./";
		}
	}

	@Override
	public Object eval(final String macro) throws ScriptException {

		new Thread(new Runnable() {
			@Override
			public void run() {

				boolean isWindows = System.getProperty("os.name")
						.toLowerCase().startsWith("windows");

				System.out.println(teOkiDirectory);
				File directory = new File(teOkiDirectory);

				String conda_code;

				if (isWindows) {
					conda_code = //"call " + conda_directory + "\\Scripts\\activate.bat " + conda_directory + "\n" +
							"call " + AssistantOptions.getInstance().getCondaPath() + "conda activate " + AssistantOptions.getInstance().getCondaEnv() + "\n" +
							"cd " + directory + "\n" +
							"ipython --gui=qt temp.py";
				} else {
					conda_code = AssistantOptions.getInstance().getCondaPath() + "conda activate " + AssistantOptions.getInstance().getCondaEnv() + "\n" +
							"cd " + directory + "\n" +
							"ipython --gui=qt  temp.py";
				}

				System.out.println(conda_code);

				PrintStream out = new PrintStream(new OutputStream() {
					@Override
					public void write(int b) throws IOException {
						//IJ.log("" + b);
					}

					@Override
					public void write(byte[] b) throws IOException {
						//IJ.log(new String(b));
					}

					@Override
					public void write(byte[] b, int off, int len) throws IOException {
						byte[] a = new byte[len];
						System.arraycopy(b, off, a, 0, len);
						//IJ.log("" + len);
						if (a.length > 2) {
							IJ.log(new String(a));
						}
					}
				});
				try {
					Files.write(Paths.get(directory + "/temp.py"), macro.getBytes());
					Files.write(Paths.get(directory + "/temp.bat"), conda_code.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}

				//parent.getTab().showErrors();
				try {
					//String exec = ProcessUtils.exec(directory, out, out, "conda activate " + conda_env, "ipython --gui=qt  temp.py");
					ProcessUtils.exec(directory, out, out, directory + "/temp.bat");
					//IJ.log(exec);
					//parent.errorHandler = handler.errorHandler;
				} catch (RuntimeException e) {
					e.printStackTrace();
				}

				IJ.log("Te Oki: Bye.");
			}
		}).start();

		return null;
	}

	public static void main(String[] args) throws IOException, ScriptException {
		String content = "print('Hello world')"; //new String(Files.readAllBytes(Paths.get(teOkiDirectory + "te_oki.py")));

		new TeOkiEngine(null).eval(content);
	}
}

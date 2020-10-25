/*-
 * #%L
 * Search framework for SciJava applications.
 * %%
 * Copyright (C) 2017 - 2018 SciJava developers.
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

package net.haesleinhuepf.clijx.assistant.search;

import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.utilities.HasAuthor;
import net.haesleinhuepf.clij2.utilities.HasLicense;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import org.scijava.search.SearchResult;

import java.util.HashMap;
import java.util.Map;

import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.*;

public class AssistantPluginSearchResult implements SearchResult {

	private static final String ICON = "/icons/search/clijx_logo.png";

	private final Map<String, String> props = new HashMap<>();
	private AssistantGUIPlugin plugin;

	public AssistantPluginSearchResult(AssistantGUIPlugin plugin) {
		this.plugin = plugin;

		CLIJMacroPlugin clijplugin = plugin.getCLIJMacroPlugin();
		if (clijplugin != null) {
			if (clijplugin instanceof OffersDocumentation){
				props.put("Description", ((OffersDocumentation) clijplugin).getDescription());
				props.put("available_for", ((OffersDocumentation) clijplugin).getAvailableForDimensions());
			}
			props.put("parameters", clijplugin.getParameterHelpText());
			if (clijplugin instanceof HasAuthor) {
				props.put("Author", ((HasAuthor) clijplugin).getAuthorName());
			}
			props.put("jar", jarFromClass(clijplugin.getClass()));
			props.put("class", clijplugin.getClass().toString());
			if (clijplugin instanceof HasLicense) {
				props.put("License", ((HasLicense) clijplugin).getLicense());
			}
			props.put("clEsperanto compatibilty",getCompatibilityString(clijplugin.getName()));
		}
	}

	@Override
	public String name() {
		return niceName(plugin.getName()) + "(" + distributionName(plugin.getCLIJMacroPlugin().getClass()) + ", " +  getCompatibilityString(plugin.getName())  + ")";
	}

	@Override
	public String identifier() {
		return name();
	}

	@Override
	public String iconPath() {
		return ICON;
	}

	@Override
	public Map<String, String> properties() {
		return props;
	}

	public AssistantGUIPlugin getPlugin() {
		return plugin;
	}
}

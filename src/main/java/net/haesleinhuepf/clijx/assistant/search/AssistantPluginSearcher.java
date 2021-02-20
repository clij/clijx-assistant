/*
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

import ij.IJ;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.services.MenuService;
import net.imglib2.img.array.ArrayImgs;
import org.scijava.MenuEntry;
import org.scijava.MenuPath;
import org.scijava.Priority;
import org.scijava.app.AppService;
import org.scijava.module.ModuleInfo;
import org.scijava.module.ModuleService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.search.SearchResult;
import org.scijava.search.Searcher;
import org.scijava.search.module.ModuleSearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.getCompatibilityString;
import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.isAdvancedPlugin;

@Plugin(type = Searcher.class)
public class AssistantPluginSearcher implements Searcher {

	@Parameter
	private ModuleService moduleService;

	@Parameter
	private AppService appService;

	@Override
	public String title() {
		return "CLIJx-Assistant";
	}

	@Override
	public List<SearchResult> search(final String text, final boolean fuzzy) {
		if (text.isEmpty()) return Collections.emptyList();

		ArrayList<SearchResult> list = new ArrayList<>();

		MenuService service = MenuService.getInstance();

		String[] names_and_tags = service.getNamesAndTags();
		String[] search_items = text.toLowerCase().trim().split(" ");
		for (String name_and_tags : names_and_tags) {
			String name_lower = name_and_tags.toLowerCase();
			boolean ok = true;
			for (String search_item : search_items) {
				if (!name_lower.contains(search_item)) {
					ok = false;
					break;
				}
			}
			if (ok) {
				AssistantGUIPlugin plugin = service.getPluginByName(name_and_tags.split(",")[0]);
				if (plugin != null) {
					list.add(new AssistantPluginSearchResult(plugin));
				}
			}
		}

		return list;
	}

	// -- Utility methods --

	/**
	 * Gets a human-readable title for the module, or null if none.
	 * <p>
	 * We do not use {@link ModuleInfo#getTitle()} because that method tries very
	 * hard to return something in every case, whereas we only want to give really
	 * nice titles, or null if the module is inappropriate.
	 * </p>
	 */
	public static String title(final ModuleInfo info) {
		// use object label, if available
		final String label = info.getLabel();
		if (label != null && !label.isEmpty()) return label;

		// use name of leaf menu item, if available
		final MenuPath menuPath = info.getMenuPath();
		if (menuPath != null && menuPath.size() > 0) {
			final MenuEntry menuLeaf = menuPath.getLeaf();
			final String menuName = menuLeaf.getName();
			if (menuName != null && !menuName.isEmpty()) return menuName;
		}

		return null;
	}

	/** Gets the icon path associated with the given module. */
	public static String iconPath(final ModuleInfo info) {
		final String iconPath = info.getIconPath();
		if (iconPath != null) return iconPath;
		final MenuPath menuPath = info.getMenuPath();
		return menuPath == null || menuPath.getLeaf() == null ? //
			null : menuPath.getLeaf().getIconPath();
	}

	/** Gets an abbreviated location for the given module. */
	public static String location(final ModuleInfo info, final String baseDir) {
		String path = info.getLocation();
		if (path == null) return null;
		if (path.startsWith("file:/")) path = path.replaceFirst("file:/+", "/");
		if (baseDir != null && path.startsWith(baseDir)) {
			if (path.length() == baseDir.length()) return "";
			path = path.substring(baseDir.length() + 1);
		}
		return path;
	}

	// -- Helper methods --

	private boolean isGoodModule(final ModuleInfo info) {
		return info.isVisible() && info.isEnabled() && title(info) != null;
	}

	private boolean startsWith(final ModuleInfo info, final String desiredLower) {
		final String title = title(info);
		return title != null && title.toLowerCase().startsWith(desiredLower);
	}

	private boolean hasSubstring(final ModuleInfo info,
		final String desiredLower)
	{
		final String title = title(info);
		return title != null && //
			title.toLowerCase().matches(".*" + desiredLower + ".*");
	}
}

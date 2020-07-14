run("Close All");

open("C:/structure/data/clincubator_data/Lund_HSCMaxP_6h_10h.tif");

measures = split("average_distance_of_touching_neighbors loca_standard_deviation_of_average_distance_of_touching_neighbors average_distance_of_1_closest_points average_distance_of_2_closest_points spot_detection__ average_distance_of_3_closest_points label_map average_distance_of_6_closest_points voronoi_diagram average_distance_of_10_closest_points mesh_touching_neighbors spot_density_locally_(radius_10) distance_mesh_of_touching_neighbors spot_density_locally_(radius_25) touch_count_mesh spot_density_locally_(radius_50) touch_portion_mesh spot_density_locally_(radius_75) mean_touch_portion_of_touching_neighbors mean_intensity number_of_touching_neighbors minimum_intensity local_mean_number_of_touching_neighbors maximum_intensity local_median_number_of_touching_neighbors standard_deviation_intensity local_standard_deviation_number_of_touching_neighbors pixel_count mean_distance_to_centroid mean_distance_to_mass_center max_distance_to_centroid max_distance_to_mass_center max_mean_distance_to_centroid_ratio max_mean_distance_to_mass_center_ratio", " ");

target_path = "C:/structure/code/incubator/images/";

for (i = 0; i < lengthOf(measures); i++) {
	measure = measures[i];
	run("Neighbor analysis frame by frame on multiple GPUs (experimental)", "input_type=[Raw image] background_subtraction_radius=0 spot_detection_blur_sigma=3 min=70 max=3.4e38 min_0=0 max_0=3.4e38 fill exclude_labels_on_edges " + measure);
	getDimensions(width, height, channels, slices, frames);
	Stack.setFrame(frames / 2);

	run("Enhance Contrast", "saturated=0.35");
	run("8-bit");

	run("Duplicate...", " ");
	saveAs("png", target_path + measure + "_single.png");	
	close();

	run("Animated Gif ... ", "name=whatever set_global_lookup_table_options=[Do not use] optional=[] image=[No Disposal] set=200 number=1000 transparency=[No Transparency] red=0 green=0 blue=0 index=0 filename=[" + target_path + measure + ".gif" + "]");
	close();
	
	IJ.log("## " + measure);
	IJ.log("![Image](images/" + measure + "_single.png);");
	IJ.log("![Image](images/" + measure + ".gif);");

	//break;
}

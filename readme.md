# CLIJx-Assistant
[CLIJx-Assistant](https://clij.github.io/assistant) is an intuitive user interface for building custom GPU-accelerated image processing workflows using [CLIJ2](https://clij.github.io) in [Fiji](https://fiji.sc). 
It visualizes workflows as image date flow graphs while building them. 
It suggests what to do next and generates scripts and human readable protocols to facilitate reproducible bio-image analysis. 
These generated scripts also be executed in other platforms such as Matlab, Icy, Python and QuPath.

CLIJx-Assistant is under development and is subject to change. 
Give it a try and let us know what you think!
Do not use it for routine research yet.  
Provide feedback and stay tuned.

<iframe src="images/clijxa_teaser1_fast.mp4" width="800" height="640"></iframe>
[Download slower version of the video](images/clijxa_teaser1.mp4) [Image data source: Daniela Vorkel, Myers lab, CSBD / MPI CBG]

## Overview
* Introduction
  * [Installation](https://clij.github.io/assistant/installation)
  * [Building workflows](https://clij.github.io/assistant/getting_started)
  * [Saving and loading workflows](https://clij.github.io/assistant/save_and_load)
  * [Undo parameter changes](https://clij.github.io/assistant/undo)

* Filtering / correction
  * [Image filtering](https://clij.github.io/assistant/filtering)
  * [Drift correction](https://clij.github.io/assistant/drift_correction)

* Transformation
  * [Maximum projection](https://clij.github.io/assistant/intensity_projection)
  * [Crop, Pan & zoom](https://clij.github.io/assistant/crop_pan_zoom)
  * [Multi-channel image visualisation](https://clij.github.io/assistant/multi_channel_support)
  * [Cylinder projection](https://clij.github.io/assistant/cylinder_projection)
  * [Sphere projection](https://clij.github.io/assistant/sphere_projection)

* Regionalisation
  * [Nuclei segmentation](https://clij.github.io/assistant/segmentation_nuclei)
  * [Cell segmentation based on membranes](https://clij.github.io/assistant/segmentation_cells)
  * [Optimize parameters for binarization](https://clij.github.io/assistant/parameter_optimization)
  * [Pixel classifier (Weka)](https://clij.github.io/assistant/clijx_weka_pixel_classifier)

* Analysis
  * [Cell neighbor analysis](https://clij.github.io/assistant/neighbor_analysis_generated)
  * [Distance analysis using MorholibJ extension](https://clij.github.io/assistant/morpholibj_chamfer_distance_map)
  * [Label classifier (Weka)](https://clij.github.io/assistant/clijx_weka_label_classifier)

* Reproducibility / interoperability
  * [Export workflows as ImageJ Script](https://clij.github.io/assistant/macro_export)
  * [Export human readable protocols and ImageJ Macro Markdown notebooks](https://clij.github.io/assistant/supplementary_methods_section_generator)
  * [Generate CLIJx / Fiji plugins](https://clij.github.io/assistant/generate_clijx_plugins)
  * [Export as Icy Protocol](https://clij.github.io/assistant/icy_protocol_export)
  * [Export Groovy Script for QuPath](https://clij.github.io/assistant/export_to_clupath)
  * [Export workflows as Python script using clEsperanto and Napari](https://clij.github.io/assistant/te_oki_export)

* [Reference](https://clij.github.io/assistant/reference)

* Extensibility
  * [CLIJ2 Plugin template](https://github.com/clij/clij2-plugin-template)
  * [CLIJ2 imglib2 extensions](https://github.com/clij/clijx-assistant-imglib2)
  * [CLIJ2 ImageJ extensions](https://github.com/clij/clijx-assistant-imagej)
  * [CLIJ2 ImageJ2 extensions](https://github.com/clij/clijx-assistant-imagej2)
  * [CLIJ2 BoneJ extensions](https://github.com/clij/clijx-assistant-bonej)
  * [CLIJ2 MorphoLibJ extensions](https://github.com/clij/clijx-assistant-morpholibj)
  * [CLIJ2 ImageJ 3D Suite extensions](https://github.com/clij/clijx-assistant-imagej3dsuite)
  * [CLIJ2 SimpleITK extensions](https://github.com/clij/clijx-assistant-simpleitk)

## Feedback welcome!
I'm eager to receiving feedback: rhaase at mpi minus cbg dot de

[Imprint](https://clij.github.io/imprint)

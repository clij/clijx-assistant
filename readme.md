# CLIJx-Assistant
[CLIJx-Assistant](https://clij.github.io/assistant) is an intuitive user interface for building custom GPU-accelerated image processing workflows using [CLIJ2](https://clij.github.io) in [Fiji](https://fiji.sc). 
It suggests what to do next, optimizes parameters automatically and generates scripts for reproducing workflows in various scripting languages.

CLIJx-Assistant is under development and is subject to change. 
Please treat everything with care.
Do not use it for routine research yet. 
Planned release is early 2021. 
Stay tuned.

![Image](images/suggestions.png)


## Overview
* [Installation](https://clij.github.io/assistant/installation)
* [Getting started](https://clij.github.io/assistant/getting_started)
* [Reference](https://clij.github.io/assistant/reference)

* [Image filtering](https://clij.github.io/assistant/filtering)
* [Drift correction](https://clij.github.io/assistant/drift_correction)
* Intensity correction

* [Maximum projection](https://clij.github.io/assistant/intensity_projection)
* [Multi-channel image visualisation](https://clij.github.io/assistant/multi_channel_support)
* [Cylinder projection](https://clij.github.io/assistant/cylinder_projection)
* [Sphere projection](https://clij.github.io/assistant/sphere_projection)
* Spot detection
* Cell segmentation
  * [Nuclei segmentation](https://clij.github.io/assistant/segmentation_nuclei)
  * [Cell segmentation based on membranes](https://clij.github.io/assistant/segmentation_cells)
  * [Pixel classifier (Weka)](https://clij.github.io/assistant/clijx_weka_pixel_classifier)
* [Cell neighbor analysis](https://clij.github.io/assistant/neighbor_analysis_generated)

* [Parameter optimization](https://clij.github.io/assistant/parameter_optimization)

* [Export workflows as ImageJ Script](https://clij.github.io/assistant/macro_export)
* [Export workflows as Python script using clEsperanto and Napari](https://clij.github.io/assistant/te_oki_export)

* Extensibility
  * [CLIJ2 Plugin template](https://github.com/clij/clij2-plugin-template)
  * [CLIJ2 imglib2 example plugin](https://github.com/haesleinhuepf/clijx-assistant-imglib2)
  * [CLIJ2 ImageJ example plugin](https://github.com/haesleinhuepf/clijx-assistant-imagej)
  * [CLIJ2 ImageJ2 example plugin](https://github.com/haesleinhuepf/clijx-assistant-imagej2)


## Instant feedback
You can fine tune parameters of your workflow while inspecting different z-planes or time points of your data set and see results instantly.
<iframe src="images/incubator_segmentation_intro.mp4" width="500" height="800"></iframe>
[Download video](images/incubator_segmentation_intro.mp4) [Image data source: Daniela Vorkel, Myers lab, CSBD / MPI CBG]




## Wish list and known issues
* Add menu entry for "Apply to all time points"
* If a plugin has three parameters, e.g. "sigma_x", "sigma_y" and "sigma_z", allow to change these three together with one click.
* Make dialogs use physical units in general or switchable between pixel units and physical units.

Also have an idea for improving CLIJx-Incubator? Let me know! I'm eager to receiving feedback: rhaase at mpi minus cbg dot de




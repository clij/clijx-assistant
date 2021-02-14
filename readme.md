# CLIJx-Assistant
[CLIJx-Assistant](https://clij.github.io/assistant) is an intuitive user interface for building custom GPU-accelerated image processing workflows using [CLIJ2](https://clij.github.io) in [Fiji](https://fiji.sc). 
It visualizes workflows as image date flow graphs while building them. 
It suggests what to do next and generates scripts and human readable protocols to facilitate reproducible bio-image analysis. 
These generated scripts also be executed in other platforms such as Matlab, Icy, Python and QuPath.

If you use CLIJx-assistant, pleace cite it: 

Robert Haase, Akanksha Jain, Stephane Rigaud, Daniela Vorkel, Pradeep Rajasekhar, Theresa Suckert, Talley J Lambert, Juan Nunez-Iglesias, Daniel P Poole, Pavel Tomancak, Eugene W Myers. Interactive design of GPU-accelerated Image Data Flow Graphs and cross-platform deployment using multi-lingual code generation. [BioRxiv preprint](https://www.biorxiv.org/content/10.1101/2020.11.19.386565v1)

CLIJx-Assistant is under development and is subject to change. 
Give it a try and let us know what you think!
Do not use it for routine research yet. 
Stay tuned.

![Image](images/teaser_landscape.gif)
[Image data source: Daniela Vorkel, Myers lab, CSBD / MPI CBG]

## Overview
* Introduction
  * [Installation](https://clij.github.io/assistant/installation)
  * [Building workflows](https://clij.github.io/assistant/getting_started)
  * [Saving and loading workflows](https://clij.github.io/assistant/save_and_load)
  * [Undo parameter changes](https://clij.github.io/assistant/undo)

* Filtering / correction
  * [Image filtering](https://clij.github.io/assistant/filtering)
  * [Gamma correction](https://clij.github.io/assistant/gamma_correction)
  * [Drift correction](https://clij.github.io/assistant/drift_correction)

* Transformation
  * [Intensity projection](https://clij.github.io/assistant/intensity_projection)
  * [Crop, Pan & zoom](https://clij.github.io/assistant/crop_pan_zoom)
  * [Cylinder projection](https://clij.github.io/assistant/cylinder_projection)
  * [Sphere projection](https://clij.github.io/assistant/sphere_projection)
  * [Drift correction](https://clij.github.io/assistant/drift_correction)

* Regionalisation
  * [Nuclei segmentation](https://clij.github.io/assistant/segmentation_nuclei)
  * [Cell segmentation based on membranes](https://clij.github.io/assistant/segmentation_cells)
  * [Optimize parameters for binarization](https://clij.github.io/assistant/parameter_optimization)
  * [Label classification](https://clij.github.io/assistant/clijx_weka_label_classifier)

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

## Acknowledgements
We would like to thank everybody who helped developing, testing and motivating this project. In particular thanks go to 
Akanksha Jain (EPFL Basel),
Bert Nitzsche (PoL TU Dresden),
Bradley Lowekamp (NIAID Washington),
Bruno C. Vellutini (MPI CBG Dresden),  
Christian Tischer (EMBL Heidelberg),
Daniela Vorkel (MPI CBG Dresden), 
Eugene W. Myers (MPI CBG Dresden)
Florian Jug (MPI CBG Dresden), 
Gayathri Nadar (MPI CBG Dresden),
Irene Seijo Barandiaran (MPI CBG Dresden),
Johannes Girstmair (MPI CBG Dresden),
Juan Nunes-Iglesias (Monash University Melbourne),
Kisha Sivanathan (Harvard Medical School Boston),
Lior Pytowski (University of Oxford),
Marion Louveaux (Institut Pasteur Paris),
Matthias Arzt (MPI CBG Dresden),
Nik Cordes,
Noreen Walker (MPI CBG Dresden),
Pavel Tomancak (MPI CBG Dresden),
Pete Bankhead (University of Edinburgh),
Pradeep Rajasekhar (Monash University Melbourne),
Romain Guiet (EPFL Lausanne),
Sebastian Munck (VIB Leuven),
Stéphane Dallongeville (Institut Pasteur)
Stéphane~Rigaud (Institut Pasteur Paris),
Stein Rørvik,
Talley J. Lambert (Harvard Medical School Boston),
Tanner Fadero (U Chapel Hill),
Theresa Suckert (OncoRay, TU Dresden),

Furthermore, the constant support by the Image Science and the NEUBIAS communities is fantastic.
 
This work was supported by the German Federal Ministry of Research and Education (BMBF) under the code 031L0044 (Sysbio II).

## Feedback welcome!
I'm eager to receiving feedback: rhaase at mpi minus cbg dot de

[Imprint](https://clij.github.io/imprint)

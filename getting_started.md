# Getting started designing image processing workflows with the CLIJx Assistant
Open your 3D+channel+time data set. It's recommended to start the assistant from a file that has been loaded from or saved to disk.
Afterwards, activate CLIJx-Assistant by clicking on its tool icon.

![Image](images/installation_ok.png)

## Building workflows - step by step
CLIJx-Assistant has a built-in suggestions of what to do next: 
Just right click in any image that has the assistant attached.

![Image](images/suggestion_make_isotropic.png) 
[Image data source: Irene Seijo Barandiaran, Grapin-Botton lab, MPI CBG]

Consider the suggestions but also explore the categories of all available operations. 

![Image](images/menu_rigid_tranform.png)
[Image data source: Irene Seijo Barandiaran, Grapin-Botton lab, MPI CBG]

You also find all CLIJx-Assistant operations in Fijis search bar. They have their own category to not be mixed up with
CLIJ2 operations:

![Image](images/fiji_search.png)

## Interoperability with classical ImageJ and Fiji operations
As CLIJx-Assistant runs in classical ImageJ windows, you can use ImageJ operations on the shown images. 
However, they may be overwritten as soon as CLIJx-Assistant recomputes its results.
Thus, it is recommended to duplicate an image before applying classical functions to it. 
You can use ImageJ's `Duplicate...` menu or the built in menu:

![Image](images/interoperability_imagej.png)

## The assistant in action
<iframe src="images/basic_usage.mp4" width="600" height="300"></iframe>
[Download video](images/basic_usage.mp4)

If you want to keep an eye on memory usage in the GPU, 
the menu `Plugins > ImageJ on GPU (CLIJx) > Memory Display` allows you to overview available memory and memory consumption while building your workflow.

![Image](images/memory_display.png)

Back to [CLIJx-Assistant](https://clij.github.io/assistant)

[Imprint](https://clij.github.io/imprint)


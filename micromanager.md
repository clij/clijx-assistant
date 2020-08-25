# Integration in MicroManager
To install CLIJx-Incubator to [MicroManager](), download a recent MicroManager gamma 
[nightly build](https://valelab4.ucsf.edu/~MM/nightlyBuilds/2.0.0-gamma/Windows/),
[clij2-imagej](https://github.com/clij/clij2-imagej1/releases/download/2.1.1.0/clij2-image1_-2.1.1.0-jar-with-dependencies.jar),
[clij-legacy](https://github.com/clij/clij-legacy/releases/download/0.1.0/clij-legacy_-0.1.0.jar),
[clij-clearcl](https://github.com/clij/clij2/releases/download/2.0.0.21/clij-clearcl-2.0.0.21.jar),
[clij-coremem](https://github.com/clij/clij2/releases/download/2.0.0.21/clij-coremem-2.0.0.10.jar),
[clijx_](https://github.com/clij/clijx/releases/download/0.29.1.3/clijx_-0.29.1.3.jar)
 and
[clijx-assistant](https://sites.imagej.net/clincubator/plugins/clijx-incubator_-0.2.2.4.jar-20200819215628). 
Remove the number by the end of the filename, so that they are all end with .jar. 
Copy the .jar files to you MicroManager installation folder `/plugins/Micro-Manager/`. 
Within this folder, please delete older versions such as `clij-clearcl-0.10.0` and `clij-coremem-0.6.0`.

Afterwards, start MicroManger and its multi-dimensional acquisition tool. 
As soon as you started acquisition, execute these two menus:
* 
* 

![Image](images/script_export.png)

<iframe src="images/incubator_generate_macro.mp4" width="540" height="260"></iframe>
[Download video](images/incubator_generate_macro.mp4) [Image data source: Daniela Vorkel, Myers lab, CSBD / MPI CBG]


Back to [CLIJx-Assistant](https://clij.github.io/assistant)

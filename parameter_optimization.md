# Parameter optimization
If a workflow results in a binary image and some annotations are availabe, its parameters can be automatically optimized.
To prepare the optimization, draw Freehand or Polyline ROIs and store them in the ROI Manager. 
Name the positive annotations "p" and the negative annotations "n" (background).
If the ROI manager doesn't contain any annotations, it will open and start the annotation tool which is very similar to ImageJs Freehand tool.
Use the mouse to annotate positive regions and they will automatically be saved to the ROI manger with the correct name.
If you hold CTRL while drawing, they will be stored as negative/background annotations in the ROI manager.

When annotations are ready, click the "Optimize parameters" menu:

<iframe src="images/optimize.mp4" width="540" height="540"></iframe>
[Download video](images/optimize.mp4) [Image data source: Broad BioImage Benchmark collection](https://bbbc.broadinstitute.org/BBBC008)

Note: For good scientific practice, please save the annotations and the protocol of the analysis. 
The latter can be done by [exporting a script or human readable workflow](https://clij.github.io/assistant/macro_export).

Back to [CLIJx-Assistant](https://clij.github.io/assistant)
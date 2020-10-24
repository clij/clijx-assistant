# Nuclei segmentation
Nuclei segmentation in 3D data is challenging because of background intensity, uneven intensity in Z-dimension, noise 
and simply the amoung of pixels which need to be processed. 
Real-time experience while configuring a workflow for nuclei segmentation can be achieved when utilizing classical methods
such as filtering, thresholding and watershed techniques. 
It is recommended to utilize modern [GDDR6-based GPU hardware](https://clij.github.io/assistant/installation#hardware) for 3D segmentation.

## How to do 3D cell nuclei segmentation
Open your data set. [Start the CLIJx-Assistant](https://clij.github.io/assistant/getting_started) and follow such a workflows:

* Your dataset
  * CLIJx-Assistant Starting point
    * [Optional: Noise removal and Background subtraction]
      * Threshold DoG
        * Parametric Watershed
          * Connected Components Labeling
            * Maximum Z projection

After assembling your workflow, put these operations next to each other, change the parameters.

<iframe src="images/incubator_segmentation_3d_nuclei.mp4" width="540" height="540"></iframe>
[Download video](images/incubator_segmentation_3d_nuclei.mp4)
[Image data source: Daniela Vorkel, Myers lab, CSBD / MPI CBG]

There are many ways for detecting nuclei and extending their size, e.g. to study neighborhood relationships.
<iframe src="images/clijxa_teaser1_fast.mp4" width="800" height="640"></iframe>
[Download video](images/clijxa_teaser1.mp4) [Image data source: Daniela Vorkel, Myers lab, CSBD / MPI CBG]

Back to [CLIJx-Assistant](https://clij.github.io/assistant)

[Imprint](https://clij.github.io/imprint)

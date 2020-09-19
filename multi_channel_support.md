# Multi-channel visualisation
Multi-channel image data is supported although increased memory consumption may be observed. 
In general, all operations available in CLIJx-Assistant also process multi-channel data. 
However, for typical [image segmentation](https://clij.github.io/assistant/segmentation_nuclei) workflows, it is recommended to extract a single channel, eg. before segmenting the image.

It is recommended to utilize modern [GDDR6-based GPU hardware](https://clij.github.io/assistant/installation#hardware) for 3D visualisation.

## How to visualize multi-channel 3D data
Open your data set. [Start the CLIJx-Assistant](https://clij.github.io/assistant/getting_started), ensure that pixel/voxel size is correctly configured and follow such a workflows:

* Your dataset
  * CLIJx-Assistant Starting point
    * Make isotropic
      * Rigid Transform
        * Maximum Z Projection
      
<iframe src="images/incbator_multichannel.mp4" width="450" height="275"></iframe>
[Download video](images/incbator_multichannel.mp4) [Image data source: Johannes Girstmair, Tomancak lab, MPI CBG]


Back to [CLIJx-Assistant](https://clij.github.io/assistant)

[Imprint](https://clij.github.io/imprint)

# Drift correction
CLIJx-Incubator comes with two operations for drift correction:
* Drift correction by center of mass fixation
* Drift correction by centroid fixation

Both operations basically do the same: They measure where the content of the image is located (on average) and shift it
so that it stays in place. You can configure where the average position is supposed to be. 
Drift correction by centroid fixation has another parameter: Enter an intensity value about background intensity to
allow the plugin differentiating background and the object to drift-correct in the foreground.

## How to apply configure drift correction
Open your time lapse data set. [Start the CLIJx-Incubator](https://clij.github.io/incubator/getting_started) and follow these steps:

* Your dataset
  * CLIJx-Incubator Starting point
    * Maximum Z Projection
    * Drift correction by centroid fixation
      * Maximum Z Projection

Align the resulting windows from the left to the right on your screen. 
Configure the desired center position.
Go through time and see if the drift correction works:

<iframe src="images/incubator_drift_correction.mp4" width="540" height="260"></iframe>
[Download video](images/incubator_drift_correction.mp4) 
[Image data source: Daniela Vorkel, Myers lab, CSBD / MPI CBG]



Back to [CLIJx-Incubator](https://clij.github.io/incubator)
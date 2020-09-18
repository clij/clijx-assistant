## Supplementary Methods Section generator
Just like [generating scripts](https://clij.github.io/assistant/macro_export), one can also
generate human readable protocols of workflows. Just click the right-click menu `Generate Script > Human readable protocol`.

Note: As modern reference / citation checking tools might detect these generated texts as illegal citations, it is 
recommended to mark these texts as generated.

![Image](images/human_readable_protocol.png)

### Example
```
This protocol documents an image processing workflow using CLIJx-Incubator.
Read more about it online: https://clij.github.io/incubator/ 

Overview
 * Copy 
   * Gaussian Blur 
     * Threshold Otsu 
       * Connected Components Labeling 
         * Extend Labeling Via Voronoi 
           * Draw Distance Mesh Between Touching Labels 

We start by processing the image "blobs.gif" for simplicity, we call it image1.


As the next step we applied "Copy" on image1 and got a new image out, 
image2, also titled "CLIJx Image of blobs.gif".


As the next step we applied "Gaussian Blur" on image2 and got a new image out, 
image3, also titled "Gaussian Blur of CLIJx Image of blobs.gif".
Therefore,  we used the parameters sigmaX = 2, sigmaY = 2 and sigmaZ = 2.

Then, we applied "Threshold Otsu", a CLIJ plugin programmed by Robert Haase based on work by G. Landini and W. Rasband,  on image3 and got a new image out, 
image4, also titled "Threshold Otsu of Gaussian Blur of CLIJx Image of blobs.gif".


Then, we applied "Connected Components Labeling" on image4 and got a new image out, 
image5, also titled "Connected Components Labeling of Threshold Otsu of Gaussian Blur of CLIJx Image of blobs.gif".


Afterwards, we applied "Extend Labeling Via Voronoi" on image5 and got a new image out, 
image6, also titled "Extend Labeling Via Voronoi of Connected Components Labeling of Threshold Otsu of Gaussian Blur of CLIJx Image of blobs.gif".


As the next step we applied "Draw Distance Mesh Between Touching Labels" on image6 and got a new image out, 
image7, also titled "Draw Distance Mesh Between Touching Labels of Extend Labeling Via Voronoi of Connected Components Labeling of Threshold Otsu of Gaussian Blur of CLIJx Image of blobs.gif".
```

Back to [CLIJx-Assistant](https://clij.github.io/assistant)

[Imprint](https://clij.github.io/imprint)

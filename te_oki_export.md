# Exporting workflows as Python script using clEsperanto and Napari
After you finished designing your image analysis workflow, you can export a python script which uses Napari 
for visualisation and execute if from Fijis script editor.

![Image](images/te_oki_menu.png)

## Installation
If you want to run Python scripts from Fiji, please follow the instructions [here](https://clij.github.io/assistant/installation#te_oki).

## Executing python from Fiji: Te Oki
If you want to run the generated scripts from Fiji, make sure conda is part of the PATH variable. 
You can configure which conda environment is used in Fijis menu 
Plugins > ImageJ on GPU (CLIJx-Assistant) > Options > Conda configuration (Te Oki)

Furthermore, activate the scripting language Te Oki in Fijis script editor to run the generated script:
![Image](images/te_oki_language_menu.png)

## Note
Not all commands have been translate to python yet. You can get an overview about supported and 
yet missing operations in [this list]().

Stay tuned and check out http://clesperanto.net to learn more.

## Usage

<iframe src="images/te_oki_fast.mp4" width="540" height="260"></iframe>
[Download video](images/te_oki.mp4) [Image data source: Daniela Vorkel, Myers lab, CSBD / MPI CBG]

**Please note:** While CLIJx-Assistant is running, the GPU may be busy and full of images. 
Thus, before running your generated macro, close all CLIJx-Assistant windows.

Back to [CLIJx-Assistant](https://clij.github.io/assistant)

[Imprint](https://clij.github.io/imprint)

# Generate CLIJx / Fiji plugins
Starting from a existing workflow, you can generate a CLIJx / Fiji plugin without the need to code anything. 
It generates a .jar file which you can ship to collaborators and ask them to install it into the `/plugins/` 
directory of their Fiji installation.

## Installation
In order to build Fiji plugins, please follow the instructions [here](https://clij.github.io/assistant/installation#maven).

## How does it work?
After entering some parameters, such as plugin name, description, author and maven/java/git installation folders, a template is downloaded from here:
https://github.com/clij/clijx-assistant-plugin-generator-template
Afterwards, some placeholders in this [ImageJ/Fiji/CLIJ maven project](https://imagej.net/Maven) are replaced before it is compiled and the resulting .jar file is installed to your Fijis `/plugins/` folder.

<iframe src="images/generate_clijx_plugins.mp4" width="540" height="260"></iframe>
[Download video](images/generate_clijx_plugins.mp4) 

Back to [CLIJx-Assistant](https://clij.github.io/assistant)

[Imprint](https://clij.github.io/imprint)

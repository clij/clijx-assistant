# Generate CLIJx / Fiji plugins
Starting from a existing workflow, you can generate a CLIJx / Fiji plugin without the need to code anything. 
It generates a .jar file which you can ship to collaborators and ask them to install it into the `/plugins/` 
directory of their Fiji installation.

## Installation
In order to build Fiji plugins, you need to install and Java Development kit, version 8 or higher, e.g. [OpenJDK](https://openjdk.java.net/).
Furthermore, you need [git](https://git-scm.com/) and [maven](https://maven.apache.org/). To make it work easily, it is recommended to add the git and maven  
installation directories to the PATH variable of your environment (How to: 
[Windows](https://answers.microsoft.com/en-us/windows/forum/windows_10-other_settings/adding-path-variable/97300613-20cb-4d85-8d0e-cc9d3549ba23)
[Linux](https://opensource.com/article/17/6/set-path-linux)
[MacOS](https://support.apple.com/guide/terminal/use-environment-variables-apd382cc5fa-4f58-4449-b20a-41c53c006f8f/mac)
).

## How does it work?
After entering some parameters, such as plugin name, description, author and maven/java/git installation folders, a template is downloaded from here:
https://github.com/clij/clijx-assistant-plugin-generator-template
Afterwards, some placeholders in this [ImageJ/Fiji/CLIJ maven project](https://imagej.net/Maven) are replaced before it is compiled and the resulting .jar file is installed to your Fijis `/plugins/` folder.

<iframe src="images/generate_clijx_plugins.mp4" width="260" height="260"></iframe>
[Download video](images/generate_clijx_plugins.mp4) 

Back to [CLIJx-Assistant](https://clij.github.io/assistant)

[Imprint](https://clij.github.io/imprint)

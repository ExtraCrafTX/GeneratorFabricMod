# GeneratorFabricMod [![Release][releases-badge]][releases-link]
Prompts for various information and outputs a skeleton mod, ready to be modified. Prevents faffing about with the example mod and having to change various fields, potentially missing certain information.

Also provides basic validation to prevent some errors.

## Installation
#### 1. Download the latest [release][releases-link]
#### 2. Unpack the distribution to a folder of your choice
#### 3. Add the `bin` directory to your `PATH` environment variable
**Windows:**  
* In the start menu, search for `Environment variables` and select `Edit the system environment variables`.  
* In the system properties screen that comes up, press the `Environment variables` button in the bottom right.
* Under `System variables`, select `Path` then click `Edit`.
* Add an entry for the `bin` folder and click OK to save.

**Linux and Mac:**
* Configure the PATH environment variable to include the `bin` directory:  
    ```bash
    $ export PATH=$PATH:/your/chosen/folder/GeneratorFabricMod/bin
    ```

## Usage
Once the `bin` directory has been added to your `PATH`, navigate to the folder where you would like to create a new project. Here, simply run GeneratorFabricMod:
```bash
$ GeneratorFabricMod
```
GeneratorFabricMod should start and load all the data it needs.  
Once all the data has been loaded, you may begin answering the questions.

#### List prompts
For some of the prompts such as the Minecraft version, you will be shown a list. Simply use the up and down arrow keys or k and j to move up and down the list, respectively. Use return/enter to select the currently highlighted option.

#### SemVer prompts
For the mod version, the prompt will enforce SemVer. You can simply type out the version in full, and the field will accept it.  
Alternatively, for the major, minor and patch versions, you can use the up and down arrow keys to increment or decrement the version.

To move between sections you can use the left and right arrow keys, or you can enter a . to go to the next section.

Pressing the - key at any point in the version numbers will take you to the pre-release section, and pressing + at any point at all will take you to the build metadata section.

## TemplateMakerFabric
This project uses [TemplateMakerFabric](https://github.com/ExtraCrafTX/TemplateMakerFabric) to get the data it needs for prompting and for outputting the skeleton.

[releases-badge]:https://img.shields.io/github/v/release/ExtraCrafTX/GeneratorFabricMod?include_prereleases
[releases-link]:https://github.com/ExtraCrafTX/GeneratorFabricMod/releases
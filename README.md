# i18n-binder
from http://code.google.com/p/i18n-binder:

The i18n Binder provides an Apache ANT task and an Apache Maven Plugin which allows you to 
* map i18n properties from multiple property files to an Microsoft Excel XLS file 
* write changed properties within the XLS file back to the original source files 
* create a Java source code facade based on the property files and their folder structure

In the first step the locales are separated from the absolute file path by a declared regualar expression pattern. This results in a as many columns as locales are matched within the XLS file. As rows all property keys per file are listed.

In the second step the properies changed within the XLS file are merged back into the original files. This is done with minimal impact on the property files which means comments an blank line are maintained as they are.

# Features

* Supports file encoding like UTF-8
* Allows minimal invasive changes (comments, blank line are maintained)
* Easy to integrate in build tools like Apache Ant and Apache Maven
* Pretty good overview over property values for different locales
* Easier translation changes
* Validation of existence of property keys over all locales
* Generation of a Java source code facade

# Prerequisites

* Java SE 1.6+
* Apache ant 1.8.1+ (ant should be available in `%Path%` / console)

# Usage
## Provide i18n directory
Provide a directory structure filled with property files you want to convert.

## Adapt i18nBinder.properties

Open the `i18nBinder.properties` within the root folder of the extracted package. Adapt the necessary properies there.

For example * the directory of the i18n files * the regular expression pattern for the file grouping

As default the regular expression groups locales are supposed to be at the end of the filename, like e.g.:

`C:\i18n_src\subfolder1\administration_de_DE.properties C:\i18n_src\subfolder1\administration_en_US.properties ...`

If your i18n directory has another structure like e.g.:

`C:\i18n_src\de_DE\administration.properties C:\i18n_src\en_US\administration.properties ...`

you have to change the grouping expression.

See the i18nBinder.properties file template for examples.

## Create XLS File

Now call

`ant createXLSFile`

to create the XLS file from the properties.

This will result in a new XLS file in your root directory

## Write properties back to the i18n directory files

If you have a XLS file you can write the changes back to the original property files.

This is done by

`ant writeProperties`

## Create a Java source code facade based on the directories and property files

Adapt the i18nBinder.properties:

```
// Filename of the created Java facade source file (only created if createJavaFacade is set to true)
javaFacadeFilename = I18nFacade.java
packageName = org.omnaest.some.package.example 
baseNameInTargetPlattform = i18n
```

Note: the `baseNameInTargetPlattform` flag will add a folder structure to the facade. This is important if your target plattform will have additional folders before those you run the i18nBinder with.

the run:

`ant createJavaFacade`


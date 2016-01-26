# Prerequisites #

  * [Java SE 1.6+](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
  * [Apache ant 1.8.1+](http://ant.apache.org/) (ant should be available in [%Path% / console](http://ant.apache.org/manual/install.html#setup))

# Download #

Download a release package from the download tab and extract it.
The content of the extracted folder should look like:
![http://i18n-binder.googlecode.com/svn/wiki/other/screenshotReleasePackageContent.jpg](http://i18n-binder.googlecode.com/svn/wiki/other/screenshotReleasePackageContent.jpg)

# Usage #

## Provide i18n directory ##

Provide a directory structure filled with property files you want to convert.

## Adapt i18nBinder.properties ##

Open the _i18nBinder.properties_ within the root folder of the extracted package.
Adapt the necessary properies there.

For example
  * the directory of the i18n files
  * the regular expression pattern for the file grouping

As default the regular expression groups locales are supposed to be at the end of the filename, like e.g.:

```
C:\i18n_src\subfolder1\administration_de_DE.properties
C:\i18n_src\subfolder1\administration_en_US.properties
...
```

If your i18n directory has another structure like e.g.:

```
C:\i18n_src\de_DE\administration.properties
C:\i18n_src\en_US\administration.properties
...
```

you have to change the grouping expression.

See the [i18nBinder.properties file](http://i18n-binder.googlecode.com/svn/trunk/i18nBinder/i18nBinderAntBuildScript/i18nBinder.properties) template for examples.

## Create XLS File ##

Now call

```
ant createXLSFile
```

to create the XLS file from the properties.

This will result in a new XLS file in your root directory which if you open it will look like:

![http://i18n-binder.googlecode.com/svn/wiki/other/screenshotXLSExampleFile.jpg](http://i18n-binder.googlecode.com/svn/wiki/other/screenshotXLSExampleFile.jpg)


## Write properties back to the i18n directory files ##

If you have a XLS file you can write the changes back to the original property files.

This is done by

```
ant writeProperties
```


## Create a Java source code facade based on the directories and property files ##

Adapt the i18nBinder.properties:

```
# Filename of the created Java facade source file. The facade is only created if createJavaFacade is set to true
javaFacadeFilename          = I18nFacade.java
packageName                 = org.omnaest.some.package.example
baseNameInTargetPlattform   = i18n
```

Note: the **baseNameInTargetPlattform** flag will add a folder structure to the facade. This is important if your target plattform will have additional folders before those you run the i18nBinder with.

the run:

```
ant createJavaFacade
```

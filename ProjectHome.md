# i18n Binder #

&lt;wiki:gadget url="http://www.ohloh.net/p/584227/widgets/project\_users.xml" height="100" border="0"/&gt;

&lt;wiki:gadget url="http://www.ohloh.net/p/584227/widgets/project\_thin\_badge.xml" height="60" border="0"/&gt;

<a href='http://www.xing.com/profile/Danny_Kunz'><img src='http://www.xing.com/img/buttons/10_en_btn.gif' alt='Danny Kunz' width='85' height='23' /></a>

The i18n Binder provides an Apache ANT task and an Apache Maven Plugin which allows you to
  * map i18n properties from multiple property files to an Microsoft Excel XLS file
  * write changed properties within the XLS file back to the original source files
  * create a Java source code facade based on the property files and their folder structure

In the first step the locales are separated from the absolute file path by a declared regualar expression pattern.
This results in a as many columns as locales are matched within the XLS file.
As rows all property keys per file are listed.

In the second step the properies changed within the XLS file are merged back into the original files. This is done with minimal impact on the property files which means comments an blank line are maintained as they are.

## Features ##

  * Supports file encoding like UTF-8
  * Allows minimal invasive changes (comments, blank line are maintained)
  * Easy to integrate in build tools like Apache Ant and Apache Maven
  * Pretty good overview over property values for different locales
  * Easier translation changes
  * Validation of existence of property keys over all locales
  * Generation of a Java source code facade

## Getting started ##

See [GettingStarted](GettingStarted.md)

## Releases ##

## Release 0.1.17 ##

2013 March, 19th

Notes:
  * Fixed [Issue 9](https://code.google.com/p/i18n-binder/issues/detail?id=9): Unicode sequences should be unescaped in Excel
  * Added a new regex example to the ant properties file (thanks here to the suggestion from a user of Actionscript and Flex)

## Release 0.1.16 ##

2012 September, 7th

Notes:
  * Applied patch of [Issue 8](https://code.google.com/p/i18n-binder/issues/detail?id=8): Not able to export non-UTF-8 properties files properly
> > (btw thanks for the support)
  * Added a first approach of a MavenPlugin similar to the current Ant task

## Release 0.1.14 ##

2012 June, 6th

Notes:
  * Changed the Java source code facade to be able to generate separated subclasses in nested packages which reflect the folder structure of the underlying i18n files

## Release 0.1.12 and 0.1.13 ##

2012 May, 18th

Notes:
  * Added Excel XLSX format read and write capabilities (Simply change the suffix of the generated file to .xlsx to enable this) / Fix for [Issue 3](https://code.google.com/p/i18n-binder/issues/detail?id=3)
  * Changed the Java source code facade to be able to create separated subclasses


## Release 0.1.10 and 0.1.11 ##

2011 December, 30th

**!!!This released breaks the previous Java source code facade API signature!!!**

Notes:
  * Changed the Java source code facade to have less generated code (about 1/3 compared to the previously generated files)
  * Added some overview tables to the class nodes of the facade
  * The facade allows now easier mocking (See FacadeMocking)

## Release 0.1.9 ##

2011 December, 11th

Notes:
  * Writing of property files does not throw IndexOutOfBounds exception for missing properties anymore. Instead a warning message at the end of the writing operation is printed for each property key and locale

## Release 0.1.8 ##

2011 November, 27th

Enhanced the methods of the Java source code facade:
  * added even more try...(...) methods which do not throw exceptions

## Release 0.1.7 ##

2011 November, 13th

Enhanced the methods of the Java source code facade:
  * added tryTranslate(...) methods which do not throw exceptions
  * changed allProperties() to resolve all keys dynamically at runtime


## Release 0.1.6 ##

2011 November, 9th

  * Enhanced the methods of the Java source code facade
  * Supporting utf-8 output for the facade


## Release 0.1.5 ##

2011 November, 8th

Improved generation algorithm of Java source code facade

## Release 0.1.4 ##

2011 November, 6th

Features:

  * Added first support for generating a **Java source code facade** based on the property files and their content. (Regard new ant task **createJavaFacade** and the **changed 18nBinder.properties**)

Maintenance / fixed Issues:
  * http://code.google.com/p/i18n-binder/issues/detail?id=2&can=7 (again)
  * Now supporting UTF-8 BOMs within files again


## Release 0.1.3 ##

2011 October, 14th

Maintenance release with fixed Issues:
  * http://code.google.com/p/i18n-binder/issues/detail?id=1&can=7
  * http://code.google.com/p/i18n-binder/issues/detail?id=2&can=7

## Donation ##
If you think this plugin helps you doing your work better make a 5 EUR donation or a donation with any amount you may choose with Paypal:
&lt;wiki:gadget url="https://i18n-binder.googlecode.com/svn/misc/donateGadget.xml" height="50" border="0" /&gt;
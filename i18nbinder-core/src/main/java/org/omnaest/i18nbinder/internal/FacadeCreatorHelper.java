/*******************************************************************************
 * Copyright 2011 Danny Kunz
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.omnaest.i18nbinder.internal;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.i18nbinder.grouping.FileGroup;
import org.omnaest.i18nbinder.grouping.FileGroupToPropertiesAdapter;
import org.omnaest.i18nbinder.grouping.FileGrouper;
import org.omnaest.i18nbinder.internal.SourceCodeBuilder.JavaDocBuilder;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverterElementToMapEntry;
import org.omnaest.utils.structure.element.filter.ElementFilterNotBlank;
import org.omnaest.utils.structure.hierarchy.TokenMonoHierarchy;
import org.omnaest.utils.structure.hierarchy.TokenMonoHierarchy.TokenElementPath;
import org.omnaest.utils.structure.map.SimpleEntry;

/**
 * Helper to create a i18n facade Java source code file based on property files
 * @author Omnaest
 */
public class FacadeCreatorHelper
{
	private static final String ISO_8859_1 = "ISO-8859-1";

	public static final String DEFAULT_JAVA_FACADE_FILENAME_I18N_FACADE = "I18nFacade";

	private static final String GENERATOR_URL = "https://github.com/schlothauer-wauer/i18n-binder";

	/**
	 * @param propertyFileSet
	 * @param localeFilter
	 * @param fileNameLocaleGroupPattern
	 * @param groupingPatternGroupingGroupIndexList
	 * @param i18nFacadeName
	 * @param externalizeTypes
	 * @param propertyfileEncoding
	 * @return
	 */
	public static Map<String, String> createI18nInterfaceFacadeFromPropertyFiles(Set<File> propertyFileSet,
		LocaleFilter localeFilter,
		String fileNameLocaleGroupPattern,
		List<Integer> groupingPatternGroupingGroupIndexList,
		String baseNameInTargetPlattform,
		String baseFolderIgnoredPath,
		String packageName,
		String i18nFacadeName,
		boolean externalizeTypes,
		String propertyfileEncoding)
	{

		final Map<String, String> retmap = new LinkedHashMap<String, String>();

		if (propertyFileSet != null)
		{
			Map<String, FileGroup> fileGroupIdentifierToFileGroupMap;
			{
				FileGrouper fileGrouper = new FileGrouper();
				try
				{
					if (fileNameLocaleGroupPattern != null)
					{
						fileGrouper.setGroupingPatternString(fileNameLocaleGroupPattern);
					}
					if (groupingPatternGroupingGroupIndexList != null)
					{
						fileGrouper.setGroupingPatternGroupingGroupIndexList(groupingPatternGroupingGroupIndexList);
					}
				}
				catch (Exception e)
				{
					ModifierHelper.logger.info(e.getMessage());
				}
				fileGrouper.setGroupingPatternReplacementToken("");
				fileGrouper.addAllFiles(propertyFileSet);
				fileGroupIdentifierToFileGroupMap = fileGrouper.determineFileGroupIdentifierToFileGroupMap();
			}

			List<FileGroupToPropertiesAdapter> fileGroupToPropertiesAdapterList = new ArrayList<FileGroupToPropertiesAdapter>();
			{
				for (String fileGroupIdentifier : fileGroupIdentifierToFileGroupMap.keySet())
				{
					FileGroup fileGroup = fileGroupIdentifierToFileGroupMap.get(fileGroupIdentifier);

					FileGroupToPropertiesAdapter fileGroupToPropertiesAdapter = new FileGroupToPropertiesAdapter(fileGroup);
					fileGroupToPropertiesAdapter.setFileEncoding(propertyfileEncoding);

					fileGroupToPropertiesAdapterList.add(fileGroupToPropertiesAdapter);
				}

				Collections.sort(fileGroupToPropertiesAdapterList, new Comparator<FileGroupToPropertiesAdapter>()
				{
					@Override
					public int compare(FileGroupToPropertiesAdapter fileGroupToPropertiesAdapter1,
						FileGroupToPropertiesAdapter fileGroupToPropertiesAdapter2)
					{

						String fileGroupIdentifier1 = fileGroupToPropertiesAdapter1.getFileGroup().getFileGroupIdentifier();
						String fileGroupIdentifier2 = fileGroupToPropertiesAdapter2.getFileGroup().getFileGroupIdentifier();

						return fileGroupIdentifier1.compareTo(fileGroupIdentifier2);
					}
				});
			}

			//determine all locales but fix the order
			List<String> localeList = new ArrayList<String>();
			{
				Set<String> localeSet = new HashSet<String>();
				for (FileGroupToPropertiesAdapter fileGroupToPropertiesAdapter : fileGroupToPropertiesAdapterList)
				{
					localeSet.addAll(fileGroupToPropertiesAdapter.determineGroupTokenList());
				}
				localeList.addAll(localeSet);

				for (String locale : localeSet)
				{
					if (!localeFilter.isLocaleAccepted(locale))
					{
						localeList.remove(locale);
					}
				}

				Collections.sort(localeList);
			}

			//facade source code
			{
				TokenMonoHierarchy<String, PropertyKeyAndValues> tokenMonoHierarchy = new TokenMonoHierarchy<String, PropertyKeyAndValues>();

				for (FileGroupToPropertiesAdapter fileGroupToPropertiesAdapter : fileGroupToPropertiesAdapterList)
				{
					String fileGroupIdentifier = fileGroupToPropertiesAdapter.getFileGroup().getFileGroupIdentifier();

					List<String> tokenPathElementList = new ArrayList<String>();
					{
						final String pathDelimiter = "[\\\\/]";

						if (StringUtils.isNotBlank(baseNameInTargetPlattform))
						{
							String[] baseNameTokens = baseNameInTargetPlattform.split(pathDelimiter);

							tokenPathElementList.addAll(Arrays.asList(baseNameTokens));
						}

						String[] fileGroupIdentifierTokens = fileGroupIdentifier.replaceFirst(Pattern.quote(baseFolderIgnoredPath), "").split(pathDelimiter);
						if (fileGroupIdentifierTokens.length > 0)
						{
							String lastToken = fileGroupIdentifierTokens[fileGroupIdentifierTokens.length - 1];
							lastToken = lastToken.replaceAll("\\.properties$", "").replaceAll("_", "");
							fileGroupIdentifierTokens[fileGroupIdentifierTokens.length - 1] = lastToken;

							tokenPathElementList.addAll(Arrays.asList(fileGroupIdentifierTokens));
						}

						tokenPathElementList = ListUtils.filter(tokenPathElementList, new ElementFilterNotBlank());
					}

					ModifierHelper.logger.info("Processing: " + fileGroupIdentifier);

					List<String> propertyKeyList = new ArrayList<String>(fileGroupToPropertiesAdapter.determinePropertyKeySet());
					Collections.sort(propertyKeyList);
					for (String propertyKey : propertyKeyList)
					{
						if (propertyKey != null)
						{
							PropertyKeyAndValues propertyKeyAndValues = new PropertyKeyAndValues();
							{
								propertyKeyAndValues.propertyKey = propertyKey;

								for (String locale : localeList)
								{

									String value = fileGroupToPropertiesAdapter.resolvePropertyValue(propertyKey, locale);
									value = StringUtils.defaultString(value);
									if (StringUtils.isNotBlank(value))
									{
										propertyKeyAndValues.valueList.add(locale + "=" + value);
									}
								}
							}

							TokenElementPath<String> tokenElementPath = new TokenElementPath<String>(tokenPathElementList);
							tokenMonoHierarchy.addTokenElementPathWithValues(tokenElementPath, propertyKeyAndValues);
						}
					}
				}

				final Map<String, StringBuilder> externalizedClassToContentMap = externalizeTypes ? new LinkedHashMap<String, StringBuilder>()
					: null;
				retmap.put(packageName + "." + i18nFacadeName,
					buildFacadeSource(tokenMonoHierarchy, packageName, i18nFacadeName, externalizedClassToContentMap, propertyfileEncoding));
				if (externalizeTypes)
				{
					for (String subClassName : externalizedClassToContentMap.keySet())
					{

						final StringBuilder stringBuilder = externalizedClassToContentMap.get(subClassName);
						retmap.put(subClassName, stringBuilder.toString());
					}
				}
			}
		}

		return retmap;
	}

	protected static class PropertyKeyAndValues
	{
		public String propertyKey = null;
		public List<String> valueList = new ArrayList<String>();
	}

	private static String buildFacadeSource(TokenMonoHierarchy<String, PropertyKeyAndValues> tokenMonoHierarchy,
		String packageName,
		String i18nFacadeName,
		Map<String, StringBuilder> externalizedClassToContentMap,
		String propertyfileEncoding)
	{
		StringBuilder retval = new StringBuilder();

		TokenMonoHierarchy<String, PropertyKeyAndValues>.Navigator navigator = tokenMonoHierarchy.getNavigator();

		final String className = i18nFacadeName;
		final boolean isSubClass = false;
		final String rootPackageName = packageName;
		buildFacadeSource(retval, new TreeSet<String>(),
			className, isSubClass, navigator, externalizedClassToContentMap, i18nFacadeName, packageName,
			rootPackageName, propertyfileEncoding);

		return retval.toString(); //.replaceAll("\n", System.lineSeparator());
	}

	private static void buildFacadeSource(StringBuilder strBuilder, Set<String> importSet,
		String className,
		boolean isSubClass,
		TokenMonoHierarchy<String, PropertyKeyAndValues>.Navigator navigator,
		Map<String, StringBuilder> externalizedClassToContentMap,
		String i18nFacadeName,
		String packageName,
		String rootPackageName,
		String propertyfileEncoding)
	{

		final Map<String, String> subClassNameToTokenElementMap = new LinkedHashMap<String, String>();
		final Map<String, List<String>> propertyNameToExampleValueListMap = new LinkedHashMap<String, List<String>>();
		final Map<String, String> propertyNameToPropertyKeyMap = new HashMap<String, String>();
		final String baseName = StringUtils.join(navigator.determineTokenPathElementList(), ".");
		final boolean externalizeTypes = externalizedClassToContentMap != null;
		final boolean staticModifier = !externalizeTypes && isSubClass;

		{
			List<String> tokenElementOfChildrenList = navigator.getTokenElementOfChildrenList();

			subClassNameToTokenElementMap.putAll(ListUtils.toMap(tokenElementOfChildrenList,
				new CamelCaseTokenElementToMapEntryConverter(className)));
		}

		final boolean hasAtLeastOneSubclass = !subClassNameToTokenElementMap.isEmpty();
		{
			if (navigator.hasValues())
			{
				List<PropertyKeyAndValues> propertyKeyAndValuesList = navigator.getValues();
				for (PropertyKeyAndValues propertyKeyAndValues : propertyKeyAndValuesList)
				{

					String propertyKey = propertyKeyAndValues.propertyKey;

					String propertyName = "";
					{
						String[] tokens = propertyKey.split("[^a-zA-Z0-9]");
						for (String token : tokens)
						{
							propertyName += StringUtils.capitalize(token);
						}
					}

					{
						final String key = propertyName;
						final List<String> valueList = new ArrayList<String>(propertyKeyAndValues.valueList);
						{

							final String defaultLocaleString = String.valueOf(Locale.getDefault());
							final String defaultLocaleLanguageString = String.valueOf(Locale.getDefault().getLanguage());
							Collections.sort(valueList, new Comparator<String>()
							{
								@Override
								public int compare(String o1, String o2)
								{
									int retval = 0;

									final String firstElement1 = org.omnaest.utils.structure.collection.list.ListUtils
										.firstElement(org.omnaest.utils.structure.collection.list.ListUtils.valueOf(StringUtils.split(o1, "=")));
									final String firstElement2 = org.omnaest.utils.structure.collection.list.ListUtils
										.firstElement(org.omnaest.utils.structure.collection.list.ListUtils.valueOf(StringUtils.split(o2, "=")));

									if (StringUtils.startsWith(firstElement1, defaultLocaleString))
									{
										retval--;
									}
									if (StringUtils.startsWith(firstElement2, defaultLocaleString))
									{
										retval++;
									}
									if (StringUtils.contains(firstElement1, defaultLocaleString))
									{
										retval--;
									}
									if (StringUtils.contains(firstElement2, defaultLocaleString))
									{
										retval++;
									}
									if (StringUtils.contains(firstElement1, defaultLocaleLanguageString))
									{
										retval--;
									}
									if (StringUtils.contains(firstElement2, defaultLocaleLanguageString))
									{
										retval++;
									}

									return retval;
								}
							});
						}
						propertyNameToExampleValueListMap.put(key, valueList);
					}

					{
						propertyNameToPropertyKeyMap.put(propertyName, propertyKey);
					}
				}
			}
		}

		boolean hasBaseName = StringUtils.isNotBlank(baseName);
		boolean hasProperties = !propertyNameToExampleValueListMap.keySet().isEmpty();

		SourceCodeBuilder code = new SourceCodeBuilder(strBuilder);

		//imports
		if (!isSubClass || externalizeTypes)
		{
			importSet.add("java.util.Locale");
			importSet.add("java.util.MissingResourceException");
			importSet.add("javax.annotation.Generated");

			if (!isSubClass)
			{
				importSet.add("java.util.LinkedHashMap");
				importSet.add("java.util.ResourceBundle");
			}

			if (externalizeTypes)
			{
				if (hasProperties)
				{
					importSet.add(rootPackageName + "." + i18nFacadeName);
					importSet.add(rootPackageName + "." + i18nFacadeName + ".Translator");
				}

				if (hasAtLeastOneSubclass)
				{
					for (String subClassName : subClassNameToTokenElementMap.keySet())
					{
						importSet.add(packageName + "." + StringUtils.lowerCase(className) + "." + subClassName);
					}
				}
			}
		}

		//documentation
		JavaDocBuilder javaDoc = code.javaDoc();
		javaDoc.append("This is an automatically with i18nBinder generated facade class.<br><br>");
		javaDoc.append("To modify please adapt the underlying property files.<br><br>");
		javaDoc.append("If the facade class is instantiated with a given {@link Locale} using {@link #" + className +
			"(Locale)} all non static methods will use this predefined {@link Locale} when invoked.<br><br>");
		javaDoc.append("The facade methods will silently ignore all {@link MissingResourceException}s by default. To alter this behavior see {@link #" + className +
			"(Locale, boolean)}<br><br>");
		if (hasBaseName)
		{
			javaDoc.append("Resource base: <b>" + baseName + "</b>");
		}
		if (hasProperties)
		{
			printJavaDocPropertiesExamplesForSubclassAndInstance(javaDoc, propertyNameToExampleValueListMap, propertyNameToPropertyKeyMap);
		}

		for (String subClassName : subClassNameToTokenElementMap.keySet())
		{
			javaDoc.see(subClassName);
		}

		if (hasProperties)
		{
			javaDoc.see("#translator()");
			javaDoc.see("#translator(Locale)");
		}
		javaDoc.end();
		code.append("@Generated(value = \"" + GENERATOR_URL + "/\")");

		//class
		code.begins("public " + (staticModifier ? "static " : "") + "class " + className);
		{
			//vars
			{
				if (!propertyNameToExampleValueListMap.isEmpty())
				{
					code.append("public final static String baseName = \"" + baseName + "\";");
					code.append("private final Locale locale;");
					code.append("private final boolean silentlyIgnoreMissingResourceException;");
					code.newLine();
				}

				if (!subClassNameToTokenElementMap.isEmpty())
				{
					for (String subClassName : subClassNameToTokenElementMap.keySet())
					{
						code.append("/** @see " + subClassName + " */");
						code.append("public final " + subClassName + " " + subClassName + ";");
					}
					code.newLine();
				}

				if (!isSubClass)
				{
					importSet.add(UnsupportedEncodingException.class.getName());
					// standard encoding of .properties file is ISO-8859-1, also known as Latin-1
					boolean useLatin1Encoding = propertyfileEncoding == null || propertyfileEncoding.equals(ISO_8859_1);

					code.append("/** Static access helper for the underlying resource */");
					code.begins("public static class Resource");
					code.append("/** Internally used {@link ResourceBasedTranslator}. Changing this implementation affects the behavior of the whole facade */");
					code.begins("public static ResourceBasedTranslator resourceBasedTranslator = new ResourceBasedTranslator()");
					code.append("@Override");
					code.begins("public String translate(String baseName, String key, Locale locale)");
					code.append("ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);");
					if (!useLatin1Encoding)
					{
						code.begins("try");
						code.returns("new String(resourceBundle.getString(key).getBytes(\"" + ISO_8859_1 + "\"), \"" + propertyfileEncoding + "\");");
						code.endBlock();
						code.begins("catch (UnsupportedEncodingException e)");
					}
					code.returns("resourceBundle.getString(key);");
					if (!useLatin1Encoding)
					{
						code.endBlock();
					}
					code.endBlock().newLine();
					code.append("@Override");
					code.begins("public String[] resolveAllKeys(String baseName, Locale locale)");
					code.append("ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);");
					code.returns("resourceBundle.keySet().toArray(new String[0]);");
					code.endBlock().endBlock(";").endBlock().newLine();

					code.append("/** Defines which {@link ResourceBasedTranslator} the facade should use. This affects all available instances. */");
					code.begins("public static void use(ResourceBasedTranslator resourceBasedTranslator)");
					code.append(i18nFacadeName + ".Resource.resourceBasedTranslator = resourceBasedTranslator;").endBlock().newLine();
				}
			}

			//helper classes
			if (!isSubClass)
			{
				importSet.add("java.util.Map");
				appendResourceBasedTranslatorInterface(code);
				appendTranslatorHelper(code, i18nFacadeName);
			}

			//constructor
			{
				code.javaDoc("This {@link " + className + "} constructor will create a new instance which silently ignores any {@link MissingResourceException}")
					.param("locale").see(className).end();
				code.begins("public " + className + "(Locale locale)");
				{
					code.append("this(locale, true);");
				}
				code.endBlock().newLine();

				code.javaDoc().param("locale").param("silentlyIgnoreMissingResourceException").see(className).end();
				code.begins("public " + className + "(Locale locale, boolean silentlyIgnoreMissingResourceException)");
				{
					code.append("super();");
					if (!propertyNameToExampleValueListMap.isEmpty())
					{
						code.append("this.locale = locale;");
						code.append("this.silentlyIgnoreMissingResourceException = silentlyIgnoreMissingResourceException;");
					}

					for (String subClassName : subClassNameToTokenElementMap.keySet())
					{
						code.append("this." + subClassName + " = new " + subClassName + "(locale, silentlyIgnoreMissingResourceException);");
					}
				}
				code.endBlock().newLine();
			}

			//static subclasses
			{
				for (String subClassName : subClassNameToTokenElementMap.keySet())
				{

					final boolean subClassIsSubClass = true;
					final String subClassPackageName = !externalizeTypes ? packageName : packageName + "." + StringUtils.lowerCase(className);
					StringBuilder subClassStringBuilder;
					Set<String> subClassImportSet;
					{
						if (externalizeTypes)
						{
							subClassStringBuilder = new StringBuilder();
							subClassImportSet = new TreeSet<String>();

							externalizedClassToContentMap.put(subClassPackageName + "." + subClassName, subClassStringBuilder);
						}
						else
						{
							subClassStringBuilder = strBuilder;
							subClassImportSet = importSet;
						}
					}
					buildFacadeSource(subClassStringBuilder, subClassImportSet, subClassName, subClassIsSubClass,
						navigator.newNavigatorFork().navigateToChild(subClassNameToTokenElementMap.get(subClassName)),
						externalizedClassToContentMap, i18nFacadeName, subClassPackageName, rootPackageName, propertyfileEncoding);
				}
			}

			//methods based on properties
			if (hasProperties)
			{
				boolean generateReplaceTokensFunctions = false;
				for (String propertyName : propertyNameToExampleValueListMap.keySet())
				{
					String propertyKey = propertyNameToPropertyKeyMap.get(propertyName);
					List<String> exampleValueList = propertyNameToExampleValueListMap.get(propertyName);

					List<String> replacementTokensForExampleValuesNumericPlaceholders = determineReplacementTokensForExampleValues(exampleValueList, "\\{\\d+\\}");
					List<String> replacementTokensForExampleValuesArbitraryPlaceholders = determineReplacementTokensForExampleValues(exampleValueList, "\\{\\w+\\}");

					boolean containsNumericalReplacementToken = replacementTokensForExampleValuesNumericPlaceholders.size() > 0;
					boolean containsArbitraryReplacementToken = !containsNumericalReplacementToken && replacementTokensForExampleValuesArbitraryPlaceholders.size() > 0;

					{
						code.javaDoc("Similar to {@link #get" + propertyName + "()} for the given {@link Locale}.").param("locale").see(className)
							.see("#get" + propertyName + "()").end();
						code.begins("protected String get" + propertyName + "(Locale locale)");
						code.begins("try");
						code.append("final String key = \"" + propertyKey + "\";");
						code.returns(i18nFacadeName + ".Resource.resourceBasedTranslator.translate(baseName, key, locale);");
						code.endBlock();
						code.begins("catch (MissingResourceException e)");
						code.begins("if (!this.silentlyIgnoreMissingResourceException)");
						code.append("throw e;");
						code.endBlock();
						code.returns("null;");
						code.endBlock();
						code.endBlock().newLine();

						javaDoc = code.javaDoc("Returns the value of the property key <b>" + propertyKey + "</b> for the predefined {@link Locale}.");
						printJavaDocPlaceholders(javaDoc, replacementTokensForExampleValuesArbitraryPlaceholders);
						printJavaDocValueExamples(javaDoc, exampleValueList);
						javaDoc.see(className).end();
						code.begins("public String get" + propertyName + "()");
						code.returns("get" + propertyName + "(this.locale);");
						code.endBlock().newLine();
					}

					if (containsNumericalReplacementToken)
					{
						generateReplaceTokensFunctions = true;
						code.javaDoc("Similar to  {@link #get" + propertyName + "(Object[])} using the given {@link Locale}.").params("locale", "tokens").see(className)
							.see("#get" + propertyName + "(Object[])").end();
						code.begins("public String get" + propertyName + "(Locale locale, Object... tokens)");
						code.returns("replaceTokens(get" + propertyName + "(locale), tokens);");
						code.endBlock().newLine();

						javaDoc = code.javaDoc();
						javaDoc.append("Returns the value of the property key <b>" + propertyKey +
							"</b> for the predefined {@link Locale} with all {0},{1},... placeholders replaced by the given tokens in their order.<br><br>");
						javaDoc.append("If there are not enough parameters existing placeholders will remain unreplaced.");
						printJavaDocPlaceholders(javaDoc, replacementTokensForExampleValuesNumericPlaceholders);
						printJavaDocValueExamples(javaDoc, exampleValueList);
						javaDoc.param("tokens").see(className).see("#get" + propertyName + "(Locale, Object[])").end();
						code.begins("public String get" + propertyName + "(Object... tokens)");
						code.returns("get" + propertyName + "(this.locale, tokens);");
						code.endBlock().newLine();
					}

					if (containsArbitraryReplacementToken)
					{
						importSet.add("java.util.Map");

						javaDoc = code.javaDoc();
						javaDoc.append("Returns the value of the property key <b>" + propertyKey +
							"</b> for the given {@link Locale} with arbitrary placeholder tag like {example} replaced by the given values.<br>");
						javaDoc.append("The given placeholderToReplacementMap needs the placeholder tag name and a value. E.g. for {example} the key \"example\" has to be set.");
						printJavaDocPlaceholders(javaDoc, replacementTokensForExampleValuesArbitraryPlaceholders);
						printJavaDocValueExamples(javaDoc, exampleValueList);
						javaDoc.params("locale", "placeholderToReplacementMap").see(className).see("#get" + propertyName + "(Map)").end();

						code.begins("public String get" + propertyName + "(Locale locale, Map<String, String> placeholderToReplacementMap)");
						code.append("String retval = get" + propertyName + "(locale);");
						code.begins("if (placeholderToReplacementMap != null)");
						code.begins("for (String placeholder : placeholderToReplacementMap.keySet())");
						code.begins("if (placeholder != null)");
						code.append("String token = placeholderToReplacementMap.get(placeholder);");
						code.append("retval = retval.replaceAll(\"\\\\{\" + placeholder + \"\\\\}\", token);");
						code.endBlock().endBlock().endBlock();
						code.returns("retval;");
						code.endBlock().newLine();

						code.javaDoc("Similar to  {@link #get" + propertyName + "(Locale,Map)} using the predefined {@link Locale}.").param("placeholderToReplacementMap")
							.see(className).see("#get" + propertyName + "(Locale, Map)").end();
						code.begins("public String get" + propertyName + "(Map<String, String> placeholderToReplacementMap)");
						code.returns("get" + propertyName + "(this.locale, placeholderToReplacementMap);");
						code.endBlock().newLine();
					}
				}

				if (generateReplaceTokensFunctions)
				{
					code.begins("private static String replaceTokens(String str, Object... tokens)");
					code.begins("for (int index = 0; index < tokens.length; index++)");
					code.append("String token = tokens[index] != null ? tokens[index].toString() : null;");
					code.begins("if (token != null)");
					code.append("str = str.replaceAll(\"\\\\{\" + index + \"\\\\}\", token);");
					code.endBlock().endBlock();
					code.returns("str;");
					code.endBlock().newLine();
				}

				//fluid factory methods
				{
					code.javaDoc("Returns a new instance of {@link " + className + "} which uses the given setting for the exception handling")
						.param("silentlyIgnoreMissingResourceException").see(className).end();
					code.begins("public " + className + " doSilentlyIgnoreMissingResourceException(boolean silentlyIgnoreMissingResourceException)");
					code.returns("new " + className + "(this.locale, silentlyIgnoreMissingResourceException);");
					code.endBlock().newLine();

					code.javaDoc("Returns a new instance of {@link " + className + "} which uses the given {@link Locale}").param("locale").see(className).end();
					code.begins("public " + className + " forLocale(Locale locale)");
					code.returns("new " + className + "(locale, this.silentlyIgnoreMissingResourceException);");
					code.endBlock().newLine();
				}

				//translator methods
				{
					code.javaDoc("Returns a new {@link Translator} instance using the given {@link Locale} and based on the {@value #baseName} i18n base").see(className)
						.see("#translator()").see("#translator(Locale)").returns("{@link Translator}").end();
					code.begins("public static Translator translator(Locale locale, boolean silentlyIgnoreMissingResourceException)");
					code.returns("new Translator(baseName, locale, silentlyIgnoreMissingResourceException);");
					code.endBlock().newLine();

					code.javaDoc("Returns a new {@link Translator} instance using the given {@link Locale} and based on the {@value #baseName} i18n base").see(className)
						.see("#translator()").see("#translator(Locale, boolean)").returns("{@link Translator}").end();
					code.begins("public Translator translator(Locale locale)");
					code.returns("new Translator(baseName, locale, this.silentlyIgnoreMissingResourceException);");
					code.endBlock().newLine();

					code.javaDoc("Returns a new {@link Translator} instance using the internal {@link Locale} and based on the {@value #baseName} i18n base").see(className)
						.see("#translator(Locale)").see("#translator(Locale,boolean)").returns("{@link Translator}").end();
					code.begins("public Translator translator()");
					code.returns("translator(this.locale);");
					code.endBlock().newLine();
				}
			}
		}

		code.endBlock().newLine();

		if (!isSubClass || externalizeTypes)
		{
			if (importSet.size() > 0)
			{
				StringBuilder importBuf = new StringBuilder();
				for (String importDef : importSet)
				{
					importBuf.append("import " + importDef + ";\n");
				}
				strBuilder.insert(0, importBuf.toString() + "\n");
			}

			if (StringUtils.isNotBlank(packageName))
			{
				strBuilder.insert(0, "package " + packageName + ";\n\n");
			}
		}
	}

	private static void printJavaDocPropertiesExamplesForSubclassAndInstance(JavaDocBuilder javaDoc,
		final Map<String, List<String>> propertyNameToExampleValueListMap,
		final Map<String, String> propertyNameToPropertyKeyMap)
	{
		javaDoc.append("<br><br>");
		javaDoc.append("<h1>Examples:</h1>");
		javaDoc.append("<table border=\"1\">");
		javaDoc.append("<thead>");
		javaDoc.append("<tr>");
		javaDoc.append("<th>key</th>");
		javaDoc.append("<th>examples</th>");
		javaDoc.append("</tr>");
		javaDoc.append("</thead>");
		javaDoc.append("<tbody>");
		for (String propertyName : propertyNameToExampleValueListMap.keySet())
		{

			final int exampleSizeMax = 3;

			final String propertyKey = propertyNameToPropertyKeyMap.get(propertyName);
			final List<String> exampleValueList = new ArrayList<String>(propertyNameToExampleValueListMap.get(propertyName));
			{
				while (exampleValueList.size() > exampleSizeMax)
				{
					exampleValueList.remove(exampleValueList.size() - 1);
				}
			}
			final Iterator<String> iteratorExampleValueList = exampleValueList.iterator();

			final int exampleSize = exampleValueList.size();
			if (exampleSize > 0)
			{

				javaDoc.append("<tr>");
				javaDoc.append("<td rowspan=\"" + exampleSize + "\">" + propertyKey + "</td>");
				javaDoc.append("<td>" + iteratorExampleValueList.next() + "</td>");
				javaDoc.append("</tr>");
				while (iteratorExampleValueList.hasNext())
				{
					javaDoc.append("<tr><td><small>" + iteratorExampleValueList.next() + "</small></td></tr>");
				}
			}
		}
		javaDoc.append("</tbody>");
		javaDoc.append("</table><br><br>");
	}

	private static void appendResourceBasedTranslatorInterface(SourceCodeBuilder codeBuilder)
	{
		codeBuilder.javaDoc("Basic interface which is used by the facade to resolve translated values for given keys<br><br>")
			.append("Any implementation should be thread safe").end();
		codeBuilder.begins("public static interface ResourceBasedTranslator");
		{
			codeBuilder.javaDoc("Returns the translated value for the given key respecting the base name and the given {@link Locale}").params("baseName", "key", "locale")
				.returns("translated string").end();
			codeBuilder.append("public String translate(String baseName, String key, Locale locale);").newLine();

			codeBuilder.javaDoc("Returns all available keys for the given {@link Locale}").params("baseName", "locale").append("@return").end();
			codeBuilder.append("public String[] resolveAllKeys(String baseName, Locale locale);");
		}
		codeBuilder.endBlock().newLine();
	}

	private static void appendTranslatorHelper(SourceCodeBuilder code, String I18nFacadeName)
	{
		code.javaDoc()
			.append("A {@link Translator} offers several methods to translate arbitrary keys into their i18n counterpart based on the initially given {@link Locale}.")
			.see("#translate(String)").see("#translate(String[])").see("#allPropertyKeys()").end();
		code.begins("public static class Translator");

		//translator vars and constructor
		{
			code.append("private final String baseName;");
			code.append("private final Locale locale;");
			code.append("private final boolean silentlyIgnoreMissingResourceException;");
			code.newLine();

			code.javaDoc().see("Translator").params("baseName", "locale").end();
			code.begins("public Translator(String baseName, Locale locale)");
			code.append("this(baseName, locale, true);");
			code.endBlock().newLine();

			code.javaDoc().see("Translator").params("baseName", "locale", "silentlyIgnoreMissingResourceException").end();
			code.begins("public Translator(String baseName, Locale locale, boolean silentlyIgnoreMissingResourceException)");
			code.append("super();");
			code.append("this.baseName = baseName;");
			code.append("this.locale = locale;");
			code.append("this.silentlyIgnoreMissingResourceException = silentlyIgnoreMissingResourceException;");
			code.endBlock().newLine();
		}

		//translation map methods
		{
			code.javaDoc("Returns the translated property key for the given {@link Locale}").see("Translator").see("#translate(String)").see("#translate(String[])")
				.end();
			code.begins("public String translate(Locale locale, String key)");
			code.begins("try");
			code.returns(I18nFacadeName + ".Resource.resourceBasedTranslator.translate(this.baseName, key, locale);");
			code.endBlock();
			code.begins("catch (MissingResourceException e)");
			code.begins("if (!this.silentlyIgnoreMissingResourceException)");
			code.append("throw e;");
			code.endBlock();
			code.returns("null;");
			code.endBlock();
			code.endBlock().newLine();

			code.javaDoc("Returns the translated property key for the predefined {@link Locale}").see("Translator").see("#translate(Locale, String)")
				.see("#translate(String[])").end();
			code.begins("public String translate(String key)");
			code.returns("translate(this.locale, key);").endBlock().newLine();

			code.javaDoc("Returns a translation {@link Map} with the given property keys and their respective values for the given {@link Locale}.")
				.params("locale", "keys").see("Translator").see("#allPropertyKeys()").see("#translate(String)").end();
			code.begins("public Map<String, String> translate(Locale locale, String... keys)");
			code.append("Map<String, String> retmap = new LinkedHashMap<String, String>();");
			code.begins("for (String key : keys)");
			code.append("retmap.put(key, translate(locale, key));").endBlock();
			code.returns("retmap;");
			code.endBlock().newLine();

			code.javaDoc("Returns a translation {@link Map} with the given property keys and their respective values for the predefined {@link Locale}.").param("keys")
				.see("Translator").see("#allPropertyKeys()").see("#translate(String)").end();
			code.begins("public Map<String, String> translate(String... keys)");
			code.returns("translate(this.locale, keys);").endBlock().newLine();

			code.javaDoc("Returns all available property keys for the given {@link Locale}.").param("locale").see("Translator").see("#allPropertyKeys()")
				.see("#translate(String[])").end();
			code.begins("public String[] allPropertyKeys(Locale locale)");
			code.returns(I18nFacadeName + ".Resource.resourceBasedTranslator.resolveAllKeys(this.baseName, locale);");
			code.endBlock().newLine();

			code.javaDoc("Returns all available property keys for the predefined {@link Locale}.").see("Translator").see("#allPropertyKeys(Locale)")
				.see("#translate(String[])").end();
			code.begins("public String[] allPropertyKeys()");
			code.returns("allPropertyKeys(this.locale);").endBlock().newLine();

			code.javaDoc("Returns a translation {@link Map} for the predefined {@link Locale} including all available i18n keys resolved using")
				.append("{@link #allPropertyKeys()} and their respective translation values resolved using {@link #translate(String...)}").see("Translator")
				.see("#allPropertyKeys(Locale)").see("#translate(String[])").returns("{@link Map}").end();
			code.begins("public Map<String, String> translationMap()");
			code.returns("this.translate(this.allPropertyKeys());").endBlock().newLine();

			code.javaDoc("Similar to {@link #translationMap()} for the given {@link Locale} instead.").param("locale").see("Translator").see("#allPropertyKeys(Locale)")
				.see("#translate(String[])").returns("{@link Map}").end();
			code.begins("public Map<String, String> translationMap(Locale locale)");
			code.returns("this.translate(locale, this.allPropertyKeys(locale));").endBlock();
		}
		code.endBlock().newLine();
	}

	private static void printJavaDocPlaceholders(JavaDocBuilder javaDoc, List<String> replacementTokensForExampleValuesPlaceholders)
	{
		javaDoc.append("<br><br>");
		if (!replacementTokensForExampleValuesPlaceholders.isEmpty())
		{
			javaDoc.append("Placeholders:");
			javaDoc.append("<ul>");
			for (String replacementToken : replacementTokensForExampleValuesPlaceholders)
			{
				javaDoc.append("<li><b>" + replacementToken + "</b></li>");
			}
			javaDoc.append("</ul>");
		}
	}

	/**
	 * @param javaDoc
	 * @param exampleValueList
	 */
	private static void printJavaDocValueExamples(JavaDocBuilder javaDoc, List<String> exampleValueList)
	{
		javaDoc.append("");
		javaDoc.append("Examples:");
		javaDoc.append("<ul>");
		for (String exampleValue : exampleValueList)
		{
			javaDoc.append("<li>" + exampleValue + "</li>");
		}
		javaDoc.append("</ul>");
	}

	/**
	 * @param exampleValueList
	 * @param regexTokenPattern
	 * @return
	 */
	private static List<String> determineReplacementTokensForExampleValues(List<String> exampleValueList, String regexTokenPattern)
	{

		Set<String> retset = new LinkedHashSet<String>();

		final Pattern pattern = Pattern.compile(regexTokenPattern);
		for (String exampleValue : exampleValueList)
		{
			Matcher matcher = pattern.matcher(exampleValue);
			while (matcher.find())
			{
				retset.add(matcher.group());
			}
		}

		return new ArrayList<String>(retset);
	}

	protected static class CamelCaseTokenElementToMapEntryConverter implements
		ElementConverterElementToMapEntry<String, String, String>
	{
		private static final long serialVersionUID = 1L;

		/* ********************************************** Variables ********************************************** */
		public String excludedkey = null;

		/* ********************************************** Methods ********************************************** */
		public CamelCaseTokenElementToMapEntryConverter(String excludedkey)
		{
			super();
			this.excludedkey = excludedkey;
		}

		@Override
		public Entry<String, String> convert(String element)
		{

			String key = "";
			String value = "";

			if (element != null)
			{

				String[] tokens = element.split("[^a-zA-Z0-9]");
				for (String token : tokens)
				{
					key += StringUtils.capitalize(token);
				}

				key = StringUtils.isBlank(key) ? "Root" : key;
				key = key.matches("\\d+.*") ? "_" + key : key;
				key = StringUtils.equals(key, this.excludedkey) ? key + "_" : key;

				value = element;
			}

			return new SimpleEntry<String, String>(key, value);
		}

	}
}

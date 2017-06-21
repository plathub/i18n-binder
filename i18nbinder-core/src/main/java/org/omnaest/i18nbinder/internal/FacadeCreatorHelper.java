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
				TokenMonoHierarchy<String, PropertyKeyAndValues> TokenMonoHierarchy = new TokenMonoHierarchy<String, PropertyKeyAndValues>();

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

						String[] fileGroupIdentifierTokens = fileGroupIdentifier.replaceFirst(Pattern.quote(baseFolderIgnoredPath), "")
							.split(pathDelimiter);
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
							TokenMonoHierarchy.addTokenElementPathWithValues(tokenElementPath, propertyKeyAndValues);
						}
					}
				}

				final Map<String, StringBuilder> externalizedClassToContentMap = externalizeTypes ? new LinkedHashMap<String, StringBuilder>()
					: null;
				retmap.put(packageName + "." + i18nFacadeName,
					buildFacadeSource(TokenMonoHierarchy, packageName, i18nFacadeName, externalizedClassToContentMap, propertyfileEncoding));
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

	private static String buildFacadeSource(TokenMonoHierarchy<String, PropertyKeyAndValues> TokenMonoHierarchy,
		String packageName,
		String i18nFacadeName,
		Map<String, StringBuilder> externalizedClassToContentMap,
		String propertyfileEncoding)
	{
		StringBuilder retval = new StringBuilder();

		TokenMonoHierarchy<String, PropertyKeyAndValues>.Navigator navigator = TokenMonoHierarchy.getNavigator();

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

		SourceCodeBuilder codeBuilder = new SourceCodeBuilder(strBuilder);

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
		codeBuilder.append("/**");
		codeBuilder.append(" * This is an automatically with i18nBinder generated facade class.<br><br>");
		codeBuilder.append(" * To modify please adapt the underlying property files.<br><br>");
		codeBuilder.append(" * If the facade class is instantiated with a given {@link Locale} using {@link #" + className +
			"(Locale)} all non static methods will use this predefined {@link Locale} when invoked.<br><br>");
		codeBuilder.append(" * The facade methods will silently ignore all {@link MissingResourceException}s by default. To alter this behavior see {@link #" + className +
			"(Locale, boolean)}<br><br>");
		if (hasBaseName)
		{
			codeBuilder.append(" * Resource base: <b>" + baseName + "</b>");
		}
		if (hasProperties)
		{
			printJavaDocPropertiesExamplesForSubclassAndInstance(strBuilder, propertyNameToExampleValueListMap,
				propertyNameToPropertyKeyMap);
		}

		for (String subClassName : subClassNameToTokenElementMap.keySet())
		{
			codeBuilder.append(" * @see " + subClassName);
		}

		if (hasProperties)
		{
			codeBuilder.append(" * @see #translator()");
			codeBuilder.append(" * @see #translator(Locale)");
		}
		codeBuilder.append(" */ ");
		codeBuilder.append("@Generated(value = \"" + GENERATOR_URL + "/\")");

		//class
		codeBuilder.append("public " + (staticModifier ? "static " : "") + "class " + className).beginBlock();
		{
			//vars
			{
				if (!propertyNameToExampleValueListMap.isEmpty())
				{
					codeBuilder.append("public final static String baseName = \"" + baseName + "\";");
					codeBuilder.append("private final Locale locale;");
					codeBuilder.append("private final boolean silentlyIgnoreMissingResourceException;");
				}

				for (String subClassName : subClassNameToTokenElementMap.keySet())
				{
					codeBuilder.append("/** @see " + subClassName + " */");
					codeBuilder.append("public final " + subClassName + " " + subClassName + ";");
				}

				if (!isSubClass)
				{
					importSet.add(UnsupportedEncodingException.class.getName());
					// standard encoding of .properties file is ISO-8859-1, also known as Latin-1
					boolean useLatin1Encoding = propertyfileEncoding == null || propertyfileEncoding.equals(ISO_8859_1);

					codeBuilder.newLine();
					codeBuilder.append("/** Static access helper for the underlying resource */");
					codeBuilder.append("public static class Resource");
					codeBuilder.beginBlock();
					codeBuilder.append("/** Internally used {@link ResourceBasedTranslator}. Changing this implementation affects the behavior of the whole facade */");
					codeBuilder.append("public static ResourceBasedTranslator resourceBasedTranslator = new ResourceBasedTranslator()");
					codeBuilder.beginBlock();
					codeBuilder.append("@Override");
					codeBuilder.append("public String translate(String baseName, String key, Locale locale)");
					codeBuilder.beginBlock();
					codeBuilder.append("ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);");
					if (!useLatin1Encoding)
					{
						codeBuilder.append("try").beginBlock();
						codeBuilder.append("return new String(resourceBundle.getString(key).getBytes(\"" + ISO_8859_1 + "\"), \"" + propertyfileEncoding + "\");");
						codeBuilder.endBlock();
						codeBuilder.append("catch (UnsupportedEncodingException e)");
						codeBuilder.beginBlock();
					}
					codeBuilder.append("return resourceBundle.getString(key);");
					if (!useLatin1Encoding)
					{
						codeBuilder.endBlock();
					}
					codeBuilder.endBlock().newLine();
					codeBuilder.append("@Override");
					codeBuilder.append("public String[] resolveAllKeys(String baseName, Locale locale)");
					codeBuilder.beginBlock();
					codeBuilder.append("ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);");
					codeBuilder.append("return resourceBundle.keySet().toArray(new String[0]);");
					codeBuilder.endBlock().endBlock(";").newLine().endBlock();

					codeBuilder.append("/** Defines which {@link ResourceBasedTranslator} the facade should use. This affects all available instances. */");
					codeBuilder.append("public static void use( ResourceBasedTranslator resourceBasedTranslator )");
					codeBuilder.beginBlock().append(i18nFacadeName + ".Resource.resourceBasedTranslator = resourceBasedTranslator;").endBlock().newLine();
				}
			}

			//helper classes
			{
				if (!isSubClass)
				{
					importSet.add("java.util.Map");
					appendResourceBasedTranslatorInterface(codeBuilder);
					appendTranslatorHelper(codeBuilder, i18nFacadeName);
				}
			}

			//constructor
			{
				codeBuilder.newLine().append("/**");
				codeBuilder.append(" * This {@link " + className + "} constructor will create a new instance which silently ignores any {@link MissingResourceException}");
				codeBuilder.append(" * @see " + className);
				codeBuilder.append(" * @param locale");
				codeBuilder.append(" */");
				codeBuilder.append("public " + className + "( Locale locale )").beginBlock();
				{
					codeBuilder.append("this(locale, true);");
				}
				codeBuilder.endBlock().newLine();

				codeBuilder.newLine();
				codeBuilder.append("/**");
				codeBuilder.append(" * @see " + className);
				codeBuilder.append(" * @param locale");
				codeBuilder.append(" * @param silentlyIgnoreMissingResourceException");
				codeBuilder.append(" */");
				codeBuilder.append("public " + className + "( Locale locale, boolean silentlyIgnoreMissingResourceException )");
				codeBuilder.beginBlock();
				{
					codeBuilder.append("super();");
					if (!propertyNameToExampleValueListMap.isEmpty())
					{
						codeBuilder.append("this.locale = locale;");
						codeBuilder.append("this.silentlyIgnoreMissingResourceException = silentlyIgnoreMissingResourceException;");
					}

					for (String subClassName : subClassNameToTokenElementMap.keySet())
					{
						codeBuilder.append("this." + subClassName + " = new " + subClassName + "( locale, silentlyIgnoreMissingResourceException );");
					}
				}
				codeBuilder.endBlock().newLine();
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

				for (String propertyName : propertyNameToExampleValueListMap.keySet())
				{

					String propertyKey = propertyNameToPropertyKeyMap.get(propertyName);
					List<String> exampleValueList = propertyNameToExampleValueListMap.get(propertyName);

					List<String> replacementTokensForExampleValuesNumericPlaceholders = determineReplacementTokensForExampleValues(exampleValueList, "\\{\\d+\\}");
					List<String> replacementTokensForExampleValuesArbitraryPlaceholders = determineReplacementTokensForExampleValues(exampleValueList, "\\{\\w+\\}");

					boolean containsNumericalReplacementToken = replacementTokensForExampleValuesNumericPlaceholders.size() > 0;
					boolean containsArbitraryReplacementToken = !containsNumericalReplacementToken && replacementTokensForExampleValuesArbitraryPlaceholders.size() > 0;

					{
						codeBuilder.append("/**");
						codeBuilder.append(" * Similar to {@link #get" + propertyName + "()} for the given {@link Locale}.");
						codeBuilder.append(" * @see " + className);
						codeBuilder.append(" * @see #get" + propertyName + "()");
						codeBuilder.append(" * @param locale");
						codeBuilder.append(" */");
						codeBuilder.append("protected String get" + propertyName + "(Locale locale)");
						codeBuilder.beginBlock();
						codeBuilder.append("try").beginBlock();
						codeBuilder.append("final String key = \"" + propertyKey + "\";");
						codeBuilder.append("return " + i18nFacadeName + ".Resource.resourceBasedTranslator.translate( baseName, key, locale );");
						codeBuilder.endBlock();
						codeBuilder.append("catch (MissingResourceException e)");
						codeBuilder.beginBlock();
						codeBuilder.append("if (!this.silentlyIgnoreMissingResourceException)");
						codeBuilder.beginBlock();
						codeBuilder.append("throw e;");
						codeBuilder.endBlock();
						codeBuilder.append("return null;");
						codeBuilder.endBlock();
						codeBuilder.endBlock().newLine();

						codeBuilder.append("/**");
						codeBuilder.append(" * Returns the value of the property key <b>" + propertyKey + "</b> for the predefined {@link Locale}.");
						printJavaDocPlaceholders(strBuilder, replacementTokensForExampleValuesArbitraryPlaceholders);
						printJavaDocValueExamples(strBuilder, exampleValueList);
						codeBuilder.append(" * @see " + className);
						codeBuilder.append(" */");
						codeBuilder.append("public String get" + propertyName + "()");
						codeBuilder.beginBlock();
						codeBuilder.append("return get" + propertyName + "(this.locale);");
						codeBuilder.endBlock().newLine();
					}

					if (containsNumericalReplacementToken)
					{
						codeBuilder.append("/**");
						codeBuilder.append(" * Similar to  {@link #get" + propertyName + "(Object[])} using the given {@link Locale}.");
						codeBuilder.append(" * @see " + className);
						codeBuilder.append(" * @see #get" + propertyName + "(String[])");
						codeBuilder.append(" * @param locale");
						codeBuilder.append(" * @param tokens");
						codeBuilder.append(" */");
						codeBuilder.append("public String get" + propertyName + "(Locale locale, Object... tokens)");
						codeBuilder.beginBlock();
						codeBuilder.append("String retval = get" + propertyName + "(locale);");
						codeBuilder.append("for (int ii = 0; ii < tokens.length; ii++)");
						codeBuilder.beginBlock();
						codeBuilder.append("String token = tokens[ii] != null ? tokens[ii].toString() : null;");
						codeBuilder.append("if ( token != null )");
						codeBuilder.beginBlock();
						codeBuilder.append("retval = retval.replaceAll( \"\\\\{\" + ii + \"\\\\}\", token );");
						codeBuilder.endBlock();
						codeBuilder.endBlock();
						codeBuilder.append("return retval;");
						codeBuilder.endBlock().newLine();

						codeBuilder.append("/**");
						codeBuilder.append(" * Returns the value of the property key <b>" + propertyKey +
							"</b> for the predefined {@link Locale} with all {0},{1},... placeholders replaced by the given tokens in their order.<br><br>");
						codeBuilder.append(" * If there are not enough parameters existing placeholders will remain unreplaced.");
						printJavaDocPlaceholders(strBuilder, replacementTokensForExampleValuesNumericPlaceholders);
						printJavaDocValueExamples(strBuilder, exampleValueList);
						codeBuilder.append(" * @see " + className);
						codeBuilder.append(" * @see #get" + propertyName + "(Locale, Object[])");
						codeBuilder.append(" * @param tokens");
						codeBuilder.append(" */");
						codeBuilder.append("public String get" + propertyName + "(Object... tokens)");
						codeBuilder.beginBlock();
						codeBuilder.append("return get" + propertyName + "(this.locale, tokens);");
						codeBuilder.endBlock().newLine();
					}

					if (containsArbitraryReplacementToken)
					{
						importSet.add("java.util.Map");

						codeBuilder.append("/**");
						codeBuilder.append(" * Returns the value of the property key <b>" + propertyKey +
							"</b> for the given {@link Locale} with arbitrary placeholder tag like {example} replaced by the given values.<br>");
						codeBuilder
							.append(" * The given placeholderToReplacementMap needs the placeholder tag name and a value. E.g. for {example} the key \"example\" has to be set.");
						printJavaDocPlaceholders(strBuilder, replacementTokensForExampleValuesArbitraryPlaceholders);
						printJavaDocValueExamples(strBuilder, exampleValueList);
						codeBuilder.append(" * @see " + className);
						codeBuilder.append(" * @see #get" + propertyName + "(Map)");
						codeBuilder.append(" * @param locale");
						codeBuilder.append(" * @param placeholderToReplacementMap");
						codeBuilder.append(" */");
						codeBuilder.append("public String get" + propertyName + "( Locale locale, Map<String, String> placeholderToReplacementMap )");
						codeBuilder.beginBlock();
						codeBuilder.append("String retval = get" + propertyName + "(locale);");
						codeBuilder.append("if ( placeholderToReplacementMap != null )");
						codeBuilder.beginBlock();
						codeBuilder.append("for ( String placeholder : placeholderToReplacementMap.keySet() )");
						codeBuilder.beginBlock();
						codeBuilder.append("if ( placeholder != null )");
						codeBuilder.beginBlock();
						codeBuilder.append("String token = placeholderToReplacementMap.get( placeholder );");
						codeBuilder.append("retval = retval.replaceAll( \"\\\\{\" + placeholder + \"\\\\}\", token );");
						codeBuilder.endBlock().endBlock().endBlock();
						codeBuilder.append("return retval;");
						codeBuilder.endBlock().newLine();

						codeBuilder.append("/**");
						codeBuilder.append(" * Similar to  {@link #get" + propertyName + "(Locale,Map)} using the predefined {@link Locale}.");
						codeBuilder.append(" * @see " + className);
						codeBuilder.append(" * @see #get" + propertyName + "(Locale,Map)");
						codeBuilder.append(" * @param placeholderToReplacementMap");
						codeBuilder.append(" */");
						codeBuilder.append("public String get" + propertyName + "( Map<String, String> placeholderToReplacementMap )");
						codeBuilder.beginBlock();
						codeBuilder.append("return get" + propertyName + "( this.locale, placeholderToReplacementMap );");
						codeBuilder.endBlock().newLine();
					}
				}

				//fluid factory methods
				{
					codeBuilder.append("/**");
					codeBuilder.append(" * Returns a new instance of {@link " + className + "} which uses the given setting for the exception handling");
					codeBuilder.append(" * @see " + className);
					codeBuilder.append(" * @param silentlyIgnoreMissingResourceException");
					codeBuilder.append(" */");
					codeBuilder.append("public " + className + " doSilentlyIgnoreMissingResourceException( boolean silentlyIgnoreMissingResourceException )");
					codeBuilder.beginBlock();
					codeBuilder.append("return new " + className + "( this.locale, silentlyIgnoreMissingResourceException );");
					codeBuilder.endBlock().newLine();

					codeBuilder.append("/**");
					codeBuilder.append(" * Returns a new instance of {@link " + className + "} which uses the given {@link Locale}");
					codeBuilder.append(" * @see " + className);
					codeBuilder.append(" * @param locale");
					codeBuilder.append(" */");
					codeBuilder.append("public " + className + " forLocale(Locale locale)");
					codeBuilder.beginBlock();
					codeBuilder.append("return new " + className + "(locale, this.silentlyIgnoreMissingResourceException);");
					codeBuilder.endBlock().newLine();
				}

				//translator methods
				{
					codeBuilder.append("/**");
					codeBuilder.append(" * Returns a new {@link Translator} instance using the given {@link Locale} and based on the {@value #baseName} i18n base");
					codeBuilder.append(" * @see " + className);
					codeBuilder.append(" * @see #translator()");
					codeBuilder.append(" * @see #translator(Locale)");
					codeBuilder.append(" * @return {@link Translator}");
					codeBuilder.append(" */");
					codeBuilder.append("public static Translator translator(Locale locale, boolean silentlyIgnoreMissingResourceException)");
					codeBuilder.beginBlock();
					codeBuilder.append("return new Translator( baseName, locale, silentlyIgnoreMissingResourceException );");
					codeBuilder.endBlock().newLine();

					codeBuilder.append("/**");
					codeBuilder.append(" * Returns a new {@link Translator} instance using the given {@link Locale} and based on the {@value #baseName} i18n base");
					codeBuilder.append(" * @see " + className);
					codeBuilder.append(" * @see #translator()");
					codeBuilder.append(" * @see #translator(Locale, boolean)");
					codeBuilder.append(" * @return {@link Translator}");
					codeBuilder.append(" */");
					codeBuilder.append("public Translator translator(Locale locale)");
					codeBuilder.beginBlock();
					codeBuilder.append("return new Translator( baseName, locale, this.silentlyIgnoreMissingResourceException );");
					codeBuilder.endBlock().newLine();

					codeBuilder.append("/**");
					codeBuilder.append(" * Returns a new {@link Translator} instance using the internal {@link Locale} and based on the {@value #baseName} i18n base");
					codeBuilder.append(" * @see " + className);
					codeBuilder.append(" * @see #translator(Locale)");
					codeBuilder.append(" * @see #translator(Locale,boolean)");
					codeBuilder.append(" * @return {@link Translator}");
					codeBuilder.append(" */");
					codeBuilder.append("public Translator translator()");
					codeBuilder.beginBlock();
					codeBuilder.append("return translator(this.locale);");
					codeBuilder.endBlock().newLine();
				}
			}
		}

		codeBuilder.endBlock().newLine();

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

	private static void printJavaDocPropertiesExamplesForSubclassAndInstance(StringBuilder stringBuilder,
		final Map<String, List<String>> propertyNameToExampleValueListMap,
		final Map<String, String> propertyNameToPropertyKeyMap)
	{

		stringBuilder.append(" * <br><br>\n");
		stringBuilder.append(" * <h1>Examples:</h1>\n");
		stringBuilder.append(" * <table border=\"1\">\n");

		stringBuilder.append(" * <thead>\n");
		stringBuilder.append(" * <tr>\n");
		stringBuilder.append(" * <th>key</th>\n");
		stringBuilder.append(" * <th>examples</th>\n");
		stringBuilder.append(" * </tr>\n");
		stringBuilder.append(" * </thead>\n");

		stringBuilder.append(" * <tbody>\n");
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

				stringBuilder.append(" * <tr>\n");
				stringBuilder.append(" * <td rowspan=\"" + exampleSize + "\">" + propertyKey + "</td>\n");
				stringBuilder.append(" * <td>" + iteratorExampleValueList.next() + "</td>\n");
				stringBuilder.append(" * </tr>\n");
				while (iteratorExampleValueList.hasNext())
				{

					stringBuilder.append(" * <tr>\n");
					stringBuilder.append(" * <td><small>" + iteratorExampleValueList.next() + "</small></td>\n");
					stringBuilder.append(" * </tr>\n");
				}
			}
		}
		stringBuilder.append(" * </tbody>\n");
		stringBuilder.append(" * </table><br><br>\n");
	}

	private static void appendResourceBasedTranslatorInterface(SourceCodeBuilder codeBuilder)
	{
		codeBuilder.newLine();
		codeBuilder.append("/**");
		codeBuilder.append(" * Basic interface which is used by the facade to resolve translated values for given keys<br>");
		codeBuilder.append(" * <br>");
		codeBuilder.append(" * Any implementation should be thread safe");
		codeBuilder.append(" */");
		codeBuilder.append("public static interface ResourceBasedTranslator").beginBlock();

		codeBuilder.append("/**");
		codeBuilder.append(" * Returns the translated value for the given key respecting the base name and the given {@link Locale}");
		codeBuilder.append(" * @param baseName");
		codeBuilder.append(" * @param key");
		codeBuilder.append(" * @param locale");
		codeBuilder.append(" * @return");
		codeBuilder.append(" */");
		codeBuilder.append("public String translate(String baseName, String key, Locale locale);").newLine();

		codeBuilder.append("/**");
		codeBuilder.append(" * Returns all available keys for the given {@link Locale}");
		codeBuilder.append(" * @param baseName");
		codeBuilder.append(" * @param locale");
		codeBuilder.append(" * @return");
		codeBuilder.append(" */");
		codeBuilder.append("public String[] resolveAllKeys(String baseName, Locale locale);");

		codeBuilder.endBlock().newLine();
	}

	private static void appendTranslatorHelper(SourceCodeBuilder codeBuilder, String I18nFacadeName)
	{
		codeBuilder.newLine();
		codeBuilder.append("/**");
		codeBuilder.append(" * A {@link Translator} offers several methods to translate arbitrary keys into their i18n counterpart based on the initially");
		codeBuilder.append(" * given {@link Locale}.");
		codeBuilder.append(" * ");
		codeBuilder.append(" * @see #translate(String)");
		codeBuilder.append(" * @see #translate(String[])");
		codeBuilder.append(" * @see #allPropertyKeys()");
		codeBuilder.append(" */");
		codeBuilder.append("public static class Translator").beginBlock();

		//translator vars and constructor
		{
			codeBuilder.append("private final String baseName;");
			codeBuilder.append("private final Locale locale;");
			codeBuilder.append("private final boolean silentlyIgnoreMissingResourceException;");
			codeBuilder.newLine();

			codeBuilder.append("/**");
			codeBuilder.append(" * @see Translator");
			codeBuilder.append(" * @param baseName");
			codeBuilder.append(" * @param locale");
			codeBuilder.append(" */");
			codeBuilder.append("public Translator(String baseName, Locale locale)");
			codeBuilder.beginBlock();
			codeBuilder.append("this(baseName, locale, true);");
			codeBuilder.endBlock().newLine();

			codeBuilder.append("/**\n");
			codeBuilder.append(" * @see Translator");
			codeBuilder.append(" * @param baseName");
			codeBuilder.append(" * @param locale");
			codeBuilder.append(" */");
			codeBuilder.append("public Translator( String baseName, Locale locale, boolean silentlyIgnoreMissingResourceException )");
			codeBuilder.beginBlock();
			codeBuilder.append("super();");
			codeBuilder.append("this.baseName = baseName;");
			codeBuilder.append("this.locale = locale;");
			codeBuilder.append("this.silentlyIgnoreMissingResourceException = silentlyIgnoreMissingResourceException;");
			codeBuilder.endBlock().newLine();
		}

		//translation map methods
		{
			codeBuilder.append("/**");
			codeBuilder.append(" * Returns the translated property key for the given {@link Locale}");
			codeBuilder.append(" * @see Translator");
			codeBuilder.append(" * @see #translate(String)");
			codeBuilder.append(" * @see #translate(String[])");
			codeBuilder.append(" */ ");
			codeBuilder.append("public String translate(Locale locale, String key)");
			codeBuilder.beginBlock();
			codeBuilder.append("try");
			codeBuilder.beginBlock();
			codeBuilder.append("return " + I18nFacadeName + ".Resource.resourceBasedTranslator.translate( this.baseName, key, locale );");
			codeBuilder.endBlock();
			codeBuilder.append("catch ( MissingResourceException e )");
			codeBuilder.beginBlock();
			codeBuilder.append("if (!this.silentlyIgnoreMissingResourceException)");
			codeBuilder.beginBlock();
			codeBuilder.append("throw e;");
			codeBuilder.endBlock();
			codeBuilder.append("return null;");
			codeBuilder.endBlock();
			codeBuilder.endBlock().newLine();

			codeBuilder.append("/**");
			codeBuilder.append(" * Returns the translated property key for the predefined {@link Locale}");
			codeBuilder.append(" * @see Translator");
			codeBuilder.append(" * @see #translate(Locale, String)");
			codeBuilder.append(" * @see #translate(String[])");
			codeBuilder.append(" */ ");
			codeBuilder.append("public String translate(String key)");
			codeBuilder.beginBlock();
			codeBuilder.append("return translate(this.locale, key);");
			codeBuilder.endBlock().newLine();

			codeBuilder.append("/**");
			codeBuilder.append(" * Returns a translation {@link Map} with the given property keys and their respective values for the given {@link Locale}.");
			codeBuilder.append(" * @param keys ");
			codeBuilder.append(" * @see Translator");
			codeBuilder.append(" * @see #allPropertyKeys()");
			codeBuilder.append(" * @see #translate(String)");
			codeBuilder.append(" */ ");
			codeBuilder.append("public Map<String, String> translate( Locale locale, String... keys )");
			codeBuilder.beginBlock();
			codeBuilder.append("Map<String, String> retmap = new LinkedHashMap<String, String>();");
			codeBuilder.append("for (String key : keys)");
			codeBuilder.beginBlock();
			codeBuilder.append("retmap.put(key, translate(locale, key));");
			codeBuilder.endBlock();
			codeBuilder.append("return retmap;");
			codeBuilder.endBlock().newLine();

			codeBuilder.append("/**");
			codeBuilder.append(" * Returns a translation {@link Map} with the given property keys and their respective values for the predefined {@link Locale}.");
			codeBuilder.append(" * @param keys ");
			codeBuilder.append(" * @see Translator");
			codeBuilder.append(" * @see #allPropertyKeys()");
			codeBuilder.append(" * @see #translate(String)");
			codeBuilder.append(" */ ");
			codeBuilder.append("public Map<String, String> translate(String... keys)");
			codeBuilder.beginBlock();
			codeBuilder.append("return translate(this.locale, keys);");
			codeBuilder.endBlock().newLine();

			codeBuilder.append("/**");
			codeBuilder.append(" * Returns all available property keys for the given {@link Locale}. ");
			codeBuilder.append(" * @see Translator");
			codeBuilder.append(" * @see #allPropertyKeys()");
			codeBuilder.append(" * @see #translate(String[])");
			codeBuilder.append(" */ ");
			codeBuilder.append("public String[] allPropertyKeys(Locale locale)");
			codeBuilder.beginBlock();
			codeBuilder.append("return " + I18nFacadeName + ".Resource.resourceBasedTranslator.resolveAllKeys(this.baseName, locale);");
			codeBuilder.endBlock().newLine();

			codeBuilder.append("/**");
			codeBuilder.append(" * Returns all available property keys for the predefined {@link Locale}. ");
			codeBuilder.append(" * @see Translator");
			codeBuilder.append(" * @see #allPropertyKeys(Locale)");
			codeBuilder.append(" * @see #translate(String[])");
			codeBuilder.append(" */ ");
			codeBuilder.append("public String[] allPropertyKeys()");
			codeBuilder.beginBlock();
			codeBuilder.append("return allPropertyKeys(this.locale);");
			codeBuilder.endBlock().newLine();

			codeBuilder.append("/**");
			codeBuilder.append(" * Returns a translation {@link Map} for the predefined {@link Locale} including all available i18n keys resolved using ");
			codeBuilder.append(" * {@link #allPropertyKeys()} and their respective translation values resolved using {@link #translate(String...)} ");
			codeBuilder.append(" * @see Translator");
			codeBuilder.append(" * @see #allPropertyKeys(Locale)");
			codeBuilder.append(" * @see #translate(String[])");
			codeBuilder.append(" * @return {@link Map}");
			codeBuilder.append(" */ ");
			codeBuilder.append("public Map<String, String> translationMap()");
			codeBuilder.beginBlock();
			codeBuilder.append("return this.translate(this.allPropertyKeys());");
			codeBuilder.endBlock().newLine();

			codeBuilder.append("/**");
			codeBuilder.append(" * Similar to {@link #translationMap()} for the given {@link Locale} instead. ");
			codeBuilder.append(" * @see Translator");
			codeBuilder.append(" * @see #allPropertyKeys(Locale)");
			codeBuilder.append(" * @see #translate(String[])");
			codeBuilder.append(" * @param locale");
			codeBuilder.append(" * @return {@link Map}");
			codeBuilder.append(" */ ");
			codeBuilder.append("public Map<String, String> translationMap(Locale locale)");
			codeBuilder.beginBlock();
			codeBuilder.append("return this.translate(locale, this.allPropertyKeys(locale));");
			codeBuilder.endBlock().newLine();
		}
		codeBuilder.endBlock().newLine();
	}

	private static void printJavaDocPlaceholders(StringBuilder stringBuilder, List<String> replacementTokensForExampleValuesPlaceholders)
	{
		stringBuilder.append("   * <br><br>\n");
		if (!replacementTokensForExampleValuesPlaceholders.isEmpty())
		{
			stringBuilder.append("   * Placeholders:\n");
			stringBuilder.append("   * <ul>\n");
			for (String replacementToken : replacementTokensForExampleValuesPlaceholders)
			{
				stringBuilder.append("   * <li><b>" + replacementToken + "</b></li>\n");
			}
			stringBuilder.append("   * </ul>\n");
		}
	}

	/**
	 * @param stringBuilder
	 * @param exampleValueList
	 */
	private static void printJavaDocValueExamples(StringBuilder stringBuilder, List<String> exampleValueList)
	{
		stringBuilder.append("   * \n");
		stringBuilder.append("   * Examples:\n");
		stringBuilder.append("   * <ul>\n");
		for (String exampleValue : exampleValueList)
		{
			stringBuilder.append("   * <li>" + exampleValue + "</li>\n");
		}
		stringBuilder.append("   * </ul>\n");
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

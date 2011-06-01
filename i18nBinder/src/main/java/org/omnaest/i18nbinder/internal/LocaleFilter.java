package org.omnaest.i18nbinder.internal;

import java.util.regex.Pattern;

/**
 * Locale filter which allows to easily filter locales. As default all locales are accepted.
 * 
 * @author Omnaest
 */
public class LocaleFilter
{
  /* ********************************************** Constants ********************************************** */
  private static final String LOCALE_FILTER_PATTERN_STRING_DEFAULT = ".*";
  
  /* ********************************************** Variables ********************************************** */
  protected Pattern           pattern                              = Pattern.compile( LOCALE_FILTER_PATTERN_STRING_DEFAULT );
  
  /* ********************************************** Methods ********************************************** */

  public boolean isLocaleAccepted( String locale )
  {
    return this.pattern.matcher( locale ).matches();
  }
  
  public void setPattern( Pattern pattern )
  {
    this.pattern = pattern;
  }
  
  public Pattern getPattern()
  {
    return this.pattern;
  }
  
}

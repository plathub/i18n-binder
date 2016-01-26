# How to mock the generated Java source code facade? #

Currently there are two approaches:

  1. Copy the folder structure of the original i18n files into your test files classpath and reduce the files and their content to that what is needed within your JUnit test.
  1. Implement your own ResourceBasedTranslator and supply the facade with it. (Only since 0.1.11)

## Implement your own ResourceBasedTranslator ##

```
I18nFacade.use( new ResourceBasedTranslator()
{
      @Override
      public String translate( String baseName, String key, Locale locale )
      {
        return new MapBuilder<String, String>().linkedHashMap()
                                               .put( "a", "value a" )
                                               .put( "b", "value b" )
                                               .put( "c", "value c" )
                                               .build()
                                               .get( key );
      }
      
      @Override
      public String[] resolveAllKeys( String baseName, Locale locale )
      {
        return new String[] { "a", "b", "c" };
      }
} );
```
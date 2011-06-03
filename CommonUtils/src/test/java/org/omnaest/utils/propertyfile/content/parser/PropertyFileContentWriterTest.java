package org.omnaest.utils.propertyfile.content.parser;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.propertyfile.PropertyFile;
import org.omnaest.utils.propertyfile.content.PropertyFileContent;
import org.omnaest.utils.propertyfile.content.PropertyMap;
import org.omnaest.utils.propertyfile.content.element.Property;

public class PropertyFileContentWriterTest
{
  /* ********************************************** Variables ********************************************** */
  private File srcFile  = null;
  private File destFile = null;
  
  /* ********************************************** Methods ********************************************** */
  @Before
  public void setUp() throws Exception
  {
    //
    URL resourceUrl = this.getClass().getResource( "PropertyFile.properties" );
    
    this.srcFile = new File( resourceUrl.getFile() );
    this.destFile = new File( this.srcFile.getParentFile().getAbsolutePath() + "/" + "test.properties" );
    
    //
    if ( this.destFile.exists() )
    {
      this.destFile.delete();
    }
  }
  
  @Test
  public void testWritePropertyFileContentToFile()
  {
    //
    final String fileEncoding = "UTF-8";
    PropertyFileContent propertyFileContent = PropertyFileContentParser.parsePropertyFileContent( this.srcFile, fileEncoding );
    
    //
    PropertyMap propertyMap = propertyFileContent.getPropertyMap();
    
    //
    Property property = new Property();
    property.setKey( "new.key" );
    property.addValue( "new value" );
    
    propertyMap.put( property );
    
    //
    PropertyFileContentWriter.writePropertyFileContentToFile( propertyFileContent, this.destFile, fileEncoding );
    
    //
    assertTrue( this.destFile.exists() );
    
    //
    PropertyFile propertyFile = new PropertyFile( this.destFile );
    propertyFile.load();
    boolean hasPropertyKey = propertyFile.getPropertyFileContent().hasPropertyWithSameKeyAndValue( property );
    
    //
    assertTrue( hasPropertyKey );
  }
  
}

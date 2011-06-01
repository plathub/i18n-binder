/*******************************************************************************
 * Copyright 2011 Danny Kunz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.omnaest.utils.propertyfile.content.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.propertyfile.content.Element;
import org.omnaest.utils.propertyfile.content.PropertyFileContent;
import org.omnaest.utils.propertyfile.content.element.BlankLineElement;
import org.omnaest.utils.propertyfile.content.element.Comment;
import org.omnaest.utils.propertyfile.content.element.Property;

public class PropertyFileContentParserTest
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
    
  }
  
  @Test
  public void testParsePropertyFile()
  {
    //
    PropertyFileContent propertyFileContent = PropertyFileContentParser.parsePropertyFileContent( this.srcFile, "UTF-8" );
    
    //
    assertNotNull( propertyFileContent );
    assertEquals( 13, propertyFileContent.size() );
    
    //
    Element element = null;
    
    //
    int index = 0;
    {
      //
      element = propertyFileContent.getElementByIndexPosition( index++ );
      assertTrue( element instanceof BlankLineElement );
    }
    {
      //
      element = propertyFileContent.getElementByIndexPosition( index++ );
      assertTrue( element instanceof Comment );
      
      //
      Comment comment = (Comment) element;
      assertEquals( "#", comment.getCommentIndicator() );
    }
    {
      //
      element = propertyFileContent.getElementByIndexPosition( index++ );
      assertTrue( element instanceof Comment );
      
      //
      Comment comment = (Comment) element;
      assertEquals( "!", comment.getCommentIndicator() );
    }
    {
      //
      element = propertyFileContent.getElementByIndexPosition( index++ );
      assertTrue( element instanceof Property );
      
      //
      Property property = (Property) element;
      assertEquals( "simpleKey", property.getKey() );
      assertEquals( " ", property.getDelimiter() );
      assertEquals( "Text", property.getValueList().get( 0 ) );
    }
    {
      //
      element = propertyFileContent.getElementByIndexPosition( index++ );
      assertTrue( element instanceof Property );
      
      //
      Property property = (Property) element;
      assertEquals( "key(dyn)", property.getKey() );
      assertEquals( "  = ", property.getDelimiter() );
      assertEquals( "value", property.getValueList().get( 0 ) );
    }
    {
      //
      element = propertyFileContent.getElementByIndexPosition( index++ );
      assertTrue( element instanceof Property );
      
      //
      Property property = (Property) element;
      assertEquals( "otherKey.45.67.abc", property.getKey() );
      assertEquals( ":", property.getDelimiter() );
      assertEquals( "othervalue", property.getValueList().get( 0 ) );
    }
    {
      //
      element = propertyFileContent.getElementByIndexPosition( index++ );
      assertTrue( element instanceof Property );
      
      //
      Property property = (Property) element;
      assertEquals( "multilineKey", property.getKey() );
      assertEquals( " ", property.getDelimiter() );
      assertEquals( "This paragraph ", property.getValueList().get( 0 ) );
      assertEquals( "is appended by this text", property.getValueList().get( 1 ) );
    }
    {
      //
      element = propertyFileContent.getElementByIndexPosition( index++ );
      assertTrue( element instanceof Property );
      
      //
      Property property = (Property) element;
      assertEquals( "paramKey", property.getKey() );
      assertEquals( " = ", property.getDelimiter() );
      assertEquals( "A text with a dynamical parameter: {0}", property.getValueList().get( 0 ) );
    }
    {
      //
      element = propertyFileContent.getElementByIndexPosition( index++ );
      assertTrue( element instanceof BlankLineElement );
    }
    {
      //
      element = propertyFileContent.getElementByIndexPosition( index++ );
      assertTrue( element instanceof Comment );
    }
    {
      //
      element = propertyFileContent.getElementByIndexPosition( index++ );
      assertTrue( element instanceof BlankLineElement );
    }
    {
      //
      element = propertyFileContent.getElementByIndexPosition( index++ );
      assertTrue( element instanceof BlankLineElement );
    }
    
    //    
    PropertyFileContentWriter.writePropertyFileContentToFile( propertyFileContent, this.destFile, "UTF-8" );
    
    //
    try
    {
      //
      String fileContentSrc = FileUtils.readFileToString( this.srcFile );
      String fileContentDest = FileUtils.readFileToString( this.destFile );
      
      //
      assertEquals( fileContentSrc, fileContentDest );
    }
    catch ( Exception e )
    {
      Assert.fail();
    }
    
  }
}

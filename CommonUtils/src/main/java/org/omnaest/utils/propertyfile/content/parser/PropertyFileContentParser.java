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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.omnaest.utils.propertyfile.PropertyFile;
import org.omnaest.utils.propertyfile.content.PropertyFileContent;
import org.omnaest.utils.propertyfile.content.element.BlankLineElement;
import org.omnaest.utils.propertyfile.content.element.Comment;
import org.omnaest.utils.propertyfile.content.element.Property;

/**
 * Parses the content of a {@link PropertyFile} and creates a {@link PropertyFileContent} object.
 * 
 * @see PropertyFileContent
 * @author Omnaest
 */
public class PropertyFileContentParser
{
  public static PropertyFileContent parsePropertyFileContent( File file, String fileEncoding )
  {
    //
    PropertyFileContent propertyFileContent = new PropertyFileContent();
    
    //
    if ( file != null && file.exists() )
    {
      //
      try
      {
        //        
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( new FileInputStream( file ), fileEncoding ) );
        
        //
        Pattern patternComment = Pattern.compile( "([^\\#]*)(\\!|\\#)(.*)" );
        Pattern patternProperty = Pattern.compile( "([^\\w]*)(\\w[\\w\\.]*)(\\s*=\\s*|\\s*\\:\\s*|\\s+)(.*[^\\\\]|[^\\\\]?)(\\\\?)" );
        Pattern patternBlankLine = Pattern.compile( "([^\\w]*)" );
        Pattern patternPropertyOngoingLine = Pattern.compile( "(.*[^\\\\]|[^\\\\]?)(\\\\?)" );
        
        //
        String lineContent = null;
        Property propertyMultiline = null;
        while ( ( lineContent = bufferedReader.readLine() ) != null )
        {
          //
          if ( propertyMultiline == null )
          {
            //
            Matcher matcherComment = patternComment.matcher( lineContent );
            Matcher matcherProperty = patternProperty.matcher( lineContent );
            Matcher matcherBlankLine = patternBlankLine.matcher( lineContent );
            
            //
            if ( matcherComment.matches() )
            {
              //
              Comment comment = new Comment();
              comment.setPrefixBlanks( matcherComment.group( 1 ) );
              comment.setCommentIndicator( matcherComment.group( 2 ) );
              comment.setComment( matcherComment.group( 3 ) );
              
              //
              propertyFileContent.appendElement( comment );
            }
            else if ( matcherProperty.matches() )
            {
              //
              Property property = new Property();
              property.setPrefixBlanks( matcherProperty.group( 1 ) );
              property.setKey( matcherProperty.group( 2 ) );
              property.setDelimiter( matcherProperty.group( 3 ) );
              List<String> valueList = property.getValueList();
              valueList.add( matcherProperty.group( 4 ) );
              
              //
              if ( StringUtils.isNotBlank( matcherProperty.group( 5 ) ) )
              {
                propertyMultiline = property;
              }
              else
              {
                propertyFileContent.appendElement( property );
              }
            }
            else if ( matcherBlankLine.matches() )
            {
              propertyFileContent.appendElement( new BlankLineElement().setBlanks( matcherBlankLine.group( 1 ) ) );
            }
          }
          else
          {
            //
            Matcher matcherPropertyOngoingLine = patternPropertyOngoingLine.matcher( lineContent );
            
            //
            if ( matcherPropertyOngoingLine.matches() )
            {
              //
              List<String> valueList = propertyMultiline.getValueList();
              valueList.add( matcherPropertyOngoingLine.group( 1 ) );
              
              //
              if ( StringUtils.isBlank( matcherPropertyOngoingLine.group( 2 ) ) )
              {
                //
                propertyFileContent.appendElement( propertyMultiline );
                
                //
                propertyMultiline = null;
              }
            }
          }
        }
        
        //
        bufferedReader.close();
        
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }
    
    //
    return propertyFileContent;
  }
  
}

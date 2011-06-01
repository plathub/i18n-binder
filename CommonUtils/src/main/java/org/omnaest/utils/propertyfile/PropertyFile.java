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
package org.omnaest.utils.propertyfile;

import java.io.File;

import org.omnaest.utils.propertyfile.content.PropertyFileContent;
import org.omnaest.utils.propertyfile.content.parser.PropertyFileContentParser;
import org.omnaest.utils.propertyfile.content.parser.PropertyFileContentWriter;

/**
 * Representation of a property file. Offers methods to load, modify and store property files.<br>
 * <br>
 * This API supports minimal invasive modification which means that existing comments or blank lines are not discarded.
 * 
 * @see #load()
 * @see #store()
 * @see PropertyFileContent
 * @author Omnaest
 */
public class PropertyFile
{
  /* ********************************************** Constants ********************************************** */
  public final static String    FILE_ENCODING_DEFAULT = "UTF-8";
  /* ********************************************** Variables ********************************************** */
  protected File                file                  = null;
  protected String              fileEncoding          = FILE_ENCODING_DEFAULT;
  
  protected PropertyFileContent propertyFileContent   = new PropertyFileContent();
  
  /* ********************************************** Methods ********************************************** */
  public PropertyFile( String propertyFileName )
  {
    this( new File( propertyFileName ) );
  }
  
  public PropertyFile( File propertyFile )
  {
    this.file = propertyFile;
  }
  
  /**
   * Loads the {@link PropertyFile} from disc.
   */
  public void load()
  {
    this.propertyFileContent = PropertyFileContentParser.parsePropertyFileContent( this.file, this.fileEncoding );
  }
  
  /**
   * Stores the {@link PropertyFile} to disc.
   */
  public void store()
  {
    PropertyFileContentWriter.writePropertyFileContentToFile( this.propertyFileContent, this.file, this.fileEncoding );
  }
  
  public File getFile()
  {
    return this.file;
  }
  
  public void setFile( File file )
  {
    this.file = file;
  }
  
  public PropertyFileContent getPropertyFileContent()
  {
    return this.propertyFileContent;
  }
  
  public void setPropertyFileContent( PropertyFileContent propertyFileContent )
  {
    this.propertyFileContent = propertyFileContent;
  }
  
  public String getFileEncoding()
  {
    return this.fileEncoding;
  }
  
  public void setFileEncoding( String fileEncoding )
  {
    this.fileEncoding = fileEncoding;
  }
}

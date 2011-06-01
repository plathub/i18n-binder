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
package org.omnaest.utils.propertyfile.content.element;

import java.util.ArrayList;
import java.util.List;

import org.omnaest.utils.propertyfile.content.Element;
import org.omnaest.utils.propertyfile.content.PropertyFileContent;

/**
 * @see Element
 * @see PropertyFileContent
 * @author Omnaest
 */
public class Property extends Element
{
  /* ********************************************** Variables ********************************************** */
  protected String       prefixBlanks = null;
  protected String       key          = null;
  protected String       delimiter    = null;
  protected List<String> valueList    = new ArrayList<String>();
  
  /* ********************************************** Methods ********************************************** */
  public String getKey()
  {
    return this.key;
  }
  
  public void setKey( String key )
  {
    this.key = key;
  }
  
  public String getDelimiter()
  {
    return this.delimiter;
  }
  
  public void setDelimiter( String delimiter )
  {
    this.delimiter = delimiter;
  }
  
  public List<String> getValueList()
  {
    return this.valueList;
  }
  
  public String getPrefixBlanks()
  {
    return this.prefixBlanks;
  }
  
  public void setPrefixBlanks( String prefixBlanks )
  {
    this.prefixBlanks = prefixBlanks;
  }
  
}

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
import java.util.Collection;
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
  /* ********************************************** Constants ********************************************** */
  private static final String DELIMITER_DEFAULT = " = ";
  
  /* ********************************************** Variables ********************************************** */
  protected String            prefixBlanks      = "";
  protected String            key               = null;
  protected String            delimiter         = DELIMITER_DEFAULT;
  protected List<String>      valueList         = new ArrayList<String>();
  
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
  
  public void addValue( int index, String value )
  {
    this.valueList.add( index, value );
  }
  
  public boolean addValue( String value )
  {
    return this.valueList.add( value );
  }
  
  public boolean addAllValues( Collection<String> valueCollection )
  {
    return this.valueList.addAll( valueCollection );
  }
  
  public void clearValues()
  {
    this.valueList.clear();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.delimiter == null ) ? 0 : this.delimiter.hashCode() );
    result = prime * result + ( ( this.key == null ) ? 0 : this.key.hashCode() );
    result = prime * result + ( ( this.prefixBlanks == null ) ? 0 : this.prefixBlanks.hashCode() );
    result = prime * result + ( ( this.valueList == null ) ? 0 : this.valueList.hashCode() );
    return result;
  }
  
  /**
   * Returns true, if the {@link #getKey()} and {@link #getValueList()} are equal.
   * 
   * @param object
   * @return
   */
  public boolean equalsInKeyAndValue( Object object )
  {
    if ( this == object )
    {
      return true;
    }
    if ( object == null )
    {
      return false;
    }
    if ( !( object instanceof Property ) )
    {
      return false;
    }
    Property other = (Property) object;
    
    if ( this.key == null )
    {
      if ( other.key != null )
      {
        return false;
      }
    }
    else if ( !this.key.equals( other.key ) )
    {
      return false;
    }
    if ( this.valueList == null )
    {
      if ( other.valueList != null )
      {
        return false;
      }
    }
    else if ( !this.valueList.equals( other.valueList ) )
    {
      return false;
    }
    return true;
  }
  
  @Override
  public boolean equals( Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( obj == null )
    {
      return false;
    }
    if ( !( obj instanceof Property ) )
    {
      return false;
    }
    Property other = (Property) obj;
    if ( this.delimiter == null )
    {
      if ( other.delimiter != null )
      {
        return false;
      }
    }
    else if ( !this.delimiter.equals( other.delimiter ) )
    {
      return false;
    }
    if ( this.key == null )
    {
      if ( other.key != null )
      {
        return false;
      }
    }
    else if ( !this.key.equals( other.key ) )
    {
      return false;
    }
    if ( this.prefixBlanks == null )
    {
      if ( other.prefixBlanks != null )
      {
        return false;
      }
    }
    else if ( !this.prefixBlanks.equals( other.prefixBlanks ) )
    {
      return false;
    }
    if ( this.valueList == null )
    {
      if ( other.valueList != null )
      {
        return false;
      }
    }
    else if ( !this.valueList.equals( other.valueList ) )
    {
      return false;
    }
    return true;
  }
  
}

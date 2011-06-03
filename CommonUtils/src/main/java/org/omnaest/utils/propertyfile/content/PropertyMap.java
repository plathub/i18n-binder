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
package org.omnaest.utils.propertyfile.content;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.propertyfile.content.element.Property;

/**
 * {@link Map} implementation for fast access on properties via their property keys. Returns for a key the respective modifiable
 * {@link Property}.
 * 
 * @author Omnaest
 */
public class PropertyMap extends MapAbstract<String, Property>
{
  /* ********************************************** Variables ********************************************** */
  protected PropertyFileContent propertyFileContent = null;
  
  /* ********************************************** Methods ********************************************** */

  protected PropertyMap( PropertyFileContent propertyFileContent )
  {
    super();
    this.propertyFileContent = propertyFileContent;
  }
  
  @Override
  public int size()
  {
    return this.propertyFileContent.getPropertyList().size();
  }
  
  @Override
  public Property get( Object key )
  {
    //
    Property retval = null;
    
    //
    if ( key instanceof String )
    {
      List<Property> propertyList = this.propertyFileContent.getPropertyList();
      for ( Property property : propertyList )
      {
        if ( key.equals( property.getKey() ) )
        {
          retval = property;
          break;
        }
      }
    }
    
    //
    return retval;
  }
  
  public Property put( Property property )
  {
    //
    Property retval = null;
    
    //
    if ( property != null )
    {
      retval = this.put( property.getKey(), property );
    }
    
    //
    return retval;
  }
  
  @Override
  public Property put( String key, Property property )
  {
    //
    Property retval = this.get( key );
    
    //
    if ( property != null )
    {
      //
      if ( retval != null )
      {
        //
        Element elementOld = retval;
        Element elementNew = property;
        this.propertyFileContent.replaceElement( elementOld, elementNew );
      }
      else
      {
        //
        this.propertyFileContent.appendElement( property );
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public Property remove( Object key )
  {
    //
    Property property = this.get( key );
    
    //
    if ( property != null )
    {
      //
      this.propertyFileContent.removeElement( property );
    }
    
    //
    return property;
  }
  
  @Override
  public void clear()
  {
    List<Property> propertyList = this.propertyFileContent.getPropertyList();
    for ( Property property : propertyList )
    {
      this.propertyFileContent.removeElement( property );
    }
  }
  
  @Override
  public Set<String> keySet()
  {
    //
    Set<String> retset = new HashSet<String>();
    
    //
    List<Property> propertyList = this.propertyFileContent.getPropertyList();
    for ( Property property : propertyList )
    {
      retset.add( property.getKey() );
    }
    
    // 
    return retset;
  }
  
  @Override
  public Collection<Property> values()
  {
    return this.propertyFileContent.getPropertyList();
  }
  
}

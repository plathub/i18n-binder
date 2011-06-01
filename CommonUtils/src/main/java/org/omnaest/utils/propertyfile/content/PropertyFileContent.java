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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.omnaest.utils.propertyfile.PropertyFile;
import org.omnaest.utils.propertyfile.content.element.Comment;
import org.omnaest.utils.propertyfile.content.element.Property;
import org.omnaest.utils.propertyfile.content.index.Index;
import org.omnaest.utils.propertyfile.content.index.IndexManager;

/**
 * Container for the content of a {@link PropertyFile}.
 * 
 * @see Element
 * @see Property
 * @see Comment
 * @author Omnaest
 */
public class PropertyFileContent
{
  /* ********************************************** Variables ********************************************** */
  protected List<Element> elementList  = new ArrayList<Element>();
  protected IndexManager  indexManager = new IndexManager();
  
  /* ********************************************** Methods ********************************************** */

  /**
   * Gets a new list instance of all {@link Element}s managed by this {@link PropertyFileContent} instance.
   */
  public List<Element> getElementList()
  {
    return new ArrayList<Element>( this.elementList );
  }
  
  /**
   * Returns a new list instance of all {@link Element}s managed by this {@link PropertyFileContent} instance sorted in ascending
   * order by their index position (which has the same order as the line numbers).
   * 
   * @return
   */
  public List<Element> getElementListAscendingByIndexPosition()
  {
    //
    List<Element> retlist = this.getElementList();
    
    //
    Comparator<Element> comparator = new Comparator<Element>()
    {
      
      @Override
      public int compare( Element element1, Element element2 )
      {
        //
        Integer index1 = element1.resolveIndexPosition();
        Integer index2 = element2.resolveIndexPosition();
        
        //
        return index1.compareTo( index2 );
      }
    };
    
    Collections.sort( retlist, comparator );
    
    //
    return retlist;
  }
  
  /**
   * Returns a new list instance containing all {@link Property} instances.
   * 
   * @return
   */
  public List<Property> getPropertyList()
  {
    //
    List<Property> retlist = new ArrayList<Property>();
    
    //
    for ( Element element : this.elementList )
    {
      if ( element instanceof Property )
      {
        retlist.add( (Property) element );
      }
    }
    
    //
    return retlist;
  }
  
  public void appendElement( Element element )
  {
    //
    if ( element != null )
    {
      //
      this.elementList.add( element );
      
      //
      Index line = this.indexManager.createNewAppendedIndex();
      element.setIndex( line );
    }
  }
  
  public void insertElementAfter( Element element, Element elementBefore )
  {
    //
    if ( element != null && elementBefore != null )
    {
      //
      this.elementList.add( element );
      
      //
      Index currentIndex = elementBefore.getIndex();
      Index index = this.indexManager.getPreviousIndex( currentIndex );
      element.setIndex( index );
    }
  }
  
  /**
   * Returns the number of {@link Element}s.
   * 
   * @return
   */
  public int size()
  {
    return this.elementList.size();
  }
  
  /**
   * Returns the element at the given index position. Index positions starts with index 0.
   * 
   * @param index
   * @return
   */
  public Element getElementByIndexPosition( int index )
  {
    //
    Element retval = null;
    
    //
    for ( Element element : this.elementList )
    {
      if ( element.resolveIndexPosition() == index )
      {
        retval = element;
        break;
      }
    }
    
    //
    return retval;
  }
  
  public void removeElement( Element element )
  {
    //
    if ( element != null && this.elementList.contains( element ) )
    {
      //
      this.elementList.remove( element );
      
      //
      Index index = element.getIndex();
      this.indexManager.removeIndex( index );
    }
  }
  
  /**
   * Clears all {@link Element} instances from this {@link PropertyFileContent} instance.
   */
  public void clear()
  {
    this.elementList.clear();
    this.indexManager.clear();
  }
  
  /**
   * Replaces an old {@link Element} by a new {@link Element}. The new {@link Element} will have the same {@link Index} / line
   * position as the old {@link Element} before.
   * 
   * @param elementOld
   * @param elementNew
   */
  public void replaceElement( Element elementOld, Element elementNew )
  {
    //
    if ( elementNew != null && elementOld != null )
    {
      //
      Index index = elementOld.getIndex();
      elementNew.setIndex( index );
      
      //
      this.elementList.remove( elementOld );
      this.elementList.add( elementNew );
    }
  }
  
  /**
   * Returns a {@link PropertyMap} view on the {@link Property} elements of this {@link PropertyFileContent} instance. The map is
   * backed by the {@link PropertyFileContent} and modifications to it will modify the {@link PropertyFileContent}, too, and vice
   * versa.
   * 
   * @see PropertyMap
   * @return
   */
  public PropertyMap getPropertyMap()
  {
    return new PropertyMap( this );
  }
}

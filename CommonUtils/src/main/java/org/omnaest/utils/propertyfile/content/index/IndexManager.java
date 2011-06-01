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
package org.omnaest.utils.propertyfile.content.index;

import java.util.ArrayList;
import java.util.List;

public class IndexManager
{
  /* ********************************************** Variables ********************************************** */
  protected List<Index> indexList = new ArrayList<Index>();
  
  /* ********************************************** Methods ********************************************** */
  public List<Index> getIndexList()
  {
    return this.indexList;
  }
  
  /**
   * Returns the {@link Index} is comes before the given {@link Index}.
   * 
   * @param currentIndex
   * @return
   */
  public Index getPreviousIndex( Index currentIndex )
  {
    //
    Index index = null;
    
    //
    if ( currentIndex != null )
    {
      int indexPosition = this.indexList.indexOf( currentIndex );
      if ( indexPosition > 0 )
      {
        index = this.indexList.get( indexPosition - 1 );
      }
    }
    
    //
    return index;
  }
  
  public void removeIndex( Index index )
  {
    if ( index != null && this.indexList.contains( index ) )
    {
      this.indexList.remove( index );
    }
  }
  
  public void insertIndex( int indexPosition, Index index )
  {
    //
    if ( index != null )
    {
      this.indexList.add( indexPosition, index );
    }
  }
  
  public int resolveIndexPosition( Index index )
  {
    //
    int retval = -1;
    
    //
    if ( index != null )
    {
      retval = this.indexList.indexOf( index );
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the {@link Index} which follows the given {@link Index}
   * 
   * @param currentIndex
   * @return
   */
  public Index getNextIndex( Index currentIndex )
  {
    //
    Index index = null;
    
    //
    if ( currentIndex != null )
    {
      int indexOf = this.indexList.indexOf( currentIndex );
      if ( indexOf < this.indexList.size() - 1 )
      {
        index = this.indexList.get( indexOf + 1 );
      }
    }
    
    //
    return index;
  }
  
  /**
   * Creates a new {@link Index} instance which is appended to the current list of {@link Index} elements.
   * 
   * @return
   */
  public Index createNewAppendedIndex()
  {
    //
    Index index = new Index( this );
    
    //
    this.indexList.add( index );
    
    //
    return index;
  }
  
  /**
   * Creates a new {@link Index} element before the given current {@link Index}.
   * 
   * @param currentIndex
   * @return
   */
  public Index createNewPreviousIndex( Index currentIndex )
  {
    //
    Index index = null;
    
    //
    if ( currentIndex != null )
    {
      //
      index = new Index( this );
      
      //
      int indexPosition = this.indexList.indexOf( currentIndex );
      this.indexList.add( indexPosition, index );
    }
    
    //
    return index;
  }
  
  public void clear()
  {
    this.indexList.clear();
  }
}

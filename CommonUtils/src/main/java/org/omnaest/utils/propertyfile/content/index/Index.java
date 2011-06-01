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

import org.omnaest.utils.propertyfile.content.element.Property;

/**
 * Representation of an {@link Index} which corresponds strongly to line numbers. The difference is, that {@link Property}s which
 * are spanning multiple lines have only one index position but two line numbers. <br>
 * <br>
 * An {@link Index} will be created by a {@link IndexManager}.
 * 
 * @see IndexManager
 * @see IndexManager#createNewAppendedIndex()
 * @see IndexManager#createNewPreviousIndex(Index)
 * @author Omnaest
 */
public class Index
{
  /* ********************************************** Variables ********************************************** */
  protected IndexManager lineManager = null;
  
  /* ********************************************** Methods ********************************************** */

  protected Index( IndexManager lineManager )
  {
    super();
    this.lineManager = lineManager;
  }
  
  public IndexManager getLineManager()
  {
    return this.lineManager;
  }
  
  public void setLineManager( IndexManager lineManager )
  {
    this.lineManager = lineManager;
  }
  
  public Index getPreviousLine()
  {
    return this.lineManager.getPreviousIndex( this );
  }
  
  public void removeLine()
  {
    this.lineManager.removeIndex( this );
  }
  
  public int resolveIndexPosition()
  {
    return this.lineManager.resolveIndexPosition( this );
  }
  
  public Index getNextLine()
  {
    return this.lineManager.getNextIndex( this );
  }
  
}

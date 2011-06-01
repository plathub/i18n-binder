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

import org.omnaest.utils.propertyfile.content.element.BlankLineElement;
import org.omnaest.utils.propertyfile.content.element.Comment;
import org.omnaest.utils.propertyfile.content.element.Property;
import org.omnaest.utils.propertyfile.content.index.Index;

/**
 * An {@link Element} within a {@link PropertyFileContent}
 * 
 * @see Property
 * @see Comment
 * @see Index
 * @see BlankLineElement
 * @author Omnaest
 */
public class Element
{
  /* ********************************************** Variables ********************************************** */
  protected Index index = null;
  
  /* ********************************************** Methods ********************************************** */
  protected Index getIndex()
  {
    return this.index;
  }
  
  protected void setIndex( Index line )
  {
    this.index = line;
  }
  
  public int resolveIndexPosition()
  {
    return this.index.resolveIndexPosition();
  }
  
}

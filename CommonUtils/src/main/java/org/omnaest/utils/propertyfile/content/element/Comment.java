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

import org.omnaest.utils.propertyfile.content.Element;
import org.omnaest.utils.propertyfile.content.PropertyFileContent;

/**
 * A {@link Comment} within a {@link PropertyFileContent}
 * 
 * @author Omnaest
 */
public class Comment extends Element
{
  /* ********************************************** Variables ********************************************** */
  protected String prefixBlanks     = null;
  protected String commentIndicator = null;
  protected String comment          = null;
  
  /* ********************************************** Methods ********************************************** */
  public String getCommentIndicator()
  {
    return this.commentIndicator;
  }
  
  public void setCommentIndicator( String commentIndicator )
  {
    this.commentIndicator = commentIndicator;
  }
  
  public String getComment()
  {
    return this.comment;
  }
  
  public void setComment( String comment )
  {
    this.comment = comment;
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

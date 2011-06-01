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
package org.omnaest.utils.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

/**
 * Class which allows to replace the group values of a string found by a previous {@link Matcher#matches()} call.
 * 
 * @author Omnaest
 */
public class MatchResultGroupReplacer
{
  /* ********************************************** Variables ********************************************** */
  protected MatchResult matchResult = null;
  
  /* ********************************************** Methods ********************************************** */

  public MatchResultGroupReplacer( MatchResult matchResult )
  {
    super();
    this.matchResult = matchResult;
  }
  
  /**
   * Replaces the {@link Matcher#group(int)} for all group index positions of the given map by the corresponding new values given.
   * 
   * @param groupIndexToNewValueMap
   */
  public String replaceGroups( Map<Integer, String> groupIndexToNewValueMap )
  {
    //
    String retval = matchResult.group();
    
    //
    final MatchResult matchResult = this.matchResult;
    
    //
    if ( groupIndexToNewValueMap != null )
    {
      //
      List<Integer> groupIndexListSorted = new ArrayList<Integer>( groupIndexToNewValueMap.keySet() );
      Collections.sort( groupIndexListSorted, Collections.reverseOrder() );
      
      //
      for ( Integer groupIndex : groupIndexListSorted )
      {
        if ( groupIndex.intValue() <= matchResult.groupCount() && matchResult.group( groupIndex ) != null )
        {
          //
          String valueNew = groupIndexToNewValueMap.get( groupIndex );
          
          //
          int start = matchResult.start( groupIndex );
          int end = matchResult.end( groupIndex );
          
          //
          String textBefore = retval.substring( 0, start );
          String textAfter = retval.substring( end );
          
          //
          retval = textBefore + valueNew + textAfter;
        }
      }
    }
    
    //
    return retval;
  }
}

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

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

public class MatchResultGroupReplacerTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  @Test
  public void testReplaceGroups()
  {
    //
    String text = "/ab/cdabef/ab_xyz";
    
    //
    Pattern pattern = Pattern.compile( "/(ab).*/.*?(ab)(.*?)()" );
    Matcher matcher = pattern.matcher( text );
    
    //
    assertTrue( matcher.matches() );
    
    //
    MatchResultGroupReplacer matchResultGroupReplacer = new MatchResultGroupReplacer( matcher.toMatchResult() );
    
    //
    Map<Integer, String> groupIndexToNewValueMap = new HashMap<Integer, String>();
    groupIndexToNewValueMap.put( 1, "xx" );
    groupIndexToNewValueMap.put( 2, "yy" );
    groupIndexToNewValueMap.put( 4, "zz" );
    
    //
    String textNew = matchResultGroupReplacer.replaceGroups( groupIndexToNewValueMap );
    
    //
    assertEquals( "/xx/cdabef/yy_xyzzz", textNew );
  }
  
}

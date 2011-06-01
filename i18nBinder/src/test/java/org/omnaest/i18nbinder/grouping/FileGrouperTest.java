package org.omnaest.i18nbinder.grouping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class FileGrouperTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  @Test
  public void testDetermineFileGroupIdentifierToFileGroupMap()
  {
    //
    List<File> fileList = new ArrayList<File>();
    fileList.add( new File( "C:\\temp\\admin_de_DE.properties" ) );
    fileList.add( new File( "C:\\temp\\admin_en_US.properties" ) );
    fileList.add( new File( "C:\\temp\\article_de_DE.properties" ) );
    fileList.add( new File( "C:\\temp\\article_en_US.properties" ) );
    fileList.add( new File( "C:\\temp\\article_de.properties" ) );
    fileList.add( new File( "C:\\temp\\article_en.properties" ) );
    
    //
    FileGrouper fileGrouper = new FileGrouper();
    try
    {
      fileGrouper.addAllFiles( fileList );
      fileGrouper.setGroupingPatternReplacementToken( "{locale}" );
      fileGrouper.setGroupingPatternString( "(.*?_(\\w{2,3}_\\w{2,3}|\\w{2,3})|.*())\\.\\w*" );
      fileGrouper.setGroupingPatternGroupingGroupIndexList( Arrays.asList( 2, 3 ) );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      Assert.fail();
    }
    
    //
    Map<String, FileGroup> fileGroupIdentifierToFileGroupMap = fileGrouper.determineFileGroupIdentifierToFileGroupMap();
    
    //
    assertEquals( 2, fileGroupIdentifierToFileGroupMap.size() );
    Set<String> fileGroupIdentifierSet = fileGroupIdentifierToFileGroupMap.keySet();
    assertTrue( fileGroupIdentifierSet.contains( "C:\\temp\\admin_{locale}.properties" ) );
    assertTrue( fileGroupIdentifierSet.contains( "C:\\temp\\article_{locale}.properties" ) );
    
    //
    {
      //
      FileGroup fileGroup = fileGroupIdentifierToFileGroupMap.get( "C:\\temp\\admin_{locale}.properties" );
      
      //
      Map<String, File> groupTokenToFileMap = fileGroup.getGroupTokenToFileMap();
      assertEquals( 2, groupTokenToFileMap.size() );
      assertTrue( groupTokenToFileMap.containsKey( "de_DE" ) );
      assertTrue( groupTokenToFileMap.containsKey( "en_US" ) );
    }
    {
      //
      FileGroup fileGroup = fileGroupIdentifierToFileGroupMap.get( "C:\\temp\\article_{locale}.properties" );
      
      //
      Map<String, File> groupTokenToFileMap = fileGroup.getGroupTokenToFileMap();
      assertEquals( 4, groupTokenToFileMap.size() );
      assertTrue( groupTokenToFileMap.containsKey( "de_DE" ) );
      assertTrue( groupTokenToFileMap.containsKey( "en_US" ) );
      assertTrue( groupTokenToFileMap.containsKey( "de" ) );
      assertTrue( groupTokenToFileMap.containsKey( "en" ) );
    }
  }
  
}

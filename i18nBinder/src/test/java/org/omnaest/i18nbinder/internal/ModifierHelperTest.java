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
package org.omnaest.i18nbinder.internal;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.i18nbinder.internal.XLSFile.TableRow;

public class ModifierHelperTest
{
  /* ********************************************** Constants ********************************************** */
  private final static String[] PROPERTY_FILENAMES = { "adminTest_de_DE.properties", "adminTest_en_US.properties",
      "viewTest_de_DE.properties", "viewTest_en_US.properties", "localelessTest.properties" };
  
  /* ********************************************** Variables ********************************************** */
  private Set<File>             propertyFileSet    = new HashSet<File>();
  private File                  xlsFile            = null;
  
  /* ********************************************** Methods ********************************************** */

  @Before
  public void setUp() throws Exception
  {
    //
    for ( String propertyFilename : PROPERTY_FILENAMES )
    {
      this.propertyFileSet.add( new File( this.getClass().getResource( propertyFilename ).getFile() ) );
    }
    
    //
    this.xlsFile = new File( new File( this.getClass().getResource( PROPERTY_FILENAMES[0] ).getFile() ).getParent()
                             + "\\result.xls" );
  }
  
  @Test
  public void testModifierHelper()
  {
    //
    XLSFile xlsFile = ModifierHelper.createXLSFileFromPropertyFiles( this.propertyFileSet, new LocaleFilter(), null, null );
    
    //
    xlsFile.setFile( this.xlsFile );
    xlsFile.store();
    
    //
    this.assertContent( xlsFile );
    
    //
    ModifierHelper.writeXLSFileContentToPropertyFiles( xlsFile.getFile(), null, new LocaleFilter(), true );
    
    //
    xlsFile.load();
    
    //
    this.assertContent( xlsFile );
    
  }
  
  private void assertContent( XLSFile xlsFile )
  {
    //
    List<TableRow> tableRowList = xlsFile.getTableRowList();
    assertEquals( 6 + 1, tableRowList.size() );
    
    //
    int index = 0;
    {
      TableRow tableRow = tableRowList.get( index++ );
      assertEquals( Arrays.asList( "File", "Property key", "", "de_DE", "en_US" ), tableRow );
    }
    {
      TableRow tableRow = tableRowList.get( index++ );
      assertEquals( Arrays.asList( "my.property.key1", "", "wert1", "value1" ), tableRow.subList( 1, tableRow.size() ) );
    }
    {
      TableRow tableRow = tableRowList.get( index++ );
      assertEquals( Arrays.asList( "my.property.key2", "", "wert2", "value2" ), tableRow.subList( 1, tableRow.size() ) );
    }
    {
      TableRow tableRow = tableRowList.get( index++ );
      assertEquals( Arrays.asList( "my.property.key9", "value9", "", "" ), tableRow.subList( 1, tableRow.size() ) );
    }
    {
      TableRow tableRow = tableRowList.get( index++ );
      assertEquals( Arrays.asList( "my.property.key1", "", "wert1", "value1" ), tableRow.subList( 1, tableRow.size() ) );
    }
    {
      TableRow tableRow = tableRowList.get( index++ );
      assertEquals( Arrays.asList( "my.property.key3", "", "", "value3" ), tableRow.subList( 1, tableRow.size() ) );
    }
    {
      TableRow tableRow = tableRowList.get( index++ );
      assertEquals( Arrays.asList( "my.property.key4", "", "wert4", "" ), tableRow.subList( 1, tableRow.size() ) );
    }
  }
}

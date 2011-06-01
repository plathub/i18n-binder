package org.omnaest.i18nbinder.grouping;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link FileGroup} represents a grouping of files which share a common file group identifier.
 * 
 * @see FileGrouper
 */
public class FileGroup
{
  /* ********************************************** Variables ********************************************** */
  protected String            fileGroupIdentifier = null;
  
  protected Map<String, File> groupTokenToFileMap = new HashMap<String, File>();
  
  /* ********************************************** Methods ********************************************** */
  public String getFileGroupIdentifier()
  {
    return this.fileGroupIdentifier;
  }
  
  public void setFileGroupIdentifier( String fileGroupIdentifier )
  {
    this.fileGroupIdentifier = fileGroupIdentifier;
  }
  
  public Map<String, File> getGroupTokenToFileMap()
  {
    return this.groupTokenToFileMap;
  }
  
}
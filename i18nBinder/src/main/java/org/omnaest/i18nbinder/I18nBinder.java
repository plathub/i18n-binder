package org.omnaest.i18nbinder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.omnaest.i18nbinder.internal.LocaleFilter;
import org.omnaest.i18nbinder.internal.Logger;
import org.omnaest.i18nbinder.internal.ModifierHelper;
import org.omnaest.i18nbinder.internal.XLSFile;

public class I18nBinder extends Task
{
  /* ********************************************** Variables ********************************************** */
  protected List<FileSet> fileSetList                              = new ArrayList<FileSet>();
  
  protected String        xlsFileName                              = null;
  protected String        fileEncoding                             = null;
  
  protected Boolean       logInfo                                  = false;
  
  protected LocaleFilter  localeFilter                             = new LocaleFilter();
  protected boolean       deletePropertiesWithBlankValue           = true;
  
  private String          fileNameLocaleGroupPattern               = null;
  private List<Integer>   fileNameLocaleGroupPatternGroupIndexList = null;
  
  /* ********************************************** Methods ********************************************** */
  @Override
  public void execute() throws BuildException
  {
    //
    super.execute();
    
    //
    this.run();
  }
  
  public void run()
  {
    //
    if ( this.logInfo )
    {
      ModifierHelper.logger = new Logger()
      {
        @Override
        public void info( String message )
        {
          I18nBinder.this.log( message, Project.MSG_INFO );
        }
      };
    }
    
    //
    if ( this.fileSetList.size() > 0 )
    {
      this.createXLSFileFromFiles();
    }
    else
    {
      this.writeXLSFileBackToFiles();
    }
    
  }
  
  protected void writeXLSFileBackToFiles()
  {
    //
    if ( this.xlsFileName != null )
    {
      //
      this.log( "Write properties from XLS file back to property files..." );
      
      //
      File file = new File( this.xlsFileName );
      if ( file.exists() )
      {
        ModifierHelper.writeXLSFileContentToPropertyFiles( file, this.fileEncoding, this.localeFilter,
                                                           this.deletePropertiesWithBlankValue );
      }
      
      //
      this.log( "...done" );
    }
  }
  
  /**
   * Parses the property files and creates the xls file.
   */
  protected void createXLSFileFromFiles()
  {
    //
    if ( this.xlsFileName != null && this.fileSetList.size() > 0 )
    {
      //
      this.log( "Create XLS file from property files..." );
      
      //
      Set<File> propertyFileSet = this.resolveFilesFromFileSetList( this.fileSetList );
      
      //
      XLSFile xlsFile = ModifierHelper.createXLSFileFromPropertyFiles( propertyFileSet, this.localeFilter,
                                                                       this.fileNameLocaleGroupPattern,
                                                                       this.fileNameLocaleGroupPatternGroupIndexList );
      
      //
      File file = new File( this.xlsFileName );
      xlsFile.setFile( file );
      xlsFile.store();
      
      //
      this.log( "...done" );
    }
    else
    {
      this.log( "No xls file name specified. Please provide a file name for the xls file which should be created.",
                Project.MSG_ERR );
    }
    
  }
  
  /**
   * @see #resolveFilesFromFileSet(FileSet)
   * @param fileSetList
   * @return
   */
  protected Set<File> resolveFilesFromFileSetList( List<FileSet> fileSetList )
  {
    //
    Set<File> retset = new HashSet<File>();
    
    //
    if ( fileSetList != null )
    {
      for ( FileSet fileSet : fileSetList )
      {
        retset.addAll( this.resolveFilesFromFileSet( fileSet ) );
      }
    }
    
    //
    return retset;
  }
  
  /**
   * @see #resolveFilesFromFileSetList(List)
   * @param fileSet
   * @return
   */
  protected List<File> resolveFilesFromFileSet( FileSet fileSet )
  {
    //
    List<File> retlist = new ArrayList<File>();
    
    //
    if ( fileSet != null )
    {
      //
      DirectoryScanner directoryScanner = fileSet.getDirectoryScanner();
      String[] includedFileNames = directoryScanner.getIncludedFiles();
      
      //
      if ( includedFileNames != null )
      {
        //
        File basedir = directoryScanner.getBasedir();
        
        //
        for ( String fileNameUnnormalized : includedFileNames )
        {
          //
          String fileName = fileNameUnnormalized.replaceAll( Pattern.quote( "\\" ), "/" );
          
          //
          File file = new File( basedir, fileName );
          if ( file.exists() )
          {
            retlist.add( file );
          }
        }
      }
      
    }
    
    //
    return retlist;
  }
  
  public void addFileset( FileSet fileset )
  {
    if ( fileset != null )
    {
      this.fileSetList.add( fileset );
    }
  }
  
  public String getXlsFileName()
  {
    return this.xlsFileName;
  }
  
  public void setXlsFileName( String xlsFileName )
  {
    this.log( "xlsFileName=" + xlsFileName );
    this.xlsFileName = xlsFileName;
  }
  
  public String getFileEncoding()
  {
    return this.fileEncoding;
  }
  
  public void setFileEncoding( String fileEncoding )
  {
    this.log( "fileEncoding=" + fileEncoding );
    this.fileEncoding = fileEncoding;
  }
  
  public Boolean getLogInfo()
  {
    return this.logInfo;
  }
  
  public void setLogInfo( Boolean logInfo )
  {
    this.log( "logInfo=" + logInfo );
    this.logInfo = logInfo;
  }
  
  public String getLocaleFilterRegex()
  {
    return this.localeFilter.getPattern().pattern();
  }
  
  public void setLocaleFilterRegex( String localeFilterRegex )
  {
    this.log( "localeFilterRegex=" + localeFilterRegex );
    this.localeFilter.setPattern( Pattern.compile( localeFilterRegex ) );
  }
  
  public String getFileNameLocaleGroupPattern()
  {
    return this.fileNameLocaleGroupPattern;
  }
  
  public void setFileNameLocaleGroupPattern( String fileNameLocaleGroupPattern )
  {
    //
    this.log( "fileNameLocaleGroupPattern=" + fileNameLocaleGroupPattern );
    
    //
    this.fileNameLocaleGroupPattern = fileNameLocaleGroupPattern;
    
    //
    if ( this.fileNameLocaleGroupPatternGroupIndexList == null )
    {
      this.fileNameLocaleGroupPatternGroupIndexList = Arrays.asList( 1 );
    }
  }
  
  public boolean isDeletePropertiesWithBlankValue()
  {
    return this.deletePropertiesWithBlankValue;
  }
  
  public void setDeletePropertiesWithBlankValue( boolean deletePropertiesWithBlankValue )
  {
    this.log( "deletePropertiesWithBlankValue=" + deletePropertiesWithBlankValue );
    this.deletePropertiesWithBlankValue = deletePropertiesWithBlankValue;
  }
  
  public String getFileNameLocaleGroupPatternGroupIndexList()
  {
    return StringUtils.join( this.fileNameLocaleGroupPatternGroupIndexList, "," );
  }
  
  public void setFileNameLocaleGroupPatternGroupIndexList( String fileNameLocaleGroupPatternGroupIndexStringList )
  {
    //
    this.log( "fileNameLocaleGroupPatternGroupIndexList=" + fileNameLocaleGroupPatternGroupIndexStringList );
    String[] tokens = StringUtils.split( fileNameLocaleGroupPatternGroupIndexStringList.replaceAll( ";", "," ), "," );
    
    //
    List<Integer> fileNameLocaleGroupPatternGroupIndexList = new ArrayList<Integer>();
    for ( String token : tokens )
    {
      fileNameLocaleGroupPatternGroupIndexList.add( Integer.valueOf( token ) );
    }
    
    //
    this.fileNameLocaleGroupPatternGroupIndexList = fileNameLocaleGroupPatternGroupIndexList;
  }
  
}

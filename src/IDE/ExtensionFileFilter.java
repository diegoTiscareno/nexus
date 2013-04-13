package IDE;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * A sime file filter for file choosers.
 * 
 * @author djemili
 */
public class ExtensionFileFilter extends FileFilter
{
    private String m_extension;
    private String m_description;

    public ExtensionFileFilter(String extension, String description)
    {
        m_extension = extension;
        m_description = description;
    }
    
    public ExtensionFileFilter(String extension)
    {
        m_extension = extension;
        m_description = "File of type " + extension;
    }

    /*
     * @see javax.swing.filechooser.FileFilter
     */
    @Override
    public boolean accept(File f)
    {
        return f.isDirectory() || f.getName().endsWith(m_extension);
    }

    /*
     * @see javax.swing.filechooser.FileFilter
     */
    @Override
    public String getDescription()
    {
        return m_description;
    }

    public String getExtension()
    {
        return m_extension;
    }
    
    @Override
    public String toString(){
        return m_extension;
    }
}
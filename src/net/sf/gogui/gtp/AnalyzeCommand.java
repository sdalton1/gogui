//----------------------------------------------------------------------------
// $Id: AnalyzeCommand.java 4830 2007-09-05 18:08:19Z enz $
//----------------------------------------------------------------------------

package net.sf.gogui.gtp;

import java.io.File;
import net.sf.gogui.go.ConstPointList;
import net.sf.gogui.go.GoColor;
import static net.sf.gogui.go.GoColor.BLACK;
import static net.sf.gogui.go.GoColor.WHITE;
import net.sf.gogui.go.GoPoint;
import net.sf.gogui.go.PointList;

/** Concrete analyze command including data for wildcard replacements.
    See GoGui documentation, chapter "Analyze Commands"
*/
public class AnalyzeCommand
{
    public AnalyzeCommand(AnalyzeDefinition definition)
    {
        m_definition = definition;
    }

    public String getLabel()
    {
        return m_definition.getLabel();
    }

    public GoColor getColorArg()
    {
        return m_colorArg;
    }

    public GoPoint getPointArg()
    {
        return m_pointArg;
    }

    public PointList getPointListArg()
    {
        return m_pointListArg;
    }

    public AnalyzeType getType()
    {
        return m_definition.getType();
    }

    public String getResultTitle()
    {
        StringBuilder buffer = new StringBuilder(getLabel());
        if (needsColorArg() && m_colorArg != null)
        {
            if (m_colorArg == BLACK)
                buffer.append(" Black");
            else
            {
                assert m_colorArg == WHITE;
                buffer.append(" White");
            }
        }
        if (needsPointArg() && m_pointArg != null)
        {
            buffer.append(' ');
            buffer.append(m_pointArg);
        }
        else if (needsPointListArg())
        {
            for (GoPoint p : m_pointListArg)
            {
                buffer.append(' ');
                buffer.append(p);
            }
        }
        if (needsStringArg() && m_stringArg != null)
        {
            buffer.append(' ');
            buffer.append(m_stringArg);
        }
        return buffer.toString();
    }

    public boolean isPointArgMissing()
    {
        if (needsPointArg())
            return (m_pointArg == null);
        if (needsPointListArg())
            return m_pointListArg.isEmpty();
        return false;
    }

    public boolean isTextType()
    {
        return m_definition.isTextType();
    }

    public boolean needsColorArg()
    {
        return m_definition.needsColorArg();
    }

    public boolean needsFileArg()
    {
        return m_definition.needsFileArg();
    }

    public boolean needsFileOpenArg()
    {
        return m_definition.needsFileOpenArg();
    }

    public boolean needsFileSaveArg()
    {
        return m_definition.needsFileSaveArg();
    }

    public boolean needsOnlyPointArg()
    {
        return m_definition.needsOnlyPointArg();
    }

    public boolean needsOnlyPointAndColorArg()
    {
        return m_definition.needsOnlyPointAndColorArg();
    }

    public boolean needsPointArg()
    {
        return m_definition.needsPointArg();
    }

    public boolean needsPointListArg()
    {
        return m_definition.needsPointListArg();
    }

    public boolean needsStringArg()
    {
        return m_definition.needsStringArg();
    }

    public boolean needsOptStringArg()
    {
        return m_definition.needsOptStringArg();
    }

    public String replaceWildCards(GoColor toMove)
    {
        String command = m_definition.getCommand();
        String result = command.replaceAll("%m", toMove.toString());
        if (needsPointArg() && m_pointArg != null)
            result = result.replaceAll("%p", m_pointArg.toString());
        if (needsPointListArg())
        {
            String pointList = GoPoint.toString(m_pointListArg);
            if (getType() == AnalyzeType.EPLIST
                && m_pointListArg.size() > 0)
                result = result + ' ' + pointList;
            else
                result = result.replaceAll("%P", pointList);
        }
        if (needsFileArg())
        {
            String fileArg = m_fileArg.toString();
            if (fileArg.indexOf(' ') >= 0)
                fileArg = "\"" + fileArg + "\"";
            result = result.replaceAll("%f", fileArg);
        }
        if (needsFileOpenArg())
        {
            String fileOpenArg = m_fileOpenArg.toString();
            if (fileOpenArg.indexOf(' ') >= 0)
                fileOpenArg = "\"" + fileOpenArg + "\"";
            result = result.replaceAll("%r", fileOpenArg);
        }
        if (needsFileSaveArg())
        {
            String fileSaveArg = m_fileSaveArg.toString();
            if (fileSaveArg.indexOf(' ') >= 0)
                fileSaveArg = "\"" + fileSaveArg + "\"";
            result = result.replaceAll("%w", fileSaveArg);
        }
        if (needsStringArg())
        {
            assert m_stringArg != null;
            result = result.replaceAll("%s", m_stringArg);
        }
        if (needsOptStringArg())
        {
            assert m_optStringArg != null;
            result = result.replaceAll("%o", m_optStringArg);
        }
        if (needsColorArg())
        {
            String colorString = "empty";
            if (m_colorArg == BLACK)
                colorString = "b";
            else if (m_colorArg == WHITE)
                colorString = "w";
            result = result.replaceAll("%c", colorString);
        }
        return result;
    }

    public void setColorArg(GoColor color)
    {
        assert needsColorArg();
        m_colorArg = color;
    }

    public void setFileArg(File file)
    {
        assert needsFileArg();
        m_fileArg = file;
    }

    public void setFileOpenArg(File file)
    {
        assert needsFileOpenArg();
        m_fileOpenArg = file;
    }

    public void setFileSaveArg(File file)
    {
        assert needsFileSaveArg();
        m_fileSaveArg = file;
    }

    public void setPointArg(GoPoint point)
    {
        m_pointArg = point;
    }

    public void setPointListArg(ConstPointList pointList)
    {
        m_pointListArg = new PointList(pointList);
    }

    public void setStringArg(String value)
    {
        assert needsStringArg();
        m_stringArg = value;
    }

    public void setOptStringArg(String value)
    {
        assert needsOptStringArg();
        m_optStringArg = value;
    }

    private final AnalyzeDefinition m_definition;

    private GoColor m_colorArg;

    private File m_fileArg;

    private File m_fileOpenArg;

    private File m_fileSaveArg;

    private String m_optStringArg;

    private String m_stringArg;

    private GoPoint m_pointArg;

    private PointList m_pointListArg = new PointList();
}
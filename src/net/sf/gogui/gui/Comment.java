//----------------------------------------------------------------------------
// $Id$
//----------------------------------------------------------------------------

package net.sf.gogui.gui;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/** Scroll pane for displaying a comment to the current game node. */
public class Comment
    extends JScrollPane
    implements DocumentListener
{
    /** Callback for events generated by Comment. */
    public interface Listener
    {
        void changed(String comment);

        /** Callback if some text is selected. */
        void textSelected(String text);
    }

    public Comment(Listener listener)
    {
        m_listener = listener;
        m_textPane = new GuiTextPane();
        setFocusTraversalKeys(m_textPane);
        m_textPane.addStyle("marked", Color.white, Color.decode("#38d878"), 
                            false);
        setPreferredSize();
        m_textPane.getDocument().addDocumentListener(this);
        CaretListener caretListener = new CaretListener()
            {
                public void caretUpdate(CaretEvent event)
                {
                    if (m_listener == null)
                        return;
                    JTextComponent textComponent = m_textPane.get();
                    m_listener.textSelected(textComponent.getSelectedText());
                }
            };
        m_textPane.get().addCaretListener(caretListener);
        setViewportView(m_textPane.get());
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setMonoFont(false);
    }

    public void changedUpdate(DocumentEvent e) 
    {
        fireChangedEvent();
    }

    public boolean getMonoFont()
    {
        return m_monoFont;
    }

    public boolean getScrollableTracksViewportWidth()
    {
        return true;
    }

    public String getSelectedText()
    {
        return m_textPane.get().getSelectedText();
    }

    public void insertUpdate(DocumentEvent e)
    {
        fireChangedEvent();
    }

    public void markAll(Pattern pattern)
    {        
        Document doc = m_textPane.getDocument();
        try
        {
            CharSequence text = doc.getText(0, doc.getLength());
            Matcher matcher = pattern.matcher(text);
            boolean firstMatch = true;
            while (matcher.find())
            {
                int start = matcher.start();
                int end = matcher.end();
                if (firstMatch)
                {
                    m_textPane.setStyle(0, doc.getLength(), null);
                    m_textPane.get().setCaretPosition(start);
                    firstMatch = false;
                }
                m_textPane.setStyle(start, end - start, "marked");
            }
        }
        catch (BadLocationException e)
        {
            assert(false);
        }
    }

    public void removeUpdate(DocumentEvent e)
    {
        fireChangedEvent();
    }

    /** Enable/disable fixed size font. */
    public void setMonoFont(boolean enable)
    {
        if (enable)
            GuiUtil.setMonospacedFont(m_textPane.get());
        else
            m_textPane.get().setFont(UIManager.getFont("TextArea.font"));
        m_monoFont = enable;
        m_textPane.get().repaint();
    }

    public void setComment(String comment)
    {
        if (comment == null)
            comment = "";
        if (comment.equals(m_textPane.get().getText()))
            return;
        // setText() generates a remove and insert event, and
        // we don't want to notify the listener about that yet
        m_duringSetText = true;
        m_textPane.get().setText(comment);
        m_textPane.get().setCaretPosition(0);
        m_duringSetText = false;
    }

    public void setPreferredSize()
    {
        int fontSize = GuiUtil.getDefaultMonoFontSize();
        setPreferredSize(new Dimension(15 * fontSize, 5 * fontSize));
    }

    private boolean m_monoFont;

    private boolean m_duringSetText;

    /** Serial version to suppress compiler warning.
        Contains a marker comment for serialver.sourceforge.net
    */
    private static final long serialVersionUID = 0L; // SUID    

    private final GuiTextPane m_textPane;

    private final Listener m_listener;

    private void fireChangedEvent()
    {
        if (m_duringSetText)
            return;
        String comment = m_textPane.get().getText().trim();
        m_listener.changed(comment);
    }

    private static void setFocusTraversalKeys(GuiTextPane textPane)
    {
        int id = KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS;
        Set keystrokes = new TreeSet();
        keystrokes.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, 0));
        textPane.get().setFocusTraversalKeys(id, keystrokes);
    }
}


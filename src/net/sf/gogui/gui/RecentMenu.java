// RecentMenu.java

package net.sf.gogui.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.sf.gogui.util.PrefUtil;
import net.sf.gogui.util.ObjectUtil;

class RecentMenuItem
    extends JMenuItem
{
    public RecentMenuItem(String label, String value,
                          ActionListener listener)
    {
        super(label);
        m_label = label;
        m_value = value;
        if (! ObjectUtil.equals(label, value))
            setToolTipText(value);
        addActionListener(listener);
    }

    public String getRecentMenuLabel()
    {
        return m_label;
    }

    public String getRecentMenuValue()
    {
        return m_value;
    }

    public void setRecentMenuLabel(String label)
    {
        setText(label);
        m_label = label;
    }

    private String m_label;

    private final String m_value;
}

/** Menu for recent item.
    Handles removing duplicates and storing the items between sessions. */
public final class RecentMenu
{
    /** Listener for events generated by RecentMenu. */
    public interface Listener
    {
        void itemSelected(String label, String value);
    }

    public RecentMenu(String label, String path, Listener listener)
    {
        assert listener != null;
        m_path = path;
        m_listener = listener;
        m_menu = new GuiMenu(label);
        m_actionListener = new ActionListener()
            {
                public void actionPerformed(ActionEvent event)
                {
                    RecentMenuItem item = (RecentMenuItem)event.getSource();
                    String label = item.getRecentMenuLabel();
                    String value = item.getRecentMenuValue();
                    m_listener.itemSelected(label, value);
                }
            };
        get();
        updateEnabled();
    }

    public void add(String label, String value)
    {
        for (int i = 0; i < getCount(); ++i)
            if (getValue(i).equals(value))
                m_menu.remove(i);
        JMenuItem item = new RecentMenuItem(label, value, m_actionListener);
        m_menu.add(item, 0);
        while (getCount() > MAX_ITEMS)
            m_menu.remove(getCount() - 1);
        put();
    }

    public int getCount()
    {
        return m_menu.getMenuComponentCount();
    }

    /** Don't modify the items in this menu! */
    public GuiMenu getMenu()
    {
        return m_menu;
    }

    public String getValue(int i)
    {
        return getItem(i).getRecentMenuValue();
    }

    public void remove(int i)
    {
        m_menu.remove(getItem(i));
    }

    public void setLabel(int i, String label)
    {
        getItem(i).setRecentMenuLabel(label);
        put();
    }

    /** Set menu enabled if not empty, disabled otherwise. */
    public void updateEnabled()
    {
        int count = getCount();
        m_menu.setEnabled(count > 0);
    }

    private static final int MAX_ITEMS = 20;

    private final String m_path;

    private final ActionListener m_actionListener;

    private final Listener m_listener;

    private final GuiMenu m_menu;

    private void get()
    {
        Preferences prefs = PrefUtil.getNode(m_path);
        if (prefs == null)
            return;
        int size = prefs.getInt("size", 0);
        if (size < 0)
            size = 0;
        m_menu.removeAll();
        for (int i = 0; i < size; ++i)
        {
            prefs = PrefUtil.getNode(m_path + "/" + i);
            if (prefs == null)
                break;
            String label = prefs.get("label", null);
            String value = prefs.get("value", null);
            if (label == null || value == null)
                continue;
            add(label, value);
        }
    }

    private RecentMenuItem getItem(int i)
    {
        return (RecentMenuItem)m_menu.getItem(i);
    }

    private String getLabel(int i)
    {
        return getItem(i).getRecentMenuLabel();
    }

    private void put()
    {
        Preferences prefs = PrefUtil.createNode(m_path);
        if (prefs == null)
            return;
        int size = getCount();
        prefs.putInt("size", size);
        for (int i = 0; i < size; ++i)
        {
            prefs = PrefUtil.createNode(m_path + "/" + (size - i - 1));
            if (prefs == null)
                break;
            prefs.put("label", getLabel(i));
            prefs.put("value", getValue(i));
        }
    }
}

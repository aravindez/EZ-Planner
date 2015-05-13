import javax.swing.SwingConstants;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

import java.awt.Color;

import java.time.LocalDate;
import java.time.Month;
import java.time.DayOfWeek;
import java.time.ZoneId;

public class Tile extends JPanel
{
    private LocalDate date;
    private Month month;
    private int daynum;
    private DayOfWeek day;
    private int year;
    private boolean selected;
    private Border line = BorderFactory.createLineBorder(Color.black);

    public Tile(LocalDate _date)
    {
        this.date = _date;
        this.month = date.getMonth();
        this.daynum = date.getDayOfMonth();
        this.day = date.getDayOfWeek();
        this.year = date.getYear();
        this.selected = false;
        add(new JLabel(Integer.toString(daynum), SwingConstants.LEFT));
        setBackground(new Color(255,255,255));
        setBorder(line);
    }

    public Tile()
    { this(LocalDate.now()); }

    public boolean getSelect()
    { return selected; }

    public void setSelected(boolean flag)
    {
        this.selected = flag;
        if(flag)
        { setBackground(new Color(64,188,237)); }
    }
}

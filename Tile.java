import javax.swing.SwingConstants;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

import java.awt.Color;
import java.awt.event.MouseListener;

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
        this.setBorder(line);
    }

    public Tile()
    { this(LocalDate.now()); }

    public void setSelected(boolean flag)
    { this.selected = flag; }
}

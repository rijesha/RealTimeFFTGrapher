import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartPanel;

import java.util.concurrent.Semaphore;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;


@SuppressWarnings("serial")
public class RealTimeGraph extends JPanel {

    private DynamicTimeSeriesCollection dataset;
    private JFreeChart chart = null;
    private Semaphore calendarLock;

    public RealTimeGraph(String title, String yaxis, String xaxis, Semaphore calendarLock) {
    	this.calendarLock = calendarLock;
        dataset = new DynamicTimeSeriesCollection(1, 2000, new Second());
        dataset.setTimeBase(new Second()); 

        dataset.addSeries(new float[1], 0, title);
        chart = ChartFactory.createTimeSeriesChart(
            title, yaxis, xaxis, dataset, false,
            true, false);
        final XYPlot plot = chart.getXYPlot();

        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setTickLabelsVisible(false);
        axis.setFixedAutoRange(300000); // proportional to scroll speed
        axis = plot.getRangeAxis();

        final ChartPanel chartPanel = new ChartPanel(chart);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(chartPanel);
    }

    public void update(float value) {
    	try {
			calendarLock.acquire();
		} catch (InterruptedException e) {
			System.out.println("Calendar Lock Semaphore issue");
			e.printStackTrace();
		}
        dataset.advanceTime();
        dataset.appendData(new float[]{value});
        calendarLock.release();
    }
}
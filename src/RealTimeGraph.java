import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartPanel;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;


@SuppressWarnings("serial")
public class RealTimeGraph extends JPanel {

    private DynamicTimeSeriesCollection dataset;
    private JFreeChart chart = null;

    public RealTimeGraph(String title, String yaxis, String xaxis) {
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
        dataset.advanceTime();
        dataset.appendData(new float[]{value});
    }
}
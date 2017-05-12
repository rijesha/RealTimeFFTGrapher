import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartPanel;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

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

    public static void main(String[] args) {
        JFrame frame = new JFrame("testing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final RealTimeGraph chart = new RealTimeGraph("random numbers","random numbers","time");
        frame.add(chart);
        frame.pack();
        frame.setVisible(true);
        Timer timer = new Timer(8, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        chart.update((float) (Math.random() * 10));
                    }
                });
            }
        });
        timer.start();
    }
}
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ChartPanel;

import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.jfree.chart.ChartFactory;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.axis.ValueAxis;

import java.util.concurrent.Semaphore;

@SuppressWarnings("serial")
public class XYGraph extends JPanel {
	
	private XYSeries series;
	private int seriesLength;
	private XYSeriesCollection ds = new XYSeriesCollection();
	private FftPlotter parent;
	private Semaphore calendarLock;
	
	public XYGraph(String title, String yaxis, String xaxis, int seriesLength,  double seriesInterval, FftPlotter parent, Semaphore calendarLock){
		this.calendarLock = calendarLock;
		updateSeries(seriesLength, seriesInterval);
		this.parent = parent;
		JFreeChart chart = ChartFactory.createXYLineChart(
				title, xaxis, yaxis, ds, PlotOrientation.VERTICAL, 
				true, true,	false );
		XYPlot xyPlot = chart.getXYPlot();
		ValueAxis rangeAxis = xyPlot.getRangeAxis();
		rangeAxis.setRange(0.0, 5e6);
		final ChartPanel chartPanel = new ChartPanel(chart);
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(chartPanel);
        addSlider();
	}
	
	public void updateSeriesYaxis(double[] data) {
		try {
			calendarLock.acquire();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to acquire lock");
		}
		for (int i = 0; i <seriesLength; i++)
			series.updateByIndex(i, data[i]);
		calendarLock.release();
	}
	
	public void updateSeries(int seriesLength, double seriesInterval) {
		try {
			calendarLock.acquire();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to acquire lock");
		}
		this.seriesLength = seriesLength;
		ds.removeAllSeries();
		series = new XYSeries("XYGraph");
		for (int i = 0; i <seriesLength; i++){
			series.add(i*seriesInterval, 0);
		}
		ds.addSeries(series);
		calendarLock.release();
	}

	public void addSlider(){
		JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL,
                4, 124, 4);
		framesPerSecond.addChangeListener(parent);

		//Turn on labels at major tick marks.
		framesPerSecond.setMajorTickSpacing(4);
		framesPerSecond.setMinorTickSpacing(2);
		framesPerSecond.setPaintTicks(true);
		framesPerSecond.setPaintLabels(true);
		framesPerSecond.setModel(new DefaultBoundedRangeModel()
        {
            @Override
            public void setValue(int n)
            {
              if ( n%2 == 1)
            	  n--;

              super.setValue(n);
            }
        });
		add(framesPerSecond);
	}

}

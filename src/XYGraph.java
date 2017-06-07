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

@SuppressWarnings("serial")
public class XYGraph extends JPanel {
	
	private XYSeries series;
	private int seriesLength;
	private XYSeriesCollection ds = new XYSeriesCollection();
	private FftPlotter parent;
	
	public XYGraph(String title, String yaxis, String xaxis, int seriesLength,  double seriesInterval, FftPlotter parent){
		updateSeries(seriesLength, seriesInterval);
		this.parent = parent;
		JFreeChart chart = ChartFactory.createXYLineChart(
				title, xaxis, yaxis, ds, PlotOrientation.VERTICAL, 
				true, true,	false );
		final ChartPanel chartPanel = new ChartPanel(chart);
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(chartPanel);
        addSlider();
	}
	
	public void updateSeriesYaxis(double[] data) {
		for (int i = 0; i <seriesLength; i++)
			series.updateByIndex(i, data[i]);
	}
	
	public void updateSeries(int seriesLength, double seriesInterval) {
		this.seriesLength = seriesLength;
		ds.removeAllSeries();
		series = new XYSeries("XYGraph");
		for (int i = 0; i <seriesLength; i++){
			series.add(i*seriesInterval, 0);
		}
		ds.addSeries(series);
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

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ChartPanel;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

@SuppressWarnings("serial")
public class XYGraph extends JPanel {
	
	private XYSeries series;
	private int seriesLength;
	
	public XYGraph(String title, String yaxis, String xaxis, int seriesLength,  double seriesInterval){
		this.seriesLength = seriesLength;
		series = new XYSeries("XYGraph");
		
		for (int i = 0; i <seriesLength; i++){
			series.add(i*seriesInterval, 0);
		}
				
		XYSeriesCollection ds = new XYSeriesCollection();
		ds.addSeries(series);
		
		JFreeChart chart = ChartFactory.createXYLineChart(
				title, xaxis, yaxis, ds, PlotOrientation.VERTICAL, 
				true, true,	false );
		final ChartPanel chartPanel = new ChartPanel(chart);
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(chartPanel);
	}
	
	public void updateSeriesYaxis(double[] data) {
		for (int i = 0; i <seriesLength; i++)
			series.updateByIndex(i, data[i]);
	}
	
	public void updateSeries(double[] x, double[] y) {
		for (int i = 0; i <seriesLength; i++)
			series.addOrUpdate(x[i], y[i]);
	}

}

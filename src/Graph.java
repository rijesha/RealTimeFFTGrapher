import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ChartPanel;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Graph extends JPanel {
	
	private XYSeries series;
	private int seriesLength;
	
	public Graph(int seriesLength, double d){
		JFrame frame = new JFrame("testing");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.seriesLength = seriesLength;
		series = new XYSeries("XYGraph");
		
		for (int i = 0; i <seriesLength; i++){
			series.add(i*d, 0);
		}
		
		
		XYSeriesCollection ds = new XYSeriesCollection();
		ds.addSeries(series);
		
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Oh look a chart", 
				"xaxis albel", 
				"yaxis albel", 
				ds, 
				PlotOrientation.VERTICAL, 
				true, 
				true, 
				false
				);
		final ChartPanel cp = new ChartPanel(chart);
		
		frame.add(cp);
		frame.setVisible(true);
		System.out.println("Hello");
		
	}
	
	public void updateSeries(double[] data) {
		for (int i = 0; i <seriesLength; i++){
			series.updateByIndex(i, data[i]);
		}
	}

}

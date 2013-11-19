package com.atlassian.plugins.tutorial;

import com.atlassian.jira.charts.Chart;
import com.atlassian.jira.charts.jfreechart.ChartHelper;
import com.atlassian.jira.charts.jfreechart.PieChartGenerator;
import com.atlassian.jira.security.JiraAuthenticationContext;
import org.jfree.data.general.DefaultPieDataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: datlt2
 * Date: 6/24/13
 * Time: 2:01 PM
 * To change this template use File | Settings | File Tem
 * plates.
 */
public class JTricksPieChartGenerator {

    public Chart generateChart(JiraAuthenticationContext authenticationContext, int width, int height) {
        try {
            final Map<String, Object> params = new HashMap<String, Object>();
            // Create Dataset
            DefaultPieDataset dataset = new DefaultPieDataset();

            int length = (int) (Math.random() * 10 + 1);
            for (int i = 0; i < length; i++) {
                int rand = (int) (Math.random() * 100 / 3);
                dataset.setValue(i + "", rand);
            }

            final ChartHelper helper = new PieChartGenerator(dataset, authenticationContext.getI18nHelper()).generateChart();
            helper.generate(width, height);

            params.put("chart", helper.getLocation());
            params.put("chartDataset", dataset);
            params.put("imagemap", helper.getImageMap());
            params.put("imagemapName", helper.getImageMapName());
            params.put("width", width);
            params.put("height", height);

            return new Chart(helper.getLocation(), helper.getImageMap(), helper.getImageMapName(), params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating chart", e);
        }
    }
}

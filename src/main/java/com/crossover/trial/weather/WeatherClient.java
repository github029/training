package com.crossover.trial.weather;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.model.DataPoint;

/**
 * A reference implementation for the weather client. Consumers of the REST API
 * can look at WeatherClient to understand API semantics. This existing client
 * populates the REST endpoint with dummy data useful for testing.
 *US12 for task TA54321 with status: in progress; actuals: 3 hours, to do: 15
 *Actuals: 1.5, ToDo: .5, 
 *
 *Template: <StoryDefectNumber>: TId: <TaskNumber>, status: <WorkStatus>, Actuals: <ActualEfforts>, ToDo: <ToDOHours>, <Comments>
�e.g. �US5489: TId: TA15876, status: In-progress, Actuals: 1.5, ToDo: .5, added toggle for new functionality

�If we don�t remember the Task Id i.e. TID then we can put Task Index [TI] instead to update the task details. In following example it will update the 2nd task of US5489. �US5489: TI: 2, status: In-progress, Actuals: 1.5, ToDo: .5, added toggle for new functionality

�When we mark status of the task as completed the ToDo automatically becomes 0
�The Actuals, Status, ToDo are all optional and we only need to mention those which we want to update

  * @author code test administrator
 */
public class WeatherClient {

    private static final String BASE_URI = "http://localhost:9090";

    /** end point for read queries */
    private WebTarget query;

    /** end point to supply updates */
    private WebTarget collect;

    public WeatherClient() {
        Client client = ClientBuilder.newClient();
        query = client.target(BASE_URI + "/query");
        collect = client.target(BASE_URI + "/collect");
    }

    public void pingCollect() {
        WebTarget path = collect.path("/ping");
        Response response = path.request().get();
        System.out.print("collect.ping: " + response.readEntity(String.class) + "\n");
    }

    public void query(String iata) {
        WebTarget path = query.path("/weather/" + iata + "/0");
        Response response = path.request().get();
        System.out.println("query." + iata + ".0: " + response.readEntity(String.class));
    }

    public void pingQuery() {
        WebTarget path = query.path("/ping");
        Response response = path.request().get();
        System.out.println("query.ping: " + response.readEntity(String.class));
    }

    public void populate(String pointType, double mean, double first, double median, double third, int count) {
        WebTarget path = collect.path("/weather/BOS/" + pointType);
        DataPoint dp = new DataPoint.Builder().mean(mean).first(first).median(median).third(third).count(count).build();
        Response post = path.request().post(Entity.entity(dp, "application/json"));
    }

    public void exit() {
        try {
        	
            collect.path("/exit").request().get();
        } catch (Throwable t) {
            // swallow
        }
    }

    public static void main(String[] args) {
        WeatherClient wc = new WeatherClient();
        wc.pingCollect();
        wc.populate("wind", 4.0, 0.0, 4.0, 10.0, 10);

        wc.query("BOS");
        wc.query("JFK");
        wc.query("EWR");
        wc.query("LGA");
        wc.query("MMU");

        wc.pingQuery();
        wc.exit();
        System.out.print("complete");
        System.exit(0);
    }
}

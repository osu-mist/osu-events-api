package edu.oregonstate.mist.osuevents

import edu.oregonstate.mist.osuevents.db.CacheDAO
import edu.oregonstate.mist.osuevents.db.EventsDAO
import edu.oregonstate.mist.osuevents.db.UtilHttp
import edu.oregonstate.mist.osuevents.health.EventsHealthCheck
import edu.oregonstate.mist.osuevents.resources.CacheResource
import edu.oregonstate.mist.osuevents.resources.EventsResource
import io.dropwizard.Application
import io.dropwizard.client.HttpClientBuilder
import org.apache.http.client.HttpClient
import org.skife.jdbi.v2.DBI
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.setup.Environment

/**
 * Main application class.
 */
class OSUEvents extends Application<OSUEventsConfiguration> {
    /**
     * Parses command-line arguments and runs the application.
     *
     * @param configuration
     * @param environment
     */
    @Override
    public void run(OSUEventsConfiguration configuration, Environment environment) {
        DBIFactory factory = new DBIFactory()
        DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "jdbi")
        EventsDAO eventsDAO = jdbi.onDemand(EventsDAO.class)

        String backendTimezone = configuration.cacheSource.get("backendTimezone")

        environment.jersey().register(new EventsResource(eventsDAO, backendTimezone))

        HttpClient httpClient = new HttpClientBuilder(environment)
                .using(configuration.getHttpClientConfiguration())
                .build("backend-http-client")

        UtilHttp utilHttp = new UtilHttp(configuration.cacheSource)

        CacheDAO cacheDAO = new CacheDAO(utilHttp, httpClient)
        def cacheResource = new CacheResource(cacheDAO, eventsDAO)
        cacheResource.setEndpointUri(configuration.getApi().getEndpointUri())
        environment.jersey().register(cacheResource)

        EventsHealthCheck healthCheck = new EventsHealthCheck(eventsDAO)
        environment.healthChecks().register("eventsHealthCheck", healthCheck)
    }

    /**
     * Instantiates the application class with command-line arguments.
     *
     * @param arguments
     * @throws Exception
     */
    public static void main(String[] arguments) throws Exception {
        new OSUEvents().run(arguments)
    }
}

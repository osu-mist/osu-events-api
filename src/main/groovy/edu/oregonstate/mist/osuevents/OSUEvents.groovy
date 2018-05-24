package edu.oregonstate.mist.osuevents

import edu.oregonstate.mist.api.Application
import edu.oregonstate.mist.osuevents.db.LocalistDAO
import edu.oregonstate.mist.osuevents.db.EventsDAO
import edu.oregonstate.mist.osuevents.db.EventsDAOWrapper
import edu.oregonstate.mist.osuevents.health.EventsHealthCheck
import edu.oregonstate.mist.osuevents.resources.AudiencesResource
import edu.oregonstate.mist.osuevents.resources.CountiesResource
import edu.oregonstate.mist.osuevents.resources.EventTopicsResource
import edu.oregonstate.mist.osuevents.resources.EventTypesResource
import edu.oregonstate.mist.osuevents.resources.EventsResource
import io.dropwizard.client.HttpClientBuilder
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
        this.setup(configuration, environment)

        def httpClientBuilder = new HttpClientBuilder(environment)

        if (configuration.httpClientConfiguration != null) {
            httpClientBuilder.using(configuration.httpClientConfiguration)
        }

        DBIFactory factory = new DBIFactory()
        DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "jdbi")
        EventsDAO eventsDAO = jdbi.onDemand(EventsDAO.class)
        EventsDAOWrapper eventsDAOWrapper = new EventsDAOWrapper(eventsDAO)

        environment.jersey().register(new EventsResource(eventsDAOWrapper))

        EventsHealthCheck healthCheck = new EventsHealthCheck(eventsDAO)
        environment.healthChecks().register("eventsHealthCheck", healthCheck)

        LocalistDAO localistDAO = new LocalistDAO(
                httpClientBuilder.build("backend-http-client"),
                configuration.calendarAPI.baseUrl)

        environment.jersey().register(new EventTopicsResource(localistDAO))
        environment.jersey().register(new EventTypesResource(localistDAO))
        environment.jersey().register(new AudiencesResource(localistDAO))
        environment.jersey().register(new CountiesResource(localistDAO))

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

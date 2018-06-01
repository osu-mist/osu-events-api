package edu.oregonstate.mist.osuevents

import edu.oregonstate.mist.api.Application
import edu.oregonstate.mist.osuevents.db.LocalistDAO
import edu.oregonstate.mist.osuevents.db.EventsDAO
import edu.oregonstate.mist.osuevents.db.EventsDAOWrapper
import edu.oregonstate.mist.osuevents.health.EventsHealthCheck
import edu.oregonstate.mist.osuevents.resources.AudiencesResource
import edu.oregonstate.mist.osuevents.resources.CampusesResource
import edu.oregonstate.mist.osuevents.resources.CountiesResource
import edu.oregonstate.mist.osuevents.resources.DepartmentsResource
import edu.oregonstate.mist.osuevents.resources.EventTopicsResource
import edu.oregonstate.mist.osuevents.resources.EventTypesResource
import edu.oregonstate.mist.osuevents.resources.EventsResource
import edu.oregonstate.mist.osuevents.resources.FeedResource
import edu.oregonstate.mist.osuevents.resources.LocationsResource
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

        LocalistDAO localistDAO = new LocalistDAO(
                httpClientBuilder.build("backend-http-client"),
                configuration.calendarAPI.baseUrl,
                configuration.calendarAPI.organizationID)

        EventsDAO eventsDAO = jdbi.onDemand(EventsDAO.class)
        EventsDAOWrapper eventsDAOWrapper = new EventsDAOWrapper(eventsDAO)

        ResourceObjectBuilder resourceObjectBuilder = new ResourceObjectBuilder(
                configuration.api.endpointUri)

        environment.jersey().register(new EventsResource(eventsDAOWrapper,
                localistDAO,
                resourceObjectBuilder))

        EventsHealthCheck healthCheck = new EventsHealthCheck(eventsDAO)
        environment.healthChecks().register("eventsHealthCheck", healthCheck)

        environment.jersey().register(new EventTopicsResource(localistDAO, resourceObjectBuilder))
        environment.jersey().register(new EventTypesResource(localistDAO, resourceObjectBuilder))
        environment.jersey().register(new AudiencesResource(localistDAO, resourceObjectBuilder))
        environment.jersey().register(new CountiesResource(localistDAO, resourceObjectBuilder))

        CampusesResource campusesResource = new CampusesResource(localistDAO, resourceObjectBuilder)
        campusesResource.setEndpointUri(configuration.api.endpointUri)
        environment.jersey().register(campusesResource)

        LocationsResource locationsResource = new LocationsResource(localistDAO,
                resourceObjectBuilder)
        locationsResource.setEndpointUri(configuration.api.endpointUri)
        environment.jersey().register(locationsResource)

        DepartmentsResource departmentsResource = new DepartmentsResource(localistDAO,
                resourceObjectBuilder)
        departmentsResource.setEndpointUri(configuration.api.endpointUri)
        environment.jersey().register(departmentsResource)

        //environment.jersey().register(new CSVMessageBodyWriter())
        environment.jersey().register(new FeedResource(
                eventsDAOWrapper,
                localistDAO,
                configuration.calendarAPI.defaultTimezone,
                configuration.calendarAPI.exceptionTimezone,
                configuration.calendarAPI.exceptionTimezoneCampusID
        ))
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

package edu.oregonstate.mist.osuevents

import de.thomaskrille.dropwizard_template_config.TemplateConfigBundle
import edu.oregonstate.mist.api.BuildInfoManager
import edu.oregonstate.mist.api.Configuration
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.InfoResource
import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.BasicAuthenticator
import edu.oregonstate.mist.api.PrettyPrintResponseFilter
import edu.oregonstate.mist.api.jsonapi.GenericExceptionMapper
import edu.oregonstate.mist.api.jsonapi.NotFoundExceptionMapper
import edu.oregonstate.mist.osuevents.db.CacheDAO
import edu.oregonstate.mist.osuevents.db.EventsDAO
import edu.oregonstate.mist.osuevents.db.UtilHttp
import edu.oregonstate.mist.osuevents.health.EventsHealthCheck
import edu.oregonstate.mist.osuevents.resources.CacheResource
import edu.oregonstate.mist.osuevents.resources.EventsResource
import io.dropwizard.Application
import io.dropwizard.auth.AuthDynamicFeature
import io.dropwizard.auth.AuthValueFactoryProvider
import io.dropwizard.auth.basic.BasicCredentialAuthFilter
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.apache.http.client.HttpClient
import org.skife.jdbi.v2.DBI
import io.dropwizard.jdbi.DBIFactory

/**
 * Main application class.
 */
class OSUEvents extends Application<OSUEventsConfiguration> {
    /**
     * Initializes application bootstrap.
     *
     * @param bootstrap
     */
    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        bootstrap.addBundle(new TemplateConfigBundle())
    }

    /**
     * Registers lifecycle managers and Jersey exception mappers
     * and container response filters
     *
     * @param environment
     * @param buildInfoManager
     */
    protected void registerAppManagerLogic(Environment environment,
                                           BuildInfoManager buildInfoManager) {

        environment.lifecycle().manage(buildInfoManager)

        environment.jersey().register(new NotFoundExceptionMapper())
        environment.jersey().register(new GenericExceptionMapper())
        environment.jersey().register(new PrettyPrintResponseFilter())
    }

    /**
     * Parses command-line arguments and runs the application.
     *
     * @param configuration
     * @param environment
     */
    @Override
    public void run(OSUEventsConfiguration configuration, Environment environment) {
        Resource.loadProperties()
        BuildInfoManager buildInfoManager = new BuildInfoManager()
        registerAppManagerLogic(environment, buildInfoManager)

        environment.jersey().register(new InfoResource(buildInfoManager.getInfo()))
        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<AuthenticatedUser>()
                        .setAuthenticator(new BasicAuthenticator(
                            configuration.getCredentialsList()))
                        .setRealm('OSUEvents')
                        .buildAuthFilter()
        ))
        environment.jersey().register(new AuthValueFactoryProvider.Binder
                <AuthenticatedUser>(AuthenticatedUser.class))

        DBIFactory factory = new DBIFactory()
        DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "jdbi")
        EventsDAO eventsDAO = jdbi.onDemand(EventsDAO.class)
        environment.jersey().register(new EventsResource(eventsDAO))

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

package edu.oregonstate.mist.osuevents

import com.fasterxml.jackson.annotation.JsonProperty
import edu.oregonstate.mist.api.Configuration
import io.dropwizard.client.HttpClientConfiguration

import javax.validation.Valid
import javax.validation.constraints.NotNull
import io.dropwizard.db.DataSourceFactory

public class OSUEventsConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty("database")
    DataSourceFactory database = new DataSourceFactory()

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        database
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory
    }

    @JsonProperty("cache")
    @NotNull
    @Valid
    Map<String, String> cacheSource

    @NotNull
    @Valid
    private HttpClientConfiguration httpClient = new HttpClientConfiguration()

    @JsonProperty("httpClient")
    public HttpClientConfiguration getHttpClientConfiguration() {
        httpClient
    }

    @JsonProperty("httpClient")
    public void setHttpClientConfiguration(HttpClientConfiguration httpClient) {
        this.httpClient = httpClient
    }
}
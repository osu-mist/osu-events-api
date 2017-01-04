package edu.oregonstate.mist.osuevents

import com.fasterxml.jackson.annotation.JsonProperty
import edu.oregonstate.mist.api.Configuration
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
}